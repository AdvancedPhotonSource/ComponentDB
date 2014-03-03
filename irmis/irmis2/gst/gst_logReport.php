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
        $numberResultsSearch = $xml->numberResultsSearch[0];
        $numberResultTabData = $xml->numberResultTabData[0];
	}
    else
    {
	    echo "ERROR could not parse xml string from sent data<br />";
	}
    
   //Create server-side filename. . . 
   $filename = "/tmp/log.txt";

	//Open server-side file for writing generic information
   $fp = fopen($filename, "a") or die("Could not open file.");

   //Create a timestamp in this format:     YYYY-MM-DD HH:MM:SS
   $timestamp = date(Y)."-".date(m)."-".date(d)." ".date(H).":".date(i).":".date(s);
   
   //Write generic information that applies to all query views
   fwrite($fp, "\r\n==ERROR REPORT\r\n");   
   fwrite($fp, "==Date Generated: $timestamp\r\n"); 
   fwrite($fp, "Major Category\tMinor Category\tSearch String\tSearch Results\tData Results \r\n");
   fwrite($fp, "$majorCategoryID\t\t$minorCategoryID\t\t$searchString\t\t$numberResultsSearch\t\t$numberResultTabData\t\r\n");

   fclose($fp);
?>