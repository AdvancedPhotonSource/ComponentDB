/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import portal.constants.Global; 
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class CdbPortalDriver {

    protected WebDriver driver; 
    
    public CdbPortalDriver() {
        // Create a driver for the portal 
        driver = new HtmlUnitDriver(true);                
        java.lang.System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
    }
    
    public void goHome() {
        driver.get(Global.PORTAL_URL);
    }
    
    public void goTo(String portalUrlPath) {
        driver.get(Global.PORTAL_URL + portalUrlPath);
    }

    public WebDriver getDriver() {
        return driver;
    }
    
    public boolean waitForElementToShowUp(By elementToFind) {
        return waitForElementToShowUp(elementToFind, 45000);
    }
    
    public boolean waitForElementToShowUp(By elementToFind, long duration) {
        long end = System.currentTimeMillis() + duration;
        while (System.currentTimeMillis() < end) {
            ArrayList<WebElement> resultingElement = (ArrayList<WebElement>) driver.findElements(elementToFind);
            if (resultingElement.size()>0) {
                return true; 
            }
        }
        return false; 
    }
    
    public boolean waitForRedirectionToComplete(String expectedApplicationPath) {
        return waitForRedirectionToComplete(expectedApplicationPath, 45000);
    }
    
    public boolean waitForRedirectionToComplete(String expectedApplicationPath, long duration) {
        long end = System.currentTimeMillis() + duration;
        while (System.currentTimeMillis() < end) {            
            if (driver.getCurrentUrl().equals(Global.PORTAL_URL + expectedApplicationPath)) {
                return true; 
            }
        }
        return false; 
    }

}
