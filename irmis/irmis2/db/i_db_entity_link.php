<?php   
/*
 * Defines business class LinkList which contains an array of LinkEntity.
 * 
 */

class LinkEntity {

    var $linkRecID;
    var $linkRecName;
    var $linkRecTypeID;
    var $linkRecType;
    var $linkFldID;
    var $linkFldTypeID;
    var $linkFldType;
    var $linkFldVal;
    
    function LinkEntity($linkRecID,$linkRecName,$linkRecTypeID,$linkRecType,
                        $linkFldID,$linkFldTypeID,$linkFldType,$linkFldVal) {
        $this->linkRecID = $linkRecID;
        $this->linkRecName = $linkRecName;
        $this->linkRecTypeID = $linkRecTypeID;
        $this->linkRecType = $linkRecType;
        $this->linkFldID = $linkFldID;
        $this->linkFldTypeID = $linkFldTypeID;
        $this->linkFldType = $linkFldType;
        $this->linkFldVal = $linkFldVal;
    }
    
    function getLinkRecID() {
        return $this->linkRecID;
    }
    function getLinkRecName() {
        return $this->linkRecName;
    }
    function getLinkRecTypeID() {
        return $this->linkRecTypeID;
    }
    function getLinkRecType() {
        return $this->linkRecType;
    }
    function getLinkFldTypeID() {
        return $this->linkFldTypeID;
    }
    function getLinkFldType() {
        return $this->linkFldType;
    }
    function getLinkFldVal() {
        return $this->linkFldVal;
    }
}

/*
 * Represents a list of pv links (and some secondary info) that reference a
 * given process variable.
 * */
class LinkList {
    // an array of LinkEntity
    var $linkEntities;

    function LinkList() {
        $this->linkEntities = array();
    }
    
    function length() {
        return count($this->linkEntities);
    }

    function getElement($idx) {
        return $this->linkEntities[$idx];
    }

    function getArray() {
        return $this->linkEntities;
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
        
        // build up fld table query for all DBF_INLINK,OUTLINK, and FWDLINK
        $qb = new DBQueryBuilder($tableNamePrefix);
        $qb->addColumn("f.fld_id");
        $qb->addColumn("f.rec_id");
        $qb->addColumn("f.fld_val");
        $qb->addColumn("ft.fld_type_id");
        $qb->addColumn("ft.rec_type_id");
        $qb->addColumn("ft.fld_type");
        $qb->addColumn("ft.dbd_type");
        $qb->addTable("fld f");
        $qb->addTable("fld_type ft");
        $qb->addWhere("f.fld_type_id = ft.fld_type_id");
        $qb->addWhere("ft.dbd_type in ('DBF_INLINK','DBF_OUTLINK','DBF_FWDLINK')");
        $bigWhere = "(f.fld_val like '".$recEntity->getRecName()." %'";
        $bigWhere = $bigWhere . " or f.fld_val like '".$recEntity->getRecName().".%'";
        $bigWhere = $bigWhere . " or f.fld_val = '".$recEntity->getRecName()."')";
        $qb->addWhere($bigWhere);
        
        $linkQuery = $qb->getQueryString();
        logEntry('debug',"related links field query: ".$linkQuery);
            
        if (!$linkResult = mysql_query($linkQuery, $conn)) {
            $errno = mysql_errno();
            $error = "LinkList.loadFromDB(): ".mysql_error();
            logEntry('critical',$error);
            return false;                 
        }
        $idx = 0;
        if ($linkResult) {
            while ($fldRow = mysql_fetch_array($linkResult)) {
                $fldID = $fldRow['fld_id'];
                $recID = $fldRow['rec_id'];
                $recTypeID = $fldRow['rec_type_id'];
                $fldType = $fldRow['fld_type'];
                $fldTypeID = $fldRow['fld_type_id'];
                $fldVal = $fldRow['fld_val'];
                
                // need another query to get rec_nm, rec_type
                $qb2 = new DBQueryBuilder($tableNamePrefix);
                $qb2->addColumn("r.rec_nm");
                $qb2->addColumn("rt.rec_type");
                $qb2->addTable("rec r");
                $qb2->addTable("rec_type rt");
                $qb2->addWhere("r.rec_type_id = rt.rec_type_id");
                $qb2->addWhere("r.rec_id = ".$recID);
                
                $recQuery = $qb2->getQueryString();
                
                if (!$recResult = mysql_query($recQuery, $conn)) {
                    $errno = mysql_errno();
                    $error = "LinkList.loadFromDB(): ".mysql_error();
                    logEntry('critical',$error);
                    return false;
                }                  
                if ($recResult) {
                    $recName = mysql_result($recResult,0,"rec_nm");
                    $recType = mysql_result($recResult,0,"rec_type");
                }
                
                $linkEntity = new LinkEntity($recID,$recName,$recTypeID,$recType,$fldID,$fldTypeID,$fldType,$fldVal);
                $this->linkEntities[$idx++] = $linkEntity;
            }
        }
        
        return true;
    }
}

?>