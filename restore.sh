#!/bin/bash

# Diretório onde estão armazenados os backups
BACKUP_DIR="$HOME/Secretária/backups-data"
DATE=$(date +%F)

# Função para verificar se um container está em execução
check_container_running() {
  local CONTAINER_NAME=$1
  echo -n "A verificar o container $CONTAINER_NAME... "
  if ! docker ps --format '{{.Names}}' | grep -q "$CONTAINER_NAME"; then
    echo -e "\033[40G[ERRO]"
    echo "Erro: O container $CONTAINER_NAME não está em execução!"
    return 1
  fi
  echo -e "\033[40G[OK]"
  return 0
}

# Parar todos os containers
echo -n "A parar todos os containers... "
docker-compose -f docker-compose-hascorepo-development.yml down
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Não foi possível parar os containers!"
  exit 1
fi
echo -e "\033[40G[OK]"

# Subir containers necessários para restauração
echo -n "A iniciar containers necessários para restauração... "
docker-compose -f docker-compose-hascorepo-development.yml up -d db drupal fuseki
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Não foi possível iniciar os containers necessários para restauração!"
  exit 1
fi
echo -e "\033[40G[OK]"

# Restaurar o Drupal
DRUPAL_CONTAINER="drupal"
check_container_running $DRUPAL_CONTAINER || exit 1

echo -n "A restaurar os ficheiros do Drupal... "
if [ -f "$BACKUP_DIR/drupal/drupal_files_backup_$DATE.tar.gz" ]; then
  docker cp "$BACKUP_DIR/drupal/drupal_files_backup_$DATE.tar.gz" $DRUPAL_CONTAINER:/tmp/
  docker exec $DRUPAL_CONTAINER tar xzf /tmp/drupal_files_backup_$DATE.tar.gz -C /opt/drupal/web
  docker exec $DRUPAL_CONTAINER rm /tmp/drupal_files_backup_$DATE.tar.gz
  echo -e "\033[40G[OK]"
else
  echo -e "\033[40G[ERRO]"
  echo "Erro: Backup dos ficheiros do Drupal não encontrado em $BACKUP_DIR/drupal!"
  exit 1
fi

# Restaurar a base de dados
DB_CONTAINER="drupal_db"
check_container_running $DB_CONTAINER || exit 1

echo -n "A restaurar a base de dados... "
if [ -f "$BACKUP_DIR/db/mariadb_backup_$DATE.sql" ]; then
  docker exec -i $DB_CONTAINER mysql -u drupal -pdrupal drupal < "$BACKUP_DIR/db/mariadb_backup_$DATE.sql"
  echo -e "\033[40G[OK]"
else
  echo -e "\033[40G[ERRO]"
  echo "Erro: Backup da base de dados não encontrado em $BACKUP_DIR/db!"
  exit 1
fi

# Restaurar o Fuseki
FUSEKI_CONTAINER="hascoapi_fuseki"
check_container_running $FUSEKI_CONTAINER || exit 1

echo -n "A restaurar os dados do Fuseki... "
if [ -f "$BACKUP_DIR/fuseki/fuseki_backup_$DATE.tar.gz" ]; then
  docker cp "$BACKUP_DIR/fuseki/fuseki_backup_$DATE.tar.gz" $FUSEKI_CONTAINER:/tmp/
  docker exec $FUSEKI_CONTAINER tar xzf /tmp/fuseki_backup_$DATE.tar.gz -C /fuseki/databases
  docker exec $FUSEKI_CONTAINER rm /tmp/fuseki_backup_$DATE.tar.gz
  echo -e "\033[40G[OK]"
else
  echo -e "\033[40G[ERRO]"
  echo "Erro: Backup dos dados do Fuseki não encontrado em $BACKUP_DIR/fuseki!"
  exit 1
fi

# Subir todos os containers
echo -n "A iniciar todos os containers no modo operacional... "
docker-compose -f docker-compose-hascorepo-development.yml up -d
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Não foi possível iniciar os containers!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -e "Restauração concluída com sucesso!"
