#!/bin/bash
set -e

MODULES_FILE="${MODULES_FILE:-/opt/drupal/modules.json}"

until nc -z -v -w30 $DB_HOST 3306
do
  echo "Waiting for database connection..."
  sleep 5
done
echo "Database available - executing command"

export DRUPAL_ROOT="/opt/drupal"
DRUSH_COMMAND="drush --root=${DRUPAL_ROOT}"
INSTALL_FLAG="${DRUPAL_ROOT}/web/sites/default/.installed"

clone_module() {
  local name="$1"
  local branch="$2"
  local target="/opt/drupal/web/modules/custom/$name"
  if [ -d "$target" ]; then
    echo "Updating module $name..."
    cd "$target"
    git fetch --all
    git checkout "$branch"
    git pull origin "$branch" || true
  else
    echo "Cloning module $name ($branch)..."
    git clone --branch "$branch" "https://github.com/HADatAc/${name}.git" "$target"
  fi
}

enable_modules() {
  local modules=("$@")
  for m in "${modules[@]}"; do
    echo "Enabling module: $m"
    $DRUSH_COMMAND en "$m" -y || echo "Error enabling module: $m"
  done
}

if [ ! -f "$INSTALL_FLAG" ]; then
    APACHE_PORT_CONF="/etc/apache2/ports.conf"
    echo "" > $APACHE_PORT_CONF
    echo "Listen 80" >> $APACHE_PORT_CONF

    APACHE_SITE_CONF="/etc/apache2/sites-available/000-default.conf"
    if ! grep -q "VirtualHost \*:80" $APACHE_SITE_CONF; then
    cat > $APACHE_SITE_CONF <<EOF
<VirtualHost *:80>
    DocumentRoot ${DRUPAL_ROOT}/web
    <Directory ${DRUPAL_ROOT}/web>
        Options Indexes FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>
    ErrorLog \${APACHE_LOG_DIR}/error.log
    CustomLog \${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
EOF
    fi

    echo "ServerName localhost" >> /etc/apache2/apache2.conf
    a2ensite 000-default

    if [ ! -f "$DRUPAL_ROOT/web/sites/default/settings.php" ]; then
        echo "Creating settings.php and services.yml..."
        cp $DRUPAL_ROOT/web/sites/default/default.settings.php $DRUPAL_ROOT/web/sites/default/settings.php
        cp $DRUPAL_ROOT/web/sites/default/default.services.yml $DRUPAL_ROOT/web/sites/default/services.yml
        chmod 644 $DRUPAL_ROOT/web/sites/default/settings.php
        chmod 644 $DRUPAL_ROOT/web/sites/default/services.yml
        chown www-data:www-data $DRUPAL_ROOT/web/sites/default/settings.php
        chown www-data:www-data $DRUPAL_ROOT/web/sites/default/services.yml
    fi

    sed -i "s/\$databases = \[\];/\$databases['default']['default'] = array( \
    'database' => '${DB_NAME}', \
    'username' => '${DB_USER}', \
    'password' => '${DB_PASS}', \
    'host' => '${DB_HOST}', \
    'driver' => 'mysql', \
    'prefix' => '', \
    );/" $DRUPAL_ROOT/web/sites/default/settings.php

    if grep -q "# \$settings\['file_private_path'\] = '';" $DRUPAL_ROOT/web/sites/default/settings.php; then
        sed -i "s|# \$settings\['file_private_path'\] = '';|\$settings['file_private_path'] = 'sites/default/hascorepo/';|" $DRUPAL_ROOT/web/sites/default/settings.php
    fi

    PRIVATE_DIR="$DRUPAL_ROOT/web/sites/default/hascorepo"
    mkdir -p "$PRIVATE_DIR"
    chown -R www-data:www-data "$PRIVATE_DIR"
    chmod -R u+rwx "$PRIVATE_DIR"

    if ! $DRUSH_COMMAND status bootstrap | grep -q 'Successful'; then
        echo "Installing Drupal site..."
        $DRUSH_COMMAND site:install standard \
            --db-url=mysql://${DB_USER}:${DB_PASS}@${DB_HOST}/${DB_NAME} \
            --site-name="Drupal Site" --account-name=admin --account-pass=admin -y
    fi

    $DRUSH_COMMAND cr

    # Clonar e habilitar módulos selecionados
    MODULE_NAMES=()
    if [ -f "$MODULES_FILE" ]; then
      echo "Parsing $MODULES_FILE"
      COUNT=$(jq length "$MODULES_FILE")
      if [ "$COUNT" -gt 0 ]; then
        for row in $(jq -c '.[]' "$MODULES_FILE"); do
          NAME=$(echo "$row" | jq -r '.name')
          BRANCH=$(echo "$row" | jq -r '.branch')
          [ -z "$NAME" ] && continue
          [ -z "$BRANCH" ] && BRANCH="main"
          clone_module "$NAME" "$BRANCH"
          MODULE_NAMES+=("$NAME")
        done
      fi
    else
      echo "No modules.json found; no custom modules will be cloned."
    fi

    # Módulos core/básicos
    BASE_MODULES=("color" "key")
    MODULES_TO_ENABLE=("${BASE_MODULES[@]}" "${MODULE_NAMES[@]}")
    enable_modules "${MODULES_TO_ENABLE[@]}"

    $DRUSH_COMMAND cr
    touch $INSTALL_FLAG
    echo "Flag de instalação criada em $INSTALL_FLAG."
fi

# --- Simple OAuth keys ---
KEY_DIR="/var/keys/simple_oauth"
PRIVATE_KEY="$KEY_DIR/private.key"
PUBLIC_KEY="$KEY_DIR/public.key"

mkdir -p "$KEY_DIR"
chown www-data:www-data "$KEY_DIR"
chmod 700 "$KEY_DIR"

if [ ! -f "$PRIVATE_KEY" ] || [ ! -f "$PUBLIC_KEY" ]; then
    echo "Generating new RSA keys for Simple OAuth..."
    openssl genrsa -out "$PRIVATE_KEY" 2048
    openssl rsa -in "$PRIVATE_KEY" -pubout -out "$PUBLIC_KEY"
    chmod 400 "$PRIVATE_KEY"
    chmod 444 "$PUBLIC_KEY"
    chown www-data:www-data "$PRIVATE_KEY" "$PUBLIC_KEY"
else
    echo "RSA keys already exist, skipping generation."
fi

apache2-foreground