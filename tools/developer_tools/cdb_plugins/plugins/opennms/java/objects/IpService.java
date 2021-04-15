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
public class IpService extends OpenNMSObject {
    
    @SerializedName(value = "service-name")
    private String serviceName; 
    @SerializedName(value = "node-label")
    private String nodeLabel; 
    @SerializedName(value = "ip-address")
    private String ipAddress;         

    public IpService() {
        super();
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    
}
