#!/bin/bash

echo "=== HAScorepo Test Compilation Script ==="
echo "Compilando testes de todos os diretórios..."

# Criar diretório bin se não existir
mkdir -p tests/bin

# Definir diretórios de teste
TEST_DIRS=(
    "tests/A1"
    "tests/base"
    "tests/config"
    "tests/DA"
    "tests/docker"
    "tests/DP2"
    "tests/DSG"
    "tests/INS"
    "tests/repository"
    "tests/SDD"
    "tests/STR"
    "tests/testfiles"
    "tests/utils"
)

# Encontrar todos os arquivos .java
echo "Procurando arquivos .java..."
JAVA_FILES=""
for dir in "${TEST_DIRS[@]}"; do
    if [ -d "$dir" ]; then
        FILES=$(find "$dir" -name "*.java" -type f)
        if [ ! -z "$FILES" ]; then
            echo "  - $dir: $(echo "$FILES" | wc -l) arquivos"
            JAVA_FILES="$JAVA_FILES $FILES"
        fi
    fi
done

# Também incluir arquivos na raiz de tests/
ROOT_FILES=$(find tests -maxdepth 1 -name "*.java" -type f)
if [ ! -z "$ROOT_FILES" ]; then
    echo "  - tests/ (raiz): $(echo "$ROOT_FILES" | wc -l) arquivos"
    JAVA_FILES="$JAVA_FILES $ROOT_FILES"
fi

if [ -z "$JAVA_FILES" ]; then
    echo "❌ Nenhum arquivo .java encontrado!"
    exit 1
fi

echo ""
echo "Total de arquivos Java: $(echo $JAVA_FILES | wc -w)"

# Verificar se libs existem
if [ ! -d "tests/libs" ]; then
    echo "❌ Diretório tests/libs não encontrado!"
    exit 1
fi

echo "Bibliotecas disponíveis:"
ls tests/libs/*.jar | head -5
echo "..."

# Compilar
echo ""
echo "Compilando todos os testes..."
javac -cp "tests/libs/*" -d tests/bin $JAVA_FILES

if [ $? -eq 0 ]; then
    echo "✅ Compilação bem-sucedida!"
    echo ""
    echo "Classes compiladas:"
    find tests/bin -name "*.class" | head -10
    TOTAL_CLASSES=$(find tests/bin -name "*.class" | wc -l)
    echo "... (total: $TOTAL_CLASSES classes)"
    echo ""
    echo "Para executar testes específicos:"
    echo "  ./run_specific_tests.sh A1"
    echo "  ./run_specific_tests.sh config"
    echo "  ./run_specific_tests.sh UninstallPmsrModuleTest"
else
    echo "❌ Erro na compilação!"
    echo "Verifique:"
    echo "1. Se todas as bibliotecas estão em tests/libs/"
    echo "2. Se os packages nos arquivos .java estão corretos"
    echo "3. Se não há erros de sintaxe nos arquivos"
    exit 1
fi
