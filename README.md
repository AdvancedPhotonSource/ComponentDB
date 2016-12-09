# Component DB

[![Documentation Status](https://readthedocs.org/projects/componentdb/badge/?version=latest)](http://componentdb.readthedocs.io/en/latest/?badge=latest)
[![Build Status](https://travis-ci.org/AdvancedPhotonSource/ComponentDB.svg?branch=master)](https://travis-ci.org/AdvancedPhotonSource/ComponentDB)

**Prerequisites:**

In order to deploy or develop Component DB, you must have some support software installed. Follow the instructions below to achieve this.
    
    # For red-hat based linux distribution run the following:
    yum install -y gcc libgcc expect zlib-devel openssl-devel openldap-devel readline-devel git make sed gawk autoconf automake wget mysql mysql-libs mysql-server mysql-devel curl
    # For debian based linux distributions run the following:
    apt-get install wget gcc git make curl expect mysql-server libmysqlclient-dev openssl libssl-dev libldap2-dev libsasl2-dev sed gawk

# Deployment
For detailed deployment instructions please refer to our [administrators guide](https://confluence.aps.anl.gov/display/APSUCMS/Administrator+Guide).

**Deployment Procedure:**

    # Make a new directory to hold cdb and its support directories. (replace or set DESIRED_CDB_INSTALL_DIRECTORY var with a unix directory.)
    mkdir $DESIRED_CDB_INSTALL_DIRECTORY
    cd $DESIRED_CDB_INSTALL_DIRECTORY 
    # get the distribution of Component DB (Alternativelly download a release zip and unzip it). 
    git clone https://github.com/AdvancedPhotonSource/ComponentDB.git
    # Navigate inside the distribution. 
    cd ComponentDb
    # Build support needed for the application
    make support
    # load enviornment variables with new support built. 
    source setup.sh    
    # Create deployment configuration
    make configuration
    # Create a clean db for the distribution 
    make clean-db
    # Prepare web portal configuraiton
    make configure-web-portal
    # Deploy web portal
    make deploy-web-portal
    # Deploy REST web service
    make deploy-web-service
    
    # All done... output of the command below should print url to the deployed portal. 
    echo "https://`hostname`:8181/cdb"
    
    
# Development 
For detailed development instructions please refer to our [developers guide](https://confluence.aps.anl.gov/display/APSUCMS/Developer+Guide). 

**Getting Started with development:**

    # first make a fork of this project. 
    # create a desired development directory and cdb into it
    mkdir $desired_dev_directory
    cd $desired_dev_directory
    git clone https://github.com/AdvancedPhotonSource/ComponentDB.git
    
    # Getting support software
    cd ComponentDb
    make support 
    
    # Getting the tools for development of the portal (Netbeans)
    wget http://download.netbeans.org/netbeans/8.1/final/bundles/netbeans-8.1-linux.sh
    sh netbeans-8.1-linux.sh
    # In the installation instructions, ensure that:
    # - Glassfish Option is checked
    # - Recommended Directories
    #   - desired_dev_directory/support-`hostname`/netbeans/netbeans-8.1
    #   - desired_dev_directory/support-`hostname`/netbeans/glassfish-4.1.1
    # - Use jdk located in desired_dev_directory/support-`hostname`/java/linux-x86_64
    rm netbeans-8.1-linux.sh    # When installation of netbeans is complete 
    
    # Getting the db ready
    # mysql could be installed as part of ComponentDB support by running 'make support-mysql' 
    # - Afterwards run `./etc/init.d/cdb-mysql start`
    # if you have mysql installed run...
    make clean-db     # sample-db will be coming later 
    
    # Start development
    make dev-config 
    
    # For portal development
    ../support-`hostname`/netbeans/netbeans-8.1/bin/netbeans
    # Open the project: src/java/CdbWebPortal
    
    # For web service development (Use your favorite python editor) to test run web service using:
    ./sbin/cdbWebService.sh
    
# License
[Copyright (c) UChicago Argonne, LLC. All rights reserved.](https://github.com/AdvancedPhotonSource/ComponentDB/blob/master/LICENSE)
