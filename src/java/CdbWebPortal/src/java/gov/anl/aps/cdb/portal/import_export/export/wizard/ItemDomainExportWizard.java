/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.wizard;

import gov.anl.aps.cdb.portal.import_export.export.objects.GenerateExportResult;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craig
 */
@Named(ItemDomainExportWizard.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainExportWizard implements Serializable {
    
    public static final String CONTROLLER_NAMED = "exportWizard";

    protected static final String TAB_SELECT_FORMAT = "SelectFormatTab";
    protected static final String TAB_SELECT_OPTIONS = "SelectOptionsTab";
    protected static final String TAB_DOWNLOAD_FILE = "DownloadFileTab";
    
    protected String currentTab = TAB_SELECT_FORMAT;

    protected ImportHelperBase importHelper = null;
    
    private List<CdbEntity> exportEntityList = new ArrayList<>();

    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonFinish = true;
    private Boolean disableButtonCancel = false;
    
    private DomainImportExportInfo domainInfo = null;
    
    // models for select format tab
    private String selectedFormatName = null;
    
    // models for select options tab
    private String selectedFormatOption = null;
    
    // models for file download tab
    private StreamedContent downloadFile;

    public static ItemDomainExportWizard getInstance() {
        return (ItemDomainExportWizard) SessionUtility.findBean(
                ItemDomainExportWizard.CONTROLLER_NAMED);
    }

    public ImportHelperBase getImportHelper() {
        return importHelper;
    }
    
    public void setDomainInfo(DomainImportExportInfo info) {
        reset();
        domainInfo = info;
    }

    public List<CdbEntity> getExportEntityList() {
        return exportEntityList;
    }

    public void setExportEntityList(List<CdbEntity> exportEntityList) {
        this.exportEntityList = exportEntityList;
    }
    
    public List<ImportExportFormatInfo> getFormatInfoList() {
        if (domainInfo != null) {
            return domainInfo.getFormatInfoList();
        } else {
            return new ArrayList<>();
        }
    }
    
    public String getCompletionUrl() {
        if (domainInfo != null) {
            return domainInfo.getCompletionUrl();
        } else {
            return "";
        }
    }
    
    public List<String> getFormatNames() {
        return getFormatInfoList().stream().map(e -> e.toString()).collect(Collectors.toList());
    }

    public String getSelectedFormatName() {
        return selectedFormatName;
    }

    public void setSelectedFormatName(String selectedFormatName) {
        this.selectedFormatName = selectedFormatName;
    }

    public void selectListenerFormat() {
        setEnablementForCurrentTab();
    }

    public String getSelectedFormatOption() {
        return selectedFormatOption;
    }

    public void setSelectedFormatOption(String selectedFormatOption) {
        this.selectedFormatOption = selectedFormatOption;
    }

    public Boolean supportsCustomFormat() {
        return false;
    }
    
    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    /**
     * Handles radio button click for select mode tab of wizard.
     */
    public void clickListenerFormat() {
        setEnablementForCurrentTab();
    }
    
    public Boolean getDisableButtonPrev() {
        return disableButtonPrev;
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

    public List<HelperWizardOption> getWizardOptions() {
        if (importHelper == null) {
            return new ArrayList<>();
        } else {
            return importHelper.getExportWizardOptions();
        }
    }   
    
    public boolean isRenderOptions() {
        return (!getWizardOptions().isEmpty());
    }
    
    public String onFlowProcess(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();
        
        // handle transition from select format tab
        if ((currStep.endsWith(TAB_SELECT_FORMAT))
                && (nextStep.endsWith(TAB_SELECT_OPTIONS))) {
            
            if (exportEntityList.isEmpty()) {
                SessionUtility.addErrorMessage(
                    "No rows for export in filtered item list",
                    "Please cancel and modify filters to select data for export.");
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
            
            createHelperForSelectedFormat();
            importHelper.setExportEntityList(exportEntityList);
            
            if (importHelper == null) {
                // don't allow transition if we couldn't create helper
                SessionUtility.addErrorMessage(
                    "Unable to create export helper for selected format",
                    "Please select a different format or Cancel.");
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
            
            // skip options tab if no options specified
            if (importHelper.getExportWizardOptions().isEmpty()) {
                nextStep = "exportWizard" + TAB_DOWNLOAD_FILE;
            }
        }

        // handle transition to download tab file
        if (((currStep.endsWith(TAB_SELECT_OPTIONS)) || (currStep.endsWith(TAB_SELECT_FORMAT)))
                && (nextStep.endsWith(TAB_DOWNLOAD_FILE))) {
            
            // validate and handle wizard options if appropriate
            if (currStep.endsWith(TAB_SELECT_OPTIONS)) {
                
                // validate options
                ValidInfo validOptionsInfo = importHelper.validateExportWizardOptions();
                if (!validOptionsInfo.isValid()) {
                    // don't allow transition if options validation fails
                    SessionUtility.addErrorMessage(
                            "Invalid format options",
                            validOptionsInfo.getValidString());
                    setEnablement(currStep);
                    currentTab = currStep;
                    return currStep;
                }
                
                // handle options
                ValidInfo handleOptionsInfo = importHelper.handleExportWizardOptions();
                if (!handleOptionsInfo.isValid()) {
                    // don't allow transition if handling options fails
                    SessionUtility.addErrorMessage(
                            "Error handling format options",
                            handleOptionsInfo.getValidString());
                    setEnablement(currStep);
                    currentTab = currStep;
                    return currStep;
                }
            }
            
            generateExportFile();
            
            if (downloadFile == null) {
                // don't allow transition if we couldn't gemerate export file
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
        }

        setEnablement(nextStep);
        currentTab = nextStep;
        return nextStep;
    }
    
    protected void createHelperForSelectedFormat() {
        List<ImportExportFormatInfo> infoList = getFormatInfoList();
        ImportExportFormatInfo selectedFormatInfo = null;
        for (ImportExportFormatInfo info : infoList) {
            if (info.getFormatName().equals(getSelectedFormatName())) {
                selectedFormatInfo = info;
                break;
            }
        }
        try {
            if (selectedFormatInfo != null) {
                Class helperClass = selectedFormatInfo.getHelperClass();
                importHelper = (ImportHelperBase) helperClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException ex) {

        }
    }
    
    private void generateExportFile() {
        GenerateExportResult result = importHelper.generateExportFile();
        if (result.getValidInfo().isValid()) {
            downloadFile = result.getContent();
        } else {
                SessionUtility.addErrorMessage(
                    result.getValidInfo().getValidString(),
                    "Select Cancel to continue.");
        }
    }

    /**
     * Resets models for wizard components.
     */
    protected void reset() {
        currentTab = TAB_SELECT_FORMAT;
        selectedFormatName = null;
        selectedFormatOption = null;
        importHelper = null;
        exportEntityList = new ArrayList<>();
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        String completionUrl = getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Implements the finish operation, invoked by the wizard's "Finish"
     * navigation button.
     */
    public String finish() {
        String completionUrl = getCompletionUrl();
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

        if (tab.endsWith(TAB_SELECT_FORMAT)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (getSelectedFormatName()!= null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }

        } else if (tab.endsWith(TAB_SELECT_OPTIONS)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonFinish = true;
            disableButtonNext = false;

        } else if (tab.endsWith(TAB_DOWNLOAD_FILE)) {
            disableButtonPrev = true;
            disableButtonCancel = true;
            disableButtonFinish = false;
            disableButtonNext = true;
        }
    }

}
