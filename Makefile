# ═══════════════════════════════════════════════════════════════
#  AboAli POS — Makefile
#  Usage: make <target>
# ═══════════════════════════════════════════════════════════════

# ── Directories ─────────────────────────────────────────────────
BACKEND_DIR  := aboali-pos-backend
FRONTEND_DIR := aboali-pos-system

# ── Colors for pretty output ─────────────────────────────────────
CYAN  := \033[0;36m
GREEN := \033[0;32m
RED   := \033[0;31m
YELLOW:= \033[0;33m
RESET := \033[0m

.PHONY: all start stop restart \
        db db-stop db-reset db-shell db-backup db-restore \
        backend frontend \
        build install logs status clean help

# ──────────────────────────────────────────────────────────────────
# 🚀  DEFAULT — make start
# ──────────────────────────────────────────────────────────────────
all: start

## start: Start everything — DB + Backend + Frontend
start:
	@echo "$(CYAN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@echo "$(CYAN)  🚀  Starting AboAli POS System...         $(RESET)"
	@echo "$(CYAN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@$(MAKE) db
	@echo "$(YELLOW)⏳  Waiting for DB to be healthy...$(RESET)"
	@$(MAKE) wait-db
	@echo "$(GREEN)✅  Database is ready!$(RESET)"
	@$(MAKE) backend &
	@$(MAKE) frontend &
	@echo ""
	@echo "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@echo "$(GREEN)  ✅  All services started!               $(RESET)"
	@echo "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@echo "  🗄️  PostgreSQL  → localhost:5432"
	@echo "  🖥️  pgAdmin     → http://localhost:5050"
	@echo "  ☕  Backend     → http://localhost:8080"
	@echo "  📖  Swagger UI  → http://localhost:8080/swagger-ui.html"
	@echo "  🌐  Frontend    → http://localhost:5173"
	@echo "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"

## stop: Stop everything
stop:
	@echo "$(RED)🛑  Stopping all services...$(RESET)"
	@cd $(BACKEND_DIR) && docker compose down
	@pkill -f "spring-boot:run"  2>/dev/null || true
	@pkill -f "vite"             2>/dev/null || true
	@echo "$(GREEN)✅  All services stopped.$(RESET)"

## restart: Full restart
restart: stop start

# ──────────────────────────────────────────────────────────────────
# 🗄️  DATABASE
# ──────────────────────────────────────────────────────────────────

## db: Start PostgreSQL + pgAdmin containers
db:
	@echo "$(CYAN)🗄️  Starting database containers...$(RESET)"
	@cd $(BACKEND_DIR) && docker compose up -d postgres pgadmin

## db-stop: Stop database containers
db-stop:
	@echo "$(RED)🛑  Stopping database containers...$(RESET)"
	@cd $(BACKEND_DIR) && docker compose down

## db-reset: ⚠️  WIPE database and start fresh
db-reset:
	@echo "$(RED)⚠️  WARNING: This will DELETE all data!$(RESET)"
	@read -p "Are you sure? [y/N] " confirm && [ "$$confirm" = "y" ] || exit 1
	@cd $(BACKEND_DIR) && docker compose down -v
	@cd $(BACKEND_DIR) && docker compose up -d postgres pgadmin
	@echo "$(GREEN)✅  Database reset complete. Flyway will re-run migrations.$(RESET)"

## db-shell: Open psql shell inside the container
db-shell:
	@echo "$(CYAN)🐚  Opening psql shell...$(RESET)"
	@cd $(BACKEND_DIR) && \
		DB_USER=$$(grep POSTGRES_USER .env | cut -d '=' -f2) && \
		DB_NAME=$$(grep POSTGRES_DB   .env | cut -d '=' -f2) && \
		docker exec -it aboali_pos_db psql -U $$DB_USER -d $$DB_NAME

## db-backup: Backup database to ./backups/backup_<timestamp>.sql
db-backup:
	@mkdir -p backups
	@TIMESTAMP=$$(date +%Y%m%d_%H%M%S); \
	FILE="backups/backup_$$TIMESTAMP.sql"; \
	cd $(BACKEND_DIR) && \
	DB_USER=$$(grep POSTGRES_USER .env | cut -d '=' -f2) && \
	DB_NAME=$$(grep POSTGRES_DB   .env | cut -d '=' -f2) && \
	docker exec aboali_pos_db pg_dump -U $$DB_USER $$DB_NAME > "../$$FILE" && \
	echo "$(GREEN)✅  Backup saved → $$FILE$(RESET)"

## db-restore: Restore from file — usage: make db-restore FILE=backups/backup.sql
db-restore:
	@if [ -z "$(FILE)" ]; then echo "$(RED)❌  Usage: make db-restore FILE=backups/backup.sql$(RESET)"; exit 1; fi
	@echo "$(YELLOW)⚠️  Restoring from $(FILE)...$(RESET)"
	@cd $(BACKEND_DIR) && \
		DB_USER=$$(grep POSTGRES_USER .env | cut -d '=' -f2) && \
		DB_NAME=$$(grep POSTGRES_DB   .env | cut -d '=' -f2) && \
		cat ../$(FILE) | docker exec -i aboali_pos_db psql -U $$DB_USER -d $$DB_NAME
	@echo "$(GREEN)✅  Restore complete.$(RESET)"

## wait-db: Internal — wait until postgres healthcheck passes
wait-db:
	@until cd $(BACKEND_DIR) && \
		docker compose exec -T postgres \
		pg_isready -U $$(grep POSTGRES_USER .env | cut -d '=' -f2) \
		           -d $$(grep POSTGRES_DB   .env | cut -d '=' -f2) \
		> /dev/null 2>&1; do \
		echo "  ⏳ waiting..."; sleep 2; \
	done

# ──────────────────────────────────────────────────────────────────
# ☕  BACKEND
# ──────────────────────────────────────────────────────────────────

## backend: Start Spring Boot backend
backend:
	@echo "$(CYAN)☕  Starting Spring Boot backend...$(RESET)"
	@cd $(BACKEND_DIR) && ./mvnw spring-boot:run \
		--spring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

## build: Build the backend JAR (skip tests)
build:
	@echo "$(CYAN)🔨  Building backend JAR...$(RESET)"
	@cd $(BACKEND_DIR) && ./mvnw clean package -DskipTests
	@echo "$(GREEN)✅  JAR built → $(BACKEND_DIR)/target/*.jar$(RESET)"

## test: Run backend tests
test:
	@echo "$(CYAN)🧪  Running backend tests...$(RESET)"
	@cd $(BACKEND_DIR) && ./mvnw test

# ──────────────────────────────────────────────────────────────────
# 🌐  FRONTEND
# ──────────────────────────────────────────────────────────────────

## frontend: Start React frontend (Vite)
frontend:
	@echo "$(CYAN)🌐  Starting React frontend...$(RESET)"
	@cd $(FRONTEND_DIR) && npm run dev

## install: Install frontend npm dependencies
install:
	@echo "$(CYAN)📦  Installing frontend dependencies...$(RESET)"
	@cd $(FRONTEND_DIR) && npm install
	@echo "$(GREEN)✅  Dependencies installed.$(RESET)"

# ──────────────────────────────────────────────────────────────────
# 📊  STATUS & LOGS
# ──────────────────────────────────────────────────────────────────

## status: Show running containers + ports
status:
	@echo "$(CYAN)📊  Container Status:$(RESET)"
	@cd $(BACKEND_DIR) && docker compose ps
	@echo ""
	@echo "$(CYAN)🌐  Service URLs:$(RESET)"
	@echo "  🗄️  PostgreSQL  → localhost:5432"
	@echo "  🖥️  pgAdmin     → http://localhost:5050"
	@echo "  ☕  Backend     → http://localhost:8080"
	@echo "  📖  Swagger UI  → http://localhost:8080/swagger-ui.html"
	@echo "  🌐  Frontend    → http://localhost:5173"

## logs: Tail all docker logs live
logs:
	@cd $(BACKEND_DIR) && docker compose logs -f

## logs-db: Tail postgres logs only
logs-db:
	@cd $(BACKEND_DIR) && docker compose logs -f postgres

# ──────────────────────────────────────────────────────────────────
# 🧹  CLEANUP
# ──────────────────────────────────────────────────────────────────

## clean: Remove build artifacts
clean:
	@echo "$(YELLOW)🧹  Cleaning build artifacts...$(RESET)"
	@cd $(BACKEND_DIR) && ./mvnw clean
	@cd $(FRONTEND_DIR) && rm -rf dist
	@echo "$(GREEN)✅  Clean complete.$(RESET)"

# ──────────────────────────────────────────────────────────────────
# ❓  HELP
# ──────────────────────────────────────────────────────────────────

## help: Show all available commands
help:
	@echo "$(CYAN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@echo "$(CYAN)  AboAli POS — Available Commands         $(RESET)"
	@echo "$(CYAN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
	@grep -E '^## ' $(MAKEFILE_LIST) | \
		sed 's/## //' | \
		awk -F: '{printf "  $(GREEN)make %-15s$(RESET) %s\n", $$1, $$2}'
	@echo "$(CYAN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(RESET)"
