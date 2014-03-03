<?php   
/*
 * Defines UI class that holds pv search choices.
 * 
 */

class PVSearchChoices {
    var $iocChoices;
    var $dbFileChoices;
    var $recTypeChoices;
    var $pvName;
    var $pvFieldName;
    var $pvFieldValue;

    function PVSearchChoices($iocChoices,$dbFileChoices,$recTypeChoices,$pvName,$pvFieldName,$pvFieldValue) {
        $this->iocChoices = $iocChoices;
        $this->dbFileChoices = $dbFileChoices;
        $this->recTypeChoices = $recTypeChoices;
        $this->pvName = $pvName;
        $this->pvFieldName = $pvFieldName;
        $this->pvFieldValue = $pvFieldValue;
    }
    function getIOCChoices() {
        return $this->iocChoices;
    }
    function getDBFileChoices() {
        return $this->dbFileChoices;
    }
    function getRecTypeChoices() {
        return $this->recTypeChoices;
    }
    function getPVName() {
        return $this->pvName;
    }
    function getPVFieldName() {
        return $this->pvFieldName;
    }
    function getPVFieldValue() {
        return $this->pvFieldValue;
    }
}

?>