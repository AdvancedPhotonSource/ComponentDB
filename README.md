# Component DB

# Deployment
For detailed deployment instructions please see: https://confluence.aps.anl.gov/display/APSUCMS/Administrator+Guide

**Deployment Procedure:**

    # Make a new directory to hold cdb and its support directories. (replace or set DESIRED_CDB_INSTALL_DIRECTORY var with a unix directory.)
    mkdir $DESIRED_CDB_INSTALL_DIRECTORY
    cd $DESIRED_CDB_INSTALL_DIRECTORY 
    # get the distribution of Component DB (Alternativelly download a release zip and unzip it). 
    git clone https://github.com/AdvancedPhotonSource/ComponentDB.git
    # Navigate inside the distribution. 
    cd ComponentDb
    # Load default enviornment variables 
    source setup.sh
    # Build support needed for the application
    make support
    # reload enviornment variables with new support built. 
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
Section is coming soon. 
    
# License
[Copyright (c) UChicago Argonne, LLC. All rights reserved.](https://github.com/AdvancedPhotonSource/ComponentDB/blob/master/LICENSE)
