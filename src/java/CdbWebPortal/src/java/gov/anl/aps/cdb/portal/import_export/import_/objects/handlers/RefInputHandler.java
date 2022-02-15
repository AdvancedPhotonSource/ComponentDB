/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RefObjectManager;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.io.IOException;
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
    private static final String PREFIX_QR = "qr:";

    private Class paramType;
    private List<CdbEntityController> controllers = new ArrayList<>();
    private String domainNameFilter = null;
    private boolean idOnly = false;
    private boolean singleValue = true;
    private boolean allowPaths = false;
    
    private Map<String, RefObjectManager> objectManagerMap = new HashMap<>();
    
    public RefInputHandler(String columnName) {
        super(columnName);
    }
    
    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        
        super(columnIndex, columnName, propertyName, setterMethod);
        this.controllers.add(controller);
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
    
    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter,
            boolean idOnly,
            boolean singleValue,
            boolean allowPaths) {
        
        this(columnIndex, columnName, propertyName, setterMethod, controller, paramType, domainNameFilter, idOnly, singleValue);
        this.allowPaths = allowPaths;
    }
    
    public RefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            List<CdbEntityController> controllers,
            Class paramType,
            String domainNameFilter,
            boolean idOnly,
            boolean singleValue,
            boolean allowPaths) {
        
        super(columnIndex, columnName, propertyName, setterMethod);
        this.controllers.addAll(controllers);
        this.paramType = paramType;
        this.domainNameFilter = domainNameFilter;
        this.idOnly = idOnly;
        this.singleValue = singleValue;
        this.allowPaths = allowPaths;
    }
    
    public void setDomainNameFilter(String filter) {
        domainNameFilter = filter;
    }
    
    public String getDomainNameFilter() {
        return domainNameFilter;
    }
    
    protected RefObjectManager getObjectManager(CdbEntityController controller) {
        return getObjectManager(controller, domainNameFilter);
    }
    
    public RefObjectManager getObjectManager(CdbEntityController controller, String domainNameFilter) {
        String entityTypeName = controller.getEntityTypeName();
        if (!objectManagerMap.containsKey(entityTypeName)) {
            // add a new RefObjectManager instance for entity type if not present in map
            objectManagerMap.put(entityTypeName, new RefObjectManager(controller, domainNameFilter));
        }
        return objectManagerMap.get(controller.getEntityTypeName());
    }

    @Override
    public Class getParamType() {
        return paramType;
    }
    
    protected CdbEntity getCacheObject(CdbEntityController controller, CdbEntity entity) {
        return getObjectManager(controller).getCacheObject(entity);
    }
    
    protected ParseInfo getSingleObjectById(String idString) {
        
        CdbEntity objValue = null;
        String msg = "";

        for (CdbEntityController controller : controllers) {
            try {
                objValue = getObjectManager(controller).getObjectWithId(idString);
            } catch (CdbException ex) {
                msg = "Exception searching for object with id: " + idString 
                        + " in column: " + getColumnName()
                        + " reason: " + ex.getMessage();
                break;
            }
            if (objValue != null) {
                // stop when we find a match
                break;
            }
        }       

        if (objValue == null) {
            if (msg.isEmpty()) {
                msg = "Unable to find object for: "
                        + getColumnName() + " with id: " + idString;
            }
            return new ParseInfo<>(null, false, msg);
        }
        
        return new ParseInfo<>(objValue, true, "");                        
    }

    /**
     * Allows subclasses to customize look up for single object by name. 
     */
    protected ParseInfo getSingleObjectByName(String nameString) {
        
        CdbEntity objValue = null;
        String msg = "";

        for (CdbEntityController controller : controllers) {
            try {
                objValue = getObjectManager(controller).getObjectWithName(nameString.trim(), getDomainNameFilter());
            } catch (CdbException ex) {
                msg = "Exception searching for object with name: " + nameString 
                        + " in column: " + getColumnName()
                        + " reason: " + ex.getMessage();
                break;
            }
            if (objValue != null) {
                // stop when we find a match
                break;
            }
        }       

        if (objValue == null) {
            if (msg.isEmpty()) {
                msg = "Unable to find object for: "
                        + getColumnName() + " with name: " + nameString;
            }
            return new ParseInfo<>(null, false, msg);
        }
        
        return new ParseInfo<>(objValue, true, "");                        
    }

    /**
     * Allows subclasses to customize look up for single object by qr. 
     */
    protected ParseInfo getSingleObjectByQrId(Integer qrId) {
        
        CdbEntity objValue = null;
        String msg = "";

        for (CdbEntityController controller : controllers) {
            try {
                objValue = getObjectManager(controller).getObjectWithQrId(qrId);
            } catch (CdbException ex) {
                msg = "Exception searching for object with QR: " + qrId 
                        + " in column: " + getColumnName()
                        + " reason: " + ex.getMessage();
                break;
            }
            if (objValue != null) {
                // stop when we find a match
                break;
            }
        }

        if (objValue == null) {
            if (msg.isEmpty()) {
                msg = "Unable to find object for: "
                        + getColumnName() + " with QR: " + qrId;
            }
            return new ParseInfo<>(null, false, msg);
        }
        
        return new ParseInfo<>(objValue, true, "");                        
    }

    /**
     * Allows subclasses to customize look up for single object by name. 
     */
    protected ParseInfo getObjectWithAttributes(Map<String,String> attributeMap) {
        
        CdbEntity objValue = null;
        String msg = "";

        for (CdbEntityController controller : controllers) {
            try {
                objValue = getObjectManager(controller).getObjectWithAttributes(attributeMap);
            } catch (CdbException ex) {
                msg = "Exception searching for object with attributes: " + attributeMap.toString() 
                        + " in column: " + getColumnName()
                        + " reason: " + ex.getMessage();
                break;
            }
            if (objValue != null) {
                // stop when we find a match
                break;
            }
        }

        if (objValue == null) {
            if (msg.isEmpty()) {
                msg = "Unable to find object for: "
                        + getColumnName() + " with attributes: " + attributeMap.toString();
            }
            return new ParseInfo<>(null, false, msg);
        }
        
        return new ParseInfo<>(objValue, true, "");                        
    }
    
    protected ParseInfo getObjectWithPath(String path) {
        
        CdbEntity objValue = null;
        String msg = "";
        
        for (CdbEntityController controller : controllers) {
            try {
                objValue = getObjectManager(controller).getObjectWithPath(path);
            } catch (CdbException ex) {
                msg = "Exception searching for object with path: " + path 
                        + " in column: " + getColumnName()
                        + " reason: " + ex.getMessage();
                break;
            }
            if (objValue != null) {
                // stop when we find a match
                break;
            }
        }

        if (objValue == null) {
            if (msg.isEmpty()) {
                msg = "Unable to find object for: "
                        + getColumnName() + " with path: " + path;
            }
            return new ParseInfo<>(null, false, msg);
        }
        
        return new ParseInfo<>(objValue, true, "");                        
    }
    
    public static Map<String,String> mapFromJson(String value) throws CdbException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> attributeMap = null;
        try {
            attributeMap = mapper.readValue(value, Map.class);
        } catch (JsonProcessingException e) {
            throw new CdbException("Exception parsing attribute map: " + value);
        }
        return attributeMap;
    }

    @Override
    public ParseInfo parseCellValue(String strValue) {
        
        if (strValue != null && strValue.length() > 0) {
            
            if (strValue.charAt(0) == '{') {
                // look up by map of attributes
                
                if (idOnly) {
                    String msg = "Lookup by attribute map not enabled for column: " + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                }
                
                CdbEntity objValue = null;
                String msg = "";
                
                Map<String,String> attributeMap = null;
                try {
                    attributeMap = mapFromJson(strValue);
                } catch (CdbException e) {
                    return new ParseInfo<>(null, false, e.getMessage());
                } catch (IOException e) {
                    return new ParseInfo<>(null, false, e.getMessage());
                }
                
                if (attributeMap == null) {
                    msg = "Exception parsing attribute map for column: " + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                }
                
                ParseInfo parseInfo = getObjectWithAttributes(attributeMap);
                if (!parseInfo.getValidInfo().isValid()) {
                    return parseInfo;
                } else {
                    objValue = (CdbEntity) parseInfo.getValue();
                    return new ParseInfo<>(objValue, true, "");
                }
                
            } else if (strValue.startsWith(PREFIX_QR)) {
                // parse as qrId
                
                String msg = "";
                CdbEntity objValue = null;
                
                if (!singleValue) {
                    msg = "Lookup by QR code" 
                            + " in column: " + getColumnName()
                            + " only supported for single item references, not list.";
                    return new ParseInfo<>(null, false, msg);
                }
                
                if (idOnly) {
                    msg = "Lookup by QR code not enabled for column: " + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                }

                String[] tokens = strValue.split(PREFIX_QR);
                if (tokens.length != 2) {
                    msg = "Invalid qrId format or missing qrId: " + strValue
                            + " in column: " + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                    
                } else {
                    String qrStr = tokens[1];
                    if ((qrStr == null) || (qrStr.isBlank())) {
                        msg = "Missing or emtpy qrId"
                                + " in column: " + getColumnName();
                        return new ParseInfo<>(null, false, msg);
                        
                    } else {
                        qrStr = qrStr.trim(); // remove leading/trailing space
                        qrStr = qrStr.replaceAll("\\s+", ""); // remove internal spaces e.g., "000 123 456"
                        Integer qrId = null;
                        try {
                            qrId = Integer.valueOf(qrStr);
                            
                            ParseInfo objWithQrInfo = getSingleObjectByQrId(qrId);
                            if (!objWithQrInfo.getValidInfo().isValid()) {
                                return objWithQrInfo;
                            } else {
                                objValue = (CdbEntity) objWithQrInfo.getValue();
                            }
                            
                            if (objValue == null) {
                                msg = "Unable to find object for: " + getColumnName() + " with qrId: " + qrId;
                                return new ParseInfo<>(null, false, msg);
                            }
                            return new ParseInfo<>(objValue, true, "");
                            
                        } catch (NumberFormatException ex) {
                            msg = "Invalid qrId number: " + qrStr + " for column: " + getColumnName();
                            return new ParseInfo<>(null, false, msg);
                        }
                    }
                }

            } else if (strValue.charAt(0) == '#') {
                // lookup by name(s)
                
                if (idOnly) {
                    String msg = "Lookup by name not enabled for column: "
                            + getColumnName();
                    return new ParseInfo<>(null, false, msg);
                }
                
                String nameString = strValue.substring(1);                
                if (!nameString.isEmpty()) {
                    
                    if (singleValue) {
                        // parse and lookup single name or path
                        
                        CdbEntity objValue = null;
                        String msg = "";
                        
                        if (allowPaths && (nameString.charAt(0) == '/')) {
                            // lookup by path
                            
                            // single slash indicates no parent item
                            if (nameString.equals("/")) {
                                return new ParseInfo<>(null, true, "");
                            }
                            
                            ParseInfo objWithPathInfo = getObjectWithPath(nameString.trim());
                            if (!objWithPathInfo.getValidInfo().isValid()) {
                                return objWithPathInfo;
                            } else {
                                objValue = (CdbEntity) objWithPathInfo.getValue();
                            }
                            
                        } else {
                            // lookup by name
                            
                            ParseInfo objWithNameInfo = getSingleObjectByName(nameString);
                            if (!objWithNameInfo.getValidInfo().isValid()) {
                                return objWithNameInfo;
                            } else {
                                objValue = (CdbEntity) objWithNameInfo.getValue();
                            }
                        }

                        if (objValue == null) {
                            if (msg.isEmpty()) {
                                msg = "Unable to find object for: "
                                        + getColumnName() + " with name: " + nameString;
                            }
                            return new ParseInfo<>(null, false, msg);
                        }
                        return new ParseInfo<>(objValue, true, "");
                        
                    } else {
                        // parse and lookup list of names

                        List<CdbEntity> objValueList = new ArrayList<>();
                        String[] nameTokens = nameString.split(SEPARATOR);
                        for (String nameToken : nameTokens) {
                            CdbEntity objValue = null;
                            String msg = "";
                            ParseInfo objWithNameInfo = getSingleObjectByName(nameToken.trim());
                            if (!objWithNameInfo.getValidInfo().isValid()) {
                                return objWithNameInfo;
                            } else {
                                objValue = (CdbEntity) objWithNameInfo.getValue();
                            }
                            if (objValue == null) {
                                if (msg.isEmpty()) {
                                    msg = "Unable to find object for: "
                                            + getColumnName() + " with name: " + nameToken;
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

                    CdbEntity objValue = null;
                    String msg = "";
                    
                    ParseInfo objWithidInfo = getSingleObjectById(strValue.trim());
                    if (!objWithidInfo.getValidInfo().isValid()) {
                        return objWithidInfo;
                    } else {
                        objValue = (CdbEntity) objWithidInfo.getValue();
                    }
                    
                    if (objValue == null) {
                        if (msg.isEmpty()) {
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
                        
                        CdbEntity objValue = null;
                        String msg = "";
                        
                        ParseInfo objWithidInfo = getSingleObjectById(idToken.trim());
                        if (!objWithidInfo.getValidInfo().isValid()) {
                            return objWithidInfo;
                        } else {
                            objValue = (CdbEntity) objWithidInfo.getValue();
                        }

                        if (objValue == null) {
                            if (msg.isEmpty()) {
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
