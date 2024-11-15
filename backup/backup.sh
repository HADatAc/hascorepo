#!/bin/bash

echo "A Iniciar backup completo..."

# Criando diretórios de backup, caso não existam
mkdir -p /backups/files /backups/db /backups/fuseki /backups/drupal_serialized

# 1. Backup Geral - Backup completo de dados críticos (Drupal)
echo "Backup do diretório do Drupal..."
tar czvf /backups/files/full_backup_$(date +%F).tar.gz /opt/drupal/web

# 2. Backup da base de dados do Drupal (MariaDB)
echo "Backup da base de dados Drupal..."
mysqldump -h db -u drupal -pdrupal drupal > /backups/db/mariadb_backup_$(date +%F).sql

# 3. Backup do Fuseki - Exporta RDF do Fuseki
echo "Backup do Fuseki (exportação RDF)..."
curl -X POST http://fuseki:3030/dataset/export > /backups/fuseki/fuseki_backup_$(date +%F).rdf

# 3.1 Backup Completo do Volume de Dados do Fuseki
echo "Backup completo do volume de dados do Fuseki..."
tar czvf /backups/fuseki/fuseki_volume_backup_$(date +%F).tar.gz /fuseki/databases

# 4. Backup dos arquivos do Drupal (uploads, imagens, etc.)
echo "Backup dos ficheiros do Drupal..."
tar czvf /backups/files/drupal_files_backup_$(date +%F).tar.gz -C /opt/drupal/web/sites/default/files .

# 5. Backup do Drupal em formato serializado (configurações e conteúdo)
echo "Backup das configurações e conteúdo do Drupal..."
drush cex -y --destination=/backups/drupal_serialized/config_$(date +%F)
drush sql-dump --result-file=/backups/drupal_serialized/drupal_db_serialized_$(date +%F).sql

# 6. Backup dos ficheiros do HascoAPI
echo "Backup dos ficheiros do HascoAPI..."
tar czvf /backups/files/hascoapi_files_backup_$(date +%F).tar.gz /var/hascoapi

echo "Backup completo finalizado."