/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase.ColumnModel;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase.RowModel;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author craig
 */
@Named(ItemDomainImportWizard.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainImportWizard implements Serializable {

    public static final String CONTROLLER_NAMED = "importWizard";
    
    protected static final String tabSelectFile = "SelectFileTab";    
    protected static final String tabValidate = "ValidateTab";
    
    protected ImportHelperBase importHelper = null;
    
    protected String currentTab = tabSelectFile;
    
    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonFinish = true;
    private Boolean disableButtonCancel = false;
    
    // models for select file tab
    private Boolean disableButtonUpload = true;
    protected UploadedFile uploadfileData = null;
    
    public static ItemDomainImportWizard getInstance() {
        return (ItemDomainImportWizard) SessionUtility.findBean(
                ItemDomainImportWizard.CONTROLLER_NAMED);
    }
    
    public void registerHelper(ImportHelperBase helper) {
        reset();
        importHelper = helper;
    }

    public Boolean getDisableButtonPrev() {
        return disableButtonPrev;
    }

    public void setDisableButtonPrev(Boolean disableButtonPrev) {
        this.disableButtonPrev = disableButtonPrev;
    }

    public Boolean getDisableButtonNext() {
        return disableButtonNext;
    }

    public Boolean getDisableButtonFinish() {
        return disableButtonFinish;
    }

    public Boolean getDisableButtonCancel() {
        return disableButtonCancel;
    }

    public UploadedFile getUploadfileData() {
        return uploadfileData;
    }

    public void setUploadfileData(UploadedFile uploadfileData) {
        this.uploadfileData = uploadfileData;
    }

    public Boolean getDisableButtonUpload() {
        return disableButtonUpload;
    }
    
    public Boolean getRenderFileuploadData() {
        return uploadfileData == null;
    }
    
    public Boolean getRenderOutputData() {
        return uploadfileData != null;
    }
    
    public String getUploadfileDataString() {
        if (uploadfileData == null) {
            return "";
        } else {
            return uploadfileData.getFileName();
        }
    }
    
    public List<RowModel> getRows() {
        if (importHelper != null) {
            return importHelper.getRows();
        } else {
            return new ArrayList<>();
        }
    }
    
    public List<ColumnModel> getColumns() {
        if (importHelper != null) {
            return importHelper.getColumns();
        } else {
            return new ArrayList<>();
        }        
    }
    
    protected boolean readXlsFileData(UploadedFile f) {

        InputStream inputStream;
        HSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputstream();
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }
        
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        parseSheet(rowIterator);
        
        return true;
    }
    
    protected boolean readXlsxFileData(UploadedFile f) {

        InputStream inputStream;
        XSSFWorkbook workbook = null;
        try {
            inputStream = f.getInputstream();
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            return false;
        }
        
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        parseSheet(rowIterator);
        
        return true;
    }
    
    protected void parseSheet(Iterator<Row> rowIterator) {
        
        int rowCount = -1;
        while (rowIterator.hasNext()) {
            
            rowCount = rowCount + 1;
            
            Row row = rowIterator.next();
            
            // skip header rows
            if (rowCount < importHelper.getDataStartRow()) {
                continue;
            }

            CellType celltype;

            importHelper.parseRow(row);
        }
    }
    
    public void fileUploadListenerData(FileUploadEvent event) {
        
        uploadfileData = event.getFile();
//        String fileName = uploadfileData.getFileName();
//        String contentType = uploadfileData.getContentType();
//        System.out.println("uploaded: " + fileName + " type: " + contentType);
        
        if (!readXlsxFileData(uploadfileData)) {
            uploadfileData = null;
        }
        
        setEnablementForCurrentTab();
    }
        
    /**
     * Handles FlowEvents generated by the wizard component. Determines next tab
     * based on current tab, defaults to visiting all tabs but implements
     * special cases. Skips cableDetailsTab when the type is "unspecified" since
     * that tab is empty.
     */
    public String onFlowProcess(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();

        setEnablement(nextStep);

        currentTab = nextStep;

        return nextStep;
    }

    /**
     * Resets models for wizard components.
     */
    protected void reset() {
        
        currentTab = tabSelectFile;
        
        uploadfileData = null;
        
        importHelper = null;
        
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        String completionUrl = importHelper.getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Implements the finish operation, invoked by the wizard's "Finish"
     * navigation button.
     */
    public String finish() {
        String completionUrl = importHelper.getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    public void setEnablementForCurrentTab() {
        setEnablement(currentTab);
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    protected void setEnablement(String tab) {

        // default
        disableButtonPrev = true;
        disableButtonCancel = false;
        disableButtonFinish = true;
        disableButtonNext = true;

        if (tab.endsWith(tabSelectFile)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            disableButtonFinish = true;
            
            if (uploadfileData != null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }
        } else if (tab.endsWith(tabValidate)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            if (importHelper.isValidationOnly()) {
                disableButtonFinish = false;
            } else {
                disableButtonFinish = true;
            }
        }
    }
}
