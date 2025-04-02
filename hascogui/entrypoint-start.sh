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

cd /opt/drupal/web/modules/custom

cd rep && git pull && cd ..
cd std && git pull && cd ..
cd sir && git pull && cd ..
cd sem && git pull && cd ..
cd dpl && git pull && cd ..

cd /opt/drupal

# Clear cache
$DRUSH_COMMAND cr

apache2-foreground

