/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.constants;

/**
 *
 * @author sveseli
 */
public enum DesignElementType {
    COMPONENT("component"),
    DESIGN("design");
    
    private final String type;
    private DesignElementType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
