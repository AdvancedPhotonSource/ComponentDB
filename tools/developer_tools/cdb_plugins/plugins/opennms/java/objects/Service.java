/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.objects;

/**
 *
 * @author darek
 */
public class Service extends OpenNMSObject {
    
    private String status; 
    private String serviceName;
    private String ipAddress; 
    private Integer ipInterfaceId; 
    private String statusCode;
    private String node; 
    private boolean isDown; 
    private boolean isMonitored; 

    public Service() {
        super();
    }

    public String getStatus() {
        return status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getIpInterfaceId() {
        return ipInterfaceId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getNode() {
        return node;
    }

    public boolean isIsDown() {
        return isDown;
    }

    public boolean isIsMonitored() {
        return isMonitored;
    }
    
    
    public String getStateDescription() {
        if (isDown) {
            return "Service Down"; 
        }
        
        return "Service Running"; 
    }
    
}
