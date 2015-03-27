package gov.anl.aps.cdb.common.objects;


public class PdmLinkDrawingRevision {
 
    private String state;
    private String ufid;
    private String iteration;
    private String version;
    private String icmsUrl;

    public PdmLinkDrawingRevision() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUfid() {
        return ufid;
    }

    public void setUfid(String ufid) {
        this.ufid = ufid;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIcmsUrl() {
        return icmsUrl;
    }

    public void setIcmsUrl(String icmsUrl) {
        this.icmsUrl = icmsUrl;
    }
    
    
}
