FROM drupal:10.3.0-php8.3
# Installation of basic dependencies
RUN apt-get update && \
    apt-get install -y curl netcat-openbsd git unzip mariadb-client\
            dos2unix && \
    rm -rf /var/lib/apt/lists/*
# Installation of Composer
RUN curl -sS https://getcomposer.org/installer | \
    php -- --install-dir=/usr/local/bin --filename=composer
# Set the working directory to /opt/drupal
WORKDIR /opt/drupal
# Remove all content from the folder, including hidden files and directories
RUN rm -rf /opt/drupal.* /opt/drupal* || true
# Create the Drupal project in the main directory
RUN composer create-project drupal/recommended-project:^10 . \
    --no-interaction
# Adjust permissions of the Drupal directory
RUN find /opt/drupal -type d \
    -exec 'chmod 755 {} && chown www-data:www-data {}' \;
RUN find /opt/drupal -type f \
    -exec 'chmod 644 {} && chown www-data:www-data {}' \;
RUN a2enmod rewrite
# Install Drush compatible with Drupal 10 (using version 11.x)
RUN composer global require drush/drush:^11 --prefer-dist --no-progress --no-scripts --with-all-dependencies && \
    ln -s ~/.composer/vendor/bin/drush /usr/local/bin/drush
RUN composer require drupal/bootstrap_barrio
RUN composer require hasco/hasco_barrio:@dev --no-interaction
RUN composer require drupal/color drupal/key drupal/devel --prefer-dist --no-interaction --update-with-dependencies
RUN composer require hasco/rep --no-interaction --with-all-dependencies --update-with-dependencies
RUN composer require hasco/sir --no-interaction --with-all-dependencies --update-with-dependencies
RUN composer require hasco/std --no-interaction --with-all-dependencies --update-with-dependencies
RUN composer require hasco/sem --no-interaction --with-all-dependencies --update-with-dependencies
RUN composer require hasco/dpl --no-interaction --with-all-dependencies --update-with-dependencies
RUN composer require sagres/pmsrgui:@dev --no-interaction --with-all-dependencies --update-with-dependencies

# Adjust the .htaccess file
RUN if [ ! -f /opt/drupal/web/.htaccess ]; then \
        cp /opt/drupal/web/example.htaccess /opt/drupal/web/.htaccess; \
    fi && \
    chmod 644 /opt/drupal/web/.htaccess && \
    chown www-data:www-data /opt/drupal/web/.htaccess
    
# Copy the entrypoint script
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN dos2unix /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
RUN chmod 644 /opt/drupal/web/sites/default/default.settings.php && \
    chmod 644 /opt/drupal/web/sites/default/default.services.yml && \
    chown www-data:www-data /opt/drupal/web/sites/default/default.settings.php && \
    chown www-data:www-data /opt/drupal/web/sites/default/default.services.yml
    
RUN cp web/sites/default/default.settings.php web/sites/default/settings.php && \
    cp web/sites/default/default.services.yml web/sites/default/services.yml && \
    chmod 644 web/sites/default/settings.php && \
    chmod 644 web/sites/default/services.yml && \
    chown -R www-data:www-data web/sites/default

RUN mkdir -p /opt/drupal/web/sites/default/files && \
    chmod 755 /opt/drupal/web/sites/default/files && \
    chown -R www-data:www-data /opt/drupal/web/sites/default/files
    
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["apache2-foreground"]