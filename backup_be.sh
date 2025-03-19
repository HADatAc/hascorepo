#!/bin/bash
if [ "$#" -ne 3 ]; then
    echo "Uso: $0 <NOME_SITE> <NOME_REPOSITORIO> <NOME_INSTANCIA>"
    exit 1
fi

NOME_SITE="$1"
NOME_REPOSITORIO="$2"
NOME_INSTANCIA="$3"

BACKUP_DIR="$HOME/backups-data/BE"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_backend_${NOME_SITE}_${NOME_REPOSITORIO}_${NOME_INSTANCIA}_${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"

SAGRES_HOST="ubuntu@52.214.194.214"

mkdir -p $BACKUP_DIR/fuseki

FUSEKI_CONTAINER="hascoapi_fuseki"
FUSEKI_VOLUME_PATH="/fuseki/databases"
echo -n "Starting backup of Fuseki volume... "
if docker ps --format '{{.Names}}' | grep -q "$FUSEKI_CONTAINER"; then
  if docker exec $FUSEKI_CONTAINER test -d "$FUSEKI_VOLUME_PATH"; then
    docker exec $FUSEKI_CONTAINER tar -czf /tmp/fuseki_backup_$DATE.tar.gz -C "$FUSEKI_VOLUME_PATH" .
    docker cp $FUSEKI_CONTAINER:/tmp/fuseki_backup_$DATE.tar.gz $BACKUP_DIR/fuseki
    docker exec $FUSEKI_CONTAINER rm /tmp/fuseki_backup_$DATE.tar.gz
    echo -e "\033[40G[OK]"
  else
    echo -e "\033[40G[ERRO]"
    echo "Error: The directory $FUSEKI_VOLUME_PATH does not exits in the container $FUSEKI_CONTAINER!"
  fi
else
  echo -e "\033[40G[ERRO]"
  echo "Error: The container $FUSEKI_CONTAINER is not running!"
fi

echo -n "Compressing all backups into a single file... "
tar czf $FINAL_BACKUP_PATH -C $BACKUP_DIR fuseki
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Trouble when compressing backups!"
  exit 1
fi
echo -e "\033[40G[OK]"

rm -rf $BACKUP_DIR/fuseki

echo -e "Consolidated backup created in: $FINAL_BACKUP_PATH"

echo -n "Transferring the backup to Sagres... "
scp -i /home/ubuntu/.ssh/graxiom_main.pem -P 22 $FINAL_BACKUP_PATH $SAGRES_HOST:./tmp
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failed to transfer the backup to the Sagres server!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup successfully transferred to Sagres VM"

# Finalizando
exit 0