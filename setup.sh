#!/bin/bash
set -e

DRUPAL_URL="http://drupal:8081"
ADMIN_USER="admin"
ADMIN_PASS="admin"
COOKIE_FILE="/tmp/drupal_cookie.txt"
JWT_KEY_VALUE="qwertyuiopasdfghjklzxcvbnm123456"
API_BASE_URL="http://hascoapi:9000"  

drupal_login() {
    echo "Realizando login no Drupal..."

    response=$(curl -s -c "$COOKIE_FILE" -X POST "$DRUPAL_URL/user/login" \
        -d "name=$ADMIN_USER&pass=$ADMIN_PASS&form_id=user_login_form" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -H "Referer: $DRUPAL_URL/user/login")

    if echo "$response" | grep -q "Redirecting"; then
        echo "Login realizado com sucesso."
    else
        echo "Falha no login. Verifique as credenciais ou a configuração do Drupal."
        exit 1
    fi
}


add_key() {
    echo "Obtendo tokens de formulário..."
    form_page=$(curl -s -X GET "$DRUPAL_URL/admin/config/system/keys/add" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE")

    form_build_id=$(echo "$form_page" | grep -oP '(?<=name="form_build_id" value=")[^"]+')
    form_token=$(echo "$form_page" | grep -oP '(?<=name="form_token" value=")[^"]+')

    if [[ -z "$form_build_id" || -z "$form_token" ]]; then
        echo "Erro ao obter tokens de formulário. Verifique o login e a URL."
        exit 1
    fi

    echo "Criando chave JWT no Drupal..."
    response=$(curl -s -X POST "$DRUPAL_URL/admin/config/system/keys/add" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -b "$COOKIE_FILE" \
        --data-urlencode "label=jwt" \
        --data-urlencode "id=jwt_key" \
        --data-urlencode "description=jwt" \
        --data-urlencode "key_type=authentication" \
        --data-urlencode "key_provider=config" \
        --data-urlencode "key_input_settings[key_value]=$JWT_KEY_VALUE" \
        --data-urlencode "form_build_id=$form_build_id" \
        --data-urlencode "form_token=$form_token" \
        --data-urlencode "form_id=key_add_form" \
        --data-urlencode "op=Save")

}


config_drupal_api() {
    echo "Obtendo tokens de formulário para configurar API..."
    form_page=$(curl -s -X GET "$DRUPAL_URL/admin/config/rep" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE")

    form_build_id=$(echo "$form_page" | grep -oP '(?<=name="form_build_id" value=")[^"]+')
    form_token=$(echo "$form_page" | grep -oP '(?<=name="form_token" value=")[^"]+')

    if [[ -z "$form_build_id" || -z "$form_token" ]]; then
        echo "Erro ao obter tokens de formulário. Verifique o login e a URL."
        exit 1
    fi

    echo "Configurando API no Drupal..."
    response=$(curl -s -X POST "$DRUPAL_URL/admin/config/rep" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -b "$COOKIE_FILE" \
        --data-urlencode "site_label=cienciaPT" \
        --data-urlencode "site_name=CienciaPT" \
        --data-urlencode "repository_domain_url=https://cienciapt.org" \
        --data-urlencode "repository_domain_namespace=cienciapt" \
        --data-urlencode "repository_description=cienciapt" \
        --data-urlencode "api_url=http://172.17.0.1:9000" \
        --data-urlencode "jwt_secret=jwt_key" \
        --data-urlencode "form_build_id=$form_build_id" \
        --data-urlencode "form_token=$form_token" \
        --data-urlencode "form_id=rep_form_settings" \
        --data-urlencode "op=Save configuration")
}

reload_triples() {
    echo "Obtendo tokens de formulário para recarregar triples..."
    form_page=$(curl -s -X GET "$DRUPAL_URL/admin/config/rep/namespace" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE")

    # Extrair form_build_id e form_token
    form_build_id=$(echo "$form_page" | grep -oP '(?<=name="form_build_id" value=")[^"]+')
    form_token=$(echo "$form_page" | grep -oP '(?<=name="form_token" value=")[^"]+')

    # Verificar se os tokens foram obtidos com sucesso
    if [[ -z "$form_build_id" || -z "$form_token" ]]; then
        echo "Erro ao obter tokens de formulário. Verifique o login e a URL."
        exit 1
    fi

    # Realizar o POST para recarregar os triples
    echo "Recarregando triples de todos os namespaces..."
    response=$(curl -s -X POST "$DRUPAL_URL/admin/config/rep/namespace" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -b "$COOKIE_FILE" \
        --data-urlencode "reload=Reload Triples from All NameSpaces with URL" \
        --data-urlencode "form_build_id=$form_build_id" \
        --data-urlencode "form_token=$form_token" \
        --data-urlencode "form_id=rep_form_namespace")
}

set_preferred_names() {
    form_page=$(curl -s -X GET "$DRUPAL_URL/admin/config/rep/preferred" \
        -H "Content-Type: application/json" \
        -b "$COOKIE_FILE")

    # Extrair form_build_id e form_token
    form_build_id=$(echo "$form_page" | grep -oP '(?<=name="form_build_id" value=")[^"]+')
    form_token=$(echo "$form_page" | grep -oP '(?<=name="form_token" value=")[^"]+')

    # Verificar se os tokens foram obtidos com sucesso
    if [[ -z "$form_build_id" || -z "$form_token" ]]; then
        echo "Erro ao obter tokens de formulário. Verifique o login e a URL."
        exit 1
    fi

    response=$(curl -s -X POST "$DRUPAL_URL/admin/config/rep/preferred" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -b "$COOKIE_FILE" \
        --data-urlencode "preferred_instrument=Instrument" \
        --data-urlencode "preferred_detector=Detector" \
        --data-urlencode "back=Back to rep Settings" \
        --data-urlencode "form_build_id=$form_build_id" \
        --data-urlencode "form_token=$form_token" \
        --data-urlencode "form_id=rep_form_preferred_names")

}



drupal_login       
add_key
config_drupal_api
reload_triples
set_preferred_names