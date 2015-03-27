package gov.anl.aps.cdb.common.objects;

import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;

public class PdmLinkDrawingRevision extends CdbObject {

    private String state;
    private String ufid;
    private Integer iteration;
    private String version;
    private String icmsUrl;

    private transient String displayIcmsUrl;

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

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
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

    public String getDisplayIcmsUrl() {
        if (displayIcmsUrl == null && icmsUrl != null) {
            displayIcmsUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(icmsUrl);
        }
        return displayIcmsUrl;
    }

}
