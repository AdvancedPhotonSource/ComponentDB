<?php 
   
/* Make sure given string is a process variable name as best we can 
 * determine. The DBF_INLINK, OUTLINK, and FWDLINK can also have 
 * arbitrary strings used by device support. For now, check to see
 * if link begins with # or @ or a digit. It's not a PV name in that case.
 */
function pv_utils_is_pure_pv($pvName) {
    // pv's can't begin with a digit
    if (ereg("^[0-9]+.*",$pvName,$regs)) {
        return false;
    }

    // make sure it's not a device support reference
    $pos1 = strpos($pvName, "#");
    $pos2 = strpos($pvName, "@");
    if ((is_long($pos1) && $pos1 == 0) ||
        (is_long($pos2) && $pos2 == 0) )
        return false;
    else
        return true;
}

/* Split a process variable name into two components, and return in 
 * an array of two strings. The first element should be the core pv
 * name. The second element is the field name followed by any modifiers
 * (ie. MS PP)
 */
function pv_utils_split_pv($pvName) {
    $pvSubstrings = array();
    $pos = strpos($pvName, ".");
    if (is_long($pos)) {
        $pvSubstrings[0] = substr($pvName, 0, $pos);
        $pvSubstrings[1] = substr($pvName, $pos);
    } else {
        $pos = strpos($pvName, " ");
        if (is_long($pos)) {
            $pvSubstrings[0] = substr($pvName, 0, $pos);
            $pvSubstrings[1] = substr($pvName, $pos); 
        } else {
            $pvSubstrings[0] = $pvName;
            $pvSubstrings[1] = "";
        }
    }
    return $pvSubstrings;
}
?>
