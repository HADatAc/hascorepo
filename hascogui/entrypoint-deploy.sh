#!/bin/bash
set -e

MODULES_FILE="${MODULES_FILE:-/opt/drupal/modules.json}"

# Wait for DB
until nc -z -v -w30 "$DB_HOST" 3306; do
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
    if ! $DRUSH_COMMAND en "$m" -y; then
      echo "Error enabling module: $m"
    fi
  done
}

# Wait for modules.json up to 30s
for i in {1..6}; do
  if [ -f "$MODULES_FILE" ]; then
    break
  fi
  echo "modules.json não encontrado em $MODULES_FILE (tentativa $i/6)..."
  sleep 5
done

MODULE_NAMES=()
if [ -f "$MODULES_FILE" ]; then
  echo "Parsing $MODULES_FILE"
  COUNT=$(jq length "$MODULES_FILE")
  if [ "$COUNT" -gt 0 ]; then
    while IFS= read -r row; do
      NAME=$(echo "$row" | jq -r '.name')
      BRANCH=$(echo "$row" | jq -r '.branch')
      [ -z "$NAME" ] && continue
      [ -z "$BRANCH" ] && BRANCH="main"
      clone_module "$NAME" "$BRANCH"
      MODULE_NAMES+=("$NAME")
    done < <(jq -c '.[]' "$MODULES_FILE")
  fi
else
  echo "No modules.json found at $MODULES_FILE; no custom modules will be cloned."
fi

dedupe_bootstrap_barrio_schema() {
  local schema="$DRUPAL_ROOT/web/themes/custom/bootstrap_barrio/config/schema/bootstrap_barrio.schema.yml"
  if [ -f "$schema" ]; then
    local tmp
    tmp=$(mktemp)
    awk '
      /bootstrap_barrio_messages_widget_toast_delay:/{
        if(seen){next}
        seen=1
      }
      {print}
    ' "$schema" > "$tmp" && mv "$tmp" "$schema"
  fi
}

if [ -f "$INSTALL_FLAG" ]; then
  echo "Drupal já está instalado. Pulando configuração."
else
  APACHE_PORT_CONF="/etc/apache2/ports.conf"
  echo "" > "$APACHE_PORT_CONF"
  echo "Listen 80" >> "$APACHE_PORT_CONF"
  APACHE_SITE_CONF="/etc/apache2/sites-available/000-default.conf"
  if ! grep -q "VirtualHost \*:80" "$APACHE_SITE_CONF"; then
    cat > "$APACHE_SITE_CONF" <<EOF
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
    cp "$DRUPAL_ROOT/web/sites/default/default.settings.php" "$DRUPAL_ROOT/web/sites/default/settings.php"
    cp "$DRUPAL_ROOT/web/sites/default/default.services.yml" "$DRUPAL_ROOT/web/sites/default/services.yml"
    chmod 644 "$DRUPAL_ROOT/web/sites/default/settings.php"
    chmod 644 "$DRUPAL_ROOT/web/sites/default/services.yml"
    chown www-data:www-data "$DRUPAL_ROOT/web/sites/default/settings.php"
    chown www-data:www-data "$DRUPAL_ROOT/web/sites/default/services.yml"
  fi

  sed -i "s/\$databases = \[\];/\$databases['default']['default'] = array( \
  'database' => '${DB_NAME}', \
  'username' => '${DB_USER}', \
  'password' => '${DB_PASS}', \
  'host' => '${DB_HOST}', \
  'driver' => 'mysql', \
  'prefix' => '', \
  );/" "$DRUPAL_ROOT/web/sites/default/settings.php"

  if grep -q "# \$settings\['file_private_path'\] = '';" "$DRUPAL_ROOT/web/sites/default/settings.php"; then
    sed -i "s|# \$settings\['file_private_path'\] = '';|\$settings['file_private_path'] = 'sites/default/hascorepo/';|" "$DRUPAL_ROOT/web/sites/default/settings.php"
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
  chown -R www-data:www-data "$PRIVATE_DIR"
  chmod -R u+rwx "$PRIVATE_DIR"

  if ! $DRUSH_COMMAND status bootstrap | grep -q 'Successful'; then
    echo "Installing Drupal site..."
    $DRUSH_COMMAND site:install standard \
      --db-url=mysql://${DB_USER}:${DB_PASS}@${DB_HOST}/${DB_NAME} \
      --site-name="Drupal Site" --account-name=admin --account-pass=admin -y
  else
    echo "Drupal is already installed."
  fi

  $DRUSH_COMMAND cr

  # Patch duplicate key in bootstrap_barrio schema to avoid install failure
  dedupe_bootstrap_barrio_schema

  # Theme setup
  $DRUSH_COMMAND theme:enable hasco_barrio -y || echo "Erro ao habilitar tema hasco_barrio"
  if $DRUSH_COMMAND pml --type=theme --status=enabled | grep -q 'hasco_barrio'; then
    $DRUSH_COMMAND config-set system.theme default hasco_barrio -y || echo "Não foi possível definir hasco_barrio como tema default"
  else
    echo "Tema hasco_barrio não está habilitado; não será definido como default."
  fi

  echo "Enabling modules..."
  BASE_MODULES=("color" "key" "devel")
  MODULES_TO_ENABLE=("${BASE_MODULES[@]}" "${MODULE_NAMES[@]}")
  enable_modules "${MODULES_TO_ENABLE[@]}"

  $DRUSH_COMMAND cr
  touch "$INSTALL_FLAG"
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

# Start Apache in foreground
apache2-foreground