/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

// Resolves the issue with touch devices not being able to use column filters. (Added in primefaces 6.2) 
$('.ui-column-filter').on('click touchend', function (e) {
    e.target.focus()
});

const linkRegex = new RegExp("^(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})(\.[a-zA-Z0-9]{2,})?");
let formId = '';

document.addEventListener('paste', pasteMarkdownTextArea);

function pasteMarkdownTextArea(event) {
    loadFormId(); 
    // Ignore non text area 
    let srcElement = event.srcElement;
    if (srcElement.id !== formId + ':markdownPropertyTextareaEdit') {        
        return;
    }

    pastedFiles = event.clipboardData.files;

    if (pastedFiles.length > 0) {
        event.preventDefault();

        // Clear Upload Ref
        uploadRef = document.getElementById(formId + ':lastFileReference');
        uploadRef.innerHTML = ""

        PF('loadingDialog').show();
        fileUploadWidget = PF('markdownAttachmentFileUploadWidget');
        uploadInput = document.getElementById(formId + ':markdownAttachmentFileUpload');
        uploadInput.files = pastedFiles;
        fileUploadWidget.upload();
    } else {
        var pastedText = event.clipboardData.getData("Text");
        if (pastedText !== undefined && linkRegex.test(pastedText)) {
            // Link was pasted. 
            event.preventDefault();
            var newData = " [Link Text](" + pastedText + ") ";
            addCustomDataToLogEntryValue(newData);
        }
    }
}

function pasteLatestFileReference() {
    loadFormId(); 
    
    let uploadRef = document.getElementById(formId + ':lastFileReference');
    let fileRefMd = uploadRef.innerHTML;

    if (fileRefMd === "") {
        console.error("File reference not set");
    }

    let newData = "\n\n" + fileRefMd + "\n\n";

    addCustomDataToLogEntryValue(newData);
}

function addCustomDataToLogEntryValue(newData) {
    loadFormId(); 
    
    let textArea = document.getElementById(formId + ':markdownPropertyTextareaEdit');

    let exitingValue = $(textArea).val();
    let curPos = textArea.selectionStart;

    newData = exitingValue.slice(0, curPos) + newData + exitingValue.slice(curPos);
    $(textArea).val(newData);
}

function loadFormId() {
    if (formId === '') {
        for (let form of document.getElementsByTagName('form')) {
            if (form.id.endsWith('ViewForm')) {                
                formId = form.id;
                break;
            }
        }
    }
    
    return formId; 
}