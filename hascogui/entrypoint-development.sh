#!/bin/bash
set -e

# Wait for the database to be available
until nc -z -v -w30 $DB_HOST 3306
do
  echo "Waiting for database connection..."
  sleep 5
done
echo "Database available - executing command"

# Set the root directory of Drupal
export DRUPAL_ROOT="/opt/drupal"
DRUSH_COMMAND="drush --root=${DRUPAL_ROOT}"

APACHE_PORT_CONF="/etc/apache2/ports.conf"
echo "" > $APACHE_PORT_CONF
echo "Listen 80" >> $APACHE_PORT_CONF
# Create the Apache configuration file
APACHE_SITE_CONF="/etc/apache2/sites-available/000-default.conf"
if ! grep -q "VirtualHost \*:80" $APACHE_SITE_CONF; then
  echo "<VirtualHost *:80>
      DocumentRoot ${DRUPAL_ROOT}/web
      <Directory ${DRUPAL_ROOT}/web>
          Options Indexes FollowSymLinks
          AllowOverride All
          Require all granted
      </Directory>
      ErrorLog \${APACHE_LOG_DIR}/error.log
      CustomLog \${APACHE_LOG_DIR}/access.log combined
  </VirtualHost>" > $APACHE_SITE_CONF
fi

echo "ServerName localhost" >> /etc/apache2/apache2.conf

# Enable the site
a2ensite 000-default

# Check if settings.php is already configured
if [ ! -f "$DRUPAL_ROOT/web/sites/default/settings.php" ]; then
    echo "Creating settings.php and services.yml..."
    
    # Check if default.settings.php and default.services.yml exist
    if [ -f "$DRUPAL_ROOT/web/sites/default/default.settings.php" ]; then
        cp $DRUPAL_ROOT/web/sites/default/default.settings.php $DRUPAL_ROOT/web/sites/default/settings.php
        echo "settings.php created from default.settings.php"
    else
        echo "default.settings.php not found. Make sure the Drupal installation is complete."
        exit 1
    fi
    
    if [ -f "$DRUPAL_ROOT/web/sites/default/default.services.yml" ]; then
        cp $DRUPAL_ROOT/web/sites/default/default.services.yml $DRUPAL_ROOT/web/sites/default/services.yml
        echo "services.yml created from default.services.yml"
    else
        echo "default.services.yml not found. Make sure the Drupal installation is complete."
        exit 1
    fi
    
    chmod 644 $DRUPAL_ROOT/web/sites/default/settings.php
    chmod 644 $DRUPAL_ROOT/web/sites/default/services.yml
    chown www-data:www-data $DRUPAL_ROOT/web/sites/default/settings.php
    chown www-data:www-data $DRUPAL_ROOT/web/sites/default/services.yml
fi
# Configure the database credentials in settings.php
sed -i "s/\$databases = \[\];/\$databases['default']['default'] = array( \
  'database' => '${DB_NAME}', \
  'username' => '${DB_USER}', \
  'password' => '${DB_PASS}', \
  'host' => '${DB_HOST}', \
  'driver' => 'mysql', \
  'prefix' => '', \
);/" $DRUPAL_ROOT/web/sites/default/settings.php

# Configure the private file path in settings.php
if grep -q "# \$settings\['file_private_path'\] = '';" $DRUPAL_ROOT/web/sites/default/settings.php; then
    sed -i "s|# \$settings\['file_private_path'\] = '';|\$settings['file_private_path'] = 'sites/default/hascorepo/';|" $DRUPAL_ROOT/web/sites/default/settings.php
    echo "Private file path configured in settings.php"
else
    echo "Private file path setting not found or already configured in settings.php"
fi

PRIVATE_DIR="$DRUPAL_ROOT/web/sites/default/hascorepo"

if [ ! -d "$PRIVATE_DIR" ]; then
    echo "Creating directory: $PRIVATE_DIR"
    mkdir -p "$PRIVATE_DIR"
    echo "Directory created: $PRIVATE_DIR"
fi

# Set ownership to www-data
chown -R www-data:www-data "$PRIVATE_DIR"
echo "Ownership of $PRIVATE_DIR set to www-data"

# Set permissions to +rwx
chmod -R u+rwx "$PRIVATE_DIR"
echo "Permissions of $PRIVATE_DIR set to +rwx"

# Check if the Drupal site is already installed
if ! $DRUSH_COMMAND status bootstrap | grep -q 'Successful'; then
    echo "Installing Drupal site..."
    $DRUSH_COMMAND site:install standard \
        --db-url=mysql://${DB_USER}:${DB_PASS}@${DB_HOST}/${DB_NAME} \
        --site-name="Drupal Site" --account-name=admin --account-pass=admin -y
else
    echo "Drupal is already installed."
fi

echo "Enabling hasco_barrio..."
$DRUSH_COMMAND -v theme:enable hasco_barrio -y

if $DRUSH_COMMAND pml --type=theme --status=enabled | grep -q 'hasco_barrio'; then
    $DRUSH_COMMAND config-set system.theme default hasco_barrio -y
    echo "hasco_barrio theme set as default successfully."
else
    echo "Error enabling hasco_barrio theme."
fi

# Clear cache again after setting the theme
$DRUSH_COMMAND cr

echo "Enabling modules..."
MODULES=("color" "key" "rep" "sir" "std" "sem" "dpl" "pmsr" "devel")
for MODULE in "${MODULES[@]}"; do
    echo "Enabling module: $MODULE"
    $DRUSH_COMMAND en $MODULE -y || echo "Error enabling module: $MODULE"
done

# Clear cache
$DRUSH_COMMAND cr

# Start Apache in foreground
apache2-foreground
