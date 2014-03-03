<div class="NetResultsRight">
  <table width="100%"  border="1" cellspacing="0" cellpadding="2">
  <?php 
    // null list in session implies that result size was exceeded
	$mcDetailsList = $_SESSION['mcDetailsList'];
		echo '<tr><td colspan="4"class=value>"'.$mcDetailsList->length().'" Modules for Media Converter "'.$_GET['mcID'].'"</td></tr>';
		echo '<tr>';
        echo '<th nowrap>Media Converter</th>';
	    echo '<th>Module</th>';
	    echo '<th>IOC</th>';
		echo '<th>Connection</th>';
        echo '</tr>';
    if ($mcDetailsList == null) {
        echo '<tr><td class="warning bold" colspan=4>Search produced too many results to display.<br>';
        echo 'Limit is 5000. Try using the Additional<br>';
        echo 'Search Terms to constrain your search.</td></tr>';      
         
    } else if ($mcDetailsList->length() == 0) {
        echo '<tr><td class="warning bold" colspan=4>No Media Converter Port\'s found: please try another search.</td></tr>';
        
    } else {
	        
        $mcDetailsEntities = $mcDetailsList->getArray();
        foreach ($mcDetailsEntities as $mcDetailsEntity) {
			
			if (strcasecmp($mcDetailsEntity->getmcPrimEnetMedConvCh(), $mcID) == 0) {
			echo '<td class="primary">'.$mcDetailsEntity->getmcPrimEnetMedConvCh().'</td>';
			//echo '<td class="resulttext">'.$mcDetailsEntity->getmcPrimMediaConvPort().'</td>';
			echo '<td class="resulttext">'.$mcDetailsEntity->getmcModule().'</td>';
			echo '<td class="resulttext"><a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$mcDetailsEntity->getmcConnection().'">'.$mcDetailsEntity->getmcConnection().'</a></td>';
			echo '<td><font class="pri">Primary</font></td>';
			echo '</tr>';
			}
			elseif (strcasecmp($mcDetailsEntity->getmcSecEnetMedConvCh(), $mcID) == 0) {
			echo '<td class="primary">'.$mcDetailsEntity->getmcSecEnetMedConvCh().'</td>';
			//echo '<td class="resulttext">'.$mcDetailsEntity->getmcSecMedConvPort().'</td>';
			echo '<td class="resulttext">'.$mcDetailsEntity->getmcModule().'</td>';
			echo '<td class="resulttext"><a class="hyper" href="../ioc/action_ioc_search.php?iocName='.$mcDetailsEntity->getmcConnection().'">'.$mcDetailsEntity->getmcConnection().'</a></td>';
			echo '<td><font class="sec">Secondary</font></td>';
			echo '</tr>';
			}
        }
    }
  ?>
  </table>
</div>
