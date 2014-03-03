#!/bin/sh

CRAWLER=/local/home/IRMIS/pvcrawler
PATH=/bin:/usr/bin:/usr/local/epics/extensions/bin/Linux:$CRAWLER
export PATH


(cd $CRAWLER ; pv_crawler.pl --go --boot-scan=APSBootScan  >> logs/crawler.log 2>&1 )

