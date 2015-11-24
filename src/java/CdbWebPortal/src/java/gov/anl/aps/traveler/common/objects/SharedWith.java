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
public class SharedWith extends TravelerObject {
    private String username;
    private int access; 

    public String getUsername() {
        return username;
    }

    public int getAccess() {
        return access;
    }
    
    
}
