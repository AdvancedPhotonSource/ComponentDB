/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package portal;

import portal.constants.Global;
import portal.driver.AuthenticationExecutor;
import portal.driver.CdbPortalDriver;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AuthenticationTest {

    CdbPortalDriver cdbPortalDriver = new CdbPortalDriver();
    AuthenticationExecutor authExecutor = new AuthenticationExecutor(cdbPortalDriver);

    @Test
    public void successfulAuthentication() {
        boolean successfulAuth = authExecutor.performLogin(Global.USER_NAME, Global.PASSWORD);
        if (successfulAuth) {
            successfulAuth = authExecutor.performLogout();
        }

        assertEquals(true, successfulAuth);
    }

    @Test
    public void unsuccessfulAuthentication() {
        boolean successfulAuth = authExecutor.performLogin("someUsername1234", "somePassword1234");
        if (successfulAuth) {
            authExecutor.performLogout();
        }
        assertEquals(false, successfulAuth);
    }

    @Test
    public void successfulAuthenticationFromLoginPage() {
        boolean successfulAuth = authExecutor.performLoginFromLoginPage(Global.USER_NAME, Global.PASSWORD);
        if (successfulAuth) {
            successfulAuth = authExecutor.performLogout();
        }
        assertEquals(true, successfulAuth);
    }

}
