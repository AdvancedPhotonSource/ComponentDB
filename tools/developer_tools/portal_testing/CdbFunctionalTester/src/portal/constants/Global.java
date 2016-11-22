/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal.constants;

import org.openqa.selenium.By;

public class Global {
    
    public static String PORTAL_URL = "http://localhost:8080/cdb"; 
    public static String PORTAL_LOGIN_PATH = "/views/login"; 
    public static String PORTAL_CATALOG_LIST_PATH = "/views/itemDomainCatalog/list";
    
    public static String USER_NAME = "cdb";
    public static String PASSWORD = "cdb"; 
    
    // Portal top menu bar
    public static By loginButtonRef = By.cssSelector("#loginButton");
    public static By logoutButtonRef = By.cssSelector("#logoutButton");
    public static By catalogButtonRef = By.cssSelector("#catalogButton"); 
    
    public static By statusLoginUsernameLabelRef = By.cssSelector("#currentUserLoggedInLabel"); 
    
}
