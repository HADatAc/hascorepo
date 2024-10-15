# hascogui
Docker and documentation required to create the front-end of a HASCO/Repo instance.

# Tutorial Docker-Compose

## Iniciar os Containers com Docker Compose

Com os ficheiros docker-compose.yml, Dockerfile e entrypoint.sh já configurados, siga os passos abaixo para iniciar o Drupal:
- Abra o terminal e navegue até o diretório onde os ficheiros estão localizados.
- Execute o comando a seguir para construir e iniciar os containers: ``` docker-compose up -d --build ```

Com todos os containers ativos, acedemos ao Drupal pelo seguinte URL: http://localhost:8081 com o username: admin e password: admin

## Comandos úteis

- Parar os containers: ``` docker-compose stop ```
- Parar e remover os containers e volumes: ``` docker-compose down -v ```
- Limpar Recursos Docker não utilizados: ``` docker system prune -a ```
- Verificar se os containers estão em execução: ``` docker-compose ps ```
- Aceder a um container: ``` docker exec -it "nome-do-container" bash ```


3. Connecting the Back-End API to the Front-End


