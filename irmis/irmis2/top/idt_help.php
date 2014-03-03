<?php 
    // IRMIS2 IDT Help Page
    include_once('i_common.php');
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>IRMIS2 IDT Help Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../common/irmis2.css" rel="stylesheet" type="text/css">
</head>
<body bg="#dddddd">
<?php include('../common/i_irmis_header.php'); ?>
<br><br><br><br>
<h2>IRMIS2 IDT Help Page</h2>
<h3>FAQ</h3>
<ol>
<li>What is the difference between the IRMIS Desktop (idt) and idt Test Area?<br>
The IRMIS Desktop (idt) is the production version of IRMIS. Any edits you make here are permanent and official. The idt Test Area brings up a version of the application clearly marked as a test version. You may play around here freely and practice any activity.
</li>
<li>When I click on one of the idt links, why does the web browser pop up a dialog box asking about a JNLP file?<br>
The idt application is a Java application delivered using something called Java WebStart. You need to tell your web browser what to do with the jnlp file. The dialog box allows you to find an executable that will be used to run the jnlp file. Find and select the executable on your system called javaws. For Solaris unix hosts, this will be /usr/java/jre/javaws.
</li>
<li>When I click on one of the idt links, I just get some kind of error dialog box.<br>
Go to your web browser preferences, and disable the use of any proxy server. Select "directly connect to the internet" instead. If it still doesn't work, you may have an old version of java on your system. IRMIS idt requires you have java 1.4.2. Contact IT to get that upgraded. If you have 1.4.1, that is too old. Java version 1.5 mostly works as well, but there may be some issues. You can check and modify what versions of java are used by running the javaws utility on your system. 
</li>
<li>Why do I get a dialog box asking if I trust an application from "Claude"?<br>
Java applications are digitally signed to prevent someone from spoofing the application. Click "yes" or "start" to accept the certificate, and the idt application will then start.
</li>
<li>Why does idt require that I login? I'm already logged in on my system.<br>
Java applications are isolated from your operating system, and know nothing about the user accounts of your workstation or PC. IRMIS needs to know who changes what, so the application must ask you to login separately.
</li>
<li>Even if I login, I still cannot edit any IRMIS data?<br>
Permissions to edit are controlled by an IRMIS administrator. Contact Ned Arnold if you need permission to edit.
</li>

</ol>
</body>
</html>
