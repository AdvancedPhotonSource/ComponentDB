/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import gov.anl.aps.irmis.persistence.login.Person;

/**
 *  APS-specific extended information about IOC's.
 *  This class is not required for the base configuration of 
 *  IRMIS, although we supply it as an example. There is a corresponding plug-in
 *  for the Java IRMIS desktop which uses this class. You may implement your own
 *  table, mapping, class, and plug-in as needed.
 */
public class APSIOC extends IRMISDataObject {

    private IOC ioc;
    private Person cogDeveloper;
    private Person cogTechnician;

    private String location;
    private String termServRackNo;
    private String termServName;
    private int termServPort = 0;
    private String termServFiberConvCh;
    private int termServFiberConvPort = 0;
    private String primEnetSwRackNo;
    private String primEnetSwitch;
    private String primEnetBlade;
    private int primEnetPort = 0;
    private String primEnetMedConvCh;
    private int primMediaConvPort = 0;
    private String secEnetSwRackNo;
    private String secEnetSwitch;
    private String secEnetBlade;
    private int secEnetPort = 0;
    private String secEnetMedConvCh;
    private int secMedConvPort = 0;
    private String generalFunctions;
    private String preBootInstr;
    private String postBootInstr;
    private String powerCycleCaution;
    private boolean sysResetRequired;
    private boolean inhibitAutoReboot;

    /**
     * Constructor
     */
    public APSIOC() {
    }

    /**
     * Get IOC associated with this attribute.
     */
    public IOC getIoc() {
        return this.ioc;
    }
    /**
     * Set IOC associated with this attribute.
     */
    public void setIoc(IOC value) {
        this.ioc = value;
    }

    public Person getCogDeveloper() {
        return cogDeveloper;
    }
    public void setCogDeveloper(Person value) {
        cogDeveloper = value;
    }

    public Person getCogTechnician() {
        return cogTechnician;
    }
    public void setCogTechnician(Person value) {
        cogTechnician = value;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String value) {
        location = value;
    }

    public String getTermServRackNo() {
        return termServRackNo;
    }
    public void setTermServRackNo(String value) {
        termServRackNo = value;
    }

    public String getTermServName() {
        return termServName;
    }
    public void setTermServName(String value) {
        termServName = value;
    }

    public int getTermServPort() {
        return termServPort;
    }
    public void setTermServPort(int value) {
        termServPort = value;
    }

    public String getTermServFiberConvCh() {
        return termServFiberConvCh;
    }
    public void setTermServFiberConvCh(String value) {
        termServFiberConvCh = value;
    }

    public int getTermServFiberConvPort() {
        return termServFiberConvPort;
    }
    public void setTermServFiberConvPort(int value) {
        termServFiberConvPort = value;
    }

    public String getPrimEnetSwRackNo() {
        return primEnetSwRackNo;
    }
    public void setPrimEnetSwRackNo(String value) {
        primEnetSwRackNo = value;
    }

    public String getPrimEnetSwitch() {
        return primEnetSwitch;
    }
    public void setPrimEnetSwitch(String value) {
        primEnetSwitch = value;
    }

    public String getPrimEnetBlade() {
        return primEnetBlade;
    }
    public void setPrimEnetBlade(String value) {
        primEnetBlade = value;
    }

    public int getPrimEnetPort() {
        return primEnetPort;
    }
    public void setPrimEnetPort(int value) {
        primEnetPort = value;
    }

    public String getPrimEnetMedConvCh() {
        return primEnetMedConvCh;
    }
    public void setPrimEnetMedConvCh(String value) {
        primEnetMedConvCh = value;
    }

    public int getPrimMediaConvPort() {
        return primMediaConvPort;
    }
    public void setPrimMediaConvPort(int value) {
        primMediaConvPort = value;
    }

    public String getSecEnetSwRackNo() {
        return secEnetSwRackNo;
    }
    public void setSecEnetSwRackNo(String value) {
        secEnetSwRackNo = value;
    }

    public String getSecEnetSwitch() {
        return secEnetSwitch;
    }
    public void setSecEnetSwitch(String value) {
        secEnetSwitch = value;
    }

    public String getSecEnetBlade() {
        return secEnetBlade;
    }
    public void setSecEnetBlade(String value) {
        secEnetBlade = value;
    }

    public int getSecEnetPort() {
        return secEnetPort;
    }
    public void setSecEnetPort(int value) {
        secEnetPort = value;
    }

    public String getSecEnetMedConvCh() {
        return secEnetMedConvCh;
    }
    public void setSecEnetMedConvCh(String value) {
        secEnetMedConvCh = value;
    }

    public int getSecMedConvPort() {
        return secMedConvPort;
    }
    public void setSecMedConvPort(int value) {
        secMedConvPort = value;
    }

    public String getGeneralFunctions() {
        return generalFunctions;
    }
    public void setGeneralFunctions(String value) {
        generalFunctions = value;
    }

    public String getPreBootInstr() {
        return preBootInstr;
    }
    public void setPreBootInstr(String value) {
        preBootInstr = value;
    }

    public String getPostBootInstr() {
        return postBootInstr;
    }
    public void setPostBootInstr(String value) {
        postBootInstr = value;
    }

    public String getPowerCycleCaution() {
        return powerCycleCaution;
    }
    public void setPowerCycleCaution(String value) {
        powerCycleCaution = value;
    }

    public boolean getSysResetRequired() {
        return sysResetRequired;
    }
    public void setSysResetRequired(boolean value) {
        sysResetRequired = value;
    }

    public boolean getInhibitAutoReboot() {
        return inhibitAutoReboot;
    }
    public void setInhibitAutoReboot(boolean value) {
        inhibitAutoReboot = value;
    }


    public String toString() {
        return getId().toString() + ":" + getIoc().getIocName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof APSIOC) ) return false;
        final APSIOC castOther = (APSIOC) other;
        return this.getIoc() == castOther.getIoc();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getIoc().getId());
        return result;
    }


}
