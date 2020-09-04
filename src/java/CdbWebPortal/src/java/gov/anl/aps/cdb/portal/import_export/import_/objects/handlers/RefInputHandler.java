/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class RefInputHandler extends SimpleInputHandler {

    private static final String SEPARATOR = ",";

    private CdbEntityController controller;
    private Class paramType;
    private String domainNameFilter = null;
    private boolean idOnly = false;
    private boolean singleValue = true;
    
    private Map<Object, CdbEntity> objectIdMap = new HashMap<>();

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

    @Override
    public Class getParamType() {
        return paramType;
    }


    protected CdbEntity getObjectWithId(String idString, String message_o) {
        
        CdbEntity objValue = null;
        try {
            int id = Integer.valueOf(idString.trim());
            if (objectIdMap.containsKey(id)) {
                objValue = objectIdMap.get(id);
            } else {
                objValue = controller.findById(id);
                if (objValue != null) {
                    objectIdMap.put(objValue.getId(), objValue);
                }
            }
        } catch (NumberFormatException ex) {
            message_o = "Invalid number format id: " + idString
                    + " for: " + getColumnName();
        }
        
        return objValue;
    }
    
    protected CdbEntity getObjectWithName(String nameString, String message_o) {
        
        CdbEntity objValue = null;
                
        try {
            objValue = controller.findUniqueByName(nameString, domainNameFilter);
            if (objValue != null) {
                // check cache for object so different references use same instance
                int id = (Integer) objValue.getId();
                if (objectIdMap.containsKey(id)) {
                    objValue = objectIdMap.get(id);
                } else {
                    // add this instance to cache
                    objectIdMap.put(id, objValue);
                }
            }
        } catch (CdbException ex) {
            message_o = "Exception searching for object with name: " + nameString
                    + " for: " + getColumnName()
                    + " reason: " + ex.getMessage();
        }
        
        return objValue;
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
                        CdbEntity objValue = getObjectWithName(nameString.trim(), info_o);
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
                            CdbEntity objValue = getObjectWithName(nameToken.trim(), info_o);
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
                                objValueList.add(objValue);
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
                        CdbEntity objValue = getObjectWithId(strValue.trim(), info_o);
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
                        CdbEntity objValue = getObjectWithId(idToken.trim(), info_o);
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
                            objValueList.add(objValue);
                        }
                    }
                    return new ParseInfo<>(objValueList, true, "");
                }
            }
        }
        
        return new ParseInfo<>(null, true, "");                    
    }
}
