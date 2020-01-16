/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.TravelerObjectFactory;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Travelers;

import gov.anl.aps.cdb.common.utilities.ArgumentUtility;
import gov.anl.aps.cdb.portal.plugins.support.traveler.TravelerPluginManager;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binders;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Forms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.ReleasedForm;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.ReleasedForms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.TravelerData;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.TravelerDatum;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.TravelerNotes;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class TravelerApi extends TravelerRestApi  {

    public TravelerApi(String webServiceUrl, String basicAuthUser, String basicAuthPass) throws ConfigurationError{
        super(webServiceUrl, basicAuthUser, basicAuthPass); 
    }
    
    public Travelers getTravelers() throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        //ArgumentUtility.verifyNonEmptyString("Drawing name", drawingNumber);
        String requestUrl = "/apis/travelers/";
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"travelers\": " + jsonString + "}";
        Travelers travelers = (Travelers) TravelerObjectFactory.createObject(jsonString, Travelers.class); 
        return travelers; 
    }
    
    public Traveler getTraveler(String travelerId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Traveler ID", travelerId);
        String requestUrl = "/apis/travelers/"+travelerId+"/"; 
        String jsonString = invokeGetRequest(requestUrl);
        Traveler traveler = (Traveler) TravelerObjectFactory.createObject(jsonString, Traveler.class); 
        return traveler; 
    }
    
    public TravelerData getTravelerData(String travelerId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Traveler ID", travelerId);
        String requestUrl = "/apis/travelers/"+travelerId+"/data/"; 
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"data\": " + jsonString + "}";
        TravelerData travelerData = (TravelerData) TravelerObjectFactory.createObject(jsonString, TravelerData.class); 
        return travelerData; 
    }
    
    public TravelerNotes getTravelerNotes(String travelerId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Traveler ID", travelerId);
        String requestUrl = "/apis/travelers/"+travelerId+"/notes/"; 
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"notes\": " + jsonString + "}";
        TravelerNotes travelerNotes = (TravelerNotes) TravelerObjectFactory.createObject(jsonString, TravelerNotes.class); 
        return travelerNotes; 
    }
    
    public TravelerDatum getDatum(String datumId) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyNonEmptyString("Datum Id", datumId);
        String requestUrl = "/apis/data/"+datumId + "/"; 
        String jsonString = invokeGetRequest(requestUrl);
        TravelerDatum travelerNotes = (TravelerDatum) TravelerObjectFactory.createObject(jsonString, TravelerDatum.class); 
        return travelerNotes; 
    } 
    
    public Forms getForms() throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        //ArgumentUtility.verifyNonEmptyString("Drawing name", drawingNumber);
        String requestUrl = "/apis/forms/";
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"forms\": " + jsonString + "}";
        Forms forms = (Forms) TravelerObjectFactory.createObject(jsonString, Forms.class); 
        return forms; 
    }
    
    public Form getForm(String formId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Form ID", formId);
        String requestUrl = "/apis/forms/"+formId+"/"; 
        String jsonString = invokeGetRequest(requestUrl);
        Form form = (Form) TravelerObjectFactory.createObject(jsonString, Form.class); 
        return form; 
    }
    
    public ReleasedForms getReleasedFormsCreatedFromForm(String formId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Form ID", formId);
        String requestUrl = "/apis/forms/"+formId+"/released/"; 
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"releasedForms\": " + jsonString + "}";
        ReleasedForms form = (ReleasedForms) TravelerObjectFactory.createObject(jsonString, ReleasedForms.class); 
        return form; 
    }
    
    public ReleasedForms getReleasedForms() throws ObjectNotFound, ExternalServiceError, CdbException {        
        String requestUrl = "/apis/releasedForms/"; 
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"releasedForms\": " + jsonString + "}";
        ReleasedForms form = (ReleasedForms) TravelerObjectFactory.createObject(jsonString, ReleasedForms.class); 
        return form; 
    }
    
    public Binders getBinders() throws ExternalServiceError, CdbException {
        String requestUrl = "/apis/binders/";
        String jsonString = invokeGetRequest(requestUrl);
        jsonString = "{\"binders\": " + jsonString + "}"; 
        Binders binders = (Binders) TravelerObjectFactory.createObject(jsonString, Binders.class); 
        return binders; 
    }
    
    public Binder getBinder(String binderId) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Binder ID", binderId);
        String requestUrl = "/apis/binders/"+binderId+"/"; 
        String jsonString = invokeGetRequest(requestUrl);
        Binder binder = (Binder) TravelerObjectFactory.createObject(jsonString, Binder.class); 
        return binder; 
    }
    
    public Binder createBinder(String binderTitle, String description, String userName) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyNonEmptyString("Binder Title", binderTitle);
        ArgumentUtility.verifyNonEmptyString("User Name", userName);
        
        String requestUrl = "/apis/create/binders/"; 
        Map data = new HashMap(); 
        
        data.put("userName", userName);
        data.put("description", description);
        data.put("binderTitle", binderTitle); 
        
        String jsonString = invokePostRequest(requestUrl, data);
        Binder binder = (Binder) TravelerObjectFactory.createObject(jsonString, Binder.class);         
        
        return binder;
    }
    
    public Binder addWorkToBinder(String binderId, String[] travelerIds, String userName) throws CdbException {
        String requestUrl = "/apis/addWork/binders/" + binderId + "/";
        
        Map data = new HashMap(); 
        data.put("travelerIds", travelerIds);
        data.put("userName", userName); 
        
        String jsonString = invokePostRequest(requestUrl, data); 
        Binder binder = (Binder) TravelerObjectFactory.createObject(jsonString, Binder.class);         
        
        return binder;
    }
    
    public Binder removeWorkFromBinder(String binderId, String workId, String userName) throws CdbException {
        String requestUrl = "/apis/removeWork/binders/" + binderId + "/";
        
        Map data = new HashMap();         
        data.put("workId", workId);
        data.put("userName", userName); 
        
        String jsonString = invokePostRequest(requestUrl, data);
        Binder binder = (Binder) TravelerObjectFactory.createObject(jsonString, Binder.class);         
        
        return binder;
    }
    
    public Form createForm(String formName, String userName, String html) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException{
        ArgumentUtility.verifyNonEmptyString("Form Name", formName);
        ArgumentUtility.verifyNonEmptyString("User Name", userName);
        
        String requestUrl = "/apis/create/form/"; 
        Map data = new HashMap();  

        data.put("formName", formName); 
        data.put("html", html);
        data.put("userName", userName); 
        String jsonString = invokePostRequest(requestUrl, data);
        Form form = (Form) TravelerObjectFactory.createObject(jsonString, Form.class); 
        return form; 
    }
    
    public Traveler createTraveler(String releasedFormId, String userName, String title, String devices) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException{
        ArgumentUtility.verifyNonEmptyString("Form Name", releasedFormId);
        ArgumentUtility.verifyNonEmptyString("User Name", userName);
        ArgumentUtility.verifyNonEmptyString("title", title);
        ArgumentUtility.verifyNonEmptyString("devices", devices);
        
        String requestUrl = "/apis/create/traveler/"; 
        Map data = new HashMap();  
        
        data.put("formId", releasedFormId); 
        data.put("title", title);
        data.put("userName", userName);
        data.put("devices", devices);
        String jsonString = invokePostRequest(requestUrl, data);
        Traveler traveler = (Traveler) TravelerObjectFactory.createObject(jsonString, Traveler.class); 
        return traveler; 
        
    }
    
    public Traveler updateTravelerArchived(String travelerId, Boolean archivedState) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyNonEmptyString("Traveler ID", travelerId);
        ArgumentUtility.verifyNonNullObject("Archived State", archivedState);
        
        String requestUrl = "/apis/archived/traveler/" + travelerId + "/";
        
        Map data = new HashMap();        
        data.put("archived", archivedState);
        
        String jsonString = invokePostRequest(requestUrl, data);
        Traveler traveler = (Traveler) TravelerObjectFactory.createObject(jsonString, Traveler.class); 
        return traveler; 
    }
    
    public Traveler updateTraveler(String travelerId, String userName, String title, String description, Date deadline, Double status) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyNonEmptyString("Traveler Id", travelerId);
        ArgumentUtility.verifyNonEmptyString("Traveler Title", title);
        ArgumentUtility.verifyNonEmptyString("User Name", userName);
        if (status == null || status < 0) {
            throw new InvalidArgument("Status cannot be less than 0.");
        }
        
        
        String deadlineString; 
        if (deadline != null) {
            deadlineString = deadline.toString();
        } else {
            deadlineString = ""; 
        }
        
        String requestUrl = "/apis/update/traveler/" + travelerId + "/"; 
        
        Map data = new HashMap(); 
        
        data.put("userName", userName); 
        data.put("title", title);
        data.put("description", description);
        data.put("deadline", deadlineString); 
        data.put("status", status.toString()); 
        
        String jsonString = invokePostRequest(requestUrl, data);
        
        Traveler traveler = (Traveler) TravelerObjectFactory.createObject(jsonString, Traveler.class); 
        return traveler; 
    }
    
    /*
     * Main method, used for simple testing.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        String testUser = TravelerPluginManager.getTravelerBasicAuthUsername();
        String testPass = TravelerPluginManager.getTravelerBasicAuthPassword(); 
        String testHost = TravelerPluginManager.getTravelerWebServiceUrl();
                
        try {
            TravelerApi apiClient = new TravelerApi(testHost, testUser, testPass);                        
            
            Binders binders = apiClient.getBinders();
            
            String binderId = binders.getBinders().get(0).getId(); 
            
            Binder binder = apiClient.getBinder(binderId);
            
            Binder createdBinder = apiClient.createBinder("Created From CDB", "a description", "djarosz"); 
                                                
            Travelers travelers = apiClient.getTravelers(); 
            String[] travelerIds = new String[travelers.getTravelers().size()]; 
            for (int i = 0; i < travelers.getTravelers().size(); i++) {
                Traveler traveler = travelers.getTravelers().get(i);
                System.out.println("---" + traveler.getTitle()); 
                travelerIds[i] = traveler.getId(); 
            }
            
            apiClient.addWorkToBinder(createdBinder.getId(), travelerIds, "djarosz");
            
            if (true) {
                return;
            }
            
            String travelerId = travelers.getTravelers().get(0).getId(); 
            Traveler traveler = apiClient.getTraveler(travelerId);
            System.out.println("\n getTravelerTest: " + traveler.getTitle()); 
            
            TravelerData data = apiClient.getTravelerData(travelerId);
            System.out.println("\n getTravelerDataTest: " + data.getData().get(0).getValue()); 
            
            TravelerNotes notes = apiClient.getTravelerNotes(travelerId);
            System.out.println("\n getTravelerNotesTest: " + notes.getNotes().get(0).getValue()); 
            
            TravelerDatum datum = apiClient.getDatum(travelerId);
            System.out.println("\n getDatumTest: " + datum.getValue()); 
            
            Form form = apiClient.createForm("CDB FORM", "djarosz", ""); 
            System.out.println(form.getTitle()); 
            
            traveler = apiClient.updateTraveler(travelerId, "someUser", "cdb", "World", null, 1.5);
            System.out.println(traveler);
            
            Forms forms = apiClient.getForms(); 
            
            form = apiClient.getForm("564a40b843a124d167cddc18"); 
            System.out.println(form.getTitle()); 
            Traveler travelerCreated = apiClient.createTraveler(form.getId(), "djarosz", form.getTitle(), "APSU 123"); 
            System.out.println(travelerCreated);
            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    
            
            
      
}
