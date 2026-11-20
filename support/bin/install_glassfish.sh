#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# Usage install_glassfish.sh [Install_DIR] [CONFIGURE 0/1]

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
CDB_HOSTNAME=`hostname -f`
PAYARA_DN_NAME="CN=${CDB_HOSTNAME}"

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
. $topDir/bin/versions.sh

PAYARA_ZIP_FILE=payara-$PAYARA_VERSION.zip
PAYARA_DOWNLOAD_URL=https://repo1.maven.org/maven2/fish/payara/distributions/payara/$PAYARA_VERSION/$PAYARA_ZIP_FILE

srcDir=$topDir/src
buildDir=$topDir/build
binDir=$topDir/bin
glassfishAutoLoginExpectScript=$binDir/glassfish_auto_login.expect
javaInstallDir=$topDir/java/$CDB_HOST_ARCH
if [ ! -z $1 ]; then
    payaraInstallDir=$1
else 
    payaraInstallDir=$topDir/payara/$CDB_HOST_ARCH
fi

if [ ! -z $2 ]; then
    skipConfig=$2
else
    skipConfig=0
fi

export AS_JAVA=$javaInstallDir
ASADMIN_CMD=$payaraInstallDir/bin/asadmin
KEYTOOL_CMD=$AS_JAVA/bin/keytool

# get glassfish
mkdir -p $srcDir
cd $srcDir
if [ ! -f $PAYARA_ZIP_FILE ]; then
    echo "Retrieving $PAYARA_DOWNLOAD_URL"
    curl -f -L -O $PAYARA_DOWNLOAD_URL || { rm -f $PAYARA_ZIP_FILE; exit 1; }
fi

if [ ! -f $PAYARA_ZIP_FILE ]; then
    echo "File $srcDir/$PAYARA_ZIP_FILE not found."
    exit 1
fi

mkdir -p $buildDir
cd $buildDir

echo "Unpacking $PAYARA_ZIP_FILE"
mkdir -p `dirname $payaraInstallDir`
cd `dirname $payaraInstallDir`
rm -rf `basename $payaraInstallDir`
unzip -q $srcDir/$PAYARA_ZIP_FILE
mv payara* `basename $payaraInstallDir`

if [ $skipConfig -ne 0 ]; then
    exit 0
fi

# configure directory permissions for app deployment
# owner/group can execute/read/modify bin files
# remove privileges from others
chmod -R ug+rwx $payaraInstallDir/bin
chmod -R ug+rwx $payaraInstallDir/glassfish/bin

chmod -R o-rwx $payaraInstallDir/bin
chmod -R o-rwx $payaraInstallDir/glassfish/bin

export PAYARA_DOMAIN_NAME=production
KEYSTORE_FILE=keystore.p12
CACERTS_FILE=cacerts.p12

# Read passwords up front
echo
sttyOrig=`stty -g`
stty -echo
read -p "Enter payara master password: " MASTER_PASSWORD
echo
read -p "Enter glassfish admin password: " ADMIN_PASSWORD
stty $sttyOrig
echo

# Payara 6+ ships only domain1, so the production domain is created here.
# Passwords are passed via a private password file instead of interactive prompts.
passwordFile=`mktemp /tmp/$(basename $0).XXXXXX`
chmod 600 $passwordFile
trap "rm -f $passwordFile" EXIT

echo "Creating domain $PAYARA_DOMAIN_NAME"
printf "AS_ADMIN_PASSWORD=%s\n" "$ADMIN_PASSWORD" > $passwordFile
# Hazelcast's default start port 5900 collides with VNC/macOS Screen Sharing ("Unknown protocol: RFB")
# and hangs startup, so it must be set before the first start.
$ASADMIN_CMD --user admin --passwordfile $passwordFile create-domain --checkports=false \
    --domainproperties hazelcast.start.port=$PAYARA_HAZELCAST_START_PORT \
    --keytooloptions "$PAYARA_DN_NAME" $PAYARA_DOMAIN_NAME || exit 1

# Keystores are created with the default 'changeit' master password; replace it.
echo "Changing payara master password"
printf "AS_ADMIN_MASTERPASSWORD=changeit\nAS_ADMIN_NEWMASTERPASSWORD=%s\n" "$MASTER_PASSWORD" > $passwordFile
$ASADMIN_CMD --passwordfile $passwordFile change-master-password --savemasterpassword=true $PAYARA_DOMAIN_NAME || exit 1
rm -f $passwordFile

# Your chosen master password should unlock both keystores, and reveal
# two certificates in the keystore, and many additional ones in cacerts.
echo
echo "Checking master password: list keystore certificates"
$KEYTOOL_CMD -list -v -keystore $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/$KEYSTORE_FILE --storepass $MASTER_PASSWORD > /dev/null || exit 1
echo "Checking master password: list ca certificates"
$KEYTOOL_CMD -list -v -keystore $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/$CACERTS_FILE --storepass $MASTER_PASSWORD > /dev/null || exit 1

# start glassfish
echo
echo "Starting glassfish"
$ASADMIN_CMD start-domain $PAYARA_DOMAIN_NAME
tmpFile=/tmp/`basename $0`.`id -u`.tmp

# Store admin password, to enable automatic login to localhost:4848
#$ASADMIN_CMD --host localhost --port 4848 login
# Enter admin user name [Enter to accept default]> [Just press Enter]
# Enter admin password> [e.g. 'myAdminPwd']
# Login information relevant to admin user name [admin] for host [localhost] and admin port [4848] stored at [~/.gfclient/pass] successfully.
# Make sure that this file remains protected. Information stored in this file will be used by administration commands to manage associated domain.
# Command login executed successfully.

# enable auto login
echo
echo "Enabling auto login"
eval "cat $glassfishAutoLoginExpectScript | sed 's?ADMIN_PASSWORD?$ADMIN_PASSWORD?' > $tmpFile"
chmod a+x $tmpFile
cd `dirname $ASADMIN_CMD`
$tmpFile

# stop glassfish
$ASADMIN_CMD stop-domain $PAYARA_DOMAIN_NAME
rm -f $tmpFile

# inspect keystore
echo
echo "Inspecting keystore"
cd $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/
$KEYTOOL_CMD -list -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD

# update keystore
echo
echo "Updating keystore"
$KEYTOOL_CMD -delete -alias s1as -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -delete -alias glassfish-instance -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD

$KEYTOOL_CMD -genkeypair -alias s1as -dname "$PAYARA_DN_NAME" -keyalg RSA -keysize 2048 -validity 3650 -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD
$KEYTOOL_CMD -genkeypair -alias glassfish-instance -dname "$PAYARA_DN_NAME" -keyalg RSA -keysize 2048 -validity 3650 -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD

# check keystore
echo
echo "Checking keystore"
$KEYTOOL_CMD -list -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD

# export certificates from keystore
echo
echo "Exporting certificates"
$KEYTOOL_CMD -exportcert -alias s1as -file s1as.cert -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -exportcert -alias glassfish-instance -file glassfish-instance.cert -keystore $KEYSTORE_FILE -storepass $MASTER_PASSWORD

# update cacerts
echo
echo "Updating $CACERTS_FILE, using master password as key password"
$KEYTOOL_CMD -delete -alias s1as -keystore $CACERTS_FILE -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -delete -alias glassfish-instance -keystore $CACERTS_FILE -storepass $MASTER_PASSWORD

$KEYTOOL_CMD -importcert -noprompt -alias s1as -file s1as.cert -keystore $CACERTS_FILE -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD
$KEYTOOL_CMD -importcert -noprompt -alias glassfish-instance -file glassfish-instance.cert -keystore $CACERTS_FILE -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD

# check cacerts and tidy up
echo
echo "Checking $CACERTS_FILE"
$KEYTOOL_CMD -list -keystore $CACERTS_FILE -storepass $MASTER_PASSWORD > /dev/null || exit 1
rm -f s1as.cert glassfish-instance.cert

# The commands here change the file at
# glassfish/domains/$PAYARA_DOMAIN_NAME/config/domain.xml

# start glassfish
echo
echo "Starting glassfish"
$ASADMIN_CMD start-domain $PAYARA_DOMAIN_NAME

# enable https for remote access to admin console
# requests to http://xxx:4848 are redirected to https://xxx:4848
echo
echo "Enabling https"
$ASADMIN_CMD set server-config.network-config.protocols.protocol.admin-listener.security-enabled=true
$ASADMIN_CMD enable-secure-admin

# list current JVM options
echo
echo "Current jvm options: "
$ASADMIN_CMD list-jvm-options

# change JVM Options
echo
echo "Updating jvm options"
$ASADMIN_CMD delete-jvm-options -Xmx512m
$ASADMIN_CMD create-jvm-options -Xmx2048m
$ASADMIN_CMD create-jvm-options -Xms1024m

# restart to take effect
echo
echo "Restarting glassfish"
$ASADMIN_CMD stop-domain $PAYARA_DOMAIN_NAME
$ASADMIN_CMD start-domain $PAYARA_DOMAIN_NAME

# list current JVM options
echo
echo "New jvm options: "
$ASADMIN_CMD --interactive=false list-jvm-options

# check HTTP response headers (as shown below) to make sure that there are
# clues as to the server or its version. Look for any mention of
# 'GlassFish', 'X-Powered-By', etc. If necessary:

# you can retrieve properties using:
# $ASADMIN_CMD get 'configs.config.server-config.network-config.protocols.protocol.http-listener-1.*'

# you can create new listener using:
# $ASADMIN_CMD create-network-listener --listenerport 8182 --protocol http-listener-2 --address 0.0.0.0 --enabled true sslListener

echo
echo "Configuring http headers"
$ASADMIN_CMD create-jvm-options -Dproduct.name=
$ASADMIN_CMD set server.network-config.protocols.protocol.http-listener-1.http.xpowered-by=false
$ASADMIN_CMD set server.network-config.protocols.protocol.http-listener-2.http.xpowered-by=false
$ASADMIN_CMD set server.network-config.protocols.protocol.admin-listener.http.xpowered-by=false

# check error pages
# Want to show something other than default 404 page-not-found response
# (which tend to be rather too revealing....)?
# 1 It is possible to create 404 error pages that are application-specific
# by editing that application's web.xml file, and adding an
# element like
# <error-page>
# <error-code>404</error-code>
# <location>/404.html</location>
# </error-page>
#
# 2a It is possible to create 404 error pages that are global to the server
# by editing the server's domain.xml file, and adding a property having
# name="send-error_1"
# value="code=404 path=/tmp/404.html reason=Resource_not_found"
# 2b Or by executing a command like
# asadmin set server.http-service.virtual-server.server.property.send-error_1="code=404 path=/tmp/404.html reason=Resource_not_found"
# 2c Or by using the admin console to add a new property 'send-error_1'
# with a value "code=404 path=/tmp/404.html reason=Resource_not_found"

# removing https and restoring default configuration:
# $ASADMIN_CMD set server-config.network-config.protocols.protocol.admin-listener.security-enabled=false
# $ASADMIN_CMD disable-secure-admin

# restore default JVM Options
# $ASADMIN_CMD delete-jvm-options -- -Xmx2048m
# $ASADMIN_CMD create-jvm-options -- -Xmx512m
# $ASADMIN_CMD delete-jvm-options -- -Xms1024m

# stop server after install
echo "Stopping glassfish"
$ASADMIN_CMD stop-domain $PAYARA_DOMAIN_NAME
echo "Glassfish installation/configuration done"
