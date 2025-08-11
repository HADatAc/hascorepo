#!/bin/bash

# Função para checar variável de ambiente
check_env_var() {
  local var_name=$1
  local var_value=${!var_name}

  if [ -z "$var_value" ]; then
    echo "ERROR: Environment variable $var_name is not set."
    exit 1
  fi

  if [ ! -d "$var_value" ]; then
    echo "ERROR: Directory pointed by $var_name ($var_value) does not exist."
    exit 1
  fi
}

echo "Checking environment variables..."

check_env_var "JAVA_HOME"
check_env_var "MAVEN_HOME"

echo "JAVA_HOME=$JAVA_HOME"
echo "MAVEN_HOME=$MAVEN_HOME"

# Definindo mvn executável
MVN_CMD="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MVN_CMD" ]; then
  echo "ERROR: Maven executable not found or not executable at $MVN_CMD"
  exit 1
fi

echo "Running Maven test..."

# Executa o Maven com saída detalhada e gera relatório na pasta padrão
"$MVN_CMD" clean test -Dtest=FullSetupWSandNHANES -X

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
  echo "Test executed successfully."
else
  echo "Test failed with exit code $EXIT_CODE."
fi

echo "Surefire reports generated at: target/surefire-reports"

exit $EXIT_CODE
