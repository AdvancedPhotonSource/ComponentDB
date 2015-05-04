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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for property type handlers.
 */
@Stateless
public class PropertyTypeHandlerDbFacade extends CdbEntityDbFacade<PropertyTypeHandler> {

    public PropertyTypeHandlerDbFacade() {
        super(PropertyTypeHandler.class);
    }

    public PropertyTypeHandler findByName(String name) {
        try {
            return (PropertyTypeHandler) em.createNamedQuery("PropertyTypeHandler.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

}
