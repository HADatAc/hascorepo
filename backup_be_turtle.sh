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

mkdir -p "$BACKUP_DIR/fuseki"

FUSEKI_DATASET="store"
FUSEKI_BACKUP_FILE="$BACKUP_DIR/fuseki/fuseki_backup_$DATE.ttl"

echo -n "Starting Fuseki backup (Turtle format)... "
curl -X GET "http://localhost:3030/$FUSEKI_DATASET/data?default" -H "Accept: text/turtle" -o "$FUSEKI_BACKUP_FILE"

if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao exportar os dados do Fuseki!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -n "Compressing all backups in one file... "
tar czf "$FINAL_BACKUP_PATH" -C "$BACKUP_DIR" fuseki
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure on compressing backups!"
  exit 1
fi
echo -e "\033[40G[OK]"

rm -rf "$BACKUP_DIR/fuseki"

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
