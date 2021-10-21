/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.export.objects.handlers.DomainItemTypeListOutputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnSpecInitInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.DomainItemTypeListInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class DomainItemTypeListColumnSpec extends ColumnSpec {
    
    public static final String HEADER_DOMAIN = "Domain";
    public static final String DESCRIPTION_DOMAIN = "Domain for this item.";
    public static final String PROPERTY_DOMAIN = "domain";
    public static final String SETTER_DOMAIN = "setDomain";
    public static final String GETTER_DOMAIN = "getDomain";
    
    public static final String HEADER_ITEM_TYPE = "Item Type List";
    public static final String DESCRIPTION_ITEM_TYPE = "Comma-separated list of item type ids or names for this item. Prefix list of names with # character.";
    public static final String PROPERTY_ITEM_TYPE = "itemTypeString";
    public static final String SETTER_ITEM_TYPE = "setItemTypeList";
    public static final String GETTER_ITEM_TYPE = "getItemTypeList";
    
    private DomainItemTypeListInputHandler inputHandler;
    
    public DomainItemTypeListColumnSpec(List<ColumnModeOptions> options) {
        super(null, options);
    }
    
    private InputColumnModel domainInputColumnModel(int index) {
        return new InputColumnModel(index, HEADER_DOMAIN, DESCRIPTION_DOMAIN);
    }
    
    private InputColumnModel itemTypeListColumnModel(int index) {
        return new InputColumnModel(index, HEADER_ITEM_TYPE, DESCRIPTION_ITEM_TYPE);
    }
    
    @Override
    public int getInputTemplateColumns(
            int colIndex,
            List<InputColumnModel> inputColumns_io) {

        inputColumns_io.add(domainInputColumnModel(colIndex));
        inputColumns_io.add(itemTypeListColumnModel(colIndex+1));
        return 2;
    }
    
    @Override
    protected ColumnSpecInitInfo initialize_(
            int colIndex,
            Map<Integer, String> headerValueMap,
            ImportMode mode) {
        
        boolean isValid = true;
        String validString = "";
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> inputHandlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
        
        if (isUsedForMode(mode)) {
  
            // check that domain column is provided            
            String domainHeaderValue = headerValueMap.get(colIndex);
            if (domainHeaderValue == null) {
                domainHeaderValue = "";
            }
            if ((domainHeaderValue.isEmpty()) || (!domainHeaderValue.equals(HEADER_DOMAIN))) {
                isValid = false;
                validString = "Import spreadsheet is missing expected column: '"
                        + HEADER_DOMAIN + "', actual column encountered: '" + domainHeaderValue + "'.";
                
            } else {                
                // check that item type list column is provided
                // check that domain column is provided            
                String itemListHeaderValue = headerValueMap.get(colIndex+1);
                if (itemListHeaderValue == null) {
                    itemListHeaderValue = "";
                }
                if ((itemListHeaderValue.isEmpty()) || (!itemListHeaderValue.equals(HEADER_ITEM_TYPE))) {
                    isValid = false;
                    validString = "Import spreadsheet is missing expected column: '"
                            + HEADER_ITEM_TYPE + "', actual column encountered: '" + itemListHeaderValue + "'.";

                } else {
                    // initialize input columns, handlers, and output columns
                    inputColumns.add(domainInputColumnModel(colIndex));
                    inputColumns.add(itemTypeListColumnModel(colIndex+1));
                    inputHandler = new DomainItemTypeListInputHandler(colIndex);
                    inputHandlers.add(inputHandler);
                    outputColumns.add(new OutputColumnModel(HEADER_DOMAIN, PROPERTY_DOMAIN));
                    outputColumns.add(new OutputColumnModel(HEADER_ITEM_TYPE, PROPERTY_ITEM_TYPE));
                }
            }
        }

        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new ColumnSpecInitInfo(validInfo, 2, inputColumns, inputHandlers, outputColumns);
    }
    
    @Override
    public InputHandler getInputHandler(int colIndex) {
        return inputHandler;
    }

    @Override
    public DomainItemTypeListOutputHandler getOutputHandler(ExportMode exportMode) {
        return new DomainItemTypeListOutputHandler();
    }
}
