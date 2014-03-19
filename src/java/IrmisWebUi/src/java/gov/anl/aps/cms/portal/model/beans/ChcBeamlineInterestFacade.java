/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.beans;

import gov.anl.aps.cms.portal.model.entities.ChcBeamlineInterest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ChcBeamlineInterestFacade extends AbstractFacade<ChcBeamlineInterest> {
    @PersistenceContext(unitName = "CmsWebUiPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ChcBeamlineInterestFacade() {
        super(ChcBeamlineInterest.class);
    }
    
}
