#!/bin/bash
if [ "$#" -ne 3 ]; then
    echo "Uso: $0 <NOME_SITE> <NOME_REPOSITORIO> <NOME_INSTANCIA>"
    exit 1
fi

NOME_SITE="$1"
NOME_REPOSITORIO="$2"
NOME_INSTANCIA="$3"

BACKUP_DIR="/var/data/backups-data/BE"
TEMP_DIR="/tmp/be_backup_temp"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_backend_${NOME_SITE}_${NOME_REPOSITORIO}_${NOME_INSTANCIA}_${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"
SAGRES_HOST="ubuntu@52.214.194.214"

mkdir -p "$BACKUP_DIR"
mkdir -p "$TEMP_DIR"

echo -n "Starting Fuseki Backup... "
sudo docker run --rm \
    --volumes-from hascoapi_fuseki \
    -v "$TEMP_DIR":/backup \
    ubuntu bash -c "cd /fuseki/databases && tar -czf /backup/fuseki_store.tar.gz store"

if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure exporting fuseki data!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -n "Starting HASCO API Backup... "
# Copia a pasta /var/hascoapi do container
sudo docker cp hascoapi:/var/hascoapi "$TEMP_DIR/hascoapi_var"
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error copying /var/hascoapi from hascoapi container!"
  exit 1
fi

echo -e "\033[40G[Successfully backuped /var/hascoapi folder]"

# Copia o ficheiro application.conf
sudo docker cp hascoapi:/hascoapi/conf/application.conf "$TEMP_DIR/application.conf"
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error copying application.conf from hascoapi container!"
  exit 1
fi
echo -e "\033[40G[Successfully backuped application.conf file]"

# Compacta tudo num único tar.gz
echo -n "Creating consolidated backup archive... "
tar -czf "$FINAL_BACKUP_PATH" -C "$TEMP_DIR" .
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure creating final backup archive!"
  exit 1
fi
echo -e "\033[40G[OK]"

echo -e "Backup consolidated in: $FINAL_BACKUP_PATH"

# Transfere para o servidor Sagres
echo -n "Transfering the backup file to Sagres... "
scp -i /home/ubuntu/.ssh/graxiom_main.pem -P 22 "$FINAL_BACKUP_PATH" "$SAGRES_HOST:./tmp"
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Error: Failure in transfering the backup file to Sagres!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup transfered with Success to Sagres!"

# Limpeza local
rm -rf "$TEMP_DIR"
if [ -f "$FINAL_BACKUP_PATH" ]; then
    rm -f "$FINAL_BACKUP_PATH"
    if [ $? -eq 0 ]; then
        echo "Local backup file removed: $FINAL_BACKUP_PATH"
    else
        echo "Warning: could not remove local backup file $FINAL_BACKUP_PATH"
    fi
fi

exit 0