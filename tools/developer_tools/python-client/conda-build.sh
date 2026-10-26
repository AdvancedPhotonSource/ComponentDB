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

./generatePyClient.sh $URL

if [ $? -ne 0 ]; then
  echo "Generating API failed. Exiting."
  exit 1
fi

# Clean and Build
rm -rf ./build

# Build API
conda build conda-recipe/API --output-folder ./build || exit 1

# Build CLI (against the API package just built, via the local channel)
conda build conda-recipe/CLI -c ./build --output-folder ./build || exit 1

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