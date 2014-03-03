
// Set up global variables

var defaultSearchStringSize = 2;

// This string is hardcoded in the html as the input text value.
var defaultTextBoxValue = 'Enter Search String';

// EPICS RECORD has to be last because in the function updateDisplay, it skips the last index of the array in order to not search that category.
// These values in the array need to be the same for the variables: minorCategory, headers, webPage, and webVariable.
// The first index is the string 'ALL'. This is hardcoded in the html as a variable sent to a function. Also, it is hardcoded in criteria.php.

var Category = new Array ('ALL', 'IMS', 'AOI', 'INSTALLED COMPONENTS', 'IOC', 'PLC', 'COMPONENT TYPE', 'ICMS', 'HISTORY EPICS RECORD', 'EPICS RECORD');

var majorCategory = new Array(Category.length);
var minorCategory = new Array(Category.length);
var details = new Array(Category.length);
var headers = new Array(Category.length);
var criteriaString = "";
var webPage = new Array(Category.length);
var webVariable = new Array(Category.length);
var criteriaPlaceHolderID = 'column';

function init(searchTabID, minorCategorySectionID, ALL, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, majorCategorySectionID, helpSectionID)
{
	// Initialize all information for all the categories.
	// Even though there is an index ALL for each one, it only exists for headers and webPage because they need
	// to exist otherwise an error would occur during the intialization forloop.

	// The arrays represent the text in the select boxes. They should stay capatilized.
	// Everything in these arrays are hardcoded in tabData.php and criteriaSearchResults.php and tableData.php.
	// The string '--ALL--' is also hardcoded in FilterDataTab to skip the Log File.
	// The index for '--ALL--' is referenced(hardcoded) in the function displayCriteriaResultsAll and checkAll.
	minorCategory['ALL'] = new Array ('IMS', 'AOI', 'INSTALLED COMPONENTS', 'IOC', 'PLC', 'COMPONENT TYPE', 'ICMS', 'HISTORY EPICS RECORD', 'EPICS RECORD');
	minorCategory['AOI'] = new Array ('--ALL--', 'AOI NAME', 'MACHINE', 'TECHNICAL SYSTEM', 'DESCRIPTION', 'IOC NAME', 'COGNIZANT PERSON', 'CRITICALITY', 'STATUS', 'KEYWORDS', 'CUSTOMER GROUP');
	minorCategory['IOC'] = new Array ('--ALL--', 'IOC NAME', 'STATUS', 'SYSTEM', 'LOCATION', 'COGNIZANT DEVELOPER', 'COGNIZANT TECHNICIAN');
	minorCategory['PLC'] = new Array ('--ALL--', 'PLC NAME', 'DESCRIPTION', 'LOCATION', 'IOC NAME');
	minorCategory['COMPONENT TYPE'] = new Array ('--ALL--', 'COMPONENT TYPE', 'DESCRIPTION', 'MANUFACTURER', 'FORM FACTOR', 'FUNCTION', 'COGNIZANT PERSON');
	minorCategory['EPICS RECORD'] = new Array ('--ALL--', 'PV NAME', 'ALIAS NAME');
	minorCategory['HISTORY EPICS RECORD'] = new Array ('--ALL--', 'PV NAME', 'ALIAS NAME');
	minorCategory['INSTALLED COMPONENTS'] = new Array ('--ALL--', 'INSTALLED COMPONENT NAME', 'COMPONENT TYPE', 'FORM FACTOR', 'FUNCTION');
	minorCategory['ICMS'] = new Array ('--ALL--', 'TITLE', 'AUTHOR', 'COMMENTS', 'CONTENT ID');
	minorCategory['IMS'] = new Array ('--ALL--', 'SERVICE NAME', 'COMMENTS', 'STATUS');

	// The order in each index of the header has to be in the same order as when the data is returned from tableData.php.
	// Also, the id should always be the first index because it is referenced in the function FilterDataTab.
	// The strings 'Description' and 'IOC Name(s)' are hardcoded in filterTable. Caps do matter. 
	// The strings with 'ID' have to have that and have to be in caps since is hardcoded in the functions FilterDataTab
	// and tableData.php for a regular expression to skip those from displaying in the table. The values in the headers array don't need
	// to have anything in common withe the search criteria. They both can be different from each other.
	headers['ALL'] = new Array();
	headers['AOI'] = new Array('AOI ID', 'AOI Name', 'Description', 'Cognizant1', 'Cognizant2', 'Criticality', 'IOC Name(s)', 'Customer Group', 'Status', 'Keyword');
	headers['IOC'] = new Array('IOC ID', 'IOC Name', 'Status', 'System', 'Location', 'Cognizant Developer', 'Cognizant Technician');
	headers['PLC'] = new Array('PLC ID', 'PLC Name', 'Description', 'Location', 'IOC Name');
	headers['COMPONENT TYPE'] = new Array('Component Type ID', 'Component Type',  'Description', 'Manufacturer', 'Form Factor', 'Function(s)', 'Cognizant Person', 'Jump ID');
	headers['EPICS RECORD'] = new Array('PV ID', 'PV Name', 'Alias Names', 'Record Type', 'System', 'IOC Name', 'AOI(s)');
	headers['HISTORY EPICS RECORD'] = new Array('PV ID', 'PV Name', 'PV Alias Names', 'IOC Name', 'First Boot Date for PV', 'Last Boot Date for PV', 'Subsequent Boot Date for IOC', 'AOI(s)');
	headers['ICMS'] = new Array('ICMS ID', 'ICMS Name', 'Title', 'Author', 'Comments');
	
	headers['IMS'] = new Array('IMS ID', 'IMS Service Name', 'Service Comments', 'Service Status'); 
	
	headers['INSTALLED COMPONENTS'] = new Array('Installed Components ID', 'Component Field Name',  'Component Type', 'IOC Parent', 'Housing Parent', 'Room Parent', 'Building Parent', 'Form Factor', 'Function(s)', 'Component Type ID', 'Jump ID');

	// Each one of these arrays need to be the same length as the headers array.
	// This is because the website given in the array at that index is the corresponding website for the header value in the 
	// headers array at the same index. 
	// For example the aoi name is index 1 in headers[AOI] so the webpage to go to the AOI viewer and search
	// on that name is in the webPage array at index 1.
	// The starting and ending quotes represent an empty string which are hardcoded in FilterDataTable.
	webPage['ALL'] = new Array();
	webPage['AOI'] = new Array('', '../aoi/aoi_edit_basic_search_results.php', '', '', '', '', '', '', '', '');
	webPage['IOC'] = new Array('', '../ioc/action_ioc_search.php', '', '', '', '', '');
	webPage['PLC'] = new Array('', '../plc/action_plc_search.php', '', '', '../ioc/action_ioc_search.php');
	webPage['COMPONENT TYPE'] = new Array('', '../components/action_comp_search.php', '', '', '', '', '', '');
	webPage['EPICS RECORD'] = new Array('', '', '', '', '', '../ioc/action_ioc_search.php', '../aoi/action_aoi_edit_search.php');
	webPage['HISTORY EPICS RECORD'] = new Array('', '', '', '../ioc/action_ioc_search.php', '', '', '',  '../aoi/action_aoi_edit_search.php');
	webPage['INSTALLED COMPONENTS'] = new Array('', '', '../components/action_comp_search.php', '', '', '', '', '', '', '', '');
	webPage['ICMS'] = new Array('', 'https://icmsdocs.aps.anl.gov/new_docs/idcplg', '', '', '');
	
	webPage['IMS'] = new Array('', '', '', '');

	// Make a new array for each webpage in each category.

    for (var i = 0; i < webVariable.length; i++)
    {
	 	webVariable[Category[i]] = new Array(webPage[Category[i]].length)
    }

    // For each website in the webpage array for each category name, there needs to be a corresponding webVariable two-dimensional array
    // to go along with it. The array created at this index gives the variable to send in the url and the data to send with it. The numbers
    // represent an index of the headers array value. This is because the data returned in tableData.php is saved in the same order as the 
    // headers array values. So for example aoi id is saved as index 0 of the data array. So the index 1 below for aoiName is the same index 
    // in the headers array for AOI Name.    
    // The order is important. The first variable needs to have the '?' and the rest need '&'.
	webVariable['AOI']['../aoi/aoi_edit_basic_search_results.php'] = new Array('?aoiName=', 1, '&aoiId=', 0);
	webVariable['IOC']['../ioc/action_ioc_search.php'] = new Array('?iocNameConstraint=', 1);
	webVariable['PLC']['../plc/action_plc_search.php'] = new Array('?plcNameConstraint=', 1);
	webVariable['PLC']['../ioc/action_ioc_search.php'] = new Array('?iocNameConstraint=', 4);
	webVariable['COMPONENT TYPE']['../components/action_comp_search.php'] = new Array('?ctID=', 0, '&jmp=', 7);
	webVariable['HISTORY EPICS RECORD']['../aoi/action_aoi_edit_search.php'] = new Array('?pv_search=', 1);
	webVariable['HISTORY EPICS RECORD']['../ioc/action_ioc_search.php'] = new Array('?iocNameConstraint=', 3);
	webVariable['EPICS RECORD']['../aoi/action_aoi_edit_search.php'] = new Array('?pv_search=', 1);
	webVariable['EPICS RECORD']['../ioc/action_ioc_search.php'] = new Array('?iocNameConstraint=', 5);
	
	webVariable['INSTALLED COMPONENTS']['../components/action_comp_search.php'] = new Array('?ctID=', 9, '&jmp=', 10);
	
	webVariable['ICMS']['https://icmsdocs.aps.anl.gov/new_docs/idcplg'] = new Array('?IdcService=DISPLAY_URL&dDocName=', 1);

	// Create a new object for each Category and intialize new variables.

	for (var i = 0; i < majorCategory.length; i++)
	{
		majorCategory[Category[i]] = new Object();
		majorCategory[Category[i]].usedCriteria = "";
		majorCategory[Category[i]].minorCategory = minorCategory[Category[i]];
		majorCategory[Category[i]].headers = headers[Category[i]];
		majorCategory[Category[i]].criteriaSearchResultsPlaceHolderID = new Array();
		majorCategory[Category[i]].usedCriteriaResults = new Array();
		majorCategory[Category[i]].oldSearchString = '';
		majorCategory[Category[i]].columnNumber = criteriaPlaceHolderID + (i-1);
		majorCategory[Category[i]].webPage = webPage[Category[i]];
	}

	// Make a string with all categories together to send over to a php script.

	var x;
	var criteriaArray = new Array();
	var counter = 0;
	for (x in majorCategory)
	{
		criteriaArray[counter] = x + "," + majorCategory[x].minorCategory.join();
		counter++;
	}
	criteriaString = criteriaArray.join(":");

	// Set search tab to active.

	document.getElementById(searchTabID).className = 'selectedTab';

	// Set search box active.

	document.getElementById(searchBoxID).focus();

	// Set search box text to red.

	document.getElementById(searchBoxID).style.color = 'red';
	
	// Make the help section hidden
	
	document.getElementById(helpSectionID).style.visibility = 'hidden';	

	// Insert span for every search type, needed for Firefox.
	// Otherwise, Firefox will refresh select boxes everytime.

	var minorCategoryObject = document.getElementById(minorCategorySectionID);
	var minorString = "";
	for (var i = 0; i < Category.length -1; i++)
	{
		var column = criteriaPlaceHolderID + i;
		minorString += "<span id='" + column + "' class='criteria'></span>";
	}
	minorCategoryObject.innerHTML = minorString;
	
	updateDisplayCheck(ALL, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, majorCategorySectionID, searchTabID);
}

function clearSearch(searchBoxID)
{
	var searchBoxObject = document.getElementById(searchBoxID);
	if (searchBoxObject.value == defaultTextBoxValue)
	{
		searchBoxObject.value = "";
	}
}

function updateSearch(searchBoxID)
{
	var searchBoxObject = document.getElementById(searchBoxID);

	if (searchBoxObject.value.length == 0)
	{
		searchBoxObject.value = defaultTextBoxValue;
	}
}

var criteriaPHP = 'gst_criteria.php';
var criteriaSearchResultsPHP = 'gst_criteriaSearchResults.php';
var tabDataPHP = 'gst_tabData.php';
var tableDataPHP = 'gst_tableData.php';
var csvReportPHP = 'gst_csvReport.php';
var csvOpenReportFilePHP = 'gst_csvOpenReportFile.php';
var txtReportPHP = 'gst_txtReport.php';
var txtOpenReportFilePHP = 'gst_txtOpenReportFile.php';
var logReportPHP = 'gst_logReport.php';
var helpPHP = 'gst_help.php';

function fetchData(url, dataToSend, placeID)
{
	var pageRequest = false;

	if (window.XMLHttpRequest) pageRequest = new XMLHttpRequest();
	else if (window.ActiveXObject)  pageRequest = new ActiveXObject("Microsoft.XMLHTTP");
	else return false;

	if (dataToSend)
	{
		var sendData = "sentData=" + dataToSend;
		if (url == criteriaPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataCriteria(pageRequest, placeID);}
		}
		else if (url == criteriaSearchResultsPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataCriteriaResults(pageRequest, placeID);}
		}
		else if (url == tabDataPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataTab(pageRequest, placeID);}
		}
		else if (url == tableDataPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataTable(pageRequest, placeID);}
		}
		else if (url == csvReportPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataReport(pageRequest, placeID, csvOpenReportFilePHP);}
		}
		else if (url == txtReportPHP)
		{
			pageRequest.onreadystatechange = function() {filterDataReport(pageRequest, placeID, txtOpenReportFilePHP);}
		}
		else if (url == logReportPHP)
		{
		}
		pageRequest.open('POST', url, true);
		pageRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		pageRequest.send(sendData);
    }
    else
    {
		pageRequest.onreadystatechange = function() {filterData(pageRequest, placeID);}
	    pageRequest.open('GET', url, true);
	    pageRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	    pageRequest.send(null);
    }
}

function filterData(pageRequest, placeID)
{
	var placeObject = document.getElementById(placeID);
	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		placeObject.innerHTML = pageRequest.responseText;
	}
	else if (pageRequest.readyState == 4 && pageRequest.status == 404)
	{
		placeObject.innerHTML = 'Sorry, there seems to be some kind of problem.';
	}
}

function filterDataCriteria(pageRequest, placeID)
{
	var placeObject = document.getElementById(placeID);
	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		//placeObject.innerHTML = pageRequest.responseText;	// For debugging
		
		var xmlResponse = pageRequest.responseXML;
		
		//Check for an xml response
		if (xmlResponse.documentElement)
		{
			var xmlRoot = xmlResponse.documentElement;
			
			// Check to make sure all needed tags have the values
			if ( xmlRoot.getElementsByTagName('selectBox')[0].hasChildNodes() )
			{
				var selectBox = xmlRoot.getElementsByTagName('selectBox')[0].firstChild.data;
				placeObject.innerHTML += selectBox;
			}
			else
			{
				placeObject.innerHTML = "NO XML RESULTS RETURNED";
			}
		}
		else
		{
			placeObject.innerHTML = "XML FAILED";
		}
	}
	else if (pageRequest.readyState == 4 && pageRequest.status == 404)
	{
		placeObject.innerHTML = 'Sorry, there seems to be some kind of problem.';
	}
}

function filterDataCriteriaResults(pageRequest, placeID)
{
	var placeObject = document.getElementById(placeID);

	if (pageRequest.readyState == 0)
	{
		if (placeObject)
		{
			placeObject.innerHTML = 'Fetching Data<BR>';
		}
	}
	if (pageRequest.readyState == 1)
	{
		if (placeObject)
		{
			placeObject.innerHTML = 'Fetching Data.<BR>';
		}
	}
	if (pageRequest.readyState == 2)
	{
		if (placeObject)
		{
			placeObject.innerHTML = 'Fetching Data..<BR>';
		}
	}
	if (pageRequest.readyState == 3)
	{
		if (placeObject)
		{
			placeObject.innerHTML = 'Fetching Data...<BR>';
		}
	}

	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		//placeObject.innerHTML = pageRequest.responseText;	// For debugging	
	
		var xmlResponse = pageRequest.responseXML;
		
		//Check for an xml response
		if (xmlResponse.documentElement)
		{
			var xmlRoot = xmlResponse.documentElement;
			
			// Check to make sure all needed tags have the values
			if ( xmlRoot.getElementsByTagName('majorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('minorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('placeHolderID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('searchBoxIDValue')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('tabLinksSectionID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('allTabContentSectionID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('searchTabSectionID')[0].hasChildNodes()
			  )
			{	 
				var majorCategoryID = xmlRoot.getElementsByTagName('majorCategoryID')[0].firstChild.data;
				var minorCategoryID = xmlRoot.getElementsByTagName('minorCategoryID')[0].firstChild.data;
				var placeHolderID = xmlRoot.getElementsByTagName('placeHolderID')[0].firstChild.data;
				var searchString = xmlRoot.getElementsByTagName('searchBoxIDValue')[0].firstChild.data;
				var tabLinksSectionID = xmlRoot.getElementsByTagName('tabLinksSectionID')[0].firstChild.data;
				var allTabContentSectionID = xmlRoot.getElementsByTagName('allTabContentSectionID')[0].firstChild.data;
				var searchTabSectionID = xmlRoot.getElementsByTagName('searchTabSectionID')[0].firstChild.data;
				var searchTabID = xmlRoot.getElementsByTagName('searchTabID')[0].firstChild.data;

				var placeHolderObject = document.getElementById(placeHolderID);
		
				// Check to make sure the result was returned
				if ( xmlRoot.getElementsByTagName('numberResultsSearch')[0].hasChildNodes() )
				{
					var numberResultsSearch = xmlRoot.getElementsByTagName('numberResultsSearch')[0].firstChild.data;
					
					// If the span tag still exists, place the name and number into the span
			
					if (placeHolderObject)
					{
						// Only provide a link if there are more than 0 results
			
						if (numberResultsSearch == 0)
						{
							placeHolderObject.innerHTML = majorCategoryID + ":" + minorCategoryID + " " + numberResultsSearch + "<BR>";
						}
						else
						{
							placeHolderObject.innerHTML = majorCategoryID + ":" + minorCategoryID + " " + "<a href='#' onclick=\"createTab('" + majorCategoryID + "', '" + minorCategoryID + "', '" + searchString + "', '" + tabLinksSectionID + "', '" + allTabContentSectionID + "', '" + searchTabSectionID + "', '" + numberResultsSearch + "', '" + searchTabID + "');\">" + numberResultsSearch + "</a><BR>";
						}
					}
				}
				else
				{
					placeObject.innerHTML == "NO XML RESULTS RETURNED<BR>";
				}
			}
			else
			{
				placeObject.innerHTML = "XML FAILED<BR>";
			}
		}
	}
	else if (pageRequest.readyState == 4 && pageRequest.status == 404)
	{
		placeObject.innerHTML = 'Sorry, there seems to be some kind of problem.';
	}
}

function filterDataTab(pageRequest, placeID)
{
	var placeObject = document.getElementById(placeID);

	if (pageRequest.readyState == 0)
	{
		placeObject.innerHTML = 'Fetching Data<BR>';
	}
	if (pageRequest.readyState == 1)
	{
		placeObject.innerHTML = 'Fetching Data.<BR>';
	}
	if (pageRequest.readyState == 2)
	{
		placeObject.innerHTML = 'Fetching Data..<BR>';
	}
	if (pageRequest.readyState == 3)
	{
		placeObject.innerHTML = 'Fetching Data...<BR>';
	}
	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		//placeObject.innerHTML = pageRequest.responseText; // For debugging
		
		var xmlResponse = pageRequest.responseXML;
		
		//Check for an xml response
		if (pageRequest.responseXML)
		{
			var xmlRoot = xmlResponse.documentElement;
			
			// Check to make sure all needed tags have the values
			if ( xmlRoot.getElementsByTagName('majorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('minorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('searchString')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('header')[0].hasChildNodes()			
			   )
			{
				var majorCategoryID  = xmlRoot.getElementsByTagName('majorCategoryID')[0].firstChild.data;
				var minorCategoryID = xmlRoot.getElementsByTagName('minorCategoryID')[0].firstChild.data;
				var searchString = xmlRoot.getElementsByTagName('searchString')[0].firstChild.data;
				var header = xmlRoot.getElementsByTagName('header')[0].firstChild.data;
				
				if ( xmlRoot.getElementsByTagName('checkboxes')[0].hasChildNodes() )
				{							
					// Need to check for browser, because Mozilla and Firefox break up returned data into
					// 4MB chunks, so a different line is needed for those browsers.
					var agt=navigator.userAgent.toLowerCase();
					var browser = "";
					if (agt.indexOf("opera") != -1) browser = 'Opera';
					if (agt.indexOf("staroffice") != -1) browser = 'Star Office';
					if (agt.indexOf("webtv") != -1) browser = 'WebTV';
					if (agt.indexOf("beonex") != -1) browser = 'Beonex';
					if (agt.indexOf("chimera") != -1) browser = 'Chimera';
					if (agt.indexOf("netpositive") != -1) browser = 'NetPositive';
					if (agt.indexOf("phoenix") != -1) browser = 'Phoenix';
					if (agt.indexOf("firefox") != -1) browser = 'Firefox';
					if (agt.indexOf("safari") != -1) browser = 'Safari';
					if (agt.indexOf("skipstone") != -1) browser = 'SkipStone';
					if (agt.indexOf("msie") != -1) browser = 'Internet Explorer';
					if (agt.indexOf("netscape") != -1) browser = 'Netscape';
					if (agt.indexOf("mozilla/5.0") != -1) browser = 'Mozilla';
					
					if (browser != 'Internet Explorer')
					{
						var checkBoxes = xmlRoot.getElementsByTagName('checkboxes')[0].textContent;
					}
					else
					{
						var checkBoxes = xmlRoot.getElementsByTagName('checkboxes')[0].firstChild.data;
					}
				}
				else
				{
					var checkBoxes = "";
				}
		
				var numberResultTabData = xmlRoot.getElementsByTagName('numberResultsTabData')[0].firstChild.data;
		
				var header = majorCategoryID + ':' + minorCategoryID + ':' + searchString;
		
				if (checkBoxes != "")// Check for any error in which no data was sent back.
				{
			  		tabArray[header].numberResultTabData = numberResultTabData;
		
			  		tabArray[header].ids = new Array();
					var selectAllID = tabArray[header].selectAllID;
		
			  		// Insert the Select All checkbox
		
		    		placeObject.innerHTML = "<BR><B>Check to View Details</B><BR><input type='checkbox' id='" + selectAllID + "' onclick=\"selectAll('" + header + "');\">Select All<br>";
		    		placeObject.innerHTML += checkBoxes;
		  		}
		  		else
		  		{
			  		placeObject.innerHTML = "ERROR WITH DATA, NONE RECEIVED";
		  		}
		
		  		// IF number of results are not correct, then log it into the file, except on a search of '--ALL--'.
		
		  		if (tabArray[header].numberResultTabData != tabArray[header].numberResultsSearch && minorCategoryID != '--ALL--')
			  	{
				  	var xmlHeader = '<logRequest>' + 
					  					'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
					  					'<minorCategoryID>' + minorCategoryID + '</minorCategoryID>' +
					  					'<searchString>' + searchString + '</searchString>' +
					  					'<numberResultsSearch>' + tabArray[header].numberResultsSearch + '</numberResultsSearch>' +
					  					'<numberResultTabData>' + tabArray[header].numberResultTabData + '</numberResultTabData>' +
				  					'</logRequest>';
				  	fetchData(logReportPHP, xmlHeader, "");
			  	}
		  	}
		  	else
		  	{
			  	placeObject.innerHTML = "NO XML DATA RETURNED<BR>";
		  	}
	  	}
	  	else
	  	{
		  	placeObject.innerHTML = "XML FAILED<BR>";
	  	}
  	}
  	else if (pageRequest.readyState == 4 && pageRequest.status == 404)
  	{
	  	placeObject.innerHTML = 'Sorry, there seems to be some kind of problem.';
  	}
}

function filterDataTable(pageRequest, placeID)
{
	var placeObject = document.getElementById(placeID);

	if (pageRequest.readyState == 0)
	{
		placeObject.innerHTML = 'Fetching Data<BR>';
	}
	if (pageRequest.readyState == 1)
	{
		placeObject.innerHTML = 'Fetching Data.<BR>';
	}
	if (pageRequest.readyState == 2)
	{
		placeObject.innerHTML = 'Fetching Data..<BR>';
	}
	if (pageRequest.readyState == 3)
	{
		placeObject.innerHTML = 'Fetching Data...<BR>';
	}
	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		//placeObject.innerHTML = pageRequest.responseText; // For debugging
		var xmlResponse = pageRequest.responseXML;
		
		//Check for an xml response
		if (xmlResponse.documentElement)
		{
			var xmlRoot = xmlResponse.documentElement;
			
			// Check to make sure all needed tags have the values
			if ( xmlRoot.getElementsByTagName('majorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('minorCategoryID')[0].hasChildNodes() &&
				 xmlRoot.getElementsByTagName('searchString')[0].hasChildNodes()
			   )
			{
				var majorCategoryID  = xmlRoot.getElementsByTagName('majorCategoryID')[0].firstChild.data;
				var minorCategoryID = xmlRoot.getElementsByTagName('minorCategoryID')[0].firstChild.data;
				var searchString = xmlRoot.getElementsByTagName('searchString')[0].firstChild.data;
		
				// Check to see if any results were sent back
				if (xmlRoot.getElementsByTagName('searchInfo'))
				{
					var searchInfo = xmlRoot.getElementsByTagName('searchInfo')[0];
				}
				else
				{
					var searchInfo = "";
				}
		
				var header = majorCategoryID + ":" + minorCategoryID + ":" + searchString;
		
				// Need to get the checknames from the array, instead of the returnedData, because some characters from those names were missing.
		
				var idArray = tabArray[header].checkedNames;
		
				var check = 0;
		
				// If results were sent back then save new data
		
				if(searchInfo)
				{
					for (var i = 0; i < searchInfo.childNodes.length; i++)
					{
						var row = searchInfo.childNodes[i].firstChild.data;
						var data = row.split(',,,');
		
						if (!tabArray[header].ids[data[0]])
				  		{
					  		var id = data[0];
					 		tabArray[header].ids[id] = new Object();
					  		tabArray[header].ids[id].details = new Array(data.length);
				  		}
						for (var j = 0; j < data.length; j++)
				  		{
					  		tabArray[header].ids[id].details[j] = data[j];
				  		}
					}
		  		}
		
		  		// Need to take out beginning * and add a backslash to escape any special characters
		  		
				if (searchString.charAt(0) == '*')
				{
					searchString = searchString.slice(1);
				}
				
				searchStringREGEXP = searchString.replace(/\\/g, "\\\\");
				searchStringREGEXP = searchStringREGEXP.replace(/\*/g, "\\*");
				searchStringREGEXP = searchStringREGEXP.replace(/\?/g, "\\?");
				searchStringREGEXP = searchStringREGEXP.replace(/\+/g, "\\+");
				searchStringREGEXP = searchStringREGEXP.replace(/\^/g, "\\^");
				searchStringREGEXP = searchStringREGEXP.replace(/\./g, "\\.");
				searchStringREGEXP = searchStringREGEXP.replace(/\|/g, "\\|");
				searchStringREGEXP = searchStringREGEXP.replace(/\(/g, "\\(");
				searchStringREGEXP = searchStringREGEXP.replace(/\)/g, "\\)");
				searchStringREGEXP = searchStringREGEXP.replace(/\[/g, "\\[");
				searchStringREGEXP = searchStringREGEXP.replace(/\]/g, "\\]");
				searchStringREGEXP = searchStringREGEXP.replace(/\{/g, "\\{");
				searchStringREGEXP = searchStringREGEXP.replace(/\}/g, "\\}");
									
				var searchStringREG = new RegExp(searchStringREGEXP, "ig");
				
				// Go through for every selected name and insert the details for each name into each row.
				// The loop checks all info and if any of it matches the search string, then the search string is bolded.
				
				var h = 0;
		
				// Start the table
				var table = "<table width='100%' border='1' cellspacing='0' cellpadding='2'>";
		
				// Make the header
				var headerArray = tabArray[header].headers;
				var m = 0;
				table += "<tr>";
				for (var i = 0; i < headerArray.length; i++)
				{
					if (headerArray[i].search(/ID/) == -1)
					{
						table += "<th>";
						table += headerArray[i];
						table += "</th>";
					}
				}
				table += "</tr>";
		
				for (key in idArray)
				{
					table += "<tr>";
					for (var j = 0; j < headerArray.length; j++)
					{
						if (headerArray[j].search(/ID/) == -1)
						{
							var detail = tabArray[header].ids[idArray[key]].details[j];
							
							// Search the data for the string to highlite and bold it
							var newDetail = detail.replace(searchStringREG, "<b class='search'>" + searchString + "</b>");
							
							// These two fields get to big, so a class is added to make them a certain size
							if (headerArray[j] == 'Description')
							{
								table += "<td><span id='" + m + header + "' class='moreDescription' >";
							}
							else if (headerArray[j] == 'IOC Name(s)')
							{
								table += "<td><span id='" + m + header + "' class='moreIoc' >";
							}
							else
							{
								table += "<td><span id='" + m + header + "'>";
							}
		
							// If there is a webpage provided then make a link for that detail
							if (majorCategory[majorCategoryID].webPage[j] != "")
							{
								var webPage = majorCategory[majorCategoryID].webPage[j];
								var string = "";
								for (var k = 0; k < webVariable[majorCategoryID][webPage].length; k = k + 2)
								{
									string += webVariable[majorCategoryID][webPage][k] + tabArray[header].ids[idArray[key]].details[webVariable[majorCategoryID][webPage][k+1]];
								}
								webPage = webPage + string;
		
								//Have to have target, opens in new window. Would not work using onclick=window.open.
								table += "<a href='"+webPage+"' target='_blank' class='tableData'>"+ newDetail + "</a>";
							}
							else
							{
								table += newDetail;
							}
							table += "</span></td>";
						}
						m++;
					}
					table += "</tr>";
					h++;
				}
				placeObject.innerHTML = table;
			}
			else
			{
				placeObject.innerHTML = "NO XML DATA RETURNED<BR>";
			}
		}
		else
		{
			placeObject.innerHTML = 'XML FAILED';
		}
	}
	else if (pageRequest.readyState == 4 && pageRequest.status == 404)
	{
		placeObject.innerHTML = 'Sorry, there seems to be some kind of problem.';
	}
}

function filterDataReport(pageRequest, objectPlaceID, file)
{
	if (pageRequest.readyState == 4 && pageRequest.status == 200)
	{
		var mywin = window.open(file);
	}
}

var advanceSearchCheck = false;
function displayAdvanceSearch(majorCategorySectionID, minorCategorySectionID, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, ALL, searchTabID)
{
	// Get the objects for the ID's.

	var majorCategoryObject = document.getElementById(majorCategorySectionID);
	var minorCategoryObject = document.getElementById(minorCategorySectionID);
	var criteriaResultsObject = document.getElementById(criteriaResultsSectionID);

	// Clear all usedCriteria and placeHolder and place holder and erase content from screen.

	criteriaResultsObject.innerHTML = "";

	var key;
	for (key in majorCategory)
	{
		majorCategory[key].usedCriteria = "";
		majorCategory[key].criteriaDisplayPlaceHolderID = "";

		// Remove all search results holders.

		for (key2 in majorCategory[key].criteriaSearchResultsPlaceHolderID)
		{
			delete majorCategory[key].criteriaSearchResultsPlaceHolderID[key2];
			delete majorCategory[key].usedCriteriaResults[key2];
		}
	}

	// If the div is empty than put in the search criteria, otherwise, clear the search criteria and display the '--ALL--' results for everything except EPICS RECORD

	if (majorCategoryObject.innerHTML == "" && advanceSearchCheck == false)
	{
		// To make sure that while the server is slow, multiple attempts to show the advance search options are done.
		advanceSearchCheck = true;
		
		var xmlHeader = '<criteriaRequest>' +
							'<majorCategoryID>' + ALL + '</majorCategoryID>' +
							'<minorCategorySectionID>' + minorCategorySectionID + '</minorCategorySectionID>' +
							'<criteriaResultsSectionID>' + criteriaResultsSectionID + '</criteriaResultsSectionID>' +
							'<searchBoxID>' + searchBoxID + '</searchBoxID>' +
							'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
							'<allTabContentSectionID>'+ allTabContentSectionID + '</allTabContentSectionID>' +
							'<searchTabSectionID>'+ searchTabSectionID + '</searchTabSectionID>' +
							'<criteriaString>' + criteriaString + '</criteriaString>' +
							'<searchTabID>' + searchTabID + '</searchTabID>' +
						'</criteriaRequest>';
		fetchData(criteriaPHP, xmlHeader, majorCategorySectionID);
	}
	else if (majorCategoryObject.innerHTML != "" && advanceSearchCheck == true)
	{
		advanceSearchCheck = false;
		
		// Erase content from screen.

		majorCategoryObject.innerHTML = "";
		for (var i = 0; i < Category.length-1; i++)
		{
			document.getElementById(criteriaPlaceHolderID + i).innerHTML = "";
		}

		// Display '--ALL--' results.

		updateDisplayCheck(ALL, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, majorCategorySectionID, searchTabID);
	}
}

var updateCriteriaCount = 0;
function updateCriteria(ALLSelectBoxID, minorCategorySectionID, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, searchTabID)
{
	var selectBoxObject = document.getElementById(ALLSelectBoxID);
	var criteriaResultsObject = document.getElementById(criteriaResultsSectionID);
	var length = selectBoxObject.length

	for (var i = 0; i < length; i++)
	{
		var placeHolderID = criteriaPlaceHolderID + updateCriteriaCount;

		// Find out what boxes were selected and which ones are no longer selected.

		if (selectBoxObject.options[i].selected == true)
		{
			if (majorCategory[selectBoxObject.options[i].text].usedCriteria == "")
			{
				majorCategory[selectBoxObject.options[i].text].usedCriteria = true;
				majorCategory[selectBoxObject.options[i].text].criteriaDisplayplaceHolderID = majorCategory[selectBoxObject.options[i].text].columnNumber;
				var xmlHeader = '<criteriaRequest>' +
									'<majorCategoryID>' + selectBoxObject.options[i].text + '</majorCategoryID>' +
									'<minorCategorySectionID>' + placeHolderID + '</minorCategorySectionID>' +
									'<criteriaResultsSectionID>' + criteriaResultsSectionID + '</criteriaResultsSectionID>' +
									'<searchBoxID>' + searchBoxID + '</searchBoxID>' +
									'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
									'<allTabContentSectionID>' + allTabContentSectionID + '</allTabContentSectionID>' +
									'<searchTabSectionID>' + searchTabSectionID + '</searchTabSectionID>' +
									'<criteriaString>' + criteriaString + '</criteriaString>' +
									'<searchTabID>' + searchTabID + '</searchTabID>' +
								'</criteriaRequest>';
				fetchData(criteriaPHP, xmlHeader, majorCategory[selectBoxObject.options[i].text].columnNumber);
				updateCriteriaCount++;
			}
		}
		else if ( (majorCategory[selectBoxObject.options[i].text].usedCriteria == true) && (selectBoxObject.options[i].selected != true) )
		{
			// If any of the minor category values were unselected, then it runs through and removes them from the screen.

			for (var j = 0; j < majorCategory[selectBoxObject.options[i].text].minorCategory.length; j++)
			{
				var selectMinorID = document.getElementById(selectBoxObject.options[i].text);

				if (selectMinorID.options[j].selected == true && majorCategory[selectBoxObject.options[i].text].criteriaSearchResultsPlaceHolderID[selectMinorID.options[j].text])
				{
					var holder = majorCategory[selectBoxObject.options[i].text].criteriaSearchResultsPlaceHolderID[selectMinorID.options[j].text];
					var holderID = document.getElementById(holder);
					criteriaResultsObject.removeChild(holderID);
					delete majorCategory[selectBoxObject.options[i].text].criteriaSearchResultsPlaceHolderID[selectMinorID.options[j].text];
					delete majorCategory[selectBoxObject.options[i].text].usedCriteriaResults[selectMinorID.options[j].text];
				}
			}

			// Remove selectBox from display and clear the variables.

			var holder = majorCategory[selectBoxObject.options[i].text].criteriaDisplayplaceHolderID;
			var holderObjectID = document.getElementById(holder);
            holderObjectID.innerHTML = "";
			majorCategory[selectBoxObject.options[i].text].criteriaDisplayplaceHolderID = "";
			majorCategory[selectBoxObject.options[i].text].usedCriteria = "";
		}
	}
}

function checkAll(selectBoxID)
{
	// If any of the options are selected except for 'all', then unselect 'all'.

	var selectBoxObject = document.getElementById(selectBoxID);
	for (var i = 1; i <selectBoxObject.length ; i++)
	{
		if (selectBoxObject.options[i].selected == true)
		{
			selectBoxObject.options[0].selected = false;
			return;
		}
	}
}
var updateCheck = "";
function updateDisplayCheck(ALL, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, majorCategorySectionID, searchTabID, evt)
{
	clearTimeout(updateCheck);
	updateCheck = setTimeout("updateDisplay('"+ALL+"', '"+criteriaResultsSectionID+"', '"+searchBoxID+"', '"+tabLinksSectionID+"', '"+allTabContentSectionID+"', '"+searchTabSectionID+"', '"+majorCategorySectionID+"', '" + searchTabID + "', '"+evt+"')", 400);
}
function updateDisplay(ALL, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, majorCategorySectionID, searchTabID, evt)
{
	var searchString = document.getElementById(searchBoxID).value;
	var majorCategoryObject = document.getElementById(majorCategorySectionID);
	
	
	for (var i = 0; i < majorCategory[ALL].minorCategory.length; i++)
	{
		// Check to see if any of the minorCategory boxes are displayed.
		//If they aren't then all major types are searched within the '--ALL--'except for "EPICS RECORD".

		if (majorCategory[majorCategory[ALL].minorCategory[i]].usedCriteria == true)
		{
			displaySearchCriteriaResults(majorCategory[ALL].minorCategory[i], criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, searchTabID, searchString,  evt)
		}
		// else if (majorCategoryObject.innerHTML == "" && i != (majorCategory[ALL].minorCategory.length - 1))	
		else if (majorCategoryObject.innerHTML == "")
		{
			displaySearchCriteriaResultsALL(majorCategory[ALL].minorCategory[i], criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, searchTabID, searchString,  evt)
		}
	}
}

var resultsCount = 0;
function displaySearchCriteriaResults(majorCategoryID, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, searchTabID, searchString, evt)
{
	// Check to see if the shift key was hit.

	var evt = (evt) ? evt : ((window.event) ? window.event : null);
	if(evt)
	{
		if(evt.type == "keyup")
		{
			if (evt.keyCode == "16")   //Cancel out the shift key
			{
				return;
			}
		}
	}

	// Get objects from the ID's and intialize variables.

	var searchBoxObject = document.getElementById(searchBoxID);
	var selectBoxObject = document.getElementById(majorCategoryID);
	var criteriaResultsSectionObject = document.getElementById(criteriaResultsSectionID);
	var sameSearchString = "";
	var differenceLength = "";
	var startOver = "";

	// Need to have this because if you get the string during this function, it could have changed from the time this function was initiated.
	// If the string was passed to it then take it, otherwise, get the current value from the textbox.

	if (searchString == "")
	{
		var searchBoxIDValue = document.getElementById(searchBoxID).value;
	}
	else
	{
		var searchBoxIDValue = searchString;
	}

	// Figure out if the string is the same size, bigger, smaller, or if the person deleted the entire string and is starting over.

	if (majorCategory[majorCategoryID].oldSearchString == searchBoxIDValue)
	{
		sameSearchString = true;
		differenceLength = 'none';
	}
	else
	{		
		sameSearchString = false;
		
		// Add a backslash to escape any special characters
		
		var text = majorCategory[majorCategoryID].oldSearchString;
		text = text.replace(/\\/g, "\\\\");
		text = text.replace(/\*/g, "\\*");
		text = text.replace(/\?/g, "\\?");
		text = text.replace(/\+/g, "\\+");
		text = text.replace(/\^/g, "\\^");
		text = text.replace(/\./g, "\\.");
		text = text.replace(/\|/g, "\\|");
		text = text.replace(/\(/g, "\\(");
		text = text.replace(/\)/g, "\\)");
		text = text.replace(/\[/g, "\\[");		
		text = text.replace(/\]/g, "\\]");
		text = text.replace(/\{/g, "\\{");
		text = text.replace(/\}/g, "\\}");		
		
		var pattern = new RegExp("^"+text, "i")
		var resultTest = pattern.test(searchBoxIDValue);
		
		// Check to see if the new word is the same word from before but with more characters
		if (resultTest == true)
		{
			differenceLength = 'bigger';
		}
		else
		{
			differenceLength = 'none';
		}
		if (majorCategory[majorCategoryID].oldSearchString.length < (defaultSearchStringSize + 1) && searchBoxIDValue.length > defaultSearchStringSize)
		{
			startOver = true;
		}
		else
		{
			startOver = false;
		}
	}

	// Save the new length of the string into the category.

	majorCategory[majorCategoryID].oldSearchString = searchBoxIDValue;

	// If the length is greater than defaultSearchStringSize then figure out which criteria to display and remove the ones that shouldn't be displayed.

	searchBoxObject.style.color = 'red';

	if (searchBoxIDValue.length > defaultSearchStringSize && searchBoxIDValue != defaultTextBoxValue)
	{
		searchBoxObject.style.color = 'black';
		for (var i = 0; i < selectBoxObject.length ; i++)
		{
			var placeHolderID = 'criteriaResults' + resultsCount;//This is not necessarily hardcoded. The value in the quotes doesn't matter.
			if (selectBoxObject.options[i].selected == true && majorCategory[majorCategoryID].usedCriteriaResults[selectBoxObject.options[i].text] != true)
			{
				majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[selectBoxObject.options[i].text] = placeHolderID;
				majorCategory[majorCategoryID].usedCriteriaResults[selectBoxObject.options[i].text] = true;
				criteriaResultsSectionObject.innerHTML +="<span id='" + placeHolderID + "'>Fetching Data<BR></span>";
				var xmlHeader = '<searchRequest>' +
									'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
									'<minorCategoryID>' + selectBoxObject.options[i].text + '</minorCategoryID>' +
									'<placeHolderID>' + placeHolderID + '</placeHolderID>' +
									'<searchBoxIDValue>' + searchBoxIDValue + '</searchBoxIDValue>' +
									'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
									'<allTabContentSectionID>' + allTabContentSectionID + '</allTabContentSectionID>' +
									'<searchTabSectionID>' + searchTabSectionID + '</searchTabSectionID>' +
									'<searchTabID>' + searchTabID + '</searchTabID>' +
								'</searchRequest>';
				fetchData(criteriaSearchResultsPHP, xmlHeader, placeHolderID);
				resultsCount++;
			}
			else if (selectBoxObject.options[i].selected != true && majorCategory[majorCategoryID].usedCriteriaResults[selectBoxObject.options[i].text] == true)
			{
				var holderID = majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[selectBoxObject.options[i].text];
				var holderObject = document.getElementById(holderID);
				criteriaResultsSectionObject.removeChild(holderObject);
				delete majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[selectBoxObject.options[i].text];
				delete majorCategory[majorCategoryID].usedCriteriaResults[selectBoxObject.options[i].text];
			}
			else if (	selectBoxObject.options[i].selected == true
					&&	majorCategory[majorCategoryID].usedCriteriaResults[selectBoxObject.options[i].text] == true
					&&	sameSearchString== false)
			{
				// Get the result from the place holder.

				var holderID = majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[selectBoxObject.options[i].text];
				var holderObject = document.getElementById(holderID);
				var pattern = /[0-9]+/;
				var string = holderObject.innerHTML;
				var result = pattern.exec(string);

				// If the result was zero than don't replace it again if the text length is getting bigger.

				if (result == '0' && differenceLength == 'bigger' && startOver != true)
				{
					criteriaResultsSectionObject.removeChild(holderObject);
					var link = document.createElement('span');
					link.setAttribute('id', holderID);
					criteriaResultsSectionObject.appendChild(link);
					document.getElementById(holderID).innerHTML = string;
				}
				else
				{
					criteriaResultsSectionObject.removeChild(holderObject);
					criteriaResultsSectionObject.innerHTML +="<span id='" + placeHolderID + "'>Fetching Data<BR></span>";
					var xmlHeader = '<searchRequest>' +
										'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
										'<minorCategoryID>' + selectBoxObject.options[i].text + '</minorCategoryID>' +
										'<placeHolderID>' + placeHolderID + '</placeHolderID>' +
										'<searchBoxIDValue>' + searchBoxIDValue + '</searchBoxIDValue>' +
										'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
										'<allTabContentSectionID>' + allTabContentSectionID + '</allTabContentSectionID>' +
										'<searchTabSectionID>' + searchTabSectionID + '</searchTabSectionID>' +
										'<searchTabID>' + searchTabID + '</searchTabID>' +
									'</searchRequest>';
					fetchData(criteriaSearchResultsPHP, xmlHeader, placeHolderID);
					majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[selectBoxObject.options[i].text] = placeHolderID;
					resultsCount++;
				}
			}
		}
	}
}

function displaySearchCriteriaResultsALL(majorCategoryID, criteriaResultsSectionID, searchBoxID, tabLinksSectionID, allTabContentSectionID,  searchTabSectionID, searchTabID, searchString, evt)
{
	// This function only executes when no criteria are shown.

	// Check to see if the shift key was hit.
	var evt = (evt) ? evt : ((window.event) ? window.event : null);
	var sameSearchStringLength = false;
	if(evt)
	{
		if(evt.type == "keyup")
		{
			if (evt.keyCode == "16")   //Cancel out the shift key.
			{
				return;
			}
		}
	}

	var minorCategory = majorCategory[majorCategoryID].minorCategory[0];
	var criteriaResultsObject = document.getElementById(criteriaResultsSectionID);
	var sameSearchString = "";
	var differenceLength = "";
	var startOver = "";

	// Figure out if the string is the same size, bigger, smaller, or if the person deleted the entire string and is starting over.

	var searchBoxIDValue = searchString;
	var searchBoxObject = document.getElementById(searchBoxID);

	if (majorCategory[majorCategoryID].oldSearchString == searchBoxIDValue)
	{
		sameSearchString = true;
		differenceLength = 'none';
	}
	else
	{		
		sameSearchString = false;
		
		// Add a backslash to escape any special characters
		
		var text = majorCategory[majorCategoryID].oldSearchString;
		text = text.replace(/\\/g, "\\\\");
		text = text.replace(/\*/g, "\\*");
		text = text.replace(/\?/g, "\\?");
		text = text.replace(/\+/g, "\\+");
		text = text.replace(/\^/g, "\\^");
		text = text.replace(/\./g, "\\.");
		text = text.replace(/\|/g, "\\|");
		text = text.replace(/\(/g, "\\(");
		text = text.replace(/\)/g, "\\)");
		text = text.replace(/\[/g, "\\[");		
		text = text.replace(/\]/g, "\\]");
		text = text.replace(/\{/g, "\\{");
		text = text.replace(/\}/g, "\\}");
		
		var pattern = new RegExp("^"+text, "i")
		var resultTest = pattern.test(searchBoxIDValue);
		if (resultTest == true)
		{
			differenceLength = 'bigger';
		}
		else
		{
			differenceLength = 'none';
		}
		if (majorCategory[majorCategoryID].oldSearchString.length < (defaultSearchStringSize + 1) && searchBoxIDValue.length > defaultSearchStringSize)
		{
			startOver = true;
		}
		else
		{
			startOver = false;
		}		
	}
	majorCategory[majorCategoryID].oldSearchString = searchBoxIDValue;

	searchBoxObject.style.color = 'red';

	if (searchBoxIDValue.length > defaultSearchStringSize && searchBoxIDValue != defaultTextBoxValue)
	{
		searchBoxObject.style.color = 'black';
		var placeHolderID = 'criteriaResults' + resultsCount;
		if (majorCategory[majorCategoryID].usedCriteriaResults[minorCategory] != true)
		{
			majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[minorCategory] = placeHolderID;
			majorCategory[majorCategoryID].usedCriteriaResults[minorCategory] = true;
			criteriaResultsObject.innerHTML +="<span id='" + placeHolderID + "'>Fetching Data<BR></span>";
			var xmlHeader = '<searchRequest>' +
								'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
								'<minorCategoryID>' + minorCategory + '</minorCategoryID>' +
								'<placeHolderID>' + placeHolderID + '</placeHolderID>' +
								'<searchBoxIDValue>' + searchBoxIDValue + '</searchBoxIDValue>' +
								'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
								'<allTabContentSectionID>' + allTabContentSectionID + '</allTabContentSectionID>' +
								'<searchTabSectionID>' + searchTabSectionID + '</searchTabSectionID>' +
								'<searchTabID>' + searchTabID + '</searchTabID>' +
							'</searchRequest>';
			fetchData(criteriaSearchResultsPHP, xmlHeader, placeHolderID);
			resultsCount++;
		}
		else if (majorCategory[majorCategoryID].usedCriteriaResults[minorCategory] == true && sameSearchString == false)
		{
			var holderID = majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[minorCategory];
			var holderObject = document.getElementById(holderID);
			var pattern = /[0-9]+/;
			var string = holderObject.innerHTML;
			var result = pattern.exec(string);

			if (result == '0' && differenceLength == 'bigger' && startOver != true)
			{
				criteriaResultsObject.removeChild(holderObject);
				var link = document.createElement('span');
				link.setAttribute('id', holderID);
				criteriaResultsObject.appendChild(link);
				document.getElementById(holderID).innerHTML = string;
			}
			else
			{
				criteriaResultsObject.removeChild(holderObject);
				criteriaResultsObject.innerHTML +="<span id='" + placeHolderID + "'>Fetching Data<BR></span>";
				var xmlHeader = '<searchRequest>' +
									'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
									'<minorCategoryID>' + minorCategory + '</minorCategoryID>' +
									'<placeHolderID>' + placeHolderID + '</placeHolderID>' +
									'<searchBoxIDValue>' + searchBoxIDValue + '</searchBoxIDValue>' +
									'<tabLinksSectionID>' + tabLinksSectionID + '</tabLinksSectionID>' +
									'<allTabContentSectionID>' + allTabContentSectionID + '</allTabContentSectionID>' +
									'<searchTabSectionID>' + searchTabSectionID + '</searchTabSectionID>' +
									'<searchTabID>' + searchTabID + '</searchTabID>' +
								'</searchRequest>';
				fetchData(criteriaSearchResultsPHP, xmlHeader, placeHolderID);
				majorCategory[majorCategoryID].criteriaSearchResultsPlaceHolderID[minorCategory] = placeHolderID;
				resultsCount++;
			}
		}
	}
}

var tabArray = new Array();
var tabCount = 0;
var selectCount = 0;
function createTab(majorCategoryID, minorCategoryID, searchString, tabLinksSectionID, allTabContentSectionID, searchTabSectionID, numberResultsSearch, searchTabID)
{
	var header = majorCategoryID + ":" + minorCategoryID + ":" + searchString;

	// Check to see if the tab already exists.

	if (!tabArray[header])
	{
		var tabLinksObject = document.getElementById(tabLinksSectionID);
		var allTabContentObject = document.getElementById(allTabContentSectionID);
		var listPlaceID = 'list' + tabCount;
		var reportPlaceID = 'report' + tabCount;
		var tabContentPlaceID = 'tabContent' + tabCount;
		var resultPagePlaceID = 'resultPage' + tabCount;
		var reportToolID = 'reportTool' + tabCount;
		var selectAllID = 'select' + selectCount;
		selectCount++;
		tabCount++;

    	// Initialize tabArray.

		tabArray[header] = new Object();
		tabArray[header].checkedNames = new Array();
		tabArray[header].checkedIDs = new Array();
		tabArray[header].numberChecked = 0;
		tabArray[header].listPlaceID = listPlaceID;
		tabArray[header].reportPlaceID = reportPlaceID;
		tabArray[header].reportToolID = reportToolID;
		tabArray[header].tabContentPlaceID = tabContentPlaceID;
		tabArray[header].resultPagePlaceID = resultPagePlaceID;
		tabArray[header].majorCategoryID = majorCategoryID;
		tabArray[header].minorCategoryID = minorCategoryID;
		tabArray[header].searchString = searchString;
		tabArray[header].numberResultsSearch = numberResultsSearch;
		tabArray[header].headers = majorCategory[majorCategoryID].headers;
		tabArray[header].selectAllID = selectAllID;

		tabLinksObject.innerHTML += "<a href='#' id='" + resultPagePlaceID + "' class='unselectedTab' onclick=\"expandTab('" + tabLinksSectionID + "', '" + allTabContentSectionID + "', '" + searchTabSectionID + "', '" + header + "');\">" + majorCategoryID + " : " + minorCategoryID + "(" + searchString +")" + "</a>";
		allTabContentObject.innerHTML += "<div id='" + tabContentPlaceID + "' class='unselectedContent'><a href='#' onclick=\"closeTab('" + header + "', '" + allTabContentSectionID + "', '" + tabLinksSectionID +"', '" + searchTabSectionID + "', '" + searchTabID + "');\">Close Tab</a><div id='" + listPlaceID + "' class='list'></div><div id='" + reportPlaceID + "' class='report'></div><div id='" + reportToolID + "' class='reportTool'></div></div>";

		report(header);

		// create XML string for header information

		var xmlHeader = '<searchRequest>' +
							'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
							'<minorCategoryID>' + minorCategoryID + '</minorCategoryID>' +
							'<searchString>' + searchString + '</searchString>' +
							'<header1>' + header + '</header1>' +
						'</searchRequest>';
		fetchData(tabDataPHP, xmlHeader, listPlaceID);
	}
}

function expandTab(tabLinksSectionID, allTabContentSectionID, searchTabSectionID, header)
{
	// Get the objects from the ID's.

	if (tabArray[header])
	{
		var resultPagePlaceObject = document.getElementById(tabArray[header].resultPagePlaceID);
		var tabContentObject = document.getElementById(tabArray[header].tabContentPlaceID);
	}
	else
	{
		var resultPagePlaceObject = document.getElementById(header);
		var tabContentObject = document.getElementById(searchTabSectionID);
	}

	var tabLinksSectionObject = document.getElementById(tabLinksSectionID);
	var allTabContentSectionObject = document.getElementById(allTabContentSectionID);

	// Get the needed tags from each section as an array.

	var allLinks = tabLinksSectionObject.getElementsByTagName('a');
	var allContent = allTabContentSectionObject.getElementsByTagName('div');

	// Turn all display to none.

	// Have to have this line because the design changed. If this 'div' was inside the allTabContentSectionID,
	// then whenever someone clicked to create a new tab, the search page would unselect all boxes and delete the search string.
    document.getElementById(searchTabSectionID).style.display = 'none';
	for (var i = 0; i < allLinks.length ; i++)
	{
		document.getElementById(allLinks[i].getAttribute('id')).className = 'unselectedTab';
	}

	// Turn all background to white.

	for (var i = 0; i < allContent.length ; i++)
	{
		document.getElementById(allContent[i].getAttribute('id')).style.display = 'none';
	}

	// Change class names of selected Tab to turn display to block and color to orange.

	resultPagePlaceObject.className = 'selectedTab';
	tabContentObject.style.display = 'block';

	// Change class names of all 'div' inside the content to display block.

	var allDiv = tabContentObject.getElementsByTagName('div');
	for (var i = 0; i < allDiv.length ; i++)
	{
		document.getElementById(allDiv[i].getAttribute('id')).style.display = 'block';
	}

	// This is needed for firefox. This goes back through and re-checks all the boxes since they were unselected by firefox.
	// This happens when you create a new tab. Firefox will reset all checkboxes in the other tabs when a new one is created. 

	var agt=navigator.userAgent.toLowerCase();
	var browser = "";
	if (agt.indexOf("phoenix") != -1) browser = 'Phoenix';
	if (agt.indexOf("firefox") != -1) browser = 'Firefox';
	if (agt.indexOf("safari") != -1) browser = 'Safari';
	if (agt.indexOf("msie") != -1) browser = 'Internet Explorer';
	if (agt.indexOf("netscape") != -1) browser = 'Netscape';
	if (agt.indexOf("mozilla/5.0") != -1) browser = 'Mozilla';

	if (tabArray[header] && document.getElementById(tabArray[header].selectAllID) && browser != 'Internet Explorer')
	{
		var checkedNames = tabArray[header].checkedNames;
		var checkedIDs = tabArray[header].checkedIDs;
		var numberResults = tabArray[header].numberResultTabData;
		if (checkedNames.length > 0)
		{
			if ( checkedNames.length == numberResults)
			{
				document.getElementById(tabArray[header].selectAllID).checked = true;
			}
			for (var j = 0; j < checkedNames.length; j++)
			{
				document.getElementById(checkedIDs[j]).checked = true;
			}
		}
	}
}

function closeTab(header, allTabContentSectionID, tabLinksSectionID, searchTabSectionID, searchTabID)
{
	var resultPagePlaceID = tabArray[header].resultPagePlaceID;
	var tabContentPlaceID = tabArray[header].tabContentPlaceID;
	var tabChild = document.getElementById(resultPagePlaceID);
	var contentChild = document.getElementById(tabContentPlaceID);
	var contentParent = document.getElementById(allTabContentSectionID);
	var tabParent =document.getElementById(tabLinksSectionID);

	// Remove from display and delete from the tabArray.

	tabParent.removeChild(tabChild);
	contentParent.removeChild(contentChild);
	delete tabArray[header];

	expandTab(tabLinksSectionID, allTabContentSectionID, searchTabSectionID, searchTabID);
}

var textareaID = 0;
function report(header)
{
	//Output the report tool.

	var reportToolID = tabArray[header].reportToolID;
	var reportToolObject = document.getElementById(reportToolID);
	var string = "";
    var idName = "commentBox" + textareaID;
    
	string = "<input type='button' value='Print View' onclick=\"printView('" + header + "', '" + idName + "');\">";
	string = string + "<input type='button' name='txt' value='Save as .txt' onclick=\"txt('" + header + "', '" + idName + "');\" />";
	string = string + "<input type='button' name='csv' value='Save as .csv' onclick=\"csv('" + header + "', '" + idName +  "');\" />";
	string = string + "<BR><BR>Add any comments for the report.<BR><TEXTAREA id='" + idName + "' COLS=40 ROWS=4></TEXTAREA>";
	reportToolObject.innerHTML = string;
	textareaID++;
}

function txt(header, commentBoxID)
{
	// If at least one name is checked then open the txt document.

	if (tabArray[header].checkedNames && tabArray[header].checkedNames.length > 0)
	{
		var majorCategoryID = tabArray[header].majorCategoryID;
		var minorCategoryID = tabArray[header].minorCategoryID;
		var searchString = tabArray[header].searchString;		
		var dataString = "";
		var detailArray = new Array();
		var names = tabArray[header].checkedNames;
		var j = 0;
		var headers = tabArray[header].headers;
		
		headerString = headers.join(',,,');
		commentBoxObject = document.getElementById(commentBoxID);
		
		for (i in names)
		{
			detailArray[j] = tabArray[header].ids[names[i]].details.join(",,,");
			j++;
		}
		dataString = detailArray.join(":::");

		var xmlHeader = '<txtRequest>' +
							'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
							'<minorCategoryID>' + minorCategoryID + '</minorCategoryID>' +
							'<searchString>' + searchString + '</searchString>' +
							'<commentBoxValue>' + commentBoxObject.value + '</commentBoxValue>' +
							'<headerString>' + headerString + '</headerString>' +
							'<dataString>' + dataString + '</dataString>' +
						'</txtRequest>';
							
		fetchData(txtReportPHP, xmlHeader, "");
	}
}

function csv(header, commentBoxID)
{
	// If at least one name is checked then open the csv document.

	if (tabArray[header].checkedNames.length > 0)
	{
		var majorCategoryID = tabArray[header].majorCategoryID;
		var minorCategoryID = tabArray[header].minorCategoryID;
		var searchString = tabArray[header].searchString;		
		var headers = tabArray[header].headers;		
		var dataString = "";
		var detailArray = new Array();
		var names = tabArray[header].checkedNames;
		var j = 0;
		
		headerString = headers.join(',,,');
		commentBoxObject = document.getElementById(commentBoxID);
		
		for (i in names)
		{
			detailArray[j] = tabArray[header].ids[names[i]].details.join(",,,");
			j++;
		}
		dataString = detailArray.join(":::");
		
		var xmlHeader = '<csvRequest>' +
							'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
							'<minorCategoryID>' + minorCategoryID + '</minorCategoryID>' +
							'<searchString>' + searchString + '</searchString>' +
							'<commentBoxValue>' + commentBoxObject.value + '</commentBoxValue>' +
							'<headerString>' + headerString + '</headerString>' +
							'<dataString>' + dataString + '</dataString>' +
						'</csvRequest>';	
						
		fetchData(csvReportPHP, xmlHeader, "");
	}
}

function printView(header, commentBoxID)
{
	// If at least one name is checked then open a new page with the table.

	if (tabArray[header].checkedNames.length > 0)
	{
		var majorCategoryID = tabArray[header].majorCategoryID;
		var minorCategoryID = tabArray[header].minorCategoryID;
		var searchString = tabArray[header].searchString;	
		var headers = tabArray[header].headers;
		var string = "";
		commentBoxObject = document.getElementById(commentBoxID);
		printWindow = window.open();
		
		string = "<p>IRMIS2: Integrated Relational Model of Installed Systems<BR>";
		string += "Major Category: " + majorCategoryID + "<BR>Minor Category: " + minorCategoryID + "<BR>";
		string += "Search String: " + searchString + "<BR>";
		string += "Comments:" + commentBoxObject.value + "<BR></p>";
		string += "<table width='75%' border='1' cellspacing='0' cellpadding='2'>";
		string += "<tr>";
		for (var i = 0; i < headers.length; i++)
		{
			if (headers[i].search(/ID/) == -1)
			{
				string += "<td>";
				string += headers[i];
				string += "</td>";
			}			
		}
		string += "</tr>";
		var names = tabArray[header].checkedNames
		for (var i = 0; i < names.length ; i++)
		{
			string += "<tr>";
			for (var j = 0; j < headers.length; j++)
			{
				if (headers[j].search(/ID/) == -1)
				{
					string += "<td>";
					string += tabArray[header].ids[names[i]].details[j];
					string += "</td>";
				}
			}
			string += "</tr>";
		}
		string += "</table>";
		printWindow.document.write(string);
		printWindow.document.close();
	}
}

function selectAll(header)
{
	var majorCategoryID = tabArray[header].majorCategoryID;
	var minorCategoryID = tabArray[header].minorCategoryID;
	var searchString = tabArray[header].searchString;
	var selectAllID= tabArray[header].selectAllID;

	var selectAllObject = document.getElementById(selectAllID);
	var checkedIDs = new Array();
	var checkBoxIDs = new Array();

	// This either selects all the checkboxes and populates the table, or 
	// unselects them all and deletes the table.
	if (selectAllObject.checked == true)
	{
		var numberResults = tabArray[header].numberResultTabData;
		for (var i = 0; i < numberResults; i++)
		{
			var checkboxID = header + i;
			var checkboxObject = document.getElementById(checkboxID);
			checkboxObject.checked = true;
			checkedIDs[i] = checkboxObject.value;
			checkBoxIDs[i] = i;
		}
	}
	else
	{
		var numberResults = tabArray[header].numberResultTabData;
		for (var i = 0; i < numberResults; i++)
		{
			var checkboxID = header + i;
			var checkboxObject = document.getElementById(checkboxID);
			checkboxObject.checked = false;
			checkedIDs[i] = checkboxObject.value;
			checkBoxIDs[i] = i;
		}
	}
	openResults(header, checkedIDs.join(','), checkBoxIDs.join(','));
}

function openResults(header, checkedIDsList, checkBoxIDsList)
{
	var majorCategoryID = tabArray[header].majorCategoryID;
	var minorCategoryID  = tabArray[header].minorCategoryID;
	var searchString = tabArray[header].searchString;

	var checkedIDsArray = checkedIDsList.split(',');
	var checkBoxIDsArray = checkBoxIDsList.split(',');
	var checkedCount = checkedIDsArray.length;
	var xmlNewIDs = '';

	// This for loop finds all the checkboxes selected and adds the id and name to arrays. 
	// Also, if the details haven't be brought back yet and saved, then it only sends those names
	// to go get the details for. 
	for (var i = 0; i < checkedCount; i++)
	{
		if (document.getElementById(header + checkBoxIDsArray[i]).checked == true)
		{
			if (!tabArray[header].ids[checkedIDsArray[i]])
			{
				tabArray[header].checkedNames[checkBoxIDsArray[i]] = checkedIDsArray[i];
				tabArray[header].checkedIDs[checkBoxIDsArray[i]] = header + checkBoxIDsArray[i];
				xmlNewIDs += '<idValue>' + checkedIDsArray[i] + '</idValue>';
				tabArray[header].numberChecked++;
			}
			else if (!tabArray[header].checkedNames[checkBoxIDsArray[i]])
			{
				tabArray[header].checkedNames[checkBoxIDsArray[i]] = checkedIDsArray[i];
				tabArray[header].checkedIDs[checkBoxIDsArray[i]] = header + checkBoxIDsArray[i];
				tabArray[header].numberChecked++;
			}
		}
		else if (tabArray[header].checkedNames[checkBoxIDsArray[i]])
		{
			delete tabArray[header].checkedNames[checkBoxIDsArray[i]];
			delete tabArray[header].checkedIDs[checkBoxIDsArray[i]];
			tabArray[header].numberChecked--;
		}
	}
	var xmlHeader = '<tableRequest>' +
						'<majorCategoryID>' + majorCategoryID + '</majorCategoryID>' +
						'<minorCategoryID>' + minorCategoryID + '</minorCategoryID>' +
						'<searchString>' + searchString + '</searchString>' +
						xmlNewIDs +
					'</tableRequest>';
	fetchData(tableDataPHP, xmlHeader, tabArray[header].reportPlaceID);
}

function showHelp(helpSectionID)
{
	var helpSectionObject = document.getElementById(helpSectionID)
	fetchData(helpPHP, "", helpSectionID);
	helpSectionObject.style.visibility = 'visible';	
}

function hideHelp(helpSectionID)
{
	var helpSectionObject = document.getElementById(helpSectionID)
	helpSectionObject.innerHTML = "";
	helpSectionObject.style.visibility = 'hidden';
}