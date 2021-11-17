/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author djarosz
 */
public abstract class CdbEntityFacade<T> {

    private Class<T> entityClass;

    public CdbEntityFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void create(List<T> entities) {
        for (T entity : entities) {
            create(entity);
        }
    }

    public T edit(T entity) {
        T result = getEntityManager().merge(entity);

        // delete list of connectors, if any
        if (entity instanceof CdbEntity) {
            CdbEntity cdbEntity = (CdbEntity) entity;
            for (ItemConnector connector : cdbEntity.getDeletedConnectorList()) {
                ItemConnectorFacade.getInstance().remove(connector);
            }
            cdbEntity.clearDeletedConnectorList();
        }
        
        return result;
    }

    public void edit(List<T> entities) {
        for (T entity : entities) {
            edit(entity);
        }
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public void remove(List<T> entities, T updateEntity) {
        if (updateEntity != null) {
            edit(updateEntity);
        }
        for (T entity : entities) {
            remove(entity);
        }
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public T findUniqueByName(String name, String domainName) throws CdbException {
        throw new CdbException("findUniqueByName() operation not implemented by facade");
    }
    
    public T findUniqueWithAttributes(Map<String,String> attributeMap) throws CdbException {
        throw new CdbException("findUniqueWithAttributes() operation not implemented by facade");
    }
    
    public T findByQrId(Integer qrId) throws CdbException {
        throw new CdbException("findByQrId() operation not implemented by facade");
    }
    
}
