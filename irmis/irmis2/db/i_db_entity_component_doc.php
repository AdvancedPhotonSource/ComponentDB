<?php   
/*
 * Defines business class PersonList which contains an array of PersonEntity
 * 
 */

class DocEntity {
  var $docType;
  var $uri;

  function DocEntity($docType,$uri) {
	$this->docType = $docType;
	$this->uri = $uri;
  }
  function getdocType() {
    return $this->docType;
  }
  function geturi() {
    return $this->uri;
  }
}

/*
 * Represents a list of people  (and some secondary info).
 */
class DocList {
  // an array of PersonEntity
  var $docEntities;
  
  function DocList() {
    $this->docEntities = array();
  }
  
  // length of full result set
  function length() {
    if ($this->docEntities == null)
      return 0;
    else 
      return count($this->docEntities);
  }
  
  // get a particular PersonEntity by sequential index
  function getElement($idx) {
    return $this->docEntities[$idx];
  }
  
  // get full result set as an array of PersonEntity
  function getArray() {
    return $this->docEntities;
  }
  
  
   
  function loadFromDBForDoc($dbConn, $ID) {
    global $errno;
    global $error;
    
    $conn = $dbConn->getConnection();
    $tableNamePrefix = $dbConn->getTableNamePrefix();
    
    // Begin building query for documentation table
    $qb = new DBQueryBuilder($tableNamePrefix);
	$qb->addColumn("ctd.document_type");
	$qb->addColumn("uri.uri");
	$qb->addTable("component_type_document ctd");
	$qb->addTable("uri");
    $qb->addWhere("ctd.component_type_id=".$ID);
	$qb->addWhere("ctd.uri_id=uri.uri_id");
	$qb->addSuffix("order by document_type");
	
    $docQuery = $qb->getQueryString();
    
    logEntry('debug',$docQuery);
    
    if (!$docResult = mysql_query($docQuery, $conn)) {
      $errno = mysql_errno();
      $error = "DocList.loadFromDBForDoc(): ".mysql_error();
      logEntry('critical',$error);
      return false;                 
    }
    $idx = 0;
    if ($docResult) {
      while ($docRow = mysql_fetch_array($docResult)) {
		$docType = $docRow['document_type'];
		$uri = $docRow['uri'];
        $docEntity = new docEntity($docType, $uri);
        $this->docEntities[$idx] = $docEntity;
		$idx = $idx +1;
      }
    }  
    
    return true;
  }
}
?>