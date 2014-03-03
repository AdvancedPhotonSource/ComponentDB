<?php
/*
 * Database includes
 *
 * DB query builder to help in building up big sql statements. Provides simple
 * functions for adding tables, columns, and where clauses to a db "select"
 * query. After constructing an instance of DBQueryBuilder (with an optional
 * string prefix to apply to all table name references), you then use the "add"
 * functions to add columns of interest, tables of interest, where clauses, and
 * any additional select query suffixes (ie. "order by"). A final call to the
 * getQueryString() function will return a properly constructed select
 * statement.
 */
 
  class DBQueryBuilder {
    var $cols;
    var $tables;
    var $whereClauses;
    var $querySuffixes;
    var $tableNamePrefix;  // string to prefix table names with
    
    /*
     * Constructor. $prefix is a table name prefix that will be added to all
     * table names added via the addTable() function.
     */
    function DBQueryBuilder($prefix) {
        $this->tableNamePrefix = $prefix;
        $this->cols = array();
        $this->tables = array();
        $this->whereClauses = array();
        $this->querySuffixes = array();
    }
    
    /*
     * getQueryString() builds up the final "select ..." statement and returns
     * it as a string.
     */
    function getQueryString() {
        $st = "select ";
        
        // build up columns of interest
        $first = true;
        foreach ($this->cols as $key=>$value) {
            if (!$first)
                $st = $st . ", ";
            $st = $st . $value;
            $first = false;
        }
        
        // build up tables of interest
        $st = $st . " from ";
        $first = true;
        foreach ($this->tables as $key=>$value) {
            if (!$first)
                $st = $st . ", ";
            $st = $st . $value;
            $first = false;
        }   
        
        // build up where clauses (if any)
        if ((int)$this->whereClauses > 0) {
            $st = $st . " where ";
            $first = true;
            foreach ($this->whereClauses as $key=>$value) {
                if (!$first)
                    $st = $st . " and ";
                $st = $st . $value;
                $first = false;
            }
        }
        
        // build up trailing options
        if ((int)$this->querySuffixes > 0) {
            $st = $st . " ";
            foreach ($this->querySuffixes as $key=>$value) {
                $st = $st . $value . " ";
            }
        }
        $st = $st . ";";
        return $st;
    }
    
    /*
     * addColumn() will add the given $column to the first part of the select
     * statement (ie. select $column ). You can repeatedly add columns without
     * having to inject any comma's to separate them.
     */
    function addColumn($column) {
        $this->cols[] = $column;
    }
    
    /*
     * addTable() will add a table to the "from" part of the select statement.
     * You can repeatedly add tables without having to deal with separating
     * commas. Prefixes your $table argument with tableNamePrefix.
     */
    function addTable($table) {
        $this->tables[] = $this->tableNamePrefix . $table;
    }
    function addTableIgnorePrefix($table) {
        $this->tables[] = $table;
    }
    
    /*
     * addWhere() will add a where clause to "where" part of select statement.
     * You can repeatedly add clauses without having to deal with separating
     * "and". If you need "or" logic, submit your clauses all with one
     * addWhere(). I'll try and add sensible support for logic later on.
     */
    function addWhere($clause) {
        $this->whereClauses[] = $clause;
    }
    
    /*
     * addSuffix() will add any $clause you supply to the end of the select.
     * Typically used for an "order by" section.
     */
    function addSuffix($clause) {
        $this->querySuffixes[] = $clause;
    }
    
  }

?>