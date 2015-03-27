package gov.anl.aps.cdb.common.objects;

import java.util.LinkedList;

public class PdmLinkDrawing extends CdbObject {

    private String windchillUrl; 
    private LinkedList<PdmLinkDrawingRevision> revisionList;
    
    public PdmLinkDrawing() {
    }

    public String getWindchillUrl() {
        return windchillUrl;
    }

    public void setWindchillUrl(String windchillUrl) {
        this.windchillUrl = windchillUrl;
    }

    public LinkedList<PdmLinkDrawingRevision> getRevisionList() {
        return revisionList;
    }

    public void setRevisionList(LinkedList<PdmLinkDrawingRevision> revisionList) {
        this.revisionList = revisionList;
    }

    
    
}
