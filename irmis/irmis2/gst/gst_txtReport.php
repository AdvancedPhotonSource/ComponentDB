<?
	header('Content-type: text/xml');
	$data = $_POST['sentData'];
	$dom = new domDocument;
	$dom->loadXML($data);
	$xml = simplexml_import_dom($dom);
	
	if ($xml) 
	{
		$majorCategoryID = $xml->majorCategoryID[0];
        $minorCategoryID = $xml->minorCategoryID[0];
        $searchString = $xml->searchString[0];
        $commentBoxValue = $xml->commentBoxValue[0];
        $headerString = $xml->headerString[0];
        $dataString = $xml->dataString[0];
	}
    else
    {
	    echo "ERROR could not parse xml string from sent data<br />";
	}
	
   //Create server-side filename. . . report will be created in this file
   //temporarily before transferring to user
   $filename = "/tmp/reportResultToSend.txt";

	//Open server-side file for writing generic information
   $fp = fopen($filename, "w") or die("Could not open file.");

   //Create a timestamp in this format:     YYYY-MM-DD HH:MM:SS
   $timestamp = date(Y)."-".date(m)."-".date(d)." ".date(H).":".date(i).":".date(s);
   
   //Write generic information that applies to all query views
   fwrite($fp, "IRMIS2: Integrated Relational Model of Installed Systems\r\n");
   fwrite($fp, "Global Search Report\r\n");
   fwrite($fp, "Date Generated: $timestamp\r\n");
   fwrite($fp, "\r\n");  
   fwrite($fp, "Major Category: $majorCategoryID\r\nMinor Category: $minorCategoryID\r\n");
   fwrite($fp, "Search String: $searchString\r\n");
   fwrite($fp, "Comments: $commentBoxValue\r\n\r\n");  
    
   //Column headers
   $headersArray = explode(",,,", $headerString);
   for ($i = 0; $i < count($headersArray); $i++)
   {
	   if (!ereg('ID', $headersArray[$i]))
	   {
		   fwrite($fp, "$headersArray[$i]\t");
	   }
   }
   fwrite($fp, "\r\n\r\n");
   
   //DATA
   $rows = explode(":::", $dataString);

   for ($i = 0; $i < count($rows); $i++)
   {
	   $data = explode(",,,", $rows[$i]);
	   for ($k = 0; $k < count($data); $k++)
	   {
		   if (!ereg('ID', $headersArray[$k]))
		   {
			   //fwrite($fp, "$headersArray[$i]\t");
			   fwrite($fp, "\"".$data[$k]."\"\t");
		   }		  
	   }
	   fwrite($fp, "\r\n");
   }     
    
   //Close the file pointer, since the report is now complete
   fclose($fp);
?>
	