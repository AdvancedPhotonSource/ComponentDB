/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
 */
package gov.anl.aps.cdb.common.objects;

import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;

/**
 * PDMLink drawing revision object.
 */
public class PdmLinkDrawingRevision extends CdbObject {

    private String state;
    private String ufid;
    private Integer iteration;
    private String version;
    private String icmsUrl;

    private transient String displayIcmsUrl;

    /**
     * Default constructor.
     */
    public PdmLinkDrawingRevision() {
    }

    /**
     * Get drawing state.
     *
     * @return drawing state string
     */
    public String getState() {
        return state;
    }

    /**
     * Set drawing state.
     *
     * @param state drawing state string
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get drawing revision UFID.
     *
     * @return drawing revision UFID
     */
    public String getUfid() {
        return ufid;
    }

    /**
     * Set drawing revision UFID.
     *
     * @param ufid drawing revision UFID
     */
    public void setUfid(String ufid) {
        this.ufid = ufid;
    }

    /**
     * Get drawing revision iteration.
     *
     * @return drawing revision iteration
     */
    public Integer getIteration() {
        return iteration;
    }

    /**
     * Set drawing revision iteration.
     *
     * @param iteration drawing revision iteration
     */
    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    /**
     * Get drawing revision version.
     *
     * @return drawing revision version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set drawing revision version.
     *
     * @param version drawing revision version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get ICMS URL for drawing revision.
     *
     * @return ICMS URL
     */
    public String getIcmsUrl() {
        return icmsUrl;
    }

    /**
     * Set ICMS URL for drawing revision.
     *
     * @param icmsUrl ICMS URL
     */
    public void setIcmsUrl(String icmsUrl) {
        this.icmsUrl = icmsUrl;
    }

    /**
     * Get ICMS URL string suitable for display on a web page.
     *
     * @return ICMS URL display string
     */
    public String getDisplayIcmsUrl() {
        if (displayIcmsUrl == null && icmsUrl != null) {
            displayIcmsUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(icmsUrl);
        }
        return displayIcmsUrl;
    }

}
