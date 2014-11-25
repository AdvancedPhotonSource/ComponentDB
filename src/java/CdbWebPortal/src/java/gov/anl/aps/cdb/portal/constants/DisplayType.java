/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.constants;

/**
 *
 * @author sveseli
 */
public enum DisplayType {
    FREE_FORM_TEXT(0),
    SELECTED_TEXT(1),
    HTTP_LINK(2),
    IMAGE(3);
    
    private final int type;
    private DisplayType(int type) {
        this.type = type;
    }
}
