#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# Usage install_glassfish.sh [Install_DIR] [CONFIGURE 0/1]

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')
CDB_HOSTNAME=`hostname -f`
PAYARA_VERSION=5.192
PAYARA_ZIP_FILE=payara-$PAYARA_VERSION.zip
PAYARA_DOWNLOAD_URL=https://search.maven.org/remotecontent?filepath=fish/payara/distributions/payara/$PAYARA_VERSION/$PAYARA_ZIP_FILE
PAYARA_DN_NAME="CN=${CDB_HOSTNAME}"

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`

srcDir=$topDir/src
buildDir=$topDir/build
binDir=$topDir/bin
glassfishMasterPasswordExpectScript=$binDir/glassfish_master_password.expect
glassfishAdminPasswordExpectScript=$binDir/glassfish_admin_password.expect
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
    curl -L -O $PAYARA_DOWNLOAD_URL
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

# backup passwords
echo "Backing up original passwords"
cd $payaraInstallDir/glassfish
PASSWORD_FILES="domains/$PAYARA_DOMAIN_NAME/config/domain-passwords domains/$PAYARA_DOMAIN_NAME/config/keystore.jks domains/$PAYARA_DOMAIN_NAME/config/cacerts.jks"
for f in domains/$PAYARA_DOMAIN_NAME/master-password domains/$PAYARA_DOMAIN_NAME/local-password; do
    if [ -f $f ]; then
        PASSWORD_FILES="$PASSWORD_FILES $f"
    fi
done
PASSWORD_TAR_FILE=passwords.orig.tar
tar cf $PASSWORD_TAR_FILE $PASSWORD_FILES

# Change master password
# Enter the current master password> [By default this is 'changeit']
# Enter the new master password> [e.g. 'myMasterPwd']
# Enter the new master password again>
# After entering the new master password twice, there is a pause of several
# seconds. Then you will find new versions of 'master-password'
# 'domain-passwords' 'keystore.jks' and 'cacerts.jks'.
#echo "Changing master password ['changeit']:"
#$ASADMIN_CMD change-master-password --savemasterpassword=true $PAYARA_DOMAIN_NAME || exit 1

# Read new master password
echo
echo "Changing master password"
sttyOrig=`stty -g`
stty -echo
read -p "Enter master password: " MASTER_PASSWORD
stty $sttyOrig
echo

# Change master password
tmpFile=/tmp/`basename $0`.`id -u`.tmp
eval "cat $glassfishMasterPasswordExpectScript | sed 's?OLD_PASSWORD?changeit?' | sed 's?MASTER_PASSWORD?$MASTER_PASSWORD?' | sed 's?PAYARA_DOMAIN_NAME?$PAYARA_DOMAIN_NAME?' > $tmpFile"
chmod a+x $tmpFile
cd `dirname $ASADMIN_CMD`
$tmpFile

# Your chosen master password should unlock both keystores, and reveal
# two certificates in keystore.jks, and many additional ones in cacerts.jks.
echo
echo "Checking master password: list keystore certificates"
$KEYTOOL_CMD -list -v -keystore $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/keystore.jks --storepass $MASTER_PASSWORD > /dev/null || exit 1
echo "Checking master password: list ca certificates"
$KEYTOOL_CMD -list -v -keystore $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/cacerts.jks --storepass $MASTER_PASSWORD > /dev/null || exit 1

# start glassfish
echo
echo "Starting glassfish"
$ASADMIN_CMD start-domain $PAYARA_DOMAIN_NAME

# read new glassfish admin password
echo
echo "Changing admin password"
sttyOrig=`stty -g`
stty -echo
read -p "Enter glassfish admin password: " ADMIN_PASSWORD
stty $sttyOrig
echo

# change admin password
eval "cat $glassfishAdminPasswordExpectScript | sed 's?OLD_PASSWORD??' | sed 's?ADMIN_PASSWORD?$ADMIN_PASSWORD?' > $tmpFile"
chmod a+x $tmpFile
cd `dirname $ASADMIN_CMD`
$tmpFile

# $ASADMIN_CMD change-admin-password
# Enter admin user name [default: admin]> [Just press Enter]
# Enter admin password> [By default this is blank, so just press Enter]
# Enter new admin password> [Must contain at least 8 chars (e.g. 'myAdminPwd')]
# Enter new admin password again>
# There will now be a new version of '$GLASSFISH_HOME/domains/$PAYARA_DOMAIN_NAME/config/admin-keyfile'

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

# inspect keystore.jks
echo
echo "Inspecting keystore"
cd $payaraInstallDir/glassfish/domains/$PAYARA_DOMAIN_NAME/config/
$KEYTOOL_CMD -list -keystore keystore.jks -storepass $MASTER_PASSWORD

# update keystore.jks
echo
echo "Updating keystore"
$KEYTOOL_CMD -delete -alias s1as -keystore keystore.jks -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -delete -alias glassfish-instance -keystore keystore.jks -storepass $MASTER_PASSWORD

$KEYTOOL_CMD -genkeypair -alias s1as -dname "$PAYARA_DN_NAME" -keyalg RSA -keysize 2048 -validity 3650 -keystore keystore.jks -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD
$KEYTOOL_CMD -genkeypair -alias glassfish-instance -dname "$PAYARA_DN_NAME" -keyalg RSA -keysize 2048 -validity 3650 -keystore keystore.jks -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD

# check keystore.jks
echo
echo "Checking keystore"
$KEYTOOL_CMD -list -keystore keystore.jks -storepass $MASTER_PASSWORD

# export certificates from keystore.jks
echo
echo "Exporting certificates"
$KEYTOOL_CMD -exportcert -alias s1as -file s1as.cert -keystore keystore.jks -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -exportcert -alias glassfish-instance -file glassfish-instance.cert -keystore keystore.jks -storepass $MASTER_PASSWORD

# update cacerts.jks
echo
echo "Updating cacerts.jks, using master password as key password"
$KEYTOOL_CMD -delete -alias s1as -keystore cacerts.jks -storepass $MASTER_PASSWORD
$KEYTOOL_CMD -delete -alias glassfish-instance -keystore cacerts.jks -storepass $MASTER_PASSWORD

$KEYTOOL_CMD -importcert -noprompt -alias s1as -file s1as.cert -keystore cacerts.jks -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD
$KEYTOOL_CMD -importcert -noprompt -alias glassfish-instance -file glassfish-instance.cert -keystore cacerts.jks -storepass $MASTER_PASSWORD -keypass $MASTER_PASSWORD

# check cacerts.jks and tidy up
echo
echo "Checking cacerts.jks"
$KEYTOOL_CMD -list -keystore cacerts.jks -storepass $MASTER_PASSWORD > /dev/null || exit 1
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
