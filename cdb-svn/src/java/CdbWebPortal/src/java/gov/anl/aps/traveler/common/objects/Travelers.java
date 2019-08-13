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
public class Travelers extends TravelerObject {
    
    private LinkedList<Traveler> travelers; 

    public LinkedList<Traveler> getTravelers() {
        return travelers;
    }
    
}
