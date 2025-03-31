#!/bin/bash
if [ "$#" -ne 3 ]; then
    echo "Uso: $0 <NOME_SITE> <NOME_REPOSITORIO> <NOME_INSTANCIA>"
    exit 1
fi

NOME_SITE="$1"
NOME_REPOSITORIO="$2"
NOME_INSTANCIA="$3"

BACKUP_DIR="/var/data/backups-data/BE"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_backend_${NOME_SITE}_${NOME_REPOSITORIO}_${NOME_INSTANCIA}_${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"

SAGRES_HOST="ubuntu@52.214.194.214"

mkdir -p "$BACKUP_DIR"

echo -n "Starting Fuseki Backup... "
sudo docker run --rm \
    --volumes-from hascoapi_fuseki \
    -v "$BACKUP_DIR":/backup \
    ubuntu bash -c "cd /fuseki/databases && tar -czf /backup/$FINAL_BACKUP_NAME store"

if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure exporting fuseki data!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -e "Backup consolidated in: $FINAL_BACKUP_PATH"

echo -n "Transfering the backup file to Sagres... "
scp -i /home/ubuntu/.ssh/graxiom_main.pem -P 22 "$FINAL_BACKUP_PATH" "$SAGRES_HOST:./tmp"
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure in transfering the backup file to Sagres!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup transfered with Success to Sagres!"

exit 0
