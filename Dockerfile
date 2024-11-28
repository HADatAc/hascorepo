FROM php:8.3-cli

RUN apt-get update && \
    apt-get install -y curl git \
    dos2unix && \
    rm -rf /var/lib/apt/lists/*

RUN curl -sS https://getcomposer.org/installer | \
    php -- --install-dir=/usr/local/bin --filename=composer

RUN curl -fsSL https://download.docker.com/linux/debian/gpg | tee /etc/apt/trusted.gpg.d/docker.asc > /dev/null && \
    echo "deb [arch=amd64] https://download.docker.com/linux/debian bullseye stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli

RUN composer global require drush/drush:^11 --prefer-dist --no-progress --no-scripts --with-all-dependencies && \
    ln -s /root/.composer/vendor/bin/drush /usr/local/bin/drush    


WORKDIR /opt/setup

COPY setup.sh .

RUN chmod +x setup.sh
RUN dos2unix setup.sh
CMD ["./setup.sh"]