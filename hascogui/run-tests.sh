#!/bin/bash

# Iniciar o Xvfb (X virtual framebuffer) na tela :99 com uma resolução de 1280x1024
Xvfb :99 -screen 0 1280x1024x16 &

# Definir a variável de ambiente DISPLAY para usar a tela virtual criada
export DISPLAY=:99

echo "À espera que o Drupal inicie..."
until curl -s http://drupal:8081/ > /dev/null; do
  echo "5 segundos."
  sleep 5
done
# Executar os testes Java com JUnit
java -cp ".:/opt/development/tests/selenium-java-4.26.0/*:/opt/development/tests/selenium-java-4.26.0/libs/*:/opt/development/junit4/*" org.junit.runner.JUnitCore tests.UnitTest