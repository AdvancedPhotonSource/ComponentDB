/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal.driver;

import exceptions.FunctionalException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class DriverExecutor {

    protected CdbPortalDriver cdbPortalDriver;

    public DriverExecutor(CdbPortalDriver cdbPortalDriver) {
        this.cdbPortalDriver = cdbPortalDriver;
    }

    protected WebDriver getDriver() {
        return cdbPortalDriver.getDriver();
    }
    
    protected WebElement findElementInDriver(By by) {
        return getDriver().findElement(by); 
    }
    
    protected List<WebElement> findElementsInDriver(By by) {
        return getDriver().findElements(by); 
    }
    
    protected boolean isElementShownOnPage(By by) {
        return !findElementsInDriver(by).isEmpty();
    }
    
    protected WebElement waitAndGetElement(By by) throws FunctionalException {
        if (cdbPortalDriver.waitForElementToShowUp(by)) {
            return findElementInDriver(by); 
        } 
        
        throw new FunctionalException("Element could not be found: " + by.toString());         
    }

}
