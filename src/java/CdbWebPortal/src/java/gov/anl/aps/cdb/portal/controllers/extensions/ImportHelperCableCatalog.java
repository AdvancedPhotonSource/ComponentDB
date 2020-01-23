/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
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
        private double weight = 0;
        private double diameter = 0;
        private String source = "";
        private String url = "";

        public CableCatalogRowModel(ItemDomainCableCatalog c, boolean v, String vs) {
            super(c, v, vs);
        }

        public String getCableType() {
            return ((ItemDomainCableCatalog)getEntity()).getCableType();
        }

        public double getWeight() {
            return ((ItemDomainCableCatalog)getEntity()).getWeight();
        }

        public double getDiameter() {
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
    protected static String weightHeader = "Weight";
    protected static String weightProperty = "weight";
    protected static String diameterHeader = "Diameter";
    protected static String diameterProperty = "diameter";
    protected static String sourceHeader = "Source";
    protected static String sourceProperty = "source";
    protected static String urlHeader = "URL";
    protected static String urlProperty = "url";
    
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
        double weight = 0;
        double diameter = 0;
        String source = "";
        String url = "";
        boolean isValid = true;
        String validString = "";

        Cell cell;

        cell = row.getCell(0);
        if (cell == null) {
            cableType = "";
            isValid = false;
            validString = "unspecified cableType";
        } else if (cell.getCellType() != CellType.STRING) {
            cableType = "";
            isValid = false;
            validString = "cellType is not a string";
        } else {
            cableType = cell.getStringCellValue();
        }

        cell = row.getCell(1);
        if (cell == null) {
            weight = 0;
        } else if (cell.getCellType() != CellType.NUMERIC) {
            weight = 0;
            isValid = false;
            validString = "weight is not a number";
        } else {
            weight = cell.getNumericCellValue();
        }

        cell = row.getCell(2);
        if (cell == null) {
            diameter = 0;
        } else if (cell.getCellType() != CellType.NUMERIC) {
            diameter = 0;
            isValid = false;
            validString = "diameter is not a number";
        } else {
            diameter = cell.getNumericCellValue();
        }

        cell = row.getCell(3);
        if (cell == null) {
            source = "";
        } else if (cell.getCellType() != CellType.STRING) {
            source = "";
            isValid = false;
            validString = "source is not a string";
        } else {
            source = cell.getStringCellValue();
        }

        cell = row.getCell(4);
        if (cell == null) {
            url = "";
        } else if (cell.getCellType() != CellType.STRING) {
            url = "";
            isValid = false;
            validString = "url is not a string";
        } else {
            url = row.getCell(4).getStringCellValue();
        }
        
        ItemDomainCableCatalog newType = ItemDomainCableCatalogController.getInstance().newEntityInstance();
        newType.setCableProperties(cableType, weight, diameter, source, url);
        CableCatalogRowModel info = new CableCatalogRowModel(newType, isValid, validString);
        rows.add(info);
//        if rows.contains(info) {
//            rows.add(info);
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
            
            ItemDomainCableCatalog newType = controller.newEntityInstance();
            newType.setName(catalogRow.getCableType());
            newCableTypes.add(newType);
//            newCable.setName(cableName);
//            newCable.setItemProjectList(projectList);
//
//            // set endpoints
//            newCable.setEndpoint1(itemEndpoint1);
//            newCable.setEndpoint2(itemEndpoint2);

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
