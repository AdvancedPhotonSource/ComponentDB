<?php
/*
 * Database includes
 *
 * DB connectivity for php apps. This php page must be included on every page in
 * your application that will be performing database access.
 * 
 * Your application should construct a DBConnectionManager, and retrieve a valid
 * connection for use using DBConnectionManager->getConnection(). This returns a
 * DBConnection which can be used within this db access layer, or you can use
 * DBConnection->getConnection() to get the actual php mysql connection
 * identifier in order to directly use the mysql php calls.
 */
 
 
 /*
  * DBConnection class is simple container so we can pass around
  * a db table name prefix along with connection identifier.
  */
  class DBConnection {
    var $conn;  // mysql link identifier
    var $tableNamePrefix;  // string to prefix table names with
    
    function DBConnection($conn,$prefix) {
        $this->conn = $conn;
        $this->tableNamePrefix = $prefix;
    }
    function getConnection() {
        return $this->conn;
    }
    function getTableNamePrefix() {
        return $this->tableNamePrefix;
    }
  }

/*
 * DBConnectionManager provides single location to specify all db connection
 * parameters. A valid MySQL connection (wrapped in DBConnection) is returned
 * via the getConnection function.
 */
  class DBConnectionManager {
    var $host;
    var $port;
    var $username;
    var $password;
    var $dbName;
    var $tableNamePrefix;
    
    function DBConnectionManager($host,$port,$dbname,$user,$pass,$tableNamePrefix) {
        $this->host=$host;
        $this->port=$port;
        $this->dbName = $dbname;
        $this->username = $user;
        $this->password = $pass;
        $this->tableNamePrefix = $tableNamePrefix;
    }   
    
    
    /*
     * getConnection() returns DBConnection for use by db_entity php code. 
     * Persistent connection function mysql_pconnect is used to establish
     * connection, so only one total connection to db (per apache process) is
     * ever created. Ie. one isn't created for every call to this.
     */
    function getConnection() {
       global $errno;
       global $error;
       
       $fullHostname=$this->host.":".$this->port;
       if (!($conn = mysql_connect($fullHostname,$this->username,$this->password))) {
            $errno = mysql_errno();
            $error = "DBConnectionManager.getConnection(): ".mysql_error();
            logEntry('critical',$error);
            return false;
       }
       if (!mysql_select_db($this->dbName,$conn)) {
            $errno = mysql_errno();
            $error = "DBConnectionManager.getConnection(): ".mysql_error();
            logEntry('critical',$error);
            return false;       
       }
       return new DBConnection($conn,$this->tableNamePrefix);
    }
  }

?>