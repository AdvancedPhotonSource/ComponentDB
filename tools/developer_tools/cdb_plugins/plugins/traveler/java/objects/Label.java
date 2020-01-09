/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.util.LinkedList;

/**
 *
 * @author djarosz
 */
public class Label extends TravelerObject {
    
    private LinkedList<Form> forms; 

    public LinkedList<Form> getForms() {
        return forms;
    }

    public void setForms(LinkedList<Form> forms) {
        this.forms = forms;
    }
    
}
