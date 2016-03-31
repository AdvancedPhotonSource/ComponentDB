#!/bin/sh

#
# Script used for creating CDB database
# Deployment configuration can be set in etc/$CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME [CDB_DB_SCRIPTS_DIR]]
#

CDB_DB_NAME=cdb
CDB_DB_USER=cdb
CDB_DB_PASSWORD=cdb
CDB_DB_HOST=127.0.0.1
CDB_DB_PORT=3306
CDB_DB_ADMIN_USER=root
CDB_DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov visa%.aps.anl.gov"
CDB_DB_ADMIN_PASSWORD=
CDB_DB_CHARACTER_SET=utf8

CURRENT_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
cd $CURRENT_DIR

if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist." 
    exit 1
fi
. ${CDB_ENV_FILE} > /dev/null 
CDB_ETC_DIR=$CDB_INSTALL_DIR/etc

# Use first argument as db name, if provided
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi
echo "Using DB name: $CDB_DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CDB_ROOT_DIR/etc/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

# Second argument overrides directory with db population scripts
CDB_SQL_DIR=$CDB_ROOT_DIR/db/sql/$CDB_DB_NAME
CDB_DB_SCRIPTS_DIR=${CDB_DB_SCRIPTS_DIR:=$CDB_SQL_DIR}
if [ ! -z "$2" ]; then
    CDB_DB_SCRIPTS_DIR=$2
fi
if [ ! -d $CDB_DB_SCRIPTS_DIR ]; then
    echo "DB Scripts directory $CDB_DB_SCRIPTS_DIR does not exist."
    exit 1
fi

echo "Using DB scripts directory: $CDB_DB_SCRIPTS_DIR"

# Read password if needed
if [ -z "$CDB_DB_ADMIN_PASSWORD" ]; then
    sttyOrig=`stty -g`
    stty -echo
    read -p "Enter DB root password: " CDB_DB_ADMIN_PASSWORD
    stty $sttyOrig
    echo
fi


# Read user db password if needed 
CDB_DB_PASSWORD=$CDB_DB_USER_PASSWORD
if [ -z "$CDB_DB_USER_PASSWORD" ]; then
    CDB_DB_PASSWD_FILE="$CDB_INSTALL_DIR/etc/$CDB_DB_NAME.db.passwd"
    if [ -f $CDB_DB_PASSWD_FILE ]; then
	CDB_DB_PASSWORD=`cat $CDB_DB_PASSWD_FILE`
    else
	sttyOrig=`stty -g`
	stty -echo
	read -p "Enter DB password for the $CDB_DB_USER user: " CDB_DB_PASSWORD
	stty $sttyOrig
	echo
    fi
fi
if [ -z "$CDB_DB_PASSWORD" ]; then
    echo "$CDB_DB_USER user password cannot be empty."
    exit 1
fi

# Prepare mysql config if needed
mysqlConfig=mysql.conf
if [ -d $CDB_SUPPORT_DIR/mysql ]; then
    if [ ! -f $CDB_ETC_DIR/$mysqlConfig ]; then
        echo "Local MySQL installation exists, preparing server configuration file"
        cmd="cat $CDB_ROOT_DIR/etc/$mysqlConfig.template \
            | sed 's?CDB_DB_HOST?$CDB_DB_HOST?g' \
            | sed 's?CDB_DB_PORT?$CDB_DB_PORT?g' \
            | sed 's?CDB_INSTALL_DIR?$CDB_INSTALL_DIR?g' \
            > $CDB_ETC_DIR/$mysqlConfig
        "
        eval $cmd || exit 1
        $CDB_ROOT_DIR/etc/init.d/cdb-mysqld restart
        echo "Setting DB root password"
        cmd="echo \"SET PASSWORD FOR 'root'@'localhost' = PASSWORD('$CDB_DB_ADMIN_PASSWORD');\" | $CDB_SUPPORT_DIR/mysql/$CDB_HOST_ARCH/bin/mysql -u root -h $CDB_DB_HOST"
        eval $cmd || exit 1
    fi
fi

mysqlCmd="mysql --port=$CDB_DB_PORT --host=$CDB_DB_HOST -u $CDB_DB_ADMIN_USER"
if [ ! -z "$CDB_DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$CDB_DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$CDB_DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$CDB_DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}

# recreate db
cd $CDB_SQL_DIR
sqlFile=/tmp/create_cdb_db.`id -u`.sql
rm -f $sqlFile
echo "DROP DATABASE IF EXISTS $CDB_DB_NAME;" >> $sqlFile
echo "CREATE DATABASE $CDB_DB_NAME CHARACTER SET $CDB_DB_CHARACTER_SET;" >> $sqlFile
for host in $CDB_DB_ADMIN_HOSTS; do
    echo "GRANT ALL PRIVILEGES ON $CDB_DB_NAME.* TO '$CDB_DB_USER'@'$host'
    IDENTIFIED BY '$CDB_DB_PASSWORD';" >> $sqlFile
done
execute "$mysqlCmd < $sqlFile"

# create db tables
mysqlCmd="$mysqlCmd -D $CDB_DB_NAME <"
execute $mysqlCmd create_cdb_tables.sql

# create db password file
if [ ! -d $CDB_ETC_DIR ]; then 
    mkdir -p $CDB_ETC_DIR
fi
passwordFile=$CDB_ETC_DIR/$CDB_DB_NAME.db.passwd
echo $CDB_DB_PASSWORD > $passwordFile
chmod 600 $passwordFile

# populate db
cd $CURRENT_DIR && cd $CDB_DB_SCRIPTS_DIR
CDB_DB_TABLES="\
    setting_type \
    user_info \
    user_group \
    user_user_group \
    user_setting \
    entity_info \
    source \
    property_type_category \
    property_type_handler \
    property_type \
    allowed_property_value \
    property_value \
    property_value_history \
    location_type \
    location \
    location_link \
    resource_type_category \
    resource_type \
    connector_type_category \
    connector_type \
    component_type_category \
    component_type \
    resource_type \
    log_topic \
    log \
    attachment \
    log_attachment \
    component \
    component_source \
    component_property \
    component_connector \
    component_resource \
    component_log \
    component_instance \
    component_instance_property \
    component_instance_log \
    component_instance_location_history \
    component_connector \
    assembly_component \
    assembly_component_connection \
    design \
    design_property \
    design_link \
    design_log \
    design_element \
    design_element_property \
    design_element_link \
    design_element_log \
    design_element_connection \
"
for dbTable in $CDB_DB_TABLES; do
    dbFile=populate_$dbTable.sql
    if [ -f $dbFile ]; then
        echo "Populating $dbTable using $dbFile script"
        execute $mysqlCmd $dbFile || exit 1
    else
        echo "$dbFile not found, skipping $dbTable update"
    fi
done

echo "select username from user_info inner join user_user_group on user_info.id = user_user_group.user_id inner join user_group on user_group.id = user_user_group.user_group_id where user_group.name = 'CDB_ADMIN' and user_info.password is not null;" > temporaryAdminCommand.sql

adminWithLocalPassword=`eval $mysqlCmd temporaryAdminCommand.sql`

if [ -z "$adminWithLocalPassword" ]; then
    echo "No portal admin user with a local password exists"
    read -sp "Enter password for local portal admin (username: cdb): [leave blank for no local password] " CDB_LOCAL_SYSTEM_ADMIN_PASSWORD
    echo ""
    if [ ! -z "$CDB_LOCAL_SYSTEM_ADMIN_PASSWORD" ]; then
	adminCryptPassword=`python -c "from cdb.common.utility.cryptUtility import CryptUtility; print CryptUtility.cryptPasswordWithPbkdf2('$CDB_LOCAL_SYSTEM_ADMIN_PASSWORD')"`
	echo "update user_info set password = '$adminCryptPassword' where username='cdb'" > temporaryAdminCommand.sql
        execute $mysqlCmd temporaryAdminCommand.sql
    fi
fi

execute rm temporaryAdminCommand.sql

# cleanup
execute rm -f $sqlFile
