package gov.anl.aps.cdb.utilities;

import gov.anl.aps.cdb.constants.CdbServiceProtocol;
import java.net.URL;


public class UrlUtility {

    public static final String URL_SCHEME_DELIMITER = "://";

    public static CdbServiceProtocol getProtocol(String url) {
        int index = url.indexOf(URL_SCHEME_DELIMITER);
        CdbServiceProtocol protocol = CdbServiceProtocol.HTTP;
        if (index > 0) {
            protocol = CdbServiceProtocol.fromString(url.substring(0, index));    
        }
        return protocol;
    }

    public static String getHost(String url) {
        int beginIndex = url.indexOf(URL_SCHEME_DELIMITER);
        String host = null;
        if (beginIndex > 0) {
            host = url.substring(beginIndex+URL_SCHEME_DELIMITER.length());
            host = host.replace("/", "");
            int endIndex = host.indexOf('/');
        }
        return null;
    }
        
    public static void main(String[] args) throws Exception {
        String urlString = "https://localhost.localdomain:10232/cdb";
        URL url = new URL(urlString);
        System.out.println("URL: " + urlString);
        System.out.println("Protocol: " + url.getProtocol());
        System.out.println("Host: " + url.getHost());
        System.out.println("Port: " + url.getPort());
    }
}
