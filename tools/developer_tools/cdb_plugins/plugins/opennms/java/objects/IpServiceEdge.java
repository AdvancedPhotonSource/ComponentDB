/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.objects;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author darek
 */
public class IpServiceEdge extends OpenNMSObject {
    
    @SerializedName(value = "friendly-name")
    private String friendlyName; 
    private String weight; 
    @SerializedName(value = "operational-status")
    private String operationalStatus; 
    @SerializedName(value = "ip-service")
    private IpService ipService; 
    

    public IpServiceEdge() {
        super();
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getWeight() {
        return weight;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public IpService getIpService() {
        return ipService;
    }
    
}
