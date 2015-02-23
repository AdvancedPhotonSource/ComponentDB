#!/bin/sh

SVN_ROOT=https://svn.aps.anl.gov/cdb
release=$1
tag=$2
if [ -z $release ]; then
    echo "Usage: $0 <release> [$tag]"
    exit 1
fi
if [ -z $tag ]; then
    svn copy $SVN_ROOT/trunk $SVN_ROOT/releases/$release -m "Creating
    release $release from current trunk"
else
    svn copy $SVN_ROOT/tags/$tag $SVN_ROOT/releases/$release -m "Creating
    release $release from tag $tag"
fi
    
      
