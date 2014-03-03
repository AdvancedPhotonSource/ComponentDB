<?php
	  $iocList = $_SESSION["iocList"];
	  $iocName = $_SESSION["iocName"];
	  $iocEntity = $iocList->getElementForIocName($iocName);

     //The file pointer $fp was already declared in report_generic_text.php

     $delim = ",";

     fwrite($fp, "\r\n\r\n\r\n");


      fwrite($fp, "IOC Name".$delim.$iocEntity->getIocName()."\r\n");



	  fwrite($fp, "Terminal Server Rack Number".$delim.$iocEntity->getTermServRackNo()."\r\n");


	  fwrite($fp, "Terminal Server Name".$delim.$iocEntity->getTermServName()."\r\n");


	  fwrite($fp, "Terminal Server Port".$delim.$iocEntity->getTermServPort()."\r\n");


	  fwrite($fp, "Terminal Server Fiber Converter Chassis".$delim.$iocEntity->getTermServFiberConvCh()."\r\n");


	  fwrite($fp, "Terminal Server Fiber Converter Port".$delim.$iocEntity->getTermServFiberConvPort()."\r\n");


	  fwrite($fp, "Primary Ethernet Switch Rack Number".$delim.$iocEntity->getPrimEnetSwRackNo()."\r\n");


	  fwrite($fp, "Primary Ethernet Switch".$delim.$iocEntity->getPrimEnetSwitch()."\r\n");


	  fwrite($fp, "Primary Ethernet Blade".$delim.$iocEntity->getPrimEnetBlade()."\r\n");


	  fwrite($fp, "Primary Ethernet Port".$delim.$iocEntity->getPrimEnetPort()."\r\n");


	  fwrite($fp, "Primary Ethernet Media Converter Chassis".$delim.$iocEntity->getPrimEnetMedConvCh()."\r\n");


	  fwrite($fp, "Primary Ethernet Media Converter Port".$delim.$iocEntity->getPrimMediaConvPort()."\r\n");


	  fwrite($fp, "Secondary Ethernet Switch Rack Number".$delim.$iocEntity->getSecEnetSwRackNo()."\r\n");


	  fwrite($fp, "Secondary Ethernet Switch".$delim.$iocEntity->getSecEnetSwitch()."\r\n");


	  fwrite($fp, "Secondary Ethernet Blade".$delim.$iocEntity->getSecEnetBlade()."\r\n");


	  fwrite($fp, "Secondary Ethernet Port".$delim.$iocEntity->getSecEnetPort()."\r\n");


	  fwrite($fp, "Secondary Ethernet Media Converter Chassis".$delim.$iocEntity->getSecEnetMedConvCh()."\r\n");


	  fwrite($fp, "Secondary Ethernet Media Converter Port".$delim.$iocEntity->getSecMedConvPort()."\r\n");


?>
