/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.utilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author darek
 */


public class CryptUtilityTest {
    
    public CryptUtilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of randomString method, of class CryptUtility.
     */
    @Test
    public void testRandomString() {
        System.out.println("randomString");
        char[] characterSet = new char[1];
        characterSet[0] = 'a';        
        int length = 5;
        String expResult = "aaaaa";
        String result = CryptUtility.randomString(characterSet, length);
        assertEquals(expResult, result);
    }

    /**
     * Test of cryptPasswordWithPbkdf2 and verify method, of class CryptUtility.
     */
    @Test
    public void testCryptVerifyPasswordWithPbkdf2() {
        System.out.println("cryptPasswordWithPbkdf2");
        String password = "cdb";        
        String someOtherPassword = "someOtherPassword";
        String cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(password);       
        String anotherCryptedPass = CryptUtility.cryptPasswordWithPbkdf2(someOtherPassword);
        
        // Assert statements 
        assertEquals(true, CryptUtility.verifyPasswordWithPbkdf2(password, cryptedPassword));
        assertEquals(false, CryptUtility.verifyPasswordWithPbkdf2(password, anotherCryptedPass));
        assertEquals(true, CryptUtility.verifyPasswordWithPbkdf2(someOtherPassword, anotherCryptedPass));
        

    }   
    
}
