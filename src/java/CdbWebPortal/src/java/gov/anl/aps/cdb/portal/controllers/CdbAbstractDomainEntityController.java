/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbAbstractDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain Entity Controller used for abstract domain entities such as components and designs. 
 * 
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbAbstractDomainEntityController<EntityType extends CdbAbstractDomainEntity, FacadeType extends CdbEntityDbFacade<EntityType>> extends CdbDomainEntityController<EntityType, FacadeType> implements Serializable {
    
    private List<Design> parentDesignList; 
    private int currentAbstractEntityHashCode; 
    
    
    public List<Design> getParentDesignList() {
        if (currentHasChanged()) {
            EntityType abstractDomainEntity = getCurrent(); 

            parentDesignList = new ArrayList<>(); 

            List<DesignElement> designElementList = abstractDomainEntity.getDesignElementMemberList(); 
            for (DesignElement designElement : designElementList) {
                if (parentDesignList.contains(designElement.getParentDesign()) == false) {
                    parentDesignList.add(designElement.getParentDesign());
                }
            }
        }

        return parentDesignList;
    }
    
    public Boolean getDisplayParentDesignList() {
        List<Design> abstractDomainEntityParentDesignList = getParentDesignList();
        return (abstractDomainEntityParentDesignList != null && !abstractDomainEntityParentDesignList.isEmpty());
    }
    
    public boolean currentHasChanged(){
        EntityType abstractDomainEntity = getCurrent(); 
        if (currentAbstractEntityHashCode != abstractDomainEntity.hashCode()) {
            currentAbstractEntityHashCode = hashCode(); 
            return true; 
        } 
        return false; 
    }
    
}
