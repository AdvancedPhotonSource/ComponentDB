/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
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
            Arrays.asList("drw", "asm", "prt", "sec","frm"));

    private String number; 
    private String windchillUrl;
    private String respEng; 
    private String drafter; 
    private String wbsDescription; 
    private String title1; 
    private String title2; 
    private String title3;
    private String title4;
    private String title5;

    private LinkedList<PdmLinkDrawingRevision> revisionList;

    private transient String displayWindchillUrl;

    public static boolean isExtensionValid(String drawingName) {
        if (drawingName == null || drawingName.isEmpty()) {
            return false;
        }
        String extension = FileUtility.getFileExtension(drawingName).toLowerCase();
        return VALID_EXTENSION_LIST.contains(extension);
    }


    public PdmLinkDrawing() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    public String getWindchillUrl() {
        return windchillUrl;
    }

    public void setWindchillUrl(String windchillUrl) {
        this.windchillUrl = windchillUrl;
    }

    /**
     * Shorten windchill URL string so that it is suitable for display on a web page.
     *
     * @return display value for windchill URL string
     */
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

    public String getRespEng() {
        return respEng;
    }

    public String getDrafter() {
        return drafter;
    }

    public String getWbsDescription() {
        return wbsDescription;
    }

    public String getTitle1() {
        return title1;
    }

    public String getTitle2() {
        return title2;
    }

    public String getTitle3() {
        return title3;
    }

    public String getTitle4() {
        return title4;
    }

    public String getTitle5() {
        return title5;
    }
    
    public void setRespEng(String respEng) {
        this.respEng = respEng;
    }

    public void setDrafter(String drafter) {
        this.drafter = drafter;
    }

    public void setWbsDescription(String wbsDescription) {
        this.wbsDescription = wbsDescription;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }

    public void setTitle5(String title5) {
        this.title5 = title5;
    }
   
}
