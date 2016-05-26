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
public class TravelerData extends TravelerObject {
    
    private LinkedList<TravelerDatum> data;

    public LinkedList<TravelerDatum> getData() {
        return data;
    }
    
}
