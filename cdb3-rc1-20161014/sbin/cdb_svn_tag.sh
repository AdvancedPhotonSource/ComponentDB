#!/bin/sh

SVN_ROOT=https://svn.aps.anl.gov/cdb
version=$1
if [ -z $version ]; then
    echo "Usage: $0 <version>"
    exit 1
fi
svn copy $SVN_ROOT/trunk $SVN_ROOT/tags/$version -m "Creating tag $version"
      
