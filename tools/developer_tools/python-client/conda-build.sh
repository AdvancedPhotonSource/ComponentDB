#!/bin/bash

ENV_NAME=cdb-api-cli-env
CONDA_DIR=$CONDA_PREFIX_1
echo $CONDA_DIR

if [ -z $CONDA_DIR ]
then
    CONDA_DIR=$CONDA_PREFIX
fi

if [ -z $CONDA_DIR ]
then
    echo '$CONDA_PRIFX must be defined.'
    exit 1
fi

source $CONDA_DIR/etc/profile.d/conda.sh || exit 1

# Default URL for generating updated API 
DEFAULT_URL="http://localhost:8080/cdb"

# Check if the first argument is provided, otherwise use the default URL
URL=${1:-$DEFAULT_URL}

# Output the URL being used
echo "Generating updated APIs using URL: $URL"

sh ./generatePyClient.sh $URL 

if [ $? -ne 0 ]; then
  echo "Generating API failed. Exiting."
  exit 1
fi

# Clean and Build
rm -rf ./build

# Prepare API directory 
rm -rf api-build
mkdir -p api-build/conda-recipe
cp conda-recipe/API/meta.yaml api-build/conda-recipe
cp CdbApiFactory.py api-build/
cp -R cdbApi api-build
cp setup-api.py api-build/setup.py

# Prepare CLI direactory
rm -rf cli-build
mkdir -p cli-build/conda-recipe
cp conda-recipe/CLI/meta.yaml cli-build/conda-recipe
cp -R cdbCli cli-build
cp setup-cli.py cli-build/setup.py

# Build API
cd api-build
conda build conda-recipe --output-folder ../build || exit 1
cd ..

# Build CLI
cd cli-build
conda build conda-recipe --output-folder ../build || exit 1
cd ..

# Clean up
rm -rf api-build
rm -rf cli-build

# Install build into a new env 
conda create -n $ENV_NAME -y || exit 1
conda activate $ENV_NAME || exit 1
conda install componentdb-api -c ./build -y || exit 1
conda install componentdb-cli -c ./build -y || exit 1

#Export
conda list -n $ENV_NAME --explicit > $ENV_NAME.txt

echo "Please use the c2 tool to upload the $ENV_NAME.txt"

conda activate
conda env remove -n $ENV_NAME