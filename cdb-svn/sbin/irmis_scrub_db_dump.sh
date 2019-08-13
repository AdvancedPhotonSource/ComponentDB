#dumpFile=populate_irmis.sql
dumpFile=insert_irmis6.sql
tables=`grep INTO $dumpFile | awk '{print $3}' | sort -u`

for t in $tables; do
    tName=`echo $t | sed 's?\`??g'`
    echo Working on table: $tName
    populateFile=populate_$tName.sql
    rm -f $populateFile
    cat $dumpFile | grep INSERT | grep \`$tName\`  >> $populateFile
done
