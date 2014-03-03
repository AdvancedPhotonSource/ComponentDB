<table cellpadding="1" bgcolor="dbcddc" width="100%">
<tr>
<th align="left" width="15%">IOC</th><th align="left" width="15%">Type</th><th align="left" width="70%">DB File(s)</th>
</tr>
<?php
    // dig up ioc name and db file name for selected pv
    $recEntity = $_SESSION['PVSelectedResult'];
    $iocBootEntity = $_SESSION['PVRelatedIOC'];
    // iterate over all fields, gathering all ioc_resource_id's used,
    //   then lookup the file names of these, and create a do-nothing select box to show them
    $fldList = $_SESSION['FieldSearchResults'];
    $fldEntities = $fldList->getArray();    
    
    $dbFiles = array();
    if (!is_null($fldEntities)) {
        foreach ($fldEntities as $fldEntity) {
            // hash here makes unique set
            $ids[$fldEntity->getIocResourceId()] = $fldEntity->getIocResourceId();
        }
        $idx = 0;
        foreach ($ids as $id) {
            $iocResourceEntity = $iocBootEntity->getIocResourceEntityById($id);
            if (!is_null($iocResourceEntity)) {
                $dbFiles[$idx++] = $iocResourceEntity->getDBFile();
            }
        }
    }
    
    echo "<tr><td>".$recEntity->getIOCName()."</td><td>".$recEntity->getRecType()."</td><td>\n";
    echo '<select name="dbfiles" size="1" style="width: 300px">';
    foreach ($dbFiles as $dbFile) {
        echo '<option name="' . $dbFile . '">' . $dbFile . '</option>' . "\n";    
    }
    echo '</select></td></tr>' . "\n";

?>   
</table>
<!--
<font size=2>display options:&nbsp;<a href="action_pv_details_blanks.php">hide blank fields</a></font>
-->
<br>
<table cellpadding="1" bgcolor="dbcddc" width="100%">
<tr>
<th align="left" width="30%">Field</th>
<th align="left" width="10%">Value</th>
<th align="left" width="60%"><table border=1><tr><td bgcolor="FFFFFF">default</td><td bgcolor="FFFFCC">overwritten</td><td bgcolor="ADD8E6">user-defined</td></tr></table></th>
</tr>
<?php 
    $fldList = $_SESSION['FieldSearchResults'];
    
    $fldEntities = $fldList->getArray();
    if (is_null($fldEntities)) {
        echo "<tr><td colspan=3>No fields found for this PV.</td></tr>";
        
    } else {
        foreach ($fldEntities as $fldEntity) {
            
            // determine background color of field value display
            if ($fldEntity->getFldState() == "default")
                $color = "bgcolor=FFFFFF";
            else if ($fldEntity->getFldState() == "overwritten")
                $color = "bgcolor=FFFFCC";
            else if ($fldEntity->getFldState() == "user")
                $color = "bgcolor=ADD8E6";
                
            // if field value has a PV in it, format it with html anchor
            if (($fldEntity->getDbdType() == "DBF_INLINK" ||
                $fldEntity->getDbdType() == "DBF_OUTLINK" ||
                $fldEntity->getDbdType() == "DBF_FWDLINK") &&
                pv_utils_is_pure_pv($fldEntity->getFldVal())) {
                // break up pv into pv name and suffix (if any)
                $pvSubstrings = pv_utils_split_pv($fldEntity->getFldVal());
                $pvNameSubstring = $pvSubstrings[0];
                $pvSuffixSubstring = $pvSubstrings[1];
                $fieldValue = '<a href="action_pv_details.php?singlePV=true&pvName='.urlencode($pvNameSubstring).
                              '">'.$pvNameSubstring.'</a>'.$pvSuffixSubstring;
            } else {
                $fieldValue = $fldEntity->getFldVal();
            }
                
            echo "<tr>\n";   
            echo '<td valign="top" class="one">'.$fldEntity->getFldType()."</td>\n";
            echo '<td colspan="2" valign="top" class="one" '.$color.'>'.$fieldValue."</td>\n";
            echo "</tr>\n";
        }
    }

?>
</table><br>
<a name=" links">
<b>Links</b>
<table cellpadding="1" bgcolor="dbcddc" width="100%">
<tr><th align="left">PV Used by</th><th align="left">Type</th><th align="left">With Field Value</th></tr>
<?php
    $linkList = $_SESSION['LinkSearchResults'];
    $linkEntities = $linkList->getArray();
    if (is_null($linkEntities)) {
        echo "<tr><td colspan=3>No links found for this PV</td></tr>";
    } else {
        foreach ($linkEntities as $linkEntity) {
            echo "<tr\n";
            echo '<td valign="top" class="two"><a href="action_pv_details.php?singlePV=true&pvName='.
                  urlencode($linkEntity->getLinkRecName()).'">'.$linkEntity->getLinkRecname().'</a>.'.
                  $linkEntity->getLinkFldType()."</td>\n";
            echo '<td valign="top" class="two">'.$linkEntity->getLinkRecType()."</td>\n";
            echo '<td valign="top" class="two">'.$linkEntity->getLinkFldVal()."</td>\n";
            echo "</tr>\n";
        }    
    }
?>
</table>
