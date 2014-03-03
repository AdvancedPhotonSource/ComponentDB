<?
   //Initiates a "Save As" dialog box to prompt user to download file
	   //Create a proposed filename for the user:     viewName-YYYY-MM-DD.txt
    $filename = "/tmp/reportResultToSend.txt";

  	$filename2 =$viewName."-".date(Y)."-".date(m)."-".date(d).".txt";

   	header("Content-type: text/plain");  //Document type being returned
      header("Content-Disposition: attachment; filename = $filename2");
      readfile($filename); //Transfer server-side created file to user
?>