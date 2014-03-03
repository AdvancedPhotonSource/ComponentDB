<?php   
/*
 * Defines business class FldList which contains an array of FldEntity.
 * 
 */

class FldEntity {
    var $fldID;
    var $iocResourceID;
    var $fldType;
    var $fldTypeID;
    var $dbdType;
    var $fldVal;
    var $fldState; // default val, user overwritten val, user defined

    function FldEntity($fldID,$iocResourceID,$fldType,$fldTypeID,$dbdType,$fldVal) {
        $this->fldID = $fldID;
        $this->iocResourceID = $iocResourceID;
        $this->fldType = $fldType;
        $this->fldTypeID = $fldTypeID;
        $this->dbdType = $dbdType;
        $this->fldVal = $fldVal;
    }
    function getFldID() {
        return $this->fldID;
    }
    function getIocResourceID() {
        return $this->iocResourceID;
    }
    function getFldType() {
        return $this->fldType;
    }
    function getFldTypeID() {
        return $this->fldTypeID;
    }
    function getDbdType() {
        return $this->dbdType;
    }
    function getFldVal() {
        return $this->fldVal;
    }
    // $state - one of "default","overwritten","user"
    function setFldState($state) {
        $this->fldState = $state;
    }
    function getFldState() {
        return $this->fldState;
    }
}

/*
 * Represents a list of Fields (and some secondary info) for a single process
 * variable. */
class FldList {
    // an array of FldEntity
    var $fldEntities;

    function FldList() {
        $this->fldEntities = array();
    }
    
    function length() {
        return count($this->fldEntities);
    }

    function getElement($idx) {
        return $this->fldEntities[$idx];
    }

    function getArray() {
        return $this->fldEntities;
    }

    /*
     * Load this object from the database, using constraints supplied in
     * the arguments. Note that null argument means "all". Returns
     * false if a db error occurs, true otherwise.
     * 
     * $conn - db connection identifier
     * $recEntity - RecEntity for process variable of interest  
     * */
    function loadFromDB($dbConn, $recEntity) {
        global $errno;
        global $error;
        
        $conn = $dbConn->getConnection();
        $tableNamePrefix = $dbConn->getTableNamePrefix();
        
        // build up fld table query for fields part of db files
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn("f.fld_id");
        $qb->addColumn("f.rec_id");
        $qb->addColumn("f.ioc_resource_id");
        $qb->addColumn("f.fld_val");
        $qb->addColumn("ft.fld_type");
        $qb->addColumn("ft.fld_type_id");
        $qb->addColumn("ft.dbd_type");
        $qb->addColumn("ft.def_fld_val");
        $qb->addTable("fld f");
        $qb->addTable("fld_type ft");
        $qb->addWhere("f.fld_type_id = ft.fld_type_id");
        $qb->addWhere("f.rec_id = " . $recEntity->getRecID());
        
        $fldQuery = $qb->getQueryString();
        logEntry('debug',"rec field query: ".$fldQuery);
            
        if (!$fldResult = mysql_query($fldQuery, $conn)) {
            $errno = mysql_errno();
            $error = "FldList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
        }
        $idx = 0;
        if ($fldResult) {
            while ($fldRow = mysql_fetch_array($fldResult)) {
                $fldID = $fldRow['fld_id'];
                $recID = $fldRow['rec_id'];
                $iocResourceID = $fldRow['ioc_resource_id'];
                $fldType = $fldRow['fld_type'];
                $fldTypeID = $fldRow['fld_type_id'];
                $dbdType = $fldRow['dbd_type'];
                $fldVal = $fldRow['fld_val'];
                $defFldVal = $fldRow['def_fld_val'];
                
                $fldEntity = new FldEntity($fldID,$iocResourceID,$fldType,$fldTypeID,$dbdType,$fldVal);
                $this->fldEntities[$idx++] = $fldEntity;
            }
        }
        
        // build up fld_type query for fields part of record definitions
        $qb2 = new DBQueryBuilder($tableNamePrefix);
        $qb2->addColumn("ft.fld_type_id");
        $qb2->addColumn("ft.fld_type");
        $qb2->addColumn("ft.dbd_type");
        $qb2->addColumn("ft.def_fld_val");
        $qb2->addTable("fld_type ft");
        $qb2->addTable("rec_type rt");
        $qb2->addWhere("ft.rec_type_id = rt.rec_type_id");
        $qb2->addWhere("rt.rec_type_id = " . $recEntity->getRecTypeID());
        
        $fldQuery = $qb2->getQueryString();
        logEntry('debug',"rec_type field query: ".$fldQuery);
        
        if (!$fldResult = mysql_query($fldQuery, $conn)) {
            $errno = mysql_errno();
            $error = "FldList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
        }
        if ($fldResult) {
            while ($fldRow = mysql_fetch_array($fldResult)) {
                $fldTypeID = $fldRow['fld_type_id'];
                $fldType = $fldRow['fld_type'];
                $dbdType = $fldRow['dbd_type'];
                $defFldVal = $fldRow['def_fld_val'];
                // Compare this with all field entities found in prior query.
                // Use this to determine field state (default, overwritten, user)
                // and/or create a new FldEntity
                $found = false;
                foreach ($this->fldEntities as $key=>$fldEntity) {
                    if ($fldEntity->getFldTypeID() == $fldTypeID) {
                        $found = true;
                        // state must be overwritten or user
                        if ($defFldVal == $fldEntity->getFldVal())
                            $fldEntity->setFldState("overwritten");
                        else
                            $fldEntity->setFldState("user");
                        // replace array entry (this is perl, after all)
                        $this->fldEntities[$key] = $fldEntity;
                        break;
                    }
                }
                if (!$found) {
                    // state must be default
                    $newFldEntity = new FldEntity(null,$recEntity->getIocResourceID(),
                                                  $fldType,$fldTypeID,$dbdType,$defFldVal);
                    $newFldEntity->setFldState("default");
                    $this->fldEntities[$idx++] = $newFldEntity;
                }
            }
            
        } // end if ($fldResult)
        return true;
    }
}

?>
