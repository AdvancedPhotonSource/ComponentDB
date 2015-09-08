/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for property types.
 */
@Stateless
public class PropertyTypeDbFacade extends CdbEntityDbFacade<PropertyType> {

    public PropertyTypeDbFacade() {
        super(PropertyType.class);
    }

    @Override
    public List<PropertyType> findAll() {
        return (List<PropertyType>) em.createNamedQuery("PropertyType.findAll")
                .getResultList();
    }

    public PropertyType findByName(String name) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public PropertyType findById(Integer id) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<PropertyType> findByPropertyTypeHandler(PropertyTypeHandler handler){
        try{
            return (List<PropertyType>) em.createNamedQuery("PropertyType.findByPropertyTypeHandler")
                    .setParameter("propertyTypeHandler", handler)
                    .getResultList(); 
        }catch (NoResultException ex) {
        }
        return null; 
    }
}
