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

public class StringUtilityTest {
    
    public StringUtilityTest() {
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
     * Test of isEmailAddressValid method, of class StringUtility.
     */
    @Test
    public void testIsEmailAddressValid() {
        System.out.println("isEmailAddressValid");
        String emailAddress = "user@aps.anl.gov";
        boolean expResult = true;
        boolean result = StringUtility.isEmailAddressValid(emailAddress);
        assertEquals(expResult, result);
        
        emailAddress = "@user.com";
        expResult = false;
        result = StringUtility.isEmailAddressValid(emailAddress);
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class StringUtility.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        CharSequence cs1 = "test";
        CharSequence cs2 = "test";
        CharSequence cs3 = "test2";
        
        boolean expResult = true;
        boolean result = StringUtility.equals(cs1, cs2);
        assertEquals(expResult, result);
        
        expResult = false;
        result = StringUtility.equals(cs1, cs3);
        assertEquals(expResult, result);   
    }
    
}
