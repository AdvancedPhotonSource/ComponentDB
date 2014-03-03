<script language="JavaScript">
<!--
	// build up JavaScript arrays needed to drive widgets
	
	// for ioc name select
	iocList=new Array();
	
	<?php 
		$iocIdx = 0;
		$iocBootEntities = $_SESSION['iocBootList']->getArray();
        foreach ($iocBootEntities as $iocBootEntity) {
            echo "    iocList[".$iocIdx++."]=\"".$iocBootEntity->getIocName()."\";\n";
        }
    ?>
	
	// for db file select
	iocDBFiles=new Array();
	<?php
		$iocIdx = 0;
		$iocBootEntities = $_SESSION['iocBootList']->getArray();

		foreach ($iocBootEntities as $iocBootEntity) {
			$iocResourceEntities = $iocBootEntity->getIocResourceEntities();
			$fileIdx = 0;
			echo "   iocDBFiles[".$iocIdx."]=new Array();\n";
			foreach ($iocResourceEntities as $iocResourceEntity) {
					// use regex to extract trailing file name only (exclude full path)
			        ereg("([^\/]+\.db|[^\/]+\.template)$",$iocResourceEntity->getDBFile(),$regs);
			        $fileName = $regs[0];
					echo "   iocDBFiles[" . $iocIdx . "][" . $fileIdx . "]=new Option(\"" . 
					      $fileName . "\",\"" . $iocResourceEntity->getIocResourceID() . "\");\n";
					$fileIdx = $fileIdx + 1;
			}
			$iocIdx = $iocIdx + 1;
		}
	?>
	// to map between ioc resource id in above options, and actual option array index
	iocDBFileChoices=new Array();
	<?php
	    $arrIdx = 0;
		$dbFileDropDownSelections = $_SESSION['PVSearchChoices']->getDBFileChoices();
		foreach ($dbFileDropDownSelections as $key=>$value) {
		    echo "  iocDBFileChoices[" . $arrIdx . "]=\"" . $value . "\";\n";
		    $arrIdx++;
		}
	?>
	
	// for record type select
	recordTypes=new Array();
	<?php
		$recTypeEnum = new RecordTypeEnum();
		for ($i=0 ; $i < $recTypeEnum->length(); $i++) {
			echo "    recordTypes[".$i."]=\"".$recTypeEnum->find($i)."\";\n";
		}
	?>

// Prepopulates any select elements on first page viewing.
function initializeSelects() {
	setIOCSelect();
	// setDBFileSelect(); not fully functional, so commenting out
	setRecTypeSelect();
}

// Populates ioc select 
function setIOCSelect() {

	// need this goofy element lookup due to how PHP handles multiple selects
	var iocDropDown = document.pvSearchForm.elements['iocDropDown[]'];
	iocDropDown.options[0] = new Option("---All---","-1");
	for (m=1;m<=iocList.length;m++)
		iocDropDown.options[m]=new Option(iocList[m-1],m-1);
		
	// preselect ioc choices from session
	<?php
		$iocDropDownSelections = $_SESSION['PVSearchChoices']->getIOCChoices();
		foreach ($iocDropDownSelections as $key=>$value) {
			// adjust for offset due to "---All---" choice
			$value++;
			echo "iocDropDown.options[".$value."].selected=true;";
		}
	?>
}

// Populates db file select based on ioc selection(s)
function setDBFileSelect() {
	// need these goofy element lookups due to how PHP handles multiple selects
	var dbFileDropDown = document.pvSearchForm.elements['dbFileDropDown[]'];
	var iocDropDown = document.pvSearchForm.elements['iocDropDown[]'];
	var dbFileSelectIdx = 1;
	dbFileDropDown.options.length=0;
	
	dbFileDropDown.options[0] = new Option("---All---","-1");
	for (g=0;g<iocDBFileChoices.length;g++) {
	    if (iocDBFileChoices[g] == "-1")
	        dbFileDropDown.options[0].selected = true;
	}
	
	// iterate over ioc select box to find all selections
	for (s=0;s<iocDropDown.length;s++) {
		if (iocDropDown.options[s].selected == true || 
		    iocDropDown.options[0].selected == true) {
		    if (s == 0)
		    	continue;
			// add files for this ioc to db file drop down
			filesArray = iocDBFiles[s-1];
			for (f=0;f<filesArray.length;f++) {
				dbFileDropDown.options[dbFileSelectIdx]=filesArray[f];
				// if second attribute of Option is in iocDBFileChoices, set selected to true
				choiceLoadLineId = filesArray[f].value;
				selectIt = false;
				for (g=0;g<iocDBFileChoices.length;g++) {
				    if (choiceLoadLineId == iocDBFileChoices[g])
				        selectIt = true;
				}
				if (selectIt && !dbFileDropDown.options[0].selected)
				    filesArray[f].selected = true;
				    
				dbFileSelectIdx = dbFileSelectIdx + 1;
			}
		}
	}
}

function setRecTypeSelect() {
	// need this goofy element lookup due to how PHP handles multiple selects
	var recTypeDropDown = document.pvSearchForm.elements['recTypeDropDown[]'];
	
	// prepopulate drop down 
	recTypeDropDown.options[0] = new Option("---All---","-1");
	for (m=1; m <= recordTypes.length; m++)
		recTypeDropDown.options[m] = new Option(recordTypes[m-1], m-1);
		
	// preselect record type choices from session (if any)
	<?php
		$recTypeDropDownSelections = $_SESSION['PVSearchChoices']->getRecTypeChoices();
		if (is_array($recTypeDropDownSelections)) {
			foreach ($recTypeDropDownSelections as $key=>$value) {
				// adjust for offset due to "---All---" choice
				$value++;
				echo "recTypeDropDown.options[".$value."].selected=true;";
			}
		}
	?>	
}

//-->
</script>