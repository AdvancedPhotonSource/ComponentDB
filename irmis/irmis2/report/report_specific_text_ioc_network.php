<?php
	  $iocList = $_SESSION["iocList"];
	  $iocName = $_SESSION["iocName"];
	  $iocEntity = $iocList->getElementForIocName($iocName);

     //The file pointer $fp was already declared in report_generic_text.php


	  fwrite($fp, "\r\n\r\n\r\n");

      //IOC NAME
      fwrite($fp, "==IOC Name==\r\n".$iocEntity->getIocName()."\r\n\r\n");

      //TERMINAL SERVER RACK NUMBER
	  fwrite($fp, "==Terminal Server Rack Number==\r\n".$iocEntity->getTermServRackNo()."\r\n\r\n");

      //TERMINAL SERVER NAME
	  fwrite($fp, "==Terminal Server Name==\r\n".$iocEntity->getTermServName()."\r\n\r\n");

      //TERMINAL SERVER PORT
	  fwrite($fp, "==Terminal Server Port==\r\n".$iocEntity->getTermServPort()."\r\n\r\n");

      //TERMINAL SERVER FIBER CONVERTER CHASSIS
	  fwrite($fp, "==Terminal Server Fiber Converter Chassis==\r\n".$iocEntity->getTermServFiberConvCh()."\r\n\r\n");

      //TERMINAL SERVER FIBER CONVERTER PORT
	  fwrite($fp, "==Terminal Server Fiber Converter Port==\r\n".$iocEntity->getTermServFiberConvPort()."\r\n\r\n");

      //PRIMARY ETHERNET SWITCH RACK NUMBER
	  fwrite($fp, "==Primary Ethernet Switch Rack Number==\r\n".$iocEntity->getPrimEnetSwRackNo()."\r\n\r\n");

      //PRIMARY ETHERNET SWITCH
	  fwrite($fp, "==Primary Ethernet Switch==\r\n".$iocEntity->getPrimEnetSwitch()."\r\n\r\n");

      //PRIMARY ETHERNET BLADE
	  fwrite($fp, "==Primary Ethernet Blade==\r\n".$iocEntity->getPrimEnetBlade()."\r\n\r\n");

      //PRIMARY ETHERNET PORT
	  fwrite($fp, "==Primary Ethernet Port==\r\n".$iocEntity->getPrimEnetPort()."\r\n\r\n");

      //PRIMARY ETHERNET MEDIA CONVERTER CHASSIS
	  fwrite($fp, "==Primary Ethernet Media Converter Chassis==\r\n".$iocEntity->getPrimEnetMedConvCh()."\r\n\r\n");

      //PRIMARY ETHERNET MEDIA CONVERTER PORT
	  fwrite($fp, "==Primary Ethernet Media Converter Port==\r\n".$iocEntity->getPrimMediaConvPort()."\r\n\r\n");

      //SECONDARY ETHERNET SWITCH RACK NUMBER
	  fwrite($fp, "==Secondary Ethernet Switch Rack Number==\r\n".$iocEntity->getSecEnetSwRackNo()."\r\n\r\n");

      //SECONDARY ETHERNET SWITCH
	  fwrite($fp, "==Secondary Ethernet Switch==\r\n".$iocEntity->getSecEnetSwitch()."\r\n\r\n");

      //SECONDARY ETHERNET BLADE
	  fwrite($fp, "==Secondary Ethernet Blade==\r\n".$iocEntity->getSecEnetBlade()."\r\n\r\n");

      //SECONDARY ETHERNET PORT
	  fwrite($fp, "==Secondary Ethernet Port==\r\n".$iocEntity->getSecEnetPort()."\r\n\r\n");

      //SECONDARY ETHERNET MEDIA CONVERTER CHASSIS
	  fwrite($fp, "==Secondary Ethernet Media Converter Chassis==\r\n".$iocEntity->getSecEnetMedConvCh()."\r\n\r\n");

      //SECONDARY ETHERNET MEDIA CONVERTER PORT
	  fwrite($fp, "==Secondary Ethernet Media Converter Port==\r\n".$iocEntity->getSecMedConvPort()."\r\n\r\n");


?>
