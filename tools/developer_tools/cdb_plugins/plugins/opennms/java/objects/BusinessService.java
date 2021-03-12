/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.objects;

import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;

/**
 *
 * @author darek
 */
public class BusinessService extends OpenNMSObject {
        
    private String name; 
    
    @SerializedName(value = "ip-service-edges")
    private LinkedList<IpServiceEdge> ipServiceEdges; 

    public BusinessService() {
        super();
    }

    public String getName() {
        return name;
    }

    public LinkedList<IpServiceEdge> getIpServiceEdges() {
        return ipServiceEdges;
    }
    
}
