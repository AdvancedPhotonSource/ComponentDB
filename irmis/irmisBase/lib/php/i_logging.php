<?php     
/*
 * Simple log utility, which uses underlying PHP error_log function. Currently
 * only supports the 'debug' log level, but the idea is to allow various levels,
 * and a configurable logging 'ceiling'. 
 * 
 */

$DEBUG = true;
$DEBUG_FILE = "/tmp/debug.php";

function logEntry($level, $message) {
    global $DEBUG;
    global $DEBUG_FILE;
    if ($DEBUG) {
        $timeString = date("Ymd.Hms",time());
        $message = $timeString . "|" . $message . "\n";
        error_log($message,3,$DEBUG_FILE);
    }
}
function get_timing() {
    $start = microtime();
    $start = explode(" ", $start);
    $start = (float)$start[1] + (float)$start[0];
    return $start;
} 
?>