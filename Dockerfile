FROM drupal:10.3.0-php8.3

# Instalação das dependências básicas
RUN apt-get update && \
    apt-get install -y curl netcat-openbsd git unzip mariadb-client && \
    rm -rf /var/lib/apt/lists/*

# Instalação do Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Definir o diretório de trabalho para /var/www/html
WORKDIR /var/www/html

# Apagar todo o conteúdo da pasta, incluindo arquivos e diretórios ocultos
RUN rm -rf /var/www/html/.* /var/www/html/* || true

# Criar o projeto Drupal no diretório principal
RUN composer create-project drupal/recommended-project:^10 . --no-interaction

# Ajustar as permissões do diretório do Drupal
RUN chown -R www-data:www-data /var/www/html && \
    find /var/www/html -type d -exec chmod 755 {} \; && \
    find /var/www/html -type f -exec chmod 644 {} \;

# Instalar Drush compatível com o Drupal 10 (uso da versão 11.x)
RUN composer global require drush/drush:^11 --prefer-dist --no-progress --no-scripts --with-all-dependencies && \
    ln -s ~/.composer/vendor/bin/drush /usr/local/bin/drush

RUN composer require drupal/color drupal/key drupal/devel --no-interaction

# Copiar o script de entrada
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Configurar permissões e arquivos padrão
RUN mkdir -p web/sites/default && \
    cp web/sites/default/default.settings.php web/sites/default/settings.php && \
    cp web/sites/default/default.services.yml web/sites/default/services.yml && \
    chmod 644 web/sites/default/settings.php && \
    chmod 644 web/sites/default/services.yml && \
    chown -R www-data:www-data web/sites/default

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["apache2-foreground"]
