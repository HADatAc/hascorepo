FROM php:8.3-cli

# Instala dependências e configurações básicas
RUN apt-get update && apt-get install -y \
    cron \
    curl \
    mariadb-client \
    git \
    zip \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Instala o Composer
RUN curl -sS https://getcomposer.org/installer | \
    php -- --install-dir=/usr/local/bin --filename=composer

# Instala o Drush com Composer e cria link simbólico
RUN composer global require drush/drush:^11 --prefer-dist --no-progress --no-scripts --with-all-dependencies && \
    ln -s /root/.composer/vendor/bin/drush /usr/local/bin/drush

# Copia o script de backup e configura o cron
COPY backup.sh /usr/local/bin/backup.sh
RUN chmod +x /usr/local/bin/backup.sh
COPY cronjob /etc/cron.d/backup-cron
RUN chmod 0644 /etc/cron.d/backup-cron && \
    crontab /etc/cron.d/backup-cron

# Comando para iniciar o cron
CMD ["cron", "-f"]
