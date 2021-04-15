/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms;

import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.plugins.support.opennms.api.OpenNMSApi;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.BusinessService;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.IpService;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.IpServiceEdge;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.Service;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
@Named("openNMSBean")
@SessionScoped
public class OpenNMSBean implements Serializable {
    
    private static final Logger logger = LogManager.getLogger(OpenNMSBean.class.getName());
       
    private Integer businessServiceId; 
    private OpenNMSApi apiClient;     
    
    private List<CdbServiceStatus> serviceStatusList; 
    
    @PostConstruct
    public void init() {
        String openNMSServiceUrl = OpenNMSPluginManager.getOpenNMSServiceUrl();
        String openNMSBasicAuthUsername = OpenNMSPluginManager.getOpenNMSBasicAuthUsername();
        String openNMSBasicAuthPassword = OpenNMSPluginManager.getOpenNMSBasicAuthPassword();
        businessServiceId = OpenNMSPluginManager.getOpenNMSBusinessServiceId();
        
        try { 
            apiClient = new OpenNMSApi(openNMSServiceUrl, openNMSBasicAuthUsername, openNMSBasicAuthPassword);
        } catch (ConfigurationError ex) {
            logger.error(ex);
        }
    }
    
    public List<CdbServiceStatus> getServiceStatusList() {
       return serviceStatusList; 
    }
    
    public void loadServiceStatusList() {
         serviceStatusList = new ArrayList<>(); 
        
        if (apiClient == null) {
            SessionUtility.addErrorMessage("Error fetching statuses", "Couldn't connect to open nms.");
        } else {
            try { 
                BusinessService businessServiceById = apiClient.getBusinessServiceById(businessServiceId);
                
                LinkedList<IpServiceEdge> ipServiceEdges = businessServiceById.getIpServiceEdges();
            
                for (IpServiceEdge edge : ipServiceEdges) {
                    IpService ipService = edge.getIpService();
                    Integer id = ipService.getId();

                    Service serviceById = apiClient.getServiceById(id);

                    String friendlyName = edge.getFriendlyName();
                    String stateDescription = serviceById.getStateDescription();

                    CdbServiceStatus status = new CdbServiceStatus(friendlyName, stateDescription); 
                    serviceStatusList.add(status); 
                }
            } catch (Exception ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error fetching statuses", ex.getMessage());
            }
        }
    }
    
    public class CdbServiceStatus {
        
        private String serviceName; 
        private String status; 

        public CdbServiceStatus(String serviceName, String status) {
            this.serviceName = serviceName;
            this.status = status;
        }

        public String getServiceName() {
            return serviceName;
        }     

        public String getStatus() {
            return status;
        }
    }
    
}
