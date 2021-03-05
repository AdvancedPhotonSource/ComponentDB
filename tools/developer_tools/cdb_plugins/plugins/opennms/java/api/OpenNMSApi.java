/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.BusinessService;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.IpService;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.IpServiceEdge;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.OpenNMSObjectFactory;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.Service;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darek
 */
public class OpenNMSApi extends OpenNMSRestApi {
    
    public OpenNMSApi(String webServiceUrl, String basicAuthUser, String basicAuthPass) throws ConfigurationError {
        super(webServiceUrl, basicAuthUser, basicAuthPass);
    }
    
    public BusinessService getBusinessServiceById(Integer Id) throws IOException, CdbException {
        ArgumentUtility.verifyNonNullObject("Bussiness Service Id", Id);
        String requestUrl = "/api/v2/business-services/" + Id; 
        
        String result = this.invokeGetRequest(requestUrl);
            
        return OpenNMSObjectFactory.createObject(result, BusinessService.class);        
    }
    
    public Service getServiceById(Integer Id) throws CdbException, IOException {                
        ArgumentUtility.verifyNonNullObject("Service Id", Id);
        String requestUrl = "/rest/ifservices/" + Id; 
        
        String result = this.invokeGetRequest(requestUrl);
            
        return OpenNMSObjectFactory.createObject(result, Service.class);        
    }
    
     public static void main(String[] args) {
        String webServiceUrl = "https://ctlinframon.aps.anl.gov:8443/opennms"; 
        String basicAuthUser = "admin2"; 
        String basicAuthPass = "admin"; 
        
        try { 
            OpenNMSApi client = new OpenNMSApi(webServiceUrl, basicAuthUser, basicAuthPass);
            
            BusinessService businessServiceById = client.getBusinessServiceById(111); 
            
            LinkedList<IpServiceEdge> ipServiceEdges = businessServiceById.getIpServiceEdges();
            
            for (IpServiceEdge edge : ipServiceEdges) {
                IpService ipService = edge.getIpService();
                Integer id = ipService.getId();
                
                Service serviceById = client.getServiceById(id);
                
                String friendlyName = edge.getFriendlyName();
                String stateDescription = serviceById.getStateDescription();
                                                
                System.out.println(friendlyName + " : " + stateDescription);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(OpenNMSApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
     }    
}
