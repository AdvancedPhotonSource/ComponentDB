/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    
    /**
     * Runs various tests on the portal. 
     * Assumption for a valid login:
     * -- Username: 'cdb'
     * -- Password: 'cdb'
     * 
     * @param args 
     */    
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AuthenticationTest.class); 
        
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        
        System.out.println(result.wasSuccessful());        
    }        
}
