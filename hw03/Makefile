.PHONY: help

# ANSI color codes
CYAN := \033[0;36m
BOLD := \033[1m
NC := \033[0m # No Color - resets to default
include .env
export
help: ## This help
	@echo ===================================================================
	@echo "$(BOLD)Use:$(NC)  make $(CYAN)<command>$(NC) where command one of:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "$(CYAN)%-30s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ===================================================================

.DEFAULT_GOAL := help

# Determine whether to use "docker-compose" or "docker compose"
DOCKER_COMPOSE := $(shell which docker-compose 2>/dev/null)
ifeq ($(DOCKER_COMPOSE),)
	DOCKER_COMPOSE := $(shell which docker 2>/dev/null)
	PREFIX := compose
else
	PREFIX :=
endif

# TASKS

cluster-id: ## regenerate CLUSTER_ID for kafka
	./scripts/cluster-id.sh

up: ## Runs the containers with default config
	$(DOCKER_COMPOSE) $(PREFIX) up -d --build

down: ## Stops and removes all containers
	$(DOCKER_COMPOSE) $(PREFIX) down

#exec: ## Open console in docker container for executing kafka commands
#	docker exec -it kafka-hw03 bash

run: ## run homework application
	java -cp build/libs/hw03-1.0-SNAPSHOT.jar ru.otus.kafka.hw03.Producer

jar: *.java,*.kts,*.yaml ## build homework application
	gradle build
