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

import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * PDMLink drawing object.
 */
public class PdmLinkDrawing extends CdbObject {

    /**
     * List of valid drawing extensions.
     */
    public static final List<String> VALID_EXTENSION_LIST = Collections.unmodifiableList(
            Arrays.asList("drw", "asm", "prt"));

    private String windchillUrl;
    private LinkedList<PdmLinkDrawingRevision> revisionList;

    private transient String displayWindchillUrl;

    /**
     * Check if drawing extension is valid.
     *
     * @param drawingName drawing name
     * @return true if extension is valid, false otherwise
     */
    public static boolean isExtensionValid(String drawingName) {
        if (drawingName == null || drawingName.isEmpty()) {
            return false;
        }
        String extension = FileUtility.getFileExtension(drawingName).toLowerCase();
        return VALID_EXTENSION_LIST.contains(extension);
    }

    /**
     * Default constructor.
     */
    public PdmLinkDrawing() {
    }

    /**
     * Get windchill web service URL.
     *
     * @return windchill URL string
     */
    public String getWindchillUrl() {
        return windchillUrl;
    }

    /**
     * Set windchill web service URL.
     *
     * @param windchillUrl windchill URL string
     */
    public void setWindchillUrl(String windchillUrl) {
        this.windchillUrl = windchillUrl;
    }

    /**
     * Get windchill URL string suitable for display on a web page.
     *
     * @return display value for windchill URL string
     */
    public String getDisplayWindchillUrl() {
        if (displayWindchillUrl == null && windchillUrl != null) {
            displayWindchillUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(windchillUrl);
        }
        return displayWindchillUrl;
    }

    /**
     * Get list of drawing revisions.
     *
     * @return list of drawing revision objects
     */
    public LinkedList<PdmLinkDrawingRevision> getRevisionList() {
        return revisionList;
    }

    /**
     * Set list of drawing revisions.
     *
     * @param revisionList list of drawing revision objects
     */
    public void setRevisionList(LinkedList<PdmLinkDrawingRevision> revisionList) {
        this.revisionList = revisionList;
    }

}
