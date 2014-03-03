<?php

	// Six functions to modify PHP's native session mechanism with the
	// session_set_save_handler() function.
	// _open
	// _close
	// _read
	// _write
	// _destroy
	// _clean

	function _open()
	{
		global $_sess_db;

		$db_user = $_SERVER['DB_USER'];
		$db_pass = $_SERVER['DB_PASS'];

		$db_host = 'bacchus';
		$db_database = 'irmis';


		if ($_sess_db = mysql_connect($db_host, $db_user, $db_pass))
		{
			return mysql_select_db($db_database, $_sess_db);
		}

	    echo '<br>Not able to connect to database host '.$db_host.'</br>';

		return FALSE;

	}

	function _close()
	{
		global $_sess_db;

		return mysql_close($_sess_db);

	}

	function _read($php_session_id)
	{

		global $_sess_db;

		$php_session_id = mysql_real_escape_string($php_session_id);

		$sql = "SELECT data FROM php_session WHERE php_session_id = '$php_session_id'";

		if ($result = mysql_query($sql, $_sess_db))
		{
			if (mysql_num_rows($result))
			{
				$record = mysql_fetch_assoc($result);

				return $record['data'];
			}
		}

		return '';
	}

	function _write($php_session_id, $data)
	{
		global $_sess_db;

		$access = time();
		$php_session_id = mysql_real_escape_string($php_session_id);
		$access = mysql_real_escape_string($access);
		$data = mysql_real_escape_string($data);

		$sql = "REPLACE INTO php_session VALUES ('$php_session_id', '$access', '$data')";

		return mysql_query($sql, $_sess_db);

	}

	function _destroy($php_session_id)
	{
		global $_sess_db;

		$php_session_id = mysql_real_escape_string($php_session_id);

		$sql = "DELETE FROM php_session WHERE php_session_id = '$php_session_id'";

		return mysql_query($sql, $_sess_db);
	}

	function _clean($max)
	{
		global $_sess_db;

		$old = time() - $max;
		$old = mysql_real_escape_string($old);

		$sql = "DELETE FROM sessions WHERE access < '$old'";

		return mysql_query($sql, $_sess_db);
	}

 ?>