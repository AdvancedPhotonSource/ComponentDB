/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.objects;

import java.io.Serializable;

/**
 *
 * @author darek
 */
public class OpenNMSObject implements Serializable {
    
    private Integer id; 
    private String location;

    public Integer getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }
    
}
