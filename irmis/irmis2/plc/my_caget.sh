#!/bin/sh
CURPATH=`pwd`
EPICS_CA_ADDR_LIST="164.54.188.65"
EPICS_CA_AUTO_ADDR_LIST=NO
export EPICS_CA_ADDR_LIST EPICS_CA_AUTO_ADDR_LIST
EPICS_HOST_ARCH=`/usr/local/epics/startup/EpicsHostArch`
export EPICS_HOST_ARCH 

if [ -z "${EPICS_EXTENSIONS}" ]
then
        EPICS_EXTENSIONS=/usr/local/epics/extensions ; export EPICS_EXTENSIONS
fi

${EPICS_EXTENSIONS}/bin/${EPICS_HOST_ARCH}/caget.00009 -t $1

cd $CURPATH
