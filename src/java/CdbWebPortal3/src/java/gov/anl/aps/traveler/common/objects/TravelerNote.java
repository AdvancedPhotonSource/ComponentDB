/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.traveler.common.objects;

/**
 *
 * @author djarosz
 */
public class TravelerNote extends TravelerObject {
    private String name; 
    private String value; 
    private String inputBy; 
    private String inputOn; 

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getInputBy() {
        return inputBy;
    }

    public String getInputOn() {
        return inputOn;
    }
    
}
