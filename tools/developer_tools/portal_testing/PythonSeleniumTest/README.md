# CDB Portal Selenium Tests

## Requirements

- Python 3.10+
- Chrome or Chromium
- `selenium`
- `pytest`
- `pytest-xdist`
- `pytest-rerunfailures`
- A running portal using the test database

Install the Python dependencies in the Selenium Conda environment:

```sh
python -m pip install selenium pytest pytest-xdist pytest-rerunfailures
```

## Run the full test suite

From the repository root:

```sh
source setup.sh
make test
```
