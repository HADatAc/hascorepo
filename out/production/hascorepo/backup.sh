#!/bin/bash

BACKUP_DIR="$HOME/Secretária/backups-data"
DATE=$(date +%F)

mkdir -p $BACKUP_DIR/drupal $BACKUP_DIR/drupal/composer $BACKUP_DIR/db $BACKUP_DIR/fuseki

DRUPAL_CONTAINER="drupal"
echo -n "Verificando o container $DRUPAL_CONTAINER... "
if ! docker ps --format '{{.Names}}' | grep -q "$DRUPAL_CONTAINER"; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: O container $DRUPAL_CONTAINER não está em execução!"
  exit 1
fi
echo -e "\033[40G[OK]"

# Backup do Drupal
DRUPAL_VOLUME_PATH="/opt/drupal/web"
echo -n "Iniciando backup dos ficheiros do Drupal... "
if ! docker exec $DRUPAL_CONTAINER test -d "$DRUPAL_VOLUME_PATH"; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: O diretório $DRUPAL_VOLUME_PATH não existe no container $DRUPAL_CONTAINER!"
  exit 1
fi
echo -e "\033[40G[OK]"

docker exec $DRUPAL_CONTAINER tar czf /tmp/drupal_files_backup_$DATE.tar.gz -C $DRUPAL_VOLUME_PATH .
if [ $? -ne 0 ]; then
  echo "Erro: Falha ao executar o comando tar dentro do container $DRUPAL_CONTAINER!"
  exit 1
fi
docker cp $DRUPAL_CONTAINER:/tmp/drupal_files_backup_$DATE.tar.gz $BACKUP_DIR/drupal
docker exec $DRUPAL_CONTAINER rm /tmp/drupal_files_backup_$DATE.tar.gz

echo -n "Copiando ficheiros composer.json e composer.lock... "
COMPOSER_FILES=("composer.json" "composer.lock")
for FILE in "${COMPOSER_FILES[@]}"; do
  if docker exec $DRUPAL_CONTAINER test -f "/opt/drupal/$FILE"; then
    docker cp $DRUPAL_CONTAINER:/opt/drupal/$FILE $BACKUP_DIR/drupal/composer
  else
    echo -e "\033[40G[AVISO]"
    echo "Aviso: O ficheiro $FILE não existe no container $DRUPAL_CONTAINER!"
  fi
done
echo -e "\033[40G[OK]"

DB_CONTAINER="drupal_db"
echo -n "Iniciando backup da base de dados... "
docker exec $DB_CONTAINER mysqldump -u drupal -pdrupal drupal > $BACKUP_DIR/db/mariadb_backup_$DATE.sql
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao exportar a base de dados MariaDB!"
  exit 1
fi
echo -e "\033[40G[OK]"

FUSEKI_CONTAINER="hascoapi_fuseki"
FUSEKI_VOLUME_PATH="/fuseki/databases"
echo -n "Iniciando backup do volume de dados do Fuseki... "
if docker ps --format '{{.Names}}' | grep -q "$FUSEKI_CONTAINER"; then
  if docker exec $FUSEKI_CONTAINER test -d "$FUSEKI_VOLUME_PATH"; then
    docker exec $FUSEKI_CONTAINER tar czf /tmp/fuseki_backup_$DATE.tar.gz -C "$FUSEKI_VOLUME_PATH" .
    docker cp $FUSEKI_CONTAINER:/tmp/fuseki_backup_$DATE.tar.gz $BACKUP_DIR/fuseki
    docker exec $FUSEKI_CONTAINER rm /tmp/fuseki_backup_$DATE.tar.gz
    echo -e "\033[40G[OK]"
  else
    echo -e "\033[40G[ERRO]"
    echo "Erro: O diretório $FUSEKI_VOLUME_PATH não existe no container $FUSEKI_CONTAINER!"
  fi
else
  echo -e "\033[40G[ERRO]"
  echo "Erro: O container $FUSEKI_CONTAINER não está em execução!"
fi

echo -e "Backup finalizado e guardado em $BACKUP_DIR"
