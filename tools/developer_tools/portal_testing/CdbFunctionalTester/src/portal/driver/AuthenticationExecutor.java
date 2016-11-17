/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal.driver;

import java.util.List;
import portal.constants.Global;
import portal.constants.Login;
import org.openqa.selenium.WebElement;


public class AuthenticationExecutor extends DriverExecutor {

    public AuthenticationExecutor(CdbPortalDriver cdbPortalDriver) {
        super(cdbPortalDriver);
    }
    
    public boolean performLoginFromLoginPage(String username, String password) {
        cdbPortalDriver.goTo(Global.PORTAL_LOGIN_PATH);
        
        submitLoginForm(username, password);       
        return verifySuccessfulUserLogIn(); 
    }

    public boolean performLogin(String username, String password) {   
        cdbPortalDriver.goHome(); 
        
        // Clicking on login button on the top bar.         
        WebElement loginButton = findElementInDriver(Global.loginButtonRef);
        loginButton.click();
        
        submitLoginForm(username, password);
       
        return verifySuccessfulUserLogIn(); 
    }
    
    private boolean verifySuccessfulUserLogIn() {
        // Button should be found upon successful login
        if (!cdbPortalDriver.waitForElementToShowUp(Global.logoutButtonRef)) {
            return false;
        }

        WebElement loginResult = findElementInDriver(Global.statusLoginUsernameLabelRef);
        System.out.println("Result - " + loginResult.getText());

        return true;
    }
    
    private void submitLoginForm(String username, String password) { 
        System.out.println("Attempting submission of login form with credentials - user: " + username + " pass: " + password);
        
        // Should bring up dialog...        
        WebElement usernameTextbox = findElementInDriver(Login.loginFormUsernameRef);        
        WebElement passwordTextbox = findElementInDriver(Login.loginFormPasswordRef);
        
        // Enter credentails provided.
        usernameTextbox.sendKeys(username);
        passwordTextbox.sendKeys(password);
        
        // Submit the form.
        WebElement loginFormLoginButton = findElementInDriver(Login.loginFormLoginButtonRef);
        loginFormLoginButton.click();
    }
    
    public boolean performLogout() {
        System.out.println("Logging out...");
                
        if (!isElementShownOnPage(Global.loginButtonRef)) {
            System.out.println("No Logout button is present");            
        } else {
            WebElement logoutButton = findElementInDriver(Global.logoutButtonRef);
            logoutButton.click();
            if (cdbPortalDriver.waitForElementToShowUp(Global.loginButtonRef)) {
                return true;
            }            
        }
        return false; 
    }
    
    public static void main(String[] args) {
        CdbPortalDriver portalDriver = new CdbPortalDriver();
        AuthenticationExecutor authExecutor = new AuthenticationExecutor(portalDriver); 
        authExecutor.performLogin("cdb", "cdb"); 
    }

}
