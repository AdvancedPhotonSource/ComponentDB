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
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for property values.
 */
@Stateless
public class PropertyValueDbFacade extends CdbEntityDbFacade<PropertyValue> {

    public PropertyValueDbFacade() {
        super(PropertyValue.class);
    }
    
    public List<PropertyValue> findByPropertyType(PropertyType propertyType){
        try{
            return (List<PropertyValue>) em.createNamedQuery("PropertyValue.findByPropertyType")
                    .setParameter("propertyType", propertyType)
                    .getResultList(); 
        }catch (NoResultException ex) {
        }
        return null; 
    }

}
