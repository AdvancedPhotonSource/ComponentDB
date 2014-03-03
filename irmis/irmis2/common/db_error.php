<?php 
    // DB Error Page
?>
<html>
<head>
<title>DB Error Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<div class="header">
<table width="770" border="0">
  <tr>
    <td>&nbsp;</td>
    <td><a href="../top/irmis2.php"><img src="images/irmis2Logo.png" 
name="irmis2Logo" width="160" height="85" align="absmiddle" id="irmis2Logo"></a>
<img src="images/irmis2Text.png" name="irmis2Text" align="absmiddle" id="irmis2Text"></td>
  </tr>
</table>


</div>
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
