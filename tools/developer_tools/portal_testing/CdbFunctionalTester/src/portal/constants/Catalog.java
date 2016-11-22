/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portal.constants;

import org.openqa.selenium.By;

public class Catalog {

    public static By catalogListPageAddButtonRef = By.cssSelector("#componentListForm\\:componentAddButton"); 
    
    public static By createWizardNextStepButtonRef = By.cssSelector("#addComponentForm\\:componentcreateWizardNextStep"); 
    
    public static By createWizardNameRef = By.cssSelector("#addComponentForm\\:nameInputTextCreateWizard");    
    public static By createWizardItemIdentifier1Ref = By.cssSelector("#addComponentForm\\:itemIdentifier1ITCreateWizard"); 
    public static By createWizardItemIdentifier2Ref = By.cssSelector("#addComponentForm\\:itemIdentifier2ITCreateWizard"); 
    public static By createWizardItemDescriptionRef = By.cssSelector("#addComponentForm\\:descriptionITACreateWizard"); 
    
    public static By createWizardProjectDropdownRef = By.cssSelector("#addComponentForm\\:itemProjectSelectCBCreateWizard"); 
    public static By createWizardProjectDropdownFirstSelectrion = By.xpath("//div[@id='addComponentForm:itemProjectSelectCBCreateWizard_panel']/div/div/div[2]/span");
    
    public static By createWizardOwnerSelectionRef = By.cssSelector("#addComponentForm\\:ownerUserSOMCreateWizard_label");
        
    public static String sampleCatalogItemName = "Sample Item Created By Unit Test"; 
    public static String sampleCatalogItemModel = "Model"; 
    public static String sampleCatalogItemAlternate = "Alternate Name"; 
    public static String sampleCatalogItemDescription = "My Awesome Description."; 
    
}
