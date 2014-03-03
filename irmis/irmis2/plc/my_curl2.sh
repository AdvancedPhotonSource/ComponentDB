#!/bin/sh
CURPATH=`pwd`
rm -f /tmp/icms_results.txt
/usr/bin/curl https://icmsdocs.aps.anl.gov/new_docs/groups/anl/documents/other/aps_1191886.hcst > /tmp/icms_results.txt

#https://icmsdocs.aps.anl.gov/new_docs/groups/public/documents/other/aps_1191886.hcst
