/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase {

    public class CableCatalogRowModel extends RowModel {

        private String cableType = "";
        private String weight = "";
        private String diameter = "";
        private String source = "";
        private String url = "";

        public CableCatalogRowModel(ItemDomainCableCatalog c) {
            super(c);
        }

        public String getCableType() {
            return ((ItemDomainCableCatalog)getEntity()).getCableType();
        }
        
        public String getPartNumber() {
            return ((ItemDomainCableCatalog)getEntity()).getPartNumber();
        }

        public String getWeight() {
            return ((ItemDomainCableCatalog)getEntity()).getWeight();
        }

        public String getDiameter() {
            return ((ItemDomainCableCatalog)getEntity()).getDiameter();
        }

        public String getSource() {
            return ((ItemDomainCableCatalog)getEntity()).getSource();
        }

        public String getUrl() {
            return ((ItemDomainCableCatalog)getEntity()).getCableType();
        }
    }

    protected static String cableTypeHeader = "Cable Type";
    protected static String cableTypeProperty = "cableType";
    protected static int cableTypeColumn = 0;
    protected static String partNumberHeader = "Part Number";
    protected static String partNumberProperty = "partNumber";
    protected static int partNumberColumn = 1;
    protected static String weightHeader = "Weight";
    protected static String weightProperty = "weight";
    protected static int weightColumn = 2;
    protected static String diameterHeader = "Diameter";
    protected static String diameterProperty = "diameter";
    protected static int diameterColumn = 3;
    protected static String sourceHeader = "Source";
    protected static String sourceProperty = "source";
    protected static int sourceColumn = 4;
    protected static String urlHeader = "URL";
    protected static String urlProperty = "url";
    protected static int urlColumn = 5;
    
    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    protected boolean isValidationOnly() {
        return false;
    }
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ColumnModel(cableTypeHeader, cableTypeProperty));
        columns.add(new ColumnModel(partNumberHeader, partNumberProperty));
        columns.add(new ColumnModel(weightHeader, weightProperty));
        columns.add(new ColumnModel(diameterHeader, diameterProperty));
        columns.add(new ColumnModel(sourceHeader, sourceProperty));
        columns.add(new ColumnModel(urlHeader, urlProperty));
    }

    @Override
    public int getDataStartRow() {
        return 1;
    }

    @Override
    public boolean parseRow(Row row) {

        String cableType = "";
        String partNumber = "";
        String weight = "";
        String diameter = "";
        String source = "";
        String url = "";
        boolean isValid = true;
        String validString = "";

        Cell cell;

        cell = row.getCell(cableTypeColumn);
        if (cell == null) {
            cableType = "";
            isValid = false;
            validString = "unspecified cableType";
        } else {
            cell.setCellType(CellType.STRING);
            cableType = cell.getStringCellValue();
            if (cableType.equals("")) {
                cableType = "";
                isValid = false;
                validString = "unspecified cableType";
            }
        }

        cell = row.getCell(partNumberColumn);
        if (cell == null) {
            partNumber = "";
        } else {
            cell.setCellType(CellType.STRING);
            partNumber = cell.getStringCellValue();
        }

        cell = row.getCell(weightColumn);
        if (cell == null) {
            weight = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            weight = "";
            isValid = false;
            validString = "weight is not a number";
        } else {
            weight = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(diameterColumn);
        if (cell == null) {
            diameter = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            diameter = "";
            isValid = false;
            validString = "diameter is not a number";
        } else {
            diameter = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(sourceColumn);
        if (cell == null) {
            source = "";
        } else {
            cell.setCellType(CellType.STRING);
            source = cell.getStringCellValue();
        }

        cell = row.getCell(urlColumn);
        if (cell == null) {
            url = "";
        } else {
            cell.setCellType(CellType.STRING);
            url = cell.getStringCellValue();
        }
        
        ItemDomainCableCatalogController controller = ItemDomainCableCatalogController.getInstance();
        
        ItemDomainCableCatalog newType = controller.newEntityInstance();
        newType.setCableType(cableType);
        newType.setPartNumber(partNumber);
        newType.setWeight(weight);
        newType.setDiameter(diameter);
        newType.setSource(source);
        newType.setUrl(url);
        
        CableCatalogRowModel info = new CableCatalogRowModel(newType);
        
        if (rows.contains(info)) {
            isValid = false;
            validString = "duplicate row using cable type and part number as unique ids";
        } else {
            try {
                controller.checkItemUniqueness(newType);
            } catch (CdbException ex) {
                isValid = false;
                validString = "duplicate found in database using cable type and part number as unique ids";
            }
        }
        
        info.setIsValid(isValid);
        info.setValidString(validString);
        
        rows.add(info);

        return isValid;
    }
    
    @Override
    public ImportInfo importData() {
        
        System.out.println("importing " + rows.size() + " rows");
        
        ItemDomainCableCatalogController controller = ItemDomainCableCatalogController.getInstance();
        
        String message = "";
        List<ItemDomainCableCatalog> newCableTypes = new ArrayList<>();
        for (RowModel row : rows) {
            CableCatalogRowModel catalogRow = (CableCatalogRowModel) row;
            
            newCableTypes.add((ItemDomainCableCatalog)catalogRow.getEntity());
        }
        
        try {
            controller.createList(newCableTypes);
            return new ImportInfo(true, "Import succeeded.  Created " + rows.size() + " instances.");
        } catch (CdbException ex) {
            return new ImportInfo(false, "Import failed. " + ex.getMessage() + ".");
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            return new ImportInfo(false, "Import failed. " + ex.getMessage() + ": " + t.getMessage() + ".");
        }
    }

}
