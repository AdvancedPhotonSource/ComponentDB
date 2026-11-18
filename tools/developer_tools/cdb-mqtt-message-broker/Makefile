.PHONY: help install install-base install-dev test test-cov lint format fix type-check clean quality quality-fix run-example list-handlers

BASE_DIR := ../cdb-bely-message-broker-base

help:
	@echo "CDB MQTT Message Broker - Development Commands"
	@echo ""
	@echo "Installation:"
	@echo "  make install          Install the package (and the base from this checkout)"
	@echo "  make install-dev      Install with development dependencies"
	@echo ""
	@echo "Testing:"
	@echo "  make test             Run tests"
	@echo "  make test-cov         Run tests with coverage"
	@echo ""
	@echo "Code Quality:"
	@echo "  make lint             Run linting checks"
	@echo "  make format           Format code with black"
	@echo "  make fix              Auto-fix formatting and linting issues"
	@echo "  make type-check       Run type checking with mypy"
	@echo "  make quality          Run all quality checks"
	@echo "  make quality-fix      Fix issues then run quality checks"
	@echo ""
	@echo "Utilities:"
	@echo "  make clean            Clean build artifacts"
	@echo "  make run-example      Run with the example handlers against localhost"
	@echo "  make list-handlers    List the example handlers"

# pip ignores [tool.uv.sources]; install the base from this checkout first.
install-base:
	pip install -e $(BASE_DIR)

install: install-base
	pip install -e .

install-dev: install-base
	pip install -e ".[dev]"

test:
	pytest tests/

test-cov:
	pytest tests/ \
		--cov=src/cdb_mqtt \
		--cov-report=html \
		--cov-report=term-missing

lint:
	ruff check src/ tests/ examples/
	black --check src/ tests/ examples/

format:
	black src/ tests/ examples/
	ruff check --fix src/ tests/ examples/

type-check:
	mypy src/cdb_mqtt
	mypy examples/handlers --ignore-missing-imports

quality: lint type-check test

fix:
	@echo "Auto-fixing code formatting and linting issues..."
	@black src/ tests/ examples/
	@-ruff check --fix src/ tests/ examples/
	@echo "Auto-fix complete. Run 'make lint' to see remaining issues."

quality-fix: fix
	@$(MAKE) quality || (echo ""; echo "Some issues could not be automatically fixed. Please review the errors above."; exit 1)

clean:
	find . -type d -name __pycache__ -exec rm -rf {} + 2>/dev/null || true
	find . -type f -name "*.pyc" -delete
	find . -type d -name "*.egg-info" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name ".pytest_cache" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name ".mypy_cache" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name ".ruff_cache" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name "htmlcov" -exec rm -rf {} + 2>/dev/null || true
	find . -type f -name ".coverage" -delete
	rm -rf build/ dist/

run-example:
	cdb-mqtt start \
		--broker-host localhost \
		--broker-port 1883 \
		--handlers-dir ./examples/handlers \
		--topic "cdb/#" \
		--log-level INFO \
		--lock-file /tmp/cdb-mqtt-example.lock

list-handlers:
	cdb-mqtt list-handlers --handlers-dir ./examples/handlers
