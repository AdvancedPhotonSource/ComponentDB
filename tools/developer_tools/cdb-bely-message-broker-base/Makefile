.PHONY: help install install-dev test test-cov lint format fix type-check clean quality quality-fix

help:
	@echo "CDB/BELY MQTT Message Broker Base - Development Commands"
	@echo ""
	@echo "Installation:"
	@echo "  make install          Install the package"
	@echo "  make install-dev      Install with development dependencies"
	@echo ""
	@echo "Testing:"
	@echo "  make test             Run framework tests"
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

install:
	pip install -e .

install-dev:
	pip install -e ".[dev]"

test:
	pytest tests/

test-cov:
	pytest tests/ \
		--cov=src/cdb_bely_message_broker_base \
		--cov-report=html \
		--cov-report=term-missing

lint:
	ruff check src/ tests/
	black --check src/ tests/

format:
	black src/ tests/
	ruff check --fix src/ tests/

type-check:
	mypy src/cdb_bely_message_broker_base

quality: lint type-check test

fix:
	@echo "Auto-fixing code formatting and linting issues..."
	@black src/ tests/
	@-ruff check --fix src/ tests/
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
