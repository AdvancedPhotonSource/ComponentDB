<?
   //Initiates a "Save As" dialog box to prompt user to download file
	   //Create a proposed filename for the user:     viewName-YYYY-MM-DD.txt
    $filename = "/tmp/reportResultToSend.csv";
    $filename2 =$viewName."-".date(Y)."-".date(m)."-".date(d).".csv";
    
    //Initiate a "Save As" dialog box so the file can be downloaded to user
    header("Content-type: application/vnd.oasis.opendocument.spreadsheet");//Document type being returned
	header("Content-Disposition: attachment; filename = $filename2");
	readfile($filename); //Transfer server-side created file to user
?>