package gov.anl.aps.cdb.common.objects;

import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PdmLinkDrawing extends CdbObject {

    public static final List<String> VALID_EXTENSION_LIST = Collections.unmodifiableList(
            Arrays.asList("drw", "asm", "prt"));
    
    private String windchillUrl; 
    private LinkedList<PdmLinkDrawingRevision> revisionList;
    
    private transient String displayWindchillUrl;
    
    public static boolean isExtensionValid(String drawingName) {
        if (drawingName == null || drawingName.isEmpty()) {
            return false;
        }
        String extension = FileUtility.getFileExtension(drawingName).toLowerCase();
        if (VALID_EXTENSION_LIST.contains(extension)) {
            return true;
        }
        return false;
    }
    
    public PdmLinkDrawing() {
    }

    public String getWindchillUrl() {
        return windchillUrl;
    }

    public void setWindchillUrl(String windchillUrl) {
        this.windchillUrl = windchillUrl;
    }

    public String getDisplayWindchillUrl() {
        if (displayWindchillUrl == null && windchillUrl != null) {
            displayWindchillUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(windchillUrl);
        }
        return displayWindchillUrl;
    }

    public LinkedList<PdmLinkDrawingRevision> getRevisionList() {
        return revisionList;
    }

    public void setRevisionList(LinkedList<PdmLinkDrawingRevision> revisionList) {
        this.revisionList = revisionList;
    }

    
}
