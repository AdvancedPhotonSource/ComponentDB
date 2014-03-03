<?php   
/*
 * Defines business class IOCBootList which contains an array of IOCBootEntity.
 * IOCResourceEntity is also defined here (as opposed to its own file), since it
 * is really only needed in conjunction with IOCBootEntity.
 */

/*
 * IOCBootEntity corresponds to a single record from the IRMIS ioc_boot table.
 * Also holds references to all associated IOCResourceEntity.
 */
class IOCBootEntity {
    var $iocBootID;
    var $iocName;
    var $sysBootLine;
    var $iocResourceEntities;

    function IOCBootEntity($iocBootID, $iocName, $sysBootLine, $iocResourceEntities) {
        $this->iocBootID = $iocBootID;
        $this->iocName = $iocName;
        $this->sysBootLine = $sysBootLine;
        $this->iocResourceEntities = $iocResourceEntities;
    }
    function getIocBootID() {
        return $this->iocBootID;
    }
    function getIocName() {
        return $this->iocName;
    }
    function getSysBootLine() {
        return $this->sysBootLine;
    }
    // returns array of IOCResourceEntity
    function getIocResourceEntities() {
        return $this->iocResourceEntities;
    }
    function getIocResourceEntityById($id) {
         foreach ($this->iocResourceEntities as $iocResourceEntity) {
            if ($iocResourceEntity->getIocResourceID() == $id) {
                return $iocResourceEntity;
            }    
        }
        return null;       
    }
}

/*
 * IOCResourceEntity corresponds to a single record from the IRMIS ioc_resource
 * table.
 */
class IOCResourceEntity {
    var $iocResourceID;
    var $dbFile;
    
    function IOCResourceEntity($iocResourceID, $dbFile) {
        $this->iocResourceID = $iocResourceID;
        $this->dbFile = $dbFile;
    }
    function getIocResourceID() {
        return $this->iocResourceID;
    }
    function getDBFile() {
        return $this->dbFile;
    }
}

/*
 * IOCBootList is a business object which contains a collection of information
 * representing the list of all current IOC boots in the IRMIS database. It is
 * essentially a wrapper for an array of IOCBootEntity, along with the standard
 * "loadFromDB()" function and other utility functions.
 */
class IOCBootList {
    // an array of IOCBootEntity
    var $iocBootEntities;

    function IOCBootList() {
        $this->iocBootEntities = array();
    }

    function getElement($idx) {
        return $this->iocBootEntities[$idx];
    }

    /*
     * getArray() returns an array of IOCBootEntity (assuming loadFromDB has
     * been invoked already).
     */
    function getArray() {
        return $this->iocBootEntities;
    }
    
    // Returns IOCBootEntity with primary key $id from ioc_boot table
    function getElementForId($id) {
        foreach ($this->iocBootEntities as $iocBootEntity) {
            if ($iocBootEntity->getIocBootID() == $id) {
                return $iocBootEntity;
            }    
        }
        logEntry('debug',"Unable to find IocBootEntity for id $id");
        return null;
    }

    /*
     * Conducts MySQL db transactions to initialize IOCBootList from the
     * ioc_boot and ioc_resource tables. Returns false if db error occurs, true
     * otherwise. */
    function loadFromDB($dbConn) {
        global $errno;
        global $error;
        
        $conn = $dbConn->getConnection();
        $tableNamePrefix = $dbConn->getTableNamePrefix();
        
        // Get core ioc info from ioc table
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn("ioc.ioc_id");
        $qb->addColumn("ioc_boot_id");
        $qb->addColumn("ioc_nm");
        $qb->addColumn("sys_boot_line");
        $qb->addTable("ioc");
        $qb->addTable("ioc_boot");
        $qb->addWhere("ioc.ioc_id=ioc_boot.ioc_id");
        $qb->addWhere("current_load=1");
        $qb->addWhere("ioc.active=1");
        $qb->addSuffix("order by ioc_nm");
        $iocQuery = $qb->getQueryString();
        logEntry('debug',$iocQuery);
        if (!$iocResult = mysql_query($iocQuery, $conn)) {
            $errno = mysql_errno();
            $error = "IOCBootList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
        }
        $idx = 0;
        if ($iocResult) {
            while ($iocRow = mysql_fetch_array($iocResult)) {
                // Use ioc_boot_id to retrieve related db files from ioc_resource table, and add to entity
                $qb = new DBQueryBuilder($tableNamePrefix);
                $qb->addColumn("ioc_resource_id");
                $qb->addColumn("uri");
                $qb->addTable("ioc_resource");
                $qb->addTable("uri");
                $qb->addWhere("ioc_resource.uri_id=uri.uri_id");
                $qb->addWhere("ioc_boot_id = ".$iocRow['ioc_boot_id']);
                $qb->addWhere("unreachable=0");
                $qb->addWhere("ioc_resource_type_id in (1,2)");
                $iocResourceQuery = $qb->getQueryString();
                // NOTE: not sure if this should constitute an error
                if (!$iocResourceResult = mysql_query($iocResourceQuery, $conn)) {
                    $errno = mysql_errno();
                    $error = "IOCBootList.loadFromDB(): ".mysql_error();
                    logEntry('critical',$error);
                    return false;                            
                }
                $iocResourceEntities = array();
                if ($iocResourceResult) {
                    $rIdx = 0;
                    while ($iocResourceRow = mysql_fetch_array($iocResourceResult)) {
                        $dbFilePath = $iocResourceRow['uri'];
                        $iocResourceID = $iocResourceRow['ioc_resource_id'];
                  
                        $iocResourceEntity = new IOCResourceEntity($iocResourceID, $dbFilePath);
                        $iocResourceEntities[$rIdx] = $iocResourceEntity;
                        $rIdx = $rIdx +1;
                        
                    }
                }
                $iocBootEntity = new IOCBootEntity($iocRow['ioc_boot_id'], $iocRow['ioc_nm'], $iocRow['sys_boot_line'], $iocResourceEntities);
                $this->iocBootEntities[$idx] = $iocBootEntity;
                $idx = $idx +1;
            }
        }
        return true;
    }
}

?>
