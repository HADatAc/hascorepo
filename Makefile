HASCOAPI_REPO_URL = https://github.com/HADatAc/hascoapi
HASCOAPI_DIR = hascoapiteste
BRANCH_NAME = DEVELOPMENT_V0.8

.PHONY: clone update

# Tarefa para clonar o repositório hascoapi
clone:
	@if [ ! -d "$(HASCOAPI_DIR)" ]; then \
		echo "A clonar o repositório hascoapi..."; \
		git clone $(HASCOAPI_REPO_URL) $(HASCOAPI_DIR); \
	else \
		echo "A pasta hascoapi já existe. Use 'make update' para atualizar."; \
	fi

# Tarefa para atualizar o repositório hascoapi
update: clone
	cd $(HASCOAPI_DIR) && git pull origin $(BRANCH_NAME)
