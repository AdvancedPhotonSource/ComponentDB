/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal.driver;

import exceptions.FunctionalException;
import org.openqa.selenium.WebElement;
import portal.constants.Catalog;
import portal.constants.Global;


public class CatalogExecutor extends DriverExecutor {
    
    AuthenticationExecutor authExecutor;
    
    public CatalogExecutor(CdbPortalDriver cdbPortalDriver) {
        super(cdbPortalDriver);
        authExecutor = new AuthenticationExecutor(cdbPortalDriver);
    }
    
    public boolean addSampleCatalogItem() throws FunctionalException {
        cdbPortalDriver.goHome();                
                        
        if (isElementShownOnPage(Global.loginButtonRef)) {                        
            authExecutor.performLogin(Global.USER_NAME, Global.PASSWORD);
        }
        
        WebElement catalogButton = findElementInDriver(Global.catalogButtonRef); 
        catalogButton.click();
        
        if (!cdbPortalDriver.waitForRedirectionToComplete(Global.PORTAL_CATALOG_LIST_PATH)) {
            // Catalog button failed.
            throw new FunctionalException("Redirection to catalog list page failed upon clickin of catalog button.");
        }
                        
        WebElement catalogAddButton = waitAndGetElement(Catalog.catalogListPageAddButtonRef); 
        catalogAddButton.click();
        
        // Check that first step of wizard is loaded. 
        if (!cdbPortalDriver.waitForElementToShowUp(Catalog.createWizardNameRef)) {
            throw new FunctionalException("Redirection to create wizard failed upon clicking add button on catalog list page."); 
        }
        
        WebElement createWizardNextStepButton = waitAndGetElement(Catalog.createWizardNextStepButtonRef); 
              
        // Item details of the wizard
        WebElement createWizardName = findElementInDriver(Catalog.createWizardNameRef);
        WebElement createWizardModel = findElementInDriver(Catalog.createWizardItemIdentifier1Ref); 
        WebElement createWizardAlternate = findElementInDriver(Catalog.createWizardItemIdentifier2Ref); 
        WebElement createWizardDescription = findElementInDriver(Catalog.createWizardItemDescriptionRef);                 
        
        createWizardName.sendKeys(Catalog.sampleCatalogItemName);
        createWizardModel.sendKeys(Catalog.sampleCatalogItemModel);
        createWizardAlternate.sendKeys(Catalog.sampleCatalogItemAlternate);
        createWizardDescription.sendKeys(Catalog.sampleCatalogItemDescription);
        
        //Move onto Classification step of the wizard. 
        createWizardNextStepButton.click();
        WebElement createWizardSelectProjectDropdown = waitAndGetElement(Catalog.createWizardProjectDropdownRef);         
        
        // Expand and select first project 
        createWizardSelectProjectDropdown.click(); 
        WebElement createWizardFirstCheckbox = waitAndGetElement(Catalog.createWizardProjectDropdownFirstSelectrion); 
        createWizardFirstCheckbox.click();
        
        // Permissions step 
        createWizardNextStepButton.click();       
        if (!cdbPortalDriver.waitForElementToShowUp(Catalog.createWizardOwnerSelectionRef)) {
            throw new FunctionalException("Failed to move onto the permissions step.");
        }                
        
        
        return true; 
        
    }
    
    public static void main(String[] args) throws FunctionalException {
        CdbPortalDriver portalDriver = new CdbPortalDriver();
        CatalogExecutor catalogExecutor = new CatalogExecutor(portalDriver); 
        boolean result = catalogExecutor.addSampleCatalogItem();
        if (result) {
            System.out.println("Success");
        }        
    }
    
}
