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
public class TravelerNotes extends TravelerObject {
    LinkedList<TravelerNote> notes; 

    public LinkedList<TravelerNote> getNotes() {
        return notes;
    }
    
}
