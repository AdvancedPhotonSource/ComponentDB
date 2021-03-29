/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */


const VERIFY_VIEW_MAX_ATTEMPTS = 25;

var sessionTimeout = false;

window.onload = function () {
    var thisPageId = document.getElementById('viewCurrentPageIdHiddenText').value;
    var singleTabKey = document.getElementById('singleTabViewKeyHiddenText').innerHTML;

    var currentPageUrl = window.location.href;

    if (singleTabKey !== "") {
        if (thisPageId === "") {
            // New page generate a view UUID 
            prepareNewViewID();
        } else {
            // Old page prepare to start verify 
            prepareTestExistingViewID();
        }
    } else {
        startPageVerify(false);
    }
};

function setCurrentPageViewId() {
    var viewUUID = document.getElementById('viewOpenPageIdHiddenText').innerHTML;
    document.getElementById('viewCurrentPageIdHiddenText').value = viewUUID;
    startPageVerify();
}

function startPageVerify(includeTabCheck = true) {    
    // See if already generated
    var timeHash = document.getElementById('currentTimeHashHiddenText').innerHTML;
    var failureCount = 0;

    function verifyView() {
        setTimeout(function () {
            if (sessionTimeout) {
                invalidateCurrentSession();
                return;
            }
            verifyViewOpenPageIdHiddenText();
            var newTimeHash = document.getElementById('currentTimeHashHiddenText').innerHTML;
            if (newTimeHash != timeHash) {
                timeHash = newTimeHash;
                failureCount = 0;
                verifyView();
            } else {
                failureCount++;
                if (failureCount == VERIFY_VIEW_MAX_ATTEMPTS) {
                    PF('sessionLostDialogWidget').show();
                } else {
                    verifyView();
                }
            }
        }, 2500);
    }

    verifyView();
    if (includeTabCheck) {
        debugger; 
        verifyViewOpenPageIdHiddenText();
    }
}

function sessionTimedOutEvent() {
    PF('sessionTimeoutDialogWidget').show();
    sessionTimeout = true;
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
