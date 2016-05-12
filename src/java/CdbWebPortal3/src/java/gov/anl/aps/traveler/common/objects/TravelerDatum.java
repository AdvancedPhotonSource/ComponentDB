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
public class TravelerDatum extends TravelerObject {
    private String name; 
    private String value; 
    private String inputType; 
    private String inputBy; 
    private String inputOn; 
    private String traveler; 

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getInputType() {
        return inputType;
    }

    public String getInputBy() {
        return inputBy;
    }

    public String getInputOn() {
        return inputOn;
    }

    public String getTraveler() {
        return traveler;
    }
}
