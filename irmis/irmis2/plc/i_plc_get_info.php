<?php
   function plcGetInfo($plcName, $iocName, $ref_pv, $codeVerStat, $err)
   {
   define('ERR_BOOTLOG',           1);
   define('ERR_PV_NOT_FOUND',      2);
   define('ERR_PLCNAME_NOT_FOUND', 3);
   define('ERR_LINE_NOT_FOUND',    4);
   define('ERR_CA_CONNECT',        5);

   $errList = array (  ' ',
                       'Could not open iocBootLog with iocBootLogHandle',   // $ref_pv
		       'PV not found in PLC line from st.cmd file',         // $ref_pv
                       'Found no line starting with PLC name in st.cmd file',   // $ref_pv
                       'PLC line not available in st.cmd file',             // $ref_pv
                       'Channel access failed to connect to pv'             // $codeVerStat
		    );


   // PLC data to acquire - initial values
   $err = NULL;
   // $ref_pv = 'Not Available';
   $codeVerStat = 'Not Available';
   $epicsDataStat = 'Not Available';

   if ($ref_pv == null || $ref_pv == ""){
      $err = ERR_PV_NOT_FOUND;
   }
   else{
      $err = 0;
   }

   // If pv found, do a caget of code version
   if (!$err) {
       $ca_request = '/bin/sh my_caget.sh '.$ref_pv.'.VAL';
       exec($ca_request, $codeVer, $ca_err);
       if (!$ca_err){
           // if integer, add trailer zero
           if (!strstr($codeVer[0],".")) {
               $codeVerStat = $codeVer[0].'.0';
           } else {
               $codeVerStat = $codeVer[0];
           }
       }
       else $err=ERR_CA_CONNECT;
   }

   switch ($err) {

       case ERR_BOOTLOG:
       case ERR_PV_NOT_FOUND:
       case ERR_PLCNAME_NOT_FOUND:
       case ERR_LINE_NOT_FOUND:
            $ref_pv = $errList[$err];
	    break;

       case ERR_CA_CONNECT:
            $codeVerStat = $errList[$err];
	    break;

       default:
            break;
   }
   }
?>
