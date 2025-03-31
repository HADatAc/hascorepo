#!/bin/bash
if [ "$#" -ne 3 ]; then
    echo "Uso: $0 <NOME_SITE> <NOME_REPOSITORIO> <NOME_INSTANCIA>"
    exit 1
fi

NOME_SITE="$1"
NOME_REPOSITORIO="$2"
NOME_INSTANCIA="$3"

BACKUP_DIR="/var/data/backups-data/FE"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_frontend_${NOME_SITE}_${NOME_REPOSITORIO}_${NOME_INSTANCIA}_${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"

SAGRES_HOST="ubuntu@52.214.194.214"

mkdir -p $BACKUP_DIR/drupal $BACKUP_DIR/drupal/composer $BACKUP_DIR/db
DRUPAL_CONTAINER="drupal"
echo -n "Checking the container: $DRUPAL_CONTAINER... "
if ! docker ps --format '{{.Names}}' | grep -q "$DRUPAL_CONTAINER"; then
  echo -e "\033[40G[ERRO]"
  echo "Error: The container $DRUPAL_CONTAINER is not running!"
  exit 1
fi
echo -e "\033[40G[OK]"

DRUPAL_VOLUME_PATH="/opt/drupal/web"
echo -n "Starting to backup Drupal files... "
if ! docker exec $DRUPAL_CONTAINER test -d "$DRUPAL_VOLUME_PATH"; then
  echo -e "\033[40G[ERRO]"
  echo "Error: The $DRUPAL_VOLUME_PATH directory does not exist in the $DRUPAL_CONTAINER container!"
  exit 1
fi
echo -e "\033[40G[OK]"

docker exec $DRUPAL_CONTAINER tar czf /tmp/drupal_files_backup_$DATE.tar.gz -C $DRUPAL_VOLUME_PATH .
if [ $? -ne 0 ]; then
  echo "Error: Failed to execute the tar command inside the $DRUPAL_CONTAINER container!"
  exit 1
fi
docker cp $DRUPAL_CONTAINER:/tmp/drupal_files_backup_$DATE.tar.gz $BACKUP_DIR/drupal
docker exec $DRUPAL_CONTAINER rm /tmp/drupal_files_backup_$DATE.tar.gz

echo -n "Copying composer.json and composer.lock files... "
COMPOSER_FILES=("composer.json" "composer.lock")
for FILE in "${COMPOSER_FILES[@]}"; do
  if docker exec $DRUPAL_CONTAINER test -f "/opt/drupal/$FILE"; then
    docker cp $DRUPAL_CONTAINER:/opt/drupal/$FILE $BACKUP_DIR/drupal/composer
  else
    echo -e "\033[40G[AVISO]"
    echo "Warning: The $FILE file does not exist in the $DRUPAL_CONTAINER container!"
  fi
done
echo -e "\033[40G[OK]"

DB_CONTAINER="drupal_db"
echo -n "Starting database backup..."
docker exec $DB_CONTAINER mysqldump -u drupal -pdrupal drupal > $BACKUP_DIR/db/mariadb_backup_$DATE.sql
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failed to export the MariaDB database!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -n "Compressing all backups into a single file..."
tar czf $FINAL_BACKUP_PATH -C $BACKUP_DIR drupal db
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failed to compress backups!"
  exit 1
fi
echo -e "\033[40G[OK]"

rm -rf $BACKUP_DIR/drupal $BACKUP_DIR/db

echo -e "Consolidated backup created at: $FINAL_BACKUP_PATH"

echo -n "Transferring the backup to Sagres..."
scp -i /home/ubuntu/.ssh/graxiom_main.pem -P 22 $FINAL_BACKUP_PATH $SAGRES_HOST:./tmp
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failed to transfer the backup to the Sagres server!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup successfully transferred to Sagres VM"

exit 0
