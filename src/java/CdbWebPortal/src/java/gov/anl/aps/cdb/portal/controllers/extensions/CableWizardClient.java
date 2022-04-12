/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

/**
 * This class defines the interface for clients of the class 
 * {@link ItemDomainCableDesignWizard}.
 * @author craig
 */
public interface CableWizardClient {

    /**
     * Performs cleanup in the client after the cable wizard completes and
     * closes.
     */
    public void cleanupCableWizard();
    
}
