#!/bin/bash

BACKUP_DIR="$HOME/backups-data/BE"
DATE=$(date -u +"%Y-%m-%d_%H-%M-%S")
IP_ADDRESS=$(hostname -I | awk '{print $1}')
FINAL_BACKUP_NAME="hascorepo_backup_backend${IP_ADDRESS}_${DATE}.tar.gz"
FINAL_BACKUP_PATH="$BACKUP_DIR/$FINAL_BACKUP_NAME"

SAGRES_HOST="admin@54.247.233.88"
NOME_SITE="CienciaPT"
NOME_REPOSITORIO="CienciaPT"
NOME_INSTANCIA="Development"
SAGRES_PATH="/opt/drupal/web/sites/default/$NOME_SITE/$NOME_REPOSITORIO/$NOME_INSTANCIA"

mkdir -p $BACKUP_DIR/fuseki

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

# Criando o arquivo tar.gz final
echo -n "Compactando todos os backups em um único arquivo... "
tar czf $FINAL_BACKUP_PATH -C $BACKUP_DIR fuseki
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao compactar os backups!"
  exit 1
fi
echo -e "\033[40G[OK]"

rm -rf $BACKUP_DIR/fuseki

echo -e "Backup consolidado criado em: $FINAL_BACKUP_PATH"

echo -n "Transferindo o backup para o Sagres... "
scp -P 22 $FINAL_BACKUP_PATH $SAGRES_HOST:$SAGRES_PATH
if [ $? -ne 0 ]; then
  echo -e "\033[40G[ERRO]"
  echo "Erro: Falha ao transferir o backup para o servidor Sagres!"
  exit 1
fi
echo -e "\033[40G[OK]"
echo "Backup transferido com sucesso para: $SAGRES_PATH"

# Finalizando
exit 0