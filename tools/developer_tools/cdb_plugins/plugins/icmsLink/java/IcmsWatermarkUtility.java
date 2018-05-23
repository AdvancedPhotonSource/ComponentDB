/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.icmsLink;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.sun.xml.wss.impl.misc.Base64;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.apache.log4j.Logger;
import javax.xml.soap.*;
import org.apache.xml.security.exceptions.Base64DecodingException;

/**
 *
 * @author djarosz
 */
public class IcmsWatermarkUtility {        
    
    private static final String GET_NAMESPACE_URI = "http://www.stellent.com/GetFile/";
    private static final String GET_FILE_BY_NAME_ACTION = "GetFileByName";
    private static final String GET_FILE_BY_NAME_PARAM_DOC_NAME = "dDocName";
    private static final String GET_FILE_BY_NAME_PARAM_REV = "revisionSelectionMethod";
    private static final String GET_FILE_BY_NAME_PARAM_REV_VAL = "latestReleased";
    private static final String GET_FILE_BY_NAME_PARAM_RENDITION = "rendition";
    private static final String GET_FILE_BY_NAME_PARAM_RENDITION_VAL = "web";

    private static final String SOAP_RESP_RESPONSE = "idc:GetFileByNameResponse";
    private static final String SOAP_RESP_RESULT = "idc:GetFileByNameResult";
    private static final String SOAP_RESP_FILE_INFO = "idc:FileInfo";
    private static final String SOAP_RESP_DOWNLOAD_FILE = "idc:downloadFile";
    private static final String SOAP_RESULT_AUTHOR = "idc:dDocAuthor";
    private static final String SOAP_RESULT_DOC_NAME = "idc:dDocName";
    private static final String SOAP_RESULT_REV = "idc:dRevLabel";
    private static final String SOAP_RESULT_DATE = "idc:dInDate";
    private static final String SOAP_RESULT_STATUS = "idc:dStatus";
    private static final String SOAP_RESULT_FILE_CONTENT = "idc:fileContent";
    private static final String SOAP_CUSTOM_PROPERTIES = "idc:CustomDocMetaData"; 
    private static final String SOAP_CUSTOM_PROPERTY = "idc:property";
    private static final String SOAP_CUSTOM_PROPERTY_KEY = "idc:name";
    private static final String SOAP_CUSTOM_PROPERTY_VALUE = "idc:value";
    private static final String SOAP_PROPERTY_CONTROLLED_REV = "xControlledRevisionNumber"; 
    private static final String SOAP_PROPERTY_DNS_COLLECTION_ID = "xCollection"; 
    private static final String SOAP_PROPERTY_DNS_DOC_NUM = "xDocumentID";
    
    

    private static final String ICMS_UNDER_REV_STATUS = "UNDER REVISION";

    private static final Logger logger = Logger.getLogger(IcmsWatermarkUtility.class.getName());
    
    private String soapEndpointUrl = null; 
    private String soapGetFileByNameActionUrl = null;
    private String soapUsername = null; 
    private String soapPassword = null; 
    
    String author = null;
    String rev = null;
    String controllerRev = null; 
    String date = null;
    String status = null;
    String downloadContentBase64Encoded = null;
    String docName = null;
    // Custom Props
    String controlledRev = null;
    String dnsCollectionId = null;
    String dnsDocNumber = null; 

    public IcmsWatermarkUtility(String soapEndpointUrl, String soapGetFileByNameActionUrl, String username, String password) {
        this.soapEndpointUrl = soapEndpointUrl;
        this.soapGetFileByNameActionUrl = soapGetFileByNameActionUrl;
        this.soapUsername = username;
        this.soapPassword = password; 
    }

    public byte[] generateICMSPDFDocument(String documentName) throws CdbException {
        try {
            resetVariablesForNewDocument();
            
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();

            SOAPMessage envelope = createSoapEnvelope(documentName);

            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage response = soapConnection.call(envelope, soapEndpointUrl);

            SOAPBody soapBody = response.getSOAPBody();

            Iterator childElements = soapBody.getChildElements();

            soapConnection.close();

            if (loadInformationFromSoapResponse(childElements)) {
                // Process PDF generation
                return addWatermarkToPDFFile();
            } else {
                throw new CdbException("Failed to fetch all required information from ICMS web service.");
            }
        } catch (SOAPException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new CdbException(ex); 
        } catch (FileNotFoundException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new CdbException(ex); 
        } catch (IOException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new CdbException(ex); 
        } catch (Base64DecodingException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new CdbException(ex); 
        } catch (DocumentException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new CdbException(ex); 
        }
    }

    private void resetVariablesForNewDocument() {
        author = null;
        rev = null;
        date = null;
        status = null;
        downloadContentBase64Encoded = null;
        docName = null;
    }
    
    private boolean allInformationLoaded() {
        return author != null 
                && rev != null 
                && date != null 
                && status != null 
                && downloadContentBase64Encoded != null 
                && docName != null;
    }

    /**
     * Adds a stamp of some metadata to ICMS documents.
     *
     * Function Credit: Thomas Fors
     *     
     * @return byte array ologgerf the stamped PDF file
     * @throws DocumentException - Error loading pdfstamper or creating font
     * @throws IOException - Error performing IO operation 
     * @throws Base64DecodingException - Error converting downloadContent string to byte[]
     */            
    private byte[] addWatermarkToPDFFile() throws   Base64DecodingException, DocumentException, IOException {
        byte[] pdfBytes = Base64.decode(downloadContentBase64Encoded);
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm:ss a");  
        LocalDateTime now = LocalDateTime.now();  
        String downloadTime = dtf.format(now);  
        
        UserInfo user = (UserInfo) SessionUtility.getUser(); 
        String username = null; 
        if (user != null) {
            username = user.getUsername();
        } else {
            username = "unknown user"; 
        }
        String bottomMessage = "Downloaded via APS CDB by: " + username + " at " + downloadTime; 
        
        controlledRev = updateOptionalValue(controlledRev); 
        dnsCollectionId = updateOptionalValue(dnsCollectionId);
        dnsDocNumber = updateOptionalValue(dnsDocNumber); 
        
        String watermarkContents = "Content ID: " + docName;
        watermarkContents += "      Rev: " + controlledRev;
        watermarkContents += "      Released: " + date; 
        watermarkContents += "      DNS Collection ID: " + dnsCollectionId;
        watermarkContents += "      DNS Document ID: " + dnsDocNumber; 

        PdfReader pdfReader = new PdfReader(pdfBytes);
        int n = pdfReader.getNumberOfPages();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfStamper stamp = new PdfStamper(pdfReader, out);

        PdfContentByte over;
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

        for (int i = 0; i < n; i++) {
            over = stamp.getOverContent(i + 1);
            over.beginText();
            over.setTextMatrix(30, 30);
            over.setFontAndSize(bf, 10);
            over.setColorFill(new Color(0x80, 0x80, 0x80));
            over.showTextAligned(Element.ALIGN_LEFT, watermarkContents, 25, 25, 90);
            over.showTextAligned(Element.ALIGN_LEFT, bottomMessage, 50, 10, 0);
            if (status.equals(ICMS_UNDER_REV_STATUS)) {
                over.setColorFill(new Color(0xFF, 0x00, 0x00));
            }
            //over.showTextAligned(Element.ALIGN_LEFT, status, 25, 25 + bf.getWidthPoint(watermarkContents + " - ", 10), 90);

            over.endText();
        }
        stamp.close();

        return out.toByteArray();
    }
    
    private String updateOptionalValue(String value) {
        if (value == null || value.isEmpty()) {
            return "N/A"; 
        }
        
        return value; 
    }

    private boolean loadInformationFromSoapResponse(Iterator nodeItterator) {
        return loadInformationFromSoapResponse(nodeItterator, null);
    }

    /**
     * Load ICMS information from the soap body
     *
     * @param nodeItterator
     * @param lastResp
     * @return true when successfully loaded all information
     */
    private boolean loadInformationFromSoapResponse(Iterator nodeItterator, String lastResp) {
        while (nodeItterator.hasNext()) {
            SOAPElement nextElement = getNextElement(nodeItterator);
            if (nextElement != null) {
                String nodeName = nextElement.getNodeName();
                // Drill down deeper into the hierarchy 
                if (lastResp == null || lastResp.equals(SOAP_RESP_RESPONSE)) {
                    if (nodeName.equals(SOAP_RESP_RESPONSE) || nodeName.equals(SOAP_RESP_RESULT)) {
                        Iterator childElements = nextElement.getChildElements();
                        return loadInformationFromSoapResponse(childElements, nodeName);
                    }
                } else if (lastResp.equals(SOAP_RESP_RESULT)) {
                    if (nodeName.equals(SOAP_RESP_FILE_INFO)) {
                        loadInFileInformation(nextElement);
                    } else if (nodeName.equals(SOAP_RESP_DOWNLOAD_FILE)) {
                        loadInFileDownload(nextElement);
                    }
                } else {
                    return false;
                }
            }
        }
        
        return allInformationLoaded();
    }

    private SOAPElement getNextElement(Iterator nodeItterator) {
        while (nodeItterator.hasNext()) {
            Node node = (Node) nodeItterator.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (SOAPElement) node;
            }
        }
        return null;
    }

    private void loadInFileInformation(SOAPElement fileInfoElement) {
        Iterator fileInfoElements = fileInfoElement.getChildElements();

        while (fileInfoElements.hasNext()) {
            SOAPElement nextElement = getNextElement(fileInfoElements);
            if (nextElement != null) {
                if (nextElement.getNodeName().equals(SOAP_RESULT_AUTHOR)) {
                    author = nextElement.getValue();
                } else if (nextElement.getNodeName().equals(SOAP_RESULT_DATE)) {
                    date = nextElement.getValue();
                } else if (nextElement.getNodeName().equals(SOAP_RESULT_REV)) {
                    rev = nextElement.getValue();
                } else if (nextElement.getNodeName().equals(SOAP_RESULT_STATUS)) {
                    status = nextElement.getValue();
                } else if (nextElement.getNodeName().equals(SOAP_RESULT_DOC_NAME)) {
                    docName = nextElement.getValue();
                } else if (nextElement.getNodeName().equals(SOAP_CUSTOM_PROPERTIES)) {
                    processICMSProperties(nextElement);
                }
            }
        }
    }
    
    private void processICMSProperties(SOAPElement topLevelProperty) {
        Iterator properties = topLevelProperty.getChildElements();
        
        String lastKey; 
        String lastValue; 
        
        while (properties.hasNext()) {
            SOAPElement propertyNode = getNextElement(properties);
            if (propertyNode != null) {
                if (propertyNode.getNodeName().equals(SOAP_CUSTOM_PROPERTY)) {
                    lastKey = "";
                    lastValue = ""; 
                    Iterator propertyContents = propertyNode.getChildElements();
                    while (propertyContents.hasNext()) {
                        SOAPElement propContent = getNextElement(propertyContents);
                        if (propContent != null) {
                            if (propContent.getNodeName().equals(SOAP_CUSTOM_PROPERTY_KEY)) {
                                lastKey = propContent.getValue(); 
                            } else if (propContent.getNodeName().equals(SOAP_CUSTOM_PROPERTY_VALUE)) {
                                lastValue = propContent.getValue(); 
                            }
                        }
                    }
                    // Verify if key is of interest 
                    if (lastKey.equals(SOAP_PROPERTY_CONTROLLED_REV)) {
                        controlledRev = lastValue; 
                    } else if (lastKey.equals(SOAP_PROPERTY_DNS_COLLECTION_ID)) {
                        dnsCollectionId = lastValue;
                    } else if (lastKey.equals(SOAP_PROPERTY_DNS_DOC_NUM)) {
                        dnsDocNumber = lastValue; 
                    }                    
                }
            }            
        }
    }        

    private void loadInFileDownload(SOAPElement fileDownloadElement) {
        Iterator childElements = fileDownloadElement.getChildElements();
        while (childElements.hasNext()) {
            SOAPElement nextElement = getNextElement(childElements);

            if (nextElement != null) {
                if (nextElement.getNodeName().equals(SOAP_RESULT_FILE_CONTENT)) {
                    downloadContentBase64Encoded = nextElement.getValue();
                }
            }
        }
    }

    private SOAPMessage createSoapEnvelope(String documentName) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("get", GET_NAMESPACE_URI);

        /*
            Constructed SOAP Request Message:
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:get="http://www.stellent.com/GetFile/">
                <soapenv:Header/>
                <soapenv:Body>
                   <get:GetFileByName>         
                      <get:dDocName>documentName</get:dDocName>         
                      <get:revisionSelectionMethod>latestReleased</get:revisionSelectionMethod>        
                      <get:rendition>web</get:rendition>
                   </get:GetFileByName>
                </soapenv:Body>
             </soapenv:Envelope>
         */
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(GET_FILE_BY_NAME_ACTION, "get");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(GET_FILE_BY_NAME_PARAM_DOC_NAME, "get");
        soapBodyElem1.addTextNode(documentName);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement(GET_FILE_BY_NAME_PARAM_RENDITION, "get");
        soapBodyElem2.addTextNode(GET_FILE_BY_NAME_PARAM_RENDITION_VAL);
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement(GET_FILE_BY_NAME_PARAM_REV, "get");
        soapBodyElem3.addTextNode(GET_FILE_BY_NAME_PARAM_REV_VAL);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapGetFileByNameActionUrl);
        String usernamePass = soapUsername + ":" + soapPassword;
        String authString = new String(Base64.encode(usernamePass.getBytes()));
        headers.addHeader("Authorization", "basic " + authString);

        soapMessage.saveChanges();

        return soapMessage;
    }

    public static void main(String[] args) throws CdbException {
        //IcmsWatermarkUtility test = new IcmsWatermarkUtility();
        //test.generateICMSPDFDocument("APS_1199364");        
    }

}
