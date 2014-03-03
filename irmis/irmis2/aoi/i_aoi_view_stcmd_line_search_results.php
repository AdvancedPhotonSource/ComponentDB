<div class="searchEditResultsStCmdLine">
  <table width="99%"  border="1" cellspacing="0" cellpadding="2">
<?php

    $aoiStCmdLineList = $_SESSION['aoiStCmdLineList'];

    // echo '<tr><td class="sectionHeaderCbox" width = "20%"><input type = "checkbox" name = selected[] value = "StCmdLines">Save Results</td>';

    if ($aoiStCmdLineList->length() == 0) {
     echo '<td class="sectionHeaderCbox" align = "center" colspan="2"><b>No AOI st.cmd Lines Found</b></td>';
    }
    else
    {
      echo '<td class = "sectionHeaderCbox" align = "center" colspan="2"><b>AOI st.cmd Lines</b></td>';
    }


	echo '<tr> <td class="value" width="20%">IOC Name</td> <td class="value" >st.cmd Line</td> </tr>';

    $aoiStCmdLineEntities = $aoiStCmdLineList->getArray();

    foreach ($aoiStCmdLineEntities as $aoiStCmdLineEntity) {

            $temp_stcmd_line = htmlentities($aoiStCmdLineEntity->getIOCStCmdLine());

			echo '<tr> <td width="20%">'.$aoiStCmdLineEntity->getIOCName().'</td> <td>'.$temp_stcmd_line.'</td> </tr>';
	}

?>
</table>
</div>

