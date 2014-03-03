<?php


	include_once ('i_common.php');
	include_once ('i_aoi_edit_common.php');
	include_once ('db.inc');


	// session_start();

	$_SESSION['user_name'] = "";
	$_SESSION['agent'] = "";

	// Server side of the user authenticate process
	// Retrieve LDAP user confirmation

	$file_success = 'aoi_authenticate_success.php';
	$file_failure = 'aoi_authenticate_failure.php';

	$url = 'http://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']);

	// Check for a trailing slash

	if ((substr($url, -1) == '/') OR (substr($url, -1) == '\\') ) {
		$url = substr($url,0,-1); // Chop off the slash
	}

	$url_success = $url.'/'.$file_success;
	$url_failure = $url.'/'.$file_failure;

	$user = $_GET['u'];
	$hash = $_GET['h'];
	$keep = $_GET['k'];

	$return_session_name = $_GET['c'];

	if (!empty($return_session_name)){

		session_name($return_session_name);
	}

	$date = floor(time() / 10);
	$addr = $_SERVER['REMOTE_ADDR'];

	$h1 = md5("$user-$addr-$date-$ldap_login_secret");

	$date--;
	$h2 = md5("$user-$addr-$date-$ldap_login_secret");

	if ($hash == $h1 || $hash == $h2) {
		// LDAP login succeeded

		// Perform IRMIS User Role verification
		// Use read-only database connection to the "role" table

   		$aoiEditorList = new AOIEditorList();

   		$_SESSION['aoiEditorConstraint'] = 'irmis:aoi-editor';

   		if (!$aoiEditorList->loadFromDB($conn, $_SESSION['aoiEditorConstraint'])) {
    	    		include('demo_error.php');
		    	echo "Could not load aoiEditorList from database.";
    	    		exit;
   		}

   		$searchresultslength = & $aoiEditorList->length();

   		// stuff the loaded aoiEditorList in the session, replacing the one
   		// that was there before

   		$_SESSION['aoiEditorList'] = & $aoiEditorList;

   		if ($searchresultslength > 0) {

   			//echo "Valid AOI Editors in IRMIS";

			// Confirm current user is one of the valid AOI editors in IRMIS

		    $aoiListEntities = $aoiEditorList->getArray();

		    $valid_aoi_editor = false;

		    foreach ($aoiListEntities as $aoiListEntity) {

				if ( strtolower($user) == strtolower( $aoiListEntity->getPersonUserID() ) ) {
					//echo "</br>".$user." User is a valid AOI Editor </br>";
					$valid_aoi_editor = true;
			    }
			}

			if ($valid_aoi_editor){

				// Retrieve IRMIS read/write user dbconnect credentials



				// establish connection to db (note: actually re-uses pooled connections)
				// args are: host, port, dbname, user, passwd, tableNamePrefix

				$dbConnManager = new DBConnectionManager($db_host,$db_port,$db_name_production_1,$db_user_aoi_read_write_name,$db_user_aoi_read_write_passwd,"");

				 if (!$conn = $dbConnManager->getConnection()) {
				 	  include('../common/db_error.php');
				   	  echo "<br>Could not connect to database. Please see IRMIS administrator.</br>";
				      exit;
				 }else{
					  // echo "<br>Connected to AOI Database.</br>";
				 }

				// Set up new PHP Session for user

				$_SESSION['user_name'] = $user;
				$_SESSION['agent'] = md5($_SESSION['user_name'].$_SERVER['HTTP_USER_AGENT']);


				// echo "session agent is: ".$_SESSION['agent']." ";

				// Redirect the user to AOI Editor page

				// include_once ('aoi_authenticate_success.php');

				session_write_close();

				include ('aoi_authenticate_success.php');


			}else{
				// User is not a valid AOI Editor

				header("Location: $url_failure");
				exit();
			}

		}else{
   			// No AOI Editors found in IRMIS User Roles

   			header("Location: $url_failure");
			exit();
   		}

	}else{
		// Login failed

		header("Location: $url_failure");
		exit();
	}
?>
