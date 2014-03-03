<?php   
/*
 * Defines business class RecList which contains an array of RecEntity.
 * 
 */

class RecEntity {
    var $recID;
    var $recName;
    var $recType;
    var $recTypeID;
    var $iocBootID;
    var $iocName;
    var $iocResourceID;

    function RecEntity($recID,$recName,$recType,$recTypeID,$iocBootID,$iocName,$iocResourceID) {
        $this->recID = $recID;
        $this->recName = $recName;
        $this->recType = $recType;
        $this->recTypeID = $recTypeID;
        $this->iocBootID = $iocBootID;
        $this->iocName = $iocName;
        $this->iocResourceID = $iocResourceID;
    }
    function getRecID() {
        return $this->recID;
    }
    function getRecName() {
        return $this->recName;
    }
    function getRecType() {
        return $this->recType;
    }
    function getRecTypeID() {
        return $this->recTypeID;
    }
    function getIocBootID() {
        return $this->iocBootID;
    }
    function getIocName() {
        return $this->iocName;
    }
    function getIocResourceID() {
        return $this->iocResourceID;
    }
}

/*
 * Represents a list of records (and some secondary info) resulting from a
 * search for IRMIS process variables. This class also provides display
 * logic for paging through large sets of pv's.
 *  */
class RecList {
    // an array of RecEntity
    var $recEntities;
    var $pageSize;
    var $displayIndexLow;
    var $displayIndexHigh;

    function RecList() {
        $this->recEntities = array();
        $this->pageSize=100;
        $this->displayIndexLow = 1;
        $this->displayIndexHigh = $this->displayIndexLow + $this->pageSize - 1;
    }
    
    // length of full result set
    function length() {
        if ($this->recEntities == null)
            return 0;
        else 
            return count($this->recEntities);
    }
    
    // get a particular RecEntity by sequential index
    function getElement($idx) {
        return $this->recEntities[$idx];
    }

    // get full result set as an array of RecEntity
    function getArray() {
        return $this->recEntities;
    }
    
    // get result subset for current display indices
    function getDisplayArray() {
        $tempRecEntities = array();
        $tempIdx = 0;
        for ($i=$this->displayIndexLow-1 ; $i < $this->displayIndexHigh ; $i++) {
            $tempRecEntities[$tempIdx++] = $this->recEntities[$i];
        }
        return $tempRecEntities;
    }
    
    function getDisplayIndexLow() {
        return $this->displayIndexLow;
    }
    function getDisplayIndexHigh() {
        return $this->displayIndexHigh;
    }
    function nextDisplayIndices() {
        $total = count($this->recEntities);
        $curLow = $this->displayIndexLow;
        $curHigh = $this->displayIndexHigh;
        if (($curLow + $this->pageSize) < $total) {
            $this->displayIndexLow = $curLow + $this->pageSize;
        }
        if (($curHigh + $this->pageSize) < $total) {
            $this->displayIndexHigh = $curHigh + $this->pageSize;
        } else {
            $this->displayIndexHigh = $total;
        }
    }
    function prevDisplayIndices() {
        $total = count($this->recEntities);
        $curLow = $this->displayIndexLow;
        $curHigh = $this->displayIndexHigh;
        if (($curLow - $this->pageSize) > 0) {
            $this->displayIndexLow = $curLow - $this->pageSize;
        } else {
            $this->displayIndexLow = 1;
        }
        if (($curHigh - $this->pageSize) > $this->pageSize) {
            $this->displayIndexHigh = $curHigh - $this->pageSize;
        } else {
            $this->displayIndexHigh = $this->pageSize;
        }
    }
    
    function findByPVName($pvName) {
        foreach ($this->recEntities as $recEntity) {
            if ($recEntity->getRecName() == $pvName)
                return $recEntity;    
        }    
        return null;
    }

    /*
     * Load this object from the database, using constraints supplied in
     * the arguments. Note that null argument means "all". Returns
     * false if db error occurs, true otherwise.
     * 
     * $conn - db connection identifier
     * $iocBootEntities - array of IOCBootEntity identifying ioc's of interest
     * $allIocFlag - if true, then user selected "---All---" iocs.
     * $recordTypeSelections - array of strings identifying record types of
     * interest $pvName - pv name of interest (can match using *) $pvFieldName -
     * pv record field name of interest (can match using *) $pvFieldValue - the
     * field default value must match this
     */
    function loadFromDB($dbConn,$iocBootEntities,$allIocFlag,$dbFileIDs,$recordTypeSelections,
                        $pvName,$pvFieldName,$pvFieldValue) {
        global $errno;
        global $error;
        
        $conn = $dbConn->getConnection();
        $tableNamePrefix = $dbConn->getTableNamePrefix();
        
        // build up ioc boot id/name hash for quick lookups later
        $iocBootIDNameHash = array();
        if (!is_null($iocBootEntities)) {
            foreach ($iocBootEntities as $iocBootEntity) {
                $iocBootIDNameHash[$iocBootEntity->getIocBootID()] = $iocBootEntity->getIocName();       
            }
        }
        
        // check constraints for use of "*" in matching string
        $pvNameHasWildcard = false;
        if (is_long(strpos($pvName,"*"))) {
            $pvNameHasWildcard = true;
            // replace all ocurrences of "*" in search terms with sql %
            $pvName = str_replace("*","%",$pvName);
        }
        
        // note: we will apply these two constraints as a filter 
        //       after initial records identified
        $pvFieldNameHasWildcard = false;
        if (is_long(strpos($pvFieldName,"*"))) {
            $pvFieldNameHasWildcard = true;
            $pvFieldName = str_replace("*","%",$pvFieldName);
        }
        
        $pvFieldValueHasWildcard = false;
        if (is_long(strpos($pvFieldValue,"*"))) {
            $pvFieldValueHasWildcard = true;
            $pvFieldValue = str_replace("*","%",$pvFieldValue);
        }    
        
        // Begin building query for rec and rec_type tables
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn("r.rec_id");
        $qb->addColumn("r.ioc_boot_id");
        $qb->addColumn("r.rec_nm");
        $qb->addColumn("rt.rec_type");
        $qb->addColumn("rt.rec_type_id");
        $qb->addColumn("rt.ioc_resource_id");
        $qb->addTable("rec r");
        $qb->addTable("rec_type rt");
        $qb->addWhere("r.rec_type_id = rt.rec_type_id");
            
        // build up string matching clauses
        if ($pvName != null) {
            if ($pvNameHasWildcard)
                $qb->addWhere("r.rec_nm like '".$pvName."'");
            else
                $qb->addWhere("r.rec_nm = '".$pvName."'");
        }
        
        // build up "in" clauses
        
        // if we picked a subset of ioc's, then need to add "in" clause
        if (!$allIocFlag) {
            $ioc_boot_id_in =  "r.ioc_boot_id in (";
            $first = true;
        
            // build up "r.ioc_boot_id in (...)" clause
            $iocResourceIDs = array();
            $iocResourceIdx = 0;
            foreach ($iocBootEntities as $iocBootEntity) {
                if (!$first)
                    $ioc_boot_id_in = $ioc_boot_id_in . ",";
                
                $ioc_boot_id_in = $ioc_boot_id_in . $iocBootEntity->getIocBootID();
                $first = false;
                // build up list of chosen ioc resource id as side-effect
                foreach ($iocBootEntity->getIocResourceEntities() as $iocResourceEntity) {
                    $iocResourceID = $iocResourceEntity->getIocResourceID();
                    // this will filter out repeat values
                    $iocResourceIDs[$iocResourceID] = $iocResourceID;     
                }
            }
            $ioc_boot_id_in = $ioc_boot_id_in . ")";
            $qb->addWhere($ioc_boot_id_in);
        }
        
        // null value implies "all", so we omit the where clause in that case
        if ($recordTypeSelections != null) {
            $rec_type_in = "rt.rec_type in (";
        
            // build up "rt.rec_type in (...)" clause
            $first = true;
            foreach ($recordTypeSelections as $recTypeSelection) {
                if (!$first)
                   $rec_type_in = $rec_type_in . ",";
                $rec_type_in = $rec_type_in . "'". $recTypeSelection . "'";
                $first = false;          
            }
            $rec_type_in = $rec_type_in . ")";
            $qb->addWhere($rec_type_in);
        }
        
        // done with query creation
        $recQuery = $qb->getQueryString();
        
        logEntry('debug',$recQuery);
            
        if (!$recResult = mysql_query($recQuery, $conn)) {
            $errno = mysql_errno();
            $error = "RecList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
        }
        $idx = 0;
        if ($recResult) {
            while ($recRow = mysql_fetch_array($recResult)) {
                $recID = $recRow['rec_id'];
                $recName = $recRow['rec_nm'];
                $recType = $recRow['rec_type'];
                $recTypeID = $recRow['rec_type_id'];
                $iocResourceID = $recRow['ioc_resource_id'];
                $iocBootID = $recRow['ioc_boot_id'];
                $iocName = $iocBootIDNameHash[$iocBootID];
                
                // if pvFieldName and/or pvFieldValue constraints exist, apply second query
                $keepRecResult = true;
                $qb2 = new DBQueryBuilder($tableNamePrefix);
                $qb2->addColumn("count(*)");
                $qb2->addTable("fld f");
                $qb2->addTable("fld_type ft");
                $qb2->addWhere("f.fld_type_id = ft.fld_type_id");
                $qb2->addWhere("f.rec_id = " . $recID);
 
                if ($pvFieldName != null) {
                    if ($pvFieldNameHasWildcard)
                        $qb2->addWhere("ft.fld_type like '".$pvFieldName."'");
                    else
                        $qb2->addWhere("ft.fld_type = '".$pvFieldName."'");
                }
                if ($pvFieldValue != null) {
                    if ($pvFieldValueHasWildcard)
                        $qb2->addWhere("f.fld_val like '".$pvFieldValue."'");
                    else
                        $qb2->addWhere("f.fld_val = '".$pvFieldValue."'");
                }
                if (!is_null($dbFileIDs)) {
                    $ioc_resource_id_in = "f.ioc_resource_id in (";
                    // build up "f.ioc_resource_id in (...)" clause
                    $first = true;
                    foreach ($dbFileIDs as $iocResourceID) {
                        if (!$first)
                            $ioc_resource_id_in = $ioc_resource_id_in . ",";
                        $ioc_resource_id_in = $ioc_resource_id_in . $iocResourceID;
                        $first = false;    
                    }
                    $ioc_resource_id_in = $ioc_resource_id_in . ")";
                    $qb2->addWhere($ioc_resource_id_in);
                }
                if ($pvFieldName != null || $pvFieldValue != null || !is_null($dbFileIDs)) {
                    $fldQuery = $qb2->getQueryString();
                    $fldResult = mysql_query($fldQuery, $conn) or die('unable to query with:'.$fldQuery);
                    if (mysql_result($fldResult,0) == 0)
                        $keepRecResult = false;
                }
                
                if ($keepRecResult) {
                    $recEntity = new RecEntity($recID, $recName,
                                               $recType, $recTypeID, $iocBootID, $iocName,
                                               $iocResourceID);
                    $this->recEntities[$idx++] = $recEntity;
                }
            }  // end while ($recRow
            
        }  // end if ($recResult)
        
        // update display indices
        $this->displayIndexLow = 1;
        $count = $this->length();
        if ($count < $this->pageSize) {
            $this->displayIndexHigh = $count;
        } else {
            $this->displayIndexHigh = $this->displayIndexLow + $this->pageSize - 1;
        }
        return true;
    }
}

?>