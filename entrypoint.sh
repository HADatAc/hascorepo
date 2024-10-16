#!/bin/bash
set -e

# Esperar pelo banco de dados estar disponível
until nc -z -v -w30 $DB_HOST 3306
do
  echo "Aguardando conexão com o banco de dados..."
  sleep 5
done
echo "Banco de dados disponível - executando comando"

# Definir o diretório root do Drupal
export DRUPAL_ROOT="/var/www/html/web"
DRUSH_COMMAND="drush --root=${DRUPAL_ROOT}"

APACHE_PORT_CONF="/etc/apache2/ports.conf"
echo "" > $APACHE_PORT_CONF
echo "Listen 8081" >> $APACHE_PORT_CONF
# Criar o arquivo de configuração do Apache
APACHE_SITE_CONF="/etc/apache2/sites-available/000-default.conf"
if ! grep -q "VirtualHost \*:8081" $APACHE_SITE_CONF; then
  echo "<VirtualHost *:8081>
      DocumentRoot ${DRUPAL_ROOT}
      <Directory ${DRUPAL_ROOT}>
          Options Indexes FollowSymLinks
          AllowOverride All
          Require all granted
      </Directory>
      ErrorLog \${APACHE_LOG_DIR}/error.log
      CustomLog \${APACHE_LOG_DIR}/access.log combined
  </VirtualHost>" > $APACHE_SITE_CONF
fi

echo "ServerName localhost" >> /etc/apache2/apache2.conf

# Ativar o site
a2ensite 000-default

# Verificar se o settings.php já está configurado
if [ ! -f "$DRUPAL_ROOT/sites/default/settings.php" ]; then
    echo "Criando settings.php e services.yml..."
    cp $DRUPAL_ROOT/sites/default/default.settings.php $DRUPAL_ROOT/sites/default/settings.php
    cp $DRUPAL_ROOT/sites/default/default.services.yml $DRUPAL_ROOT/sites/default/services.yml
    chmod 644 $DRUPAL_ROOT/sites/default/settings.php
    chmod 644 $DRUPAL_ROOT/sites/default/services.yml
    chown www-data:www-data $DRUPAL_ROOT/sites/default/settings.php
    chown www-data:www-data $DRUPAL_ROOT/sites/default/services.yml
fi

# Configurar as credenciais do banco de dados no settings.php
sed -i "s/\$databases = \[\];/\$databases['default']['default'] = array( \
  'database' => '${DB_NAME}', \
  'username' => '${DB_USER}', \
  'password' => '${DB_PASS}', \
  'host' => '${DB_HOST}', \
  'driver' => 'mysql', \
  'prefix' => '', \
);/" $DRUPAL_ROOT/sites/default/settings.php

# Verificar se o site Drupal já está instalado
if ! $DRUSH_COMMAND status bootstrap | grep -q 'Successful'; then
    echo "Instalando o site Drupal..."
    $DRUSH_COMMAND site:install standard \
        --db-url=mysql://${DB_USER}:${DB_PASS}@${DB_HOST}/${DB_NAME} \
        --site-name="Drupal Site" --account-name=admin --account-pass=admin -y
else
    echo "Drupal já está instalado."
fi

echo "Habilitando hasco_barrio..."
$DRUSH_COMMAND -v theme:enable hasco_barrio -y

if $DRUSH_COMMAND pml --type=theme --status=enabled | grep -q 'hasco_barrio'; then
    $DRUSH_COMMAND config-set system.theme default hasco_barrio -y
    echo "Tema hasco_barrio definido como padrão com sucesso."
else
    echo "Erro ao habilitar o tema hasco_barrio."
fi

# Limpar cache novamente após definir o tema
$DRUSH_COMMAND cr

echo "Habilitando módulos..."
MODULES=("color" "key" "devel")
for MODULE in "${MODULES[@]}"; do
    echo "Habilitando módulo: $MODULE"
    $DRUSH_COMMAND en $MODULE -y || echo "Erro ao habilitar módulo: $MODULE"
done

# Limpar cache
$DRUSH_COMMAND cr

# Iniciar o Apache em foreground
apache2-foreground
