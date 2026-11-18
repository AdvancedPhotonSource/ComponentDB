#!/bin/bash

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
ROOT_DIR=$MY_DIR

ENV_NAME=bely-mqtt-env
CONDA_DIR=$CONDA_PREFIX_1
echo $CONDA_DIR

if [ -z $CONDA_DIR ]
then
    CONDA_DIR=$CONDA_PREFIX
fi

if [ -z $CONDA_DIR ]
then
    echo '$CONDA_PREFIX must be defined.'
    exit 1
fi

source $CONDA_DIR/etc/profile.d/conda.sh || exit 1

trap 'rm -rf src' EXIT

# Clean
rm -rvf ./build
rm -rvf src

# Prepare build source
mkdir -p src
cp -Rv ../src src/
cp -Rv ../tests src/
cp -v ../pyproject.toml ../setup.py ../README.md ../LICENSE ../CHANGELOG.md ../pytest.ini src/

# Build
conda build . --output-folder ./build || exit 1

# Install build into a new env
conda create -n $ENV_NAME -y || exit 1
conda activate $ENV_NAME || exit 1
conda install bely-mqtt-message-broker -c ./build -y || exit 1


# Export
conda list -n $ENV_NAME --explicit > $ENV_NAME.txt

echo "Please use the c2 tool to upload the $ENV_NAME.txt"

# Clean up
conda activate
conda env remove -n $ENV_NAME
