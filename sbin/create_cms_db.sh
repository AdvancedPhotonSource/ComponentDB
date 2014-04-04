#!/bin/sh

DB_NAME=cms
DB_OWNER=cms
DB_OWNER_PASSWORD=cms
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ADMIN_USER=root
DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
DB_ADMIN_PASSWORD=
DB_CHARACTER_SET=utf8


MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CMS_ROOT_DIR}" ]; then
    CMS_ROOT_DIR=$MY_DIR/..
fi
CMS_SQL_DIR=$CMS_ROOT_DIR/db/sql/cms
CMS_ENV_FILE=${CMS_ROOT_DIR}/setup.sh
if [ ! -f ${CMS_ENV_FILE} ]; then
    echo "Environment file ${CMS_ENV_FILE} does not exist." 
    exit 2
fi
. ${CMS_ENV_FILE} > /dev/null 

# Read password
sttyOrig=`stty -g`
stty -echo
read -p "Enter MySQL root password: " DB_ADMIN_PASSWORD
stty $sttyOrig

mysqlCmd="mysql --port=$DB_PORT --host=$DB_HOST -u $DB_ADMIN_USER"
if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}



# recreate db
cd $CMS_SQL_DIR
sqlFile=/tmp/create_cms_db.`id -u`.sql
rm -f $sqlFile
echo "DROP DATABASE IF EXISTS $DB_NAME;" >> $sqlFile
echo "CREATE DATABASE $DB_NAME CHARACTER SET $DB_CHARACTER_SET;" >> $sqlFile
for host in $DB_ADMIN_HOSTS; do
    echo "GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_OWNER'@'$host'
    IDENTIFIED BY '$DB_OWNER_PASSWORD';" >> $sqlFile
done
execute "$mysqlCmd < $sqlFile"

# create db tables
mysqlCmd="$mysqlCmd -D $DB_NAME <"
execute $mysqlCmd create_cms_tables.sql

# populate db
execute $mysqlCmd populate_setting_type.sql
execute $mysqlCmd populate_user.sql
execute $mysqlCmd populate_user_group.sql
execute $mysqlCmd populate_user_user_group.sql
execute $mysqlCmd populate_entity_info.sql
execute $mysqlCmd populate_component_state.sql
execute $mysqlCmd populate_source.sql
execute $mysqlCmd populate_property_type.sql
execute $mysqlCmd populate_resource_category.sql
execute $mysqlCmd populate_connector_category.sql
execute $mysqlCmd populate_connector_type.sql

execute $mysqlCmd populate_component_category.sql
execute $mysqlCmd populate_component_type.sql
execute $mysqlCmd populate_resource_type.sql

execute $mysqlCmd populate_component.sql
execute $mysqlCmd populate_component_source.sql
execute $mysqlCmd populate_component_property.sql
execute $mysqlCmd populate_component_component_type.sql
execute $mysqlCmd populate_component_connector.sql
execute $mysqlCmd populate_component_connector_resource.sql

execute $mysqlCmd populate_collection.sql
execute $mysqlCmd populate_collection_component.sql

# Add development rows
#execute $mysqlCmd add_cms_development_entries.sql

# cleanup
execute rm -f $sqlFile
