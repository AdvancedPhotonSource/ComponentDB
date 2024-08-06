# Component DB

[![Documentation Status](https://readthedocs.org/projects/componentdb/badge/?version=latest)](http://componentdb.readthedocs.io/en/latest/?badge=latest)

# Prerequisites:

In order to deploy or develop Component DB, you must have some support software installed. Follow the instructions below to achieve this.
    
    # For red-hat based linux distribution run the following:
    yum install -y gcc libgcc expect zlib-devel openssl-devel openldap-devel readline-devel git make cmake sed gawk autoconf automake wget mariadb mariadb-server curl unzip rsync
    # For debian based linux distributions run the following:
    apt-get install wget gcc git make cmake build-essential libcurses-ocaml-dev curl expect mariadb-client mariadb-server openssl libssl-dev libldap2-dev libsasl2-dev sed gawk unzip rsync

***Versions used in production***
- mariadb: 10.5.22
- Other dependencies are automatically downloaded and installed when running `make support` command.


# Deployment
[//]: # "For detailed deployment instructions please refer to our [administrators guide](https://confluence.aps.anl.gov/display/APSUCMS/Administrator+Guide)."
Below are the steps to be followed to deploy an installation of CDB. These steps are designed to be done as a standard non-root user account. Before starting ensure that you have set the `root` password for your mariadb installation (See [Helpful Utility](https://mariadb.com/kb/en/mariadb-secure-installation/)).

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
    
    # All done... output of the command below should print url to the deployed portal. 
    echo "https://`hostname`:8181/cdb"

    # Valid SSL certificates can also be applied using the following command:
    # ./sbin/cdb_update_glassfish_ssl.sh KEY_FILE CRT_FILE # NOTE, this will prompt for the master password set during the `make support` step. 
    
    
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
    # Get Netbeans
    make support-netbeans

    # Load up the environment 
    source setup.sh

    # Prepare Dev DB    
    # mysql could be installed as part of ComponentDB support by running 'make support-mysql' 
    # - Afterwards run `./etc/init.d/cdb-mysql start`
    # if you have mysql installed and started run...
    make clean-db   # sample-db will be coming later 
    
    # Start development
    make dev-config     

    # Open Netbeans
    netbeans & 

## Preparing Netbeans
Once netbeans is open a few steps need to be taken to prepare netbeans for CDB development.
1. Open CDB Project: File > Open Project
2. Navigate to $desired_dev_directory/ComponentDB/src/java
3. Select CdbWebPortal and hit Open Project
4. Right click on CdbWebPortal top level under projects
5. Click "Resolve Missing Server Problem"
6. Add Server -> Payara Server
  - Installation Location: $desired_dev_directory/support-`hostname`/netbeans/payara
  - Version: 5.2022.5
  - Use the wizard's download 
7. Next -> Use Default Domain Location -> Finish add server instance wizard
8. Select the Newly added "Payara Server"
9. Copy over the required mysql client to new payara server. 
```sh 
# cd into the $desired_dev_directory/$distribution_directory
cp src/java/CdbWebPortal/lib/mariadb-java-client-3.1.0.jar ../support-`hostname`/netbeans/payara/glassfish/domains/domain1/lib/
```
10. Run the project

## ~~Python Web Service Development~~
The python web service is deprecated but is still a part of the distribution for legacy integrations. 

    # Code is located in $desired_dev_directory/ComponentDB/src/python
    # For web service development (Use your favorite python editor) to test run web service using:
    ./sbin/cdbWebService.sh
    
# License
[Copyright (c) UChicago Argonne, LLC. All rights reserved.](https://github.com/AdvancedPhotonSource/ComponentDB/blob/master/LICENSE)
