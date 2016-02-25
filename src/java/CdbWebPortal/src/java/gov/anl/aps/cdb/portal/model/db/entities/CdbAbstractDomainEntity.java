/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.util.List;

/**
 * Used for abstract domain entities such as designs and components. 
 */
public abstract class CdbAbstractDomainEntity extends CdbDomainEntity {
    
    public List<DesignElement> getDesignElementMemberList(){
        return null; 
    }
    
}
