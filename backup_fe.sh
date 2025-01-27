#!/bin/bash

BACKUP_DIR="$HOME/backups-data/FE"
NOME_SITE="CienciaPT"
NOME_REPOSITORIO="CienciaPT"
NOME_INSTANCIA="Development"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_frontend${NOME_SITE}_${NOME_REPOSITORIO}_${NOME_INSTANCIA}_${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"

SAGRES_HOST="ubuntu@54.247.233.88"

mkdir -p $BACKUP_DIR/drupal $BACKUP_DIR/drupal/composer $BACKUP_DIR/db
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


# Criando o arquivo tar.gz final
echo -n "Compactando todos os backups em um único arquivo... "
tar czf $FINAL_BACKUP_PATH -C $BACKUP_DIR drupal db
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao compactar os backups!"
  exit 1
fi
echo -e "\033[40G[OK]"

# Limpando backups temporários
rm -rf $BACKUP_DIR/drupal $BACKUP_DIR/db

echo -e "Backup consolidado criado em: $FINAL_BACKUP_PATH"

echo -n "Transferindo o backup para o Sagres... "
scp -i /home/ubuntu/.ssh/graxiom_main.pem -P 22 $FINAL_BACKUP_PATH $SAGRES_HOST:./tmp
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao transferir o backup para o servidor Sagres!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup transferido com sucesso para: $SAGRES_PATH"

echo -n "Copiando o backup para o container do Drupal na VM Sagres... "
ssh -i /home/ubuntu/.ssh/graxiom_main.pem $SAGRES_HOST "docker cp /tmp/$FINAL_BACKUP_NAME drupal:/opt/drupal/web/sites/default/$NOME_SITE/$NOME_REPOSITORIO/$NOME_INSTANCIA"
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao copiar o backup para o container do Drupal!"
  exit 1
fi
echo -e "\033[40G[OK]"

# Finalizando
exit 0