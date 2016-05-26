/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.traveler.common.objects;

import java.util.LinkedList;

/**
 *
 * @author djarosz
 */
public class Forms extends TravelerObject {
    
    private LinkedList<Form> forms; 

    public LinkedList<Form> getForms() {
        return forms;
    }

    public void setForms(LinkedList<Form> forms) {
        this.forms = forms;
    }
    
}
