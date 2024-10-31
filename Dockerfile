# Dockerfile-setup
FROM php:8.3-cli

# Instala dependências básicas
RUN apt-get update && \
    apt-get install -y curl git && \
    rm -rf /var/lib/apt/lists/*

# Define o diretório de trabalho
WORKDIR /opt/setup

# Copie o script de setup para o container
COPY setup.sh .

# Dê permissão de execução ao script
RUN chmod +x setup.sh

# Comando que será executado ao iniciar o container
CMD ["./setup.sh"]
