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

up: ## Runs the containers with default config
	$(DOCKER_COMPOSE) $(PREFIX) up -d --build
	. .env

down: ## Stops and removes all containers
	$(DOCKER_COMPOSE) $(PREFIX) down

build: ## Build the project using Gradle
	../gradlew build

run: ## Build the project using Gradle
	../gradlew run
