/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RefObjectManager;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class RefInputHandler extends SimpleInputHandler {

    private static final String SEPARATOR = ",";

    private Class paramType;
    private CdbEntityController controller;
    private String domainNameFilter = null;
    private boolean idOnly = false;
    private boolean singleValue = true;
    private RefObjectManager objectManager;
    
    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        
        super(columnIndex, columnName, propertyName, setterMethod);
        this.controller = controller;
        this.paramType = paramType;
    }

    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            boolean idOnly,
            boolean singleValue) {
        
        this(columnIndex, columnName, propertyName, setterMethod, controller, paramType);
        this.idOnly = idOnly;
        this.singleValue = singleValue;
    }

    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter) {
        
        this(columnIndex, columnName, propertyName, setterMethod, controller, paramType);
        this.domainNameFilter = domainNameFilter;
    }

    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter,
            boolean idOnly,
            boolean singleValue) {
        
        this(columnIndex, columnName, propertyName, setterMethod, controller, paramType, domainNameFilter);
        this.idOnly = idOnly;
        this.singleValue = singleValue;
    }
    
    private RefObjectManager getObjectManager() {
        if (objectManager == null) {
            objectManager = new RefObjectManager(controller, domainNameFilter);
        }
        return objectManager;
    }

    @Override
    public Class getParamType() {
        return paramType;
    }

    @Override
    public ParseInfo parseCellValue(String strValue) {
        
        if (strValue != null && strValue.length() > 0) {
            
            if (strValue.charAt(0) == '#') {
                // lookup by name(s)
                
                if (idOnly) {
                    String msg = "Lookup by name not enabled for column: "
                            + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                }
                
                String nameString = strValue.substring(1);                
                if (!nameString.isEmpty()) {
                    
                    if (singleValue) {
                        // parse and lookup single name
                        
                        String info_o = null;
                        CdbEntity objValue = 
                                getObjectManager().getObjectWithName(nameString.trim(), info_o);
                        if (objValue == null) {
                            String msg;
                            if (info_o != null) {
                                msg = info_o;
                            } else {
                                msg = "Unable to find object for: "
                                            + getColumnName()
                                            + " with name: " + nameString;
                            }
                            return new ParseInfo<>(null, false, msg);
                        }
                        return new ParseInfo<>(objValue, true, "");
                        
                    } else {
                        // parse and lookup list of names

                        List<CdbEntity> objValueList = new ArrayList<>();
                        String[] nameTokens = nameString.split(SEPARATOR);
                        for (String nameToken : nameTokens) {
                            String info_o = null;
                            CdbEntity objValue = 
                                    getObjectManager().getObjectWithName(nameToken.trim(), info_o);
                            if (objValue == null) {
                                String msg;
                                if (info_o != null) {
                                    msg = info_o;
                                } else {
                                    msg = "Unable to find object for: "
                                            + getColumnName()
                                            + " with name: " + nameToken;
                                }
                                return new ParseInfo<>(objValueList, false, msg);
                            } else {
                                if (!objValueList.contains(objValue)) {
                                    objValueList.add(objValue);
                                }
                            }
                        }
                        return new ParseInfo<>(objValueList, true, "");
                    }
                }
                
            } else {
                // lookup by id(s)
                
                if (singleValue) {
                    // parse and lookup single id
                    
                        String info_o = null;
                        CdbEntity objValue = 
                                getObjectManager().getObjectWithId(strValue.trim(), info_o);
                        if (objValue == null) {
                            String msg;
                            if (info_o != null) {
                                msg = info_o;
                            } else {
                                msg = "Unable to find object for: "
                                            + getColumnName()
                                            + " with id: " + strValue;
                            }
                            return new ParseInfo<>(null, false, msg);
                        }
                        return new ParseInfo<>(objValue, true, "");
                        
                } else {
                    // parse and lookup list of ids
                    
                    List<CdbEntity> objValueList = new ArrayList<>();
                    String[] idTokens = strValue.split(SEPARATOR);
                    for (String idToken : idTokens) {
                        String info_o = null;
                        CdbEntity objValue = getObjectManager().getObjectWithId(idToken.trim(), info_o);
                        if (objValue == null) {
                            String msg;
                            if (info_o != null) {
                                msg = info_o;
                            } else {
                                msg = "Unable to find object for: "
                                        + getColumnName()
                                        + " with id: " + idToken;
                            }
                            return new ParseInfo<>(objValueList, false, msg);
                        } else {
                            if (!objValueList.contains(objValue)) {
                                objValueList.add(objValue);
                            }
                        }
                    }
                    return new ParseInfo<>(objValueList, true, "");
                }
            }
        }
        
        return new ParseInfo<>(null, true, "");                    
    }
}
