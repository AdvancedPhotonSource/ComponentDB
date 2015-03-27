package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;

/**
 *
 * @author sveseli
 */
public class PdmLinkApi extends CdbRestApi {

    /**
     * Constructor. Initializes service url from system properties.
     *
     * @throws ConfigurationError if protocol is not http or https
     */
    public PdmLinkApi() throws ConfigurationError {
        super();
    }

    /**
     * Constructor.
     *
     * @param webServiceUrl web service url
     * @throws ConfigurationError if specified protocol is not http or https
     */
    public PdmLinkApi(String webServiceUrl) throws ConfigurationError {
        super(webServiceUrl);
    }
    
    public PdmLinkDrawing getDrawing(String name) throws InvalidArgument, ObjectNotFound, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing name", name);
        String requestUrl = "/pdmLink/drawings/" + name;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkDrawing drawing = (PdmLinkDrawing) CdbObjectFactory.createCdbObject(jsonString, PdmLinkDrawing.class);
        return drawing;
    }    
    
    /*
     * Main method for testing.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        try {
            PdmLinkApi client = new PdmLinkApi("http://zagreb.svdev.net:10232/cdb");
            PdmLinkDrawing drawing = client.getDrawing("D14100201-113160.asm");
            System.out.println("Drawing: \n" + drawing);
        } catch (CdbException ex) {
            System.out.println("Sorry: " + ex);
        }
    }
}
