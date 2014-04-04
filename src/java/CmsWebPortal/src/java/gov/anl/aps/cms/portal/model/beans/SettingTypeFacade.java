/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.model.beans;

import gov.anl.aps.cms.portal.model.entities.SettingType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class SettingTypeFacade extends AbstractFacade<SettingType>
{

    @PersistenceContext(unitName = "CmsWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SettingTypeFacade() {
        super(SettingType.class);
    }

    public SettingType findByName(String name) {
        try {
            return (SettingType) em.createNamedQuery("SettingType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }
}
