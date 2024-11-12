#!/bin/bash

echo "Iniciando backup completo..."

# 1. Backup Geral - Backup completo de dados críticos
tar czvf /backups/files/full_backup_$(date +%F).tar.gz /opt/drupal/web

# 2. Backup da base de dados do Drupal
echo "Realizando backup do banco de dados..."
mysqldump -h db -u drupal -pdrupal drupal > /backups/db/mariadb_backup_$(date +%F).sql

# 3. Backup do Fuseki - Exporta RDF do Fuseki
echo "Realizando backup do Fuseki (exportação RDF)..."
curl -X POST http://fuseki:3030/dataset/export > /backups/fuseki/fuseki_backup_$(date +%F).rdf

# 3.1 Backup Completo do Volume de Dados do Fuseki
echo "Realizando backup do volume do Fuseki..."
tar czvf /backups/fuseki/fuseki_volume_backup_$(date +%F).tar.gz /fuseki/databases

# 4. Backup dos ficheiros do Drupal
echo "Realizando backup dos arquivos do Drupal..."
tar czvf /backups/files/drupal_files_backup_$(date +%F).tar.gz -C /opt/drupal/web/sites/default/files .

# 5. Backup do Drupal em formato serializado (configurações e conteúdos)
echo "Realizando backup do Drupal em formato serializado..."
drush cex -y --destination=/backups/drupal_serialized/config_$(date +%F)
drush sql-dump --result-file=/backups/drupal_serialized/drupal_db_serialized_$(date +%F).sql

# 6. Backup dos ficheiros do HascoAPI
echo "Realizando backup dos arquivos do HascoAPI..."
tar czvf /backups/files/hascoapi_files_backup_$(date +%F).tar.gz /var/hascoapi

# 7. Backup das Configurações do Repositório Hascorepo
# echo "Realizando backup das configurações do repositório Hascorepo..."
# tar czvf /backups/files/hascorepo_config_backup_$(date +%F).tar.gz /path/to/hascorepo

echo "Backup completo finalizado."
