.PHONY: help install install-dev test test-handlers test-apprise test-apprise-integration test-all lint format fix type-check clean docs quality quality-fix

help:
	@echo "BELY MQTT Framework - Development Commands"
	@echo ""
	@echo "Installation:"
	@echo "  make install          Install the package"
	@echo "  make install-dev      Install with development dependencies"
	@echo ""
	@echo "Testing:"
	@echo "  make test             Run core framework tests"
	@echo "  make test-handlers    Run all handler tests"
	@echo "  make test-apprise     Run Apprise handler unit tests"
	@echo "  make test-apprise-integration  Run Apprise integration tests"
	@echo "  make test-all         Run all tests (framework + handlers)"
	@echo "  make test-cov         Run all tests with coverage"
	@echo ""
	@echo "Code Quality:"
	@echo "  make lint             Run linting checks"
	@echo "  make format           Format code with black"
	@echo "  make fix              Auto-fix formatting and linting issues"
	@echo "  make type-check       Run type checking with mypy"
	@echo "  make type-check-handlers  Type check example handlers"
	@echo "  make quality          Run all quality checks"
	@echo "  make quality-fix      Fix issues then run quality checks"
	@echo ""
	@echo "Utilities:"
	@echo "  make clean            Clean build artifacts"
	@echo "  make docs             Build documentation"
	@echo "  make run-example      Run example handler"

install:
	pip install -e .

install-dev:
	pip install -e ".[dev]"

test:
	pytest tests/

test-handlers:
	@echo "Running Apprise Smart Notification Handler tests..."
	pytest examples/handlers/apprise_smart_notification/test/ \
		--asyncio-mode=auto -v

test-apprise:
	@echo "Running Apprise handler unit tests..."
	pytest examples/handlers/apprise_smart_notification/test/test_handler.py --asyncio-mode=auto -v

test-apprise-integration:
	@echo "Running Apprise handler integration tests..."
	pytest examples/handlers/apprise_smart_notification/test/test_integration.py --asyncio-mode=auto -v

test-all: test test-handlers

test-cov:
	pytest tests/ \
		examples/handlers/apprise_smart_notification/test/ \
		--cov=src/bely_mqtt \
		--cov=examples/handlers/apprise_smart_notification \
		--cov-report=html \
		--cov-report=term-missing \
		--asyncio-mode=auto

lint:
	ruff check src/ tests/ examples/handlers/apprise_smart_notification/
	black --check src/ tests/ examples/handlers/apprise_smart_notification/

format:
	black src/ tests/ examples/handlers/apprise_smart_notification/
	ruff check --fix src/ tests/ examples/handlers/apprise_smart_notification/

type-check:
	mypy src/bely_mqtt
	@echo "Type checking apprise_smart_notification handler..."
	mypy examples/handlers/apprise_smart_notification --ignore-missing-imports

type-check-handlers:
	@echo "Type checking all example handlers..."
	mypy examples/handlers/apprise_smart_notification --ignore-missing-imports
	# Add other handlers here as they are converted to packages

quality: lint type-check test-all

fix:
	@echo "Auto-fixing code formatting and linting issues..."
	@echo "Running black formatter..."
	@black src/ tests/ examples/handlers/apprise_smart_notification/
	@echo "Running ruff auto-fixes..."
	@-ruff check --fix src/ tests/ examples/handlers/apprise_smart_notification/
	@echo ""
	@echo "Auto-fix complete. Run 'make lint' to see remaining issues."

quality-fix: fix
	@echo "Running quality checks after fixes..."
	@echo "======================================="
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

docs:
	@echo "Documentation is in docs/ directory"
	@echo "Main files:"
	@echo "  - README.md: Overview and API reference"
	@echo "  - docs/QUICKSTART.md: Quick start guide"
	@echo "  - docs/HANDLER_DEVELOPMENT.md: Handler development guide"
	@echo "  - docs/ARCHITECTURE.md: Architecture documentation"

run-example:
	@echo "Starting BELY MQTT Framework with example handlers..."
	bely-mqtt start \
		--broker-host localhost \
		--broker-port 1883 \
		--handlers-dir ./examples/handlers \
		--topic "bely/#" \
		--log-level INFO

list-handlers:
	bely-mqtt list-handlers --handlers-dir ./examples/handlers
