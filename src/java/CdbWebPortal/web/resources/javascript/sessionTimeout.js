/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

window.onload = function () {
    var thisPageId = document.getElementById('viewCurrentPageIdHiddenText').value;
    var currentPageUrl = window.location.href; 
    if (thisPageId === "") {
        // New page generate a view UUID 
        prepareNewViewID();
    } else {
        // Old page prepare to start verify 
        prepareTestExistingViewID(); 
    }
};

function setCurrentPageViewId() {
    var viewUUID = document.getElementById('viewOpenPageIdHiddenText').innerHTML;
    document.getElementById('viewCurrentPageIdHiddenText').value = viewUUID; 
    startPageVerify(); 
}

function startPageVerify() {
    // See if already generated

    function verifyView() {
        setTimeout(function () {
            verifyViewOpenPageIdHiddenText(); 
            verifyView();
        }, 2500);
    }

    verifyView();
    verifyViewOpenPageIdHiddenText(); 
}

function testExistingPageId() {
    if (verifyViewOpenPage()) {
        PF('invalidUseOfBackButtonDialogWidget').show(); 
    } else {
        startPageVerify(); 
    }    
}

function completeVerifyViewOpenPage() {  
    if (verifyViewOpenPage()) {
        PF('invalidUseOfMultipleTabsDialogWidget').show();
    }
}

function verifyViewOpenPage() {
    var newValue = document.getElementById('viewOpenPageIdHiddenText').innerHTML;
    var thisPageId = document.getElementById('viewCurrentPageIdHiddenText').value;
    
    return thisPageId != newValue;
}

function handleSessionTimeout(pathContextRoot)
{
    console.log('Handling session timeout')
    setTimeout(loadHomeView(pathContextRoot), 1000);  // wait for a second before loading home page
}

function loadHomeView(pathContextRoot)
{
    console.log('Loading home window after session timeout')
    window.location.replace(pathContextRoot + '/index.xhtml');
}
