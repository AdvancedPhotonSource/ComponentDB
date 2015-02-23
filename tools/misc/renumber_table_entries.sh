#!/bin/sh

# Example usage: ./tools/misc/renumber_table_entries.sh db/sql/cdb/populate_setting_type.sql

inputFile=$1
cat $inputFile | awk 'BEGIN{i=0}{i+=1;print gensub(/([0-9]+),/, i",", "g", $0)}' > $inputFile.2 && mv $inputFile.2 $inputFile
