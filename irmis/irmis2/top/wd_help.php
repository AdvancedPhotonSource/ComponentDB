<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="../common/irmis2.css">
<title>IRMIS Web Viewer Help</title>
<link rel="icon" type="image/png" href="../common/images/irmisiconLogo.png" />
</head>

<body>
<?php include('analytics.php'); ?>
<div class="helptext">

<img src="../common/images/irmis2Logo.png" alt="IRMIS2 Logo"/>
<div class="maintitle">IRMIS WEB VIEWER HELP</div>

<ul>
<li class="helplist"><a class="hyper" href="#overview">Overview</a></li>
<li class="helplist"><a class="hyper" href="#report">Report Tools</a></li>
<li class="helplist"><a class="hyper" href="#searchtips">Search Tips</a></li>
<ul>
<li class="helplist1"><a class="hyper" href="#iocsearches">IOC Searches</a></li>
<li class="helplist1"><a class="hyper" href="#racksearches">Rack Searches</a></li>
</ul>
<li class="helplist"><a class="hyper" href="#viewers">The Web Desktop Viewers</a></li>
<ul>
<li class="helplist1"><a class="hyper" href="#ac">AC Power Info</a></li>
<li class="helplist1"><a class="hyper" href="#aoi">AOI Info</a></li>
<li class="helplist1"><a class="hyper" href="#component">Component Types</a></li>
<ul>
<li class="helplist2"><a class="hyper" href="#ctsearch">Component Type Search Results</a></li>
<li class="helplist2"><a class="hyper" href="#port">Port/Cable Information</a></li>
</ul>
<li class="helplist1"><a class="hyper" href="#ioc">IOC Info</a></li>
<li class="helplist1"><a class="hyper" href="#network">Network Info</a></li>
<li class="helplist1"><a class="hyper" href="#plc">PLC Info</a></li>
<li class="helplist1"><a class="hyper" href="#racks">Rack Contents Info</a></li>
<li class="helplist1"><a class="hyper" href="#server">Server Info</a></li>
<li class="helplist1"><a class="hyper" href="#spares">Spares Info</a></li>
</ul>
</ul>

<div class="title"><a name="overview">Overview</a></div>
<div class="helpparagraph">
This documentation is intended to give an overview of the different IRMIS viewers, how they were intended to be used and how the viewers link together to allow you to find as little or as much information as you need on a given system or component.  Many of the IRMIS features are intuitive, but this document may help you find your way a little easier and show you different features you didn't know about.
IRMIS Web Desktop Navigation
Choosing any of the Web Desktop Viewers displays a self contained environment for searching for specific types of components or information.  However, many of the search results pages of these viewers will display links to other parts of IRMIS.  The intent of IRMIS is to give an overview of what equipment is installed, what AOI's are available and how all this equipment fits together and interacts with each other.
</div>

<div class="helpparagraph">
Virtually all of the information that the IRMIS Web Viewers show is obtained from the components, IOC's, cables, PLC's, and PV's that have been entered into the <em><strong>idt</strong></em> (IRMIS Desktop).  With that in mind, it's important to remember that any information seen in the viewers is a direct result of the infomation entered in the idt, either correctly or in some instances, incorrectly.  Many of the inconsistancies that are seen from time to time in the viewers can be easily corrected by verifying the fields in the idt have been entered consistantly.</div>

<div class="helpparagraph">
All of the data that comprises IRMIS is held in mySQL tables.  The Web Viewers obtain their search results through a combination of php and mySQL and then display that data with php and html.  Changes made in the idt:component application (adding new components or changing the location of components) are seen immediately in the Viewers.  IRMIS data that gets populated via a "<em>crawler</em>" may take additional time to see in the Viewers depending on the frequency a particular crawler is run and how long it takes it to finish.</div>

<div class="title"><a name="report">Report Tools</a></div>
<div class="helpparagraph">
Many of the search results will show the Report Tools at the end of the search results.  If the results list is long, you can use the Report Tools link found at the top of the page next to the number of search results found, to take you directly to them.
Comments can be added to your report by typing in the Report Comments box.  These comments will be displayed on any report selected.</div>

<ul>
<li class="helplist"><strong>View Print Version</strong> – displays the search results in a more printer friendly html version, and shows a Print button to print these results.</li>
<li class="helplist"><strong>Save as .txt</strong> – This button will format the search results into a text based file and give you the opportunity to select what application should be used to display it.  From there, the output can be saved or printed.</li>
<li class="helplist"><strong>Save as .csv</strong> – This button will format the search results into a .csv (comma separated values) file, usually opened in Microsoft Excel.  From there, the output can be saved or printed.</li>
</ul> 

<div class="title"><a name="searchtips">Search Tips</a></div>
<div class="helpparagraph">
There is a vast amount of information that is accessable with the IRMIS Viewers.  Most of it is straight forward, but there are some items that it may be helpful to know about when searchining for special components such as IOC's, Racks, Moxa's, etc..  These devices consistantly have a lot of information associated with them.</div>

<span class="title"><a name="iocsearches">IOC Searches</a></span>
<div class="helpparagraph">
Several search options are available when searching for specific IOC information.  The obvious one is the IOC viewer.  This is usually the best place to start since other pages are linked through here as well.  Some things to remember:
<ul>
<li class="helplist">The two checkboxes at the top of the search criteria area default to Accelerator IOC's.  If you want search results to include Beamline IOC's, you need to also check this box.</li>
<li class="helplist">The IOC Status pulldown defaults to All Active.  All Active includes Ancillary, Production and Development.  The Inactive selection is just the IOC's that have been decommissioned.  An alternative to using the All Active selection is All.  To search through All IOC's, select this.  Remember that the checkbox for Accelerator and Beamline also factors in.</li>
<li class="helplist">Inactive IOC's will appear slightly greyed out</li>
<li class="helplist">When performing multiple IOC searches, make sure all the search criteria makes sense, or you may wind up with a result of No IOC's Found.</li>
</ul></div>
<div class="helpparagraph">There are also less direct ways of getting to the IOC info you want, depending on the information you have.  For instance, you could use the Component Type Viewer if you didn't know the IOC name, but you knew it was a Tracewell IOC or had a 2100 processor in it.  The results of using the Component Type Viewer always show the IOC associated with the results (if applicable).  Clicking on this link will also take you to the IOC you want.</div>

<span class="title"><a name="racksearches">Rack Searches</a></span>
<ul>
<li class="helplist">The Rack Viewer is more powerful than it's name might suggest.  This viewer is designed to start at the rack level and allow you to work your way down to smaller and smaller components.  For example, searching on the room SR_Mezz_Area-02 will bring up a listing of racks contained in sector 2 of the Storage Ring mezzanine.  Each one has a Contents link to allow you to see what each rack houses.  When you follow a Contents link, the results view changes a bit and shows the "enclosure" you selected along the left side.  If any of the components housed inside the rack contain (house) other components, you will be presented with a Contents link to follow.</li>
<li class="helplist">This method becomes very handy when you come across an IOC or a Moxa server, or a chassis with a PLC inside.</li>
<li class="helplist">A lot of information can be found by continuing to follow the Contents links to find out how Components are put together and what they connect to.</li>
</ul>


<div class="title"><a name="viewers">THE WEB DESKTOP VIEWERS</a></div>
<ul>
<div class="title"><a name="ac">AC POWER INFO</a></div>
<div class="helpparagraph">
The AC Power viewer allows you to find the source power distribution for specific racks and components.  The three search criteria provided are used to find a rack in question.  This search is from a "rack" point of view.  It's designed to show the AC panel and circuit breaker that provide the power to the rack in question.</div>

<ul>
<li class="helplist"><strong>Room</strong> – Choosing a room with this pull down will show all the racks in a selected room at the APS.  A "power" link is provided to find the source AC for any of the racks listed in the room.</li>
<li class="helplist"><strong>Group Ownership</strong> – This pull down allows a more fine tuned set of results by only displaying the racks that are owned by a specific group.</li>
<li class="helplist"><strong>Rack/Enclosure Name</strong> – This free form text box allows you to type the name of the rack you're looking for.  Keep in mind that there are many racks at the APS that have the same name.  For instance, if you type in a rack name of 01, there will be quite a few that show up in the results list.</li>
</ul>



<div class="title"><a name="aoi">AOI INFO</a></div>
<div class="helpparagraph">
Information about AOI (Applications Organizing Index) can be found in the ICMS document <a target="_new" href="https://icmsdocs.aps.anl.gov/docs/groups/anl/@apsshare/@controls/documents/report/aps_1192175.pdf">Controls AOI Applications Organizing Index - What You Need To Know</a>
</div>


<div class="title"><a name="component">COMPONENT TYPES</a></div>
<div class="helpparagraph">
Component Types lets you search for information about a specific component type installed or used at the APS.  The basic search criteria are:</div>

<ul>
<li class="helplist"><strong>Name/Description</strong> – Free form text field that can search for either the Name or Description of a component.  Wildcards are not necessary and any string entered, searches through both the Component Name field and the Description field in the IRMIS tables.  Clicking on the Comp Search  button or hitting "return" after typing in the search term will start the search process.</li>
<li class="helplist"><strong>Manufacturer</strong> – The default for the Manufacturer criteria is "All", but any one manufacturer can be chosen if looking for component types that are made by a specific manufacturer.</li>
<li class="helplist"><strong>Form Factor</strong> – The default for the Form Factor criteria is "All", but any one form factor can be chosen if looking for a specific form factor.</li>
<li class="helplist"><strong>Function</strong> – The default for the Function criteria is "All", but any one function can be chosen if looking for a specific one.</li>
<li class="helplist"><strong>Cognizant Person</strong> – The Cognizant Person is usually the person that has had a hand in the design of an APS designed component, or the person that has had a significant amount to do with the implementation of a component that has been manufactured by an outside manufacturer.</li>
</ul>

<div class="helpparagraph">Any of the search criteria can be combined to narrow a search, or any can be used by themselves to get a broader range of results.  
Keep in mind that typing in the name of a manufacturer in the Name/Description field may not yield the complete set of results expected because the Name or Description fields may not contain the manufactures name.  It is better to use the Manufacturer pull down to find the complete set of components for a given manufacturer.</div>

<div class="titlesmall"><a name="ctsearch">Component Type Search Results Page</a></div>
<div class="helpparagraph">
Search results are displayed after a search has been executed.  If no search results are found, the search results area will inform you of this by displaying "No Component's found: please try another search".
Searches finding actual results will display these results in tabular form.  The number in parentheses to the right of the Component Name is the <em>component_type_id</em> number from the IRMIS tables (hovering over this number with the curser will also show this).  For the most part, this number will only be used on rare occasions for verifying the validity of the returned results.
To the far right of the results page is a column with a link to a page that contains more detailed information about the selected Component Type.</div>  

<div class="helpparagraph">
On the Details page, the links for Feature Sheet and Quick Reference Manual will display even more information about the Component Type.  Usually, a picture of it, a feature highlight, how to use it, how to configure it, or even the manufacturers manual for it. 
On the Component Instances line, if the component has been installed (using the idt component tool), the number of installed components (instances) is displayed and there are two choices available to show the exact location of each of the installed instances of this component type.</div>

<div class="helpparagraph">
The results of choosing either link show the location information a bit differently.  In some cases, the Expanded Location Path link will show additional location information that doesn't fall under the categories of Rack, Room, and Building.
Regardless of which Location Path link is selected, the page that's displayed shows the Component Type name followed by a number in parentheses.  This number is the actual <em>component_id</em> number in the IRMIS tables.  It is the identification number that IRMIS uses to find that instance of a component type.  This number can also be found in the <em>idt:component</em> application (Java based).  If uncertain of which component your looking at in either the idt or the web based viewer, you can always see this number in both of these applications to help pinpoint the exact component.</div>

<div class="titlesmall"><a name="port">Port  / Cable Information</a></div>
<div class="helpparagraph">
Port information for a given component is accessed through the Component Types viewer.  Since port information is relevant to a specific instance of a component type, it's found by first searching for the component type you're interested in with the Component Types viewer.  Once you find the component type, select the Details link for it.  On the resulting page, select either the Short or Extended Location Path.  The installed instances for that component type are shown.  You can find the exact component you're looking for by looking at the location information, the IOC that controls it, or use the <em>component_id</em> number (found in parentheses after the Component Type name) to help locate it in the <em>idt:component</em>.</div>

<div class="helpparagraph">
If the ports have been defined for this component type, then clicking on the Ports link will, at the very least, take you to the results page and list the Ports that have been defined for this component type.  If the Port has been connected to another component via a cable (by way of the <em>idt:cable</em> application), this information will also be shown.</div>

<div class="helpparagraph">
Following the Port you are interested in to the right across the results page, will show the Port Type (physical connector) and the number of pins or connections it has.  Moving further to the right shows the cable information for the cable that is connected to the port.  This information may or may not be filled in as it's up to the discretion of the person entering the cable information in the <em>idt:cable</em> application.  Farther to the right, the component at the opposite end of the cable is shown.  The Location of this component is displayed along with the port name that the cable is connected to.  It will also show any IOC that controls that opposite component and has links for the opposite component name (which takes you to that Component Type details page for that component).  The <em>component_id</em> of the opposite component is also shown, and clicking on this link rebuilds the results page and displays that specific componet on the left side of the results page and shows all of it's ports and any cables connected to them.  Essentially, re-executing the cable/port search for the component that you clicked on.</div>

<div class="helpparagraph">
By using this method of continually following cables from one device to the next, it is possible to track a connection or signal from an originating component through Light Boxes and other components.  This has the potential of becoming a great troubleshooting aid by allowing you to trace down installed cables or the signals that are on them.</div>


<div class="title"><a name="ioc">IOC INFO</a></div>
<div class="helpparagraph">
Most any information you want to know about an IOC can be found on the IOC Info viewer.  The search criteria for finding IOC's is extensive.  You can search for IOC information using the following criteria.</div>

<ul>
<li class="helplist"><strong>IOC Name</strong> – Input a string to find an IOC by it's name.  You can input all or part of it, no wildcards are necessary.</li>
<li class="helplist"><strong>System</strong> – Use this pull down to select IOC's from a specific system only.</li>
<li class="helplist"><strong>IOC Status</strong> – The IOC Status pull down defaults to All Active.  Meaning any IOC that is a functioning production IOC.  Other choices are 
<ul>
<li class="helplist1"><strong>All</strong> – For finding every IOC regardless of their Active status or whether they are in production or not.</li>
<li class="helplist1"><strong>Production</strong> – This is an IOC in service and accessible and booting from the system area.</li>
<li class="helplist1"><strong>Inactive</strong> – This is an IOC that has been removed from service.</li>
<li class="helplist1"><strong>Ancillary</strong> – This is an IOC that is used as a production IOC but usually only for a short defined period of time (iocrover1 is an example of this).</li>
<li class="helplist1"><strong>Development</strong> – This is an IOC not quite ready for Production.  It is installed, but probably booting from someone's test area while they work out the bugs.</li>
</ul>
<li class="helplist"><strong>Cognizant Developer/Technician</strong> – It has been customary to specify both a developer and a technician for each installed hardware IOC (and some Soft IOC's).  These are the people most likely able to answer questions about specific IOCs and take care of hardware and software issues.</li>
</ul>

<div class="helpparagraph">
Search results display general information about IOCs including their Status, System, Location, Cognizant's and the EPICS release that they are running.  At the far right of the results page are three links that will give more detailed information for each IOC.</div>

<ul>
<li class="helplist"><strong>General</strong> – Shows information pertaining to the function of the IOC, it's IP and Host address, boot log, and a link to view it's startup file.</li>
<li class="helplist"><strong>Network</strong> – Shows all the information pertaining to the IOC's primary and secondary Ethernet connection as well as it's terminal console connection.</li>
<li class="helplist"><strong>Contents</strong> – Shows a graphical representation of what boards are currently installed in both the front and rear slots of this IOC.</li>  
</ul>

<div class="helpparagraph">
From this page, there are also links to the Port information of each installed board and the general information for each installed board.</div>


<div class="title"><a name="network">NETWORK INFO</a></div>
<div class="helpparagraph">
Currently, the Network Info Viewer offers four ways of viewing network information.  Clicking on any of these four buttons will display the results as they relate to the different hardware that in the network infrastructure.</div>

<ul>
<li class="helplist"><strong>Show Network Switches</strong> – Clicking on this button displays a list of the network switches found on the left portion of the results section.  By clicking on the "Ports" link, an additional display on the right hand portion shows the actual ports that are used on that switch.  Clicking on the "Monitor" link, connects to the web server inside the switch itself to show current details about the status of the switch itself.</li>
<li class="helplist"><strong>Show Terminal Servers</strong> – Clicking on this button will display a list of the terminal servers found on the left portion of the results area.  Clickin on the "Ports" link, displays a list of the active ports for that switch.  It also shows what device is connected to that port.</li>
<li class="helplist"><strong>Show Media Converters</strong> – Clicking on the Media Converters button displays a list of media converters found.  Media converters are being phased out of the infrastructure in favor of faster fiber optic switches and terminal servers, but there are currently a couple of them still in existance.  Clicking on the "Ports" link will show the actual ports for this media converter and any device connected to it.</li>
<li class="helplist"><strong>Show IOC Connections</strong> – The final button will display a list of IOC's found, and the terminal server, primary and secondary ethernet switch that the IOC is using.</li>
</ul>


<div class="title"><a name="plc">PLC INFO</a></div>
<div class="helpparagraph">
The PLC Info page shows detailed information about any of the installed Programmable Logic Controllers.  If you know the name of the PLC you're looking for, it can be entered either all or in part, in the PLC Name text box.  Selecting the "PLC Search button" will find all installed PLC's with that name and display a set of search results that contains the following information.  </div>

<ul>
<li class="helplist"><strong>PLC Name</strong> – This is the unique name of the PLC.</li>
<li class="helplist"><strong>Description</strong> – A short description of what the PLC does.  This also supplies a button to edit this information in the event that the role of this PLC changes.</li>
<li class="helplist"><strong>Cognizant Group</strong> – The group responsible for this PLC.</li>
<li class="helplist"><strong>Location</strong> – The specific location of the actual PLC processor from the PLC base to the Room it resides in.</li>
<li class="helplist"><strong>IOC Name</strong> – The IOC that reads the collected data back to the control system.</li>
</ul>

<div class="helpparagraph">
Clicking on the displayed name itself takes you to the PLC General Information page.  This page displays more information about the PLC, such as the parent IOC, the Reference PV, Hardware Link, current code version and a description of the function of that particular PLC.
Below this information, there are links into ICMS that retrieve the most current PLC ladder logic and display it in pdf form.  There is also a link to any touch screen code.  Touch screen displays will only be viewable with the correct touch screen application software.</div>

<div class="helpparagraph">
Selecting the "Administrative Page" button displays a different set of search results.  The idea behind the Administrative button is to show not only the PLC Name and the IOC that communicates with it, but also to show the name of the PV that holds the PLC's version number.  By reading this PV, the PLC Info Viewer is able to show what ladder logic version is running on all the PLC's in the search results.  It then grabs the code version number from the ladder logic that's checked into ICMS.  It prints the ICMS code version, the ladder loic author or responsible person, the ladder logic code version read from the PV and determines whether the two versions match.  The results of that determination are displayed in the last column.</div>

<div class="helpparagraph">
The purpose of this excersise is to be sure that the most current ladder logic is checked into ICMS.  Anyone that needs to look at the code or troubleshoot a PLC based system will be assured that the they can view the ladder logic (in ICMS) that is currently running on the PLC.</div>


<div class="title"><a name="racks">RACKS INFO</a></div>
<div class="helpparagraph">
The Racks Info viewer will show all the equipment in a specified rack.  There are three search criteria available to help in finding the specific rack in question.

<ul>
<li class="helplist"><strong>Room</strong> – This pull down will allow finding all the racks in a specified room in the APS project.</li>
<li class="helplist"><strong>Group Ownership</strong> – Search for racks by group ownership.</li>
<li class="helplist"><strong>Rack / Enclosure Name</strong> – Type a search string to used to search for a rack or enclosure name.</li>
</ul>

<div class="helpparagraph">
Any of the search criteria can be used either alone or in conjunction with each other.
The results page starts out with the Enclosure Name.  This can be either a rack, or some other equipment housing enclosure (wall mount cabinet, power supply enclosure, water station, etc.).  The number in parentheses to the right of the Enclosure Name is its <em>component_id</em> number.  The specific instance number that IRMIS has assigned that particular component.</div>

<div class="helpparagraph">
The next column shows what type of enclosure it is (Rack, generic enclosure, power supply or RF cabinet), followed by the group ownership.  To view components or devices housed by this enclosure, click on the Contents link.  The results will display all the components located in that rack or enclosure.  Various information is also displayed about each component including it's location in the rack, description and manufacturer.  If there is a Contents link shown for one of the housed components, clicking on it will display all the components that it subsequently houses, and so on.  Any of the links displayed under the Component Type heading will direct you to the details of that particular component type.
The Report Tools are located at the end of any of the search results.  More info about the Report Tools is available in the Overview section.</div>
</div>


<div class="title"><a name="server">SERVER INFO</a></div>
<div class="helpparagraph">
The Server Info viewer allows you to search for a particular APS Server.  You can find a particular Server by using any or all of the three search criteria fields or pull downs.</div>

<ul>
<li class="helplist"><strong>Server Name</strong> – This is a free form text field that will allow searching for a specific server name (or a partial server name).  Typing in a search string and hitting Return will begin the search.  Or, alternatively you could hit the Server Search button.</li>
<li class="helplist"><strong>Operating System</strong> – Use this pull down to find Servers running a specific operating system.</li>
<li class="helplist"><strong>Cognizant Person</strong> – Use this pull down to find Servers maintained by a specific person.</li>
</ul>

<div class="helpparagraph">
All of these search criteria can be used in conjunction with one another and the more criteria you enter the more refined your search results will be.
The search results displays the server name, it's location, a description of the servers' functions, the operating system it runs and it's cognizant person.
The Report Tools are available to capture the search results in three different ways.  More about the Report Tools can be found in the Overview section.</div>


<div class="title"><a name="spares">SPARES INFO</a></div>
<div class="helpparagraph">
The Spares Info Viewer shows spare module information as it relates to the Controls Group.  Spare modules are generally located in the Controls Group Spares Cage, but can be located elsewhere especially if storing a particular module is not practicle.  Several search criteria are available to allow searching for the location of a modules' spare.
</div>

<ul>
<li class="helplist"><strong>Name / Description</strong> - This free form test field will search for a string in both the Name and Description fields.</li>
<li class="helplist"><strong>Manufacturer</strong> - Search for a component by it's manufacturer.</li>
<li class="helplist"><strong>Cognizant Person</strong> - This can be helpful if you know who is the person that would be associated with a module's operation or installation.</li>
</ul>

<div class="helpparagraph">
There are also three checkboxes to help narrow down the search results for spare modules.
</div>

<ul>
<li class="helplist"><strong>Spares Missing</strong> - To find out which modules are missing spares.</li>
<li class="helplist"><strong>Critical Spares</strong> - Shows which modules are deemed by the group to be "critical".  Critical Spares are components that have a direct impact on the operation of the machine and are capable, by their inoperability, of dumping stored beam.</li>
<li class="helplist"><strong>In Spares Cage</strong> - Lists components that are actually stored in the Controls Spares Cage.</li>
</ul>

<div class="helpparagraph">
As with all the various search criteria for all the IRMIS viewers, any of the criteria can be used either alone or with others to refine and narrow the results.  It is important to note that the purpose of the Spares Cage is to house spare modules for emergency repairs to the Control system.  It is generally used for "after hours" repairs or for "during working hours" as a last resort, if another spare cannot be located elsewhere.  It is not intended that any of these components be used for "new installation", or personal use testing.</div>


</ul>

<div class="author">sjb 11/3/2011</div>


</body>
</html>