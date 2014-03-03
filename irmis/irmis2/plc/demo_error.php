<?php 
    // PV Viewer Error Page
?>
<html>
<head>
<title>PLC Error Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<h3><font color="red">Error in application. Unable to continue.</font></h3>
<?php
    if ($errno != null and $error != null) {
        echo "<p>".$errno.":".$error."</p>\n";    
    } else {
        echo "<p>No error code (errno) or message (error) set by application. Unknown error.</p>\n";
    }
?>
</body>
</html>
