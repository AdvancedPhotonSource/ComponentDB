/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase {

    public class CableCatalogRowModel extends RowModel {

        private String cableType = "";
        private String description = "";
        private String url = "";
        private String imageUrl = "";
        private String manufacturer = "";
        private String partNumber = "";
        private String diameter = "";
        private String weight = "";
        private String conductors = "";
        private String insulation = "";
        private String jacketColor = "";
        private String voltageRating = "";
        private String fireLoad = "";
        private String heatLimit = "";
        private String bendRadius = "";
        private String team = "";

        public CableCatalogRowModel(ItemDomainCableCatalog c) {
            super(c);
        }

        public String getCableType() {
            return ((ItemDomainCableCatalog)getEntity()).getCableType();
        }
        
        public String getDescription() {
            return ((ItemDomainCableCatalog)getEntity()).getDescription();
        }
        
        public String getUrl() {
            return ((ItemDomainCableCatalog)getEntity()).getUrl();
        }
        
        public String getImageUrl() {
            return ((ItemDomainCableCatalog)getEntity()).getImageUrl();
        }
        
        public String getManufacturer() {
            return ((ItemDomainCableCatalog)getEntity()).getManufacturer();
        }

        public String getPartNumber() {
            return ((ItemDomainCableCatalog)getEntity()).getPartNumber();
        }

        public String getDiameter() {
            return ((ItemDomainCableCatalog)getEntity()).getDiameter();
        }

        public String getWeight() {
            return ((ItemDomainCableCatalog)getEntity()).getWeight();
        }

        public String getConductors() {
            return ((ItemDomainCableCatalog)getEntity()).getConductors();
        }

        public String getInsulation() {
            return ((ItemDomainCableCatalog)getEntity()).getInsulation();
        }

        public String getJacketColor() {
            return ((ItemDomainCableCatalog)getEntity()).getJacketColor();
        }

        public String getVoltageRating() {
            return ((ItemDomainCableCatalog)getEntity()).getVoltageRating();
        }

        public String getFireLoad() {
            return ((ItemDomainCableCatalog)getEntity()).getFireLoad();
        }

        public String getHeatLimit() {
            return ((ItemDomainCableCatalog)getEntity()).getHeatLimit();
        }

        public String getBendRadius() {
            return ((ItemDomainCableCatalog)getEntity()).getBendRadius();
        }

        public String getTeam() {
            return ((ItemDomainCableCatalog)getEntity()).getTeam();
        }

    }

    protected static String cableTypeHeader = "Cable Type";
    protected static String cableTypeProperty = "cableType";
    protected static int cableTypeColumn = 0;
    
    protected static String descriptionHeader = "Description";
    protected static String descriptionProperty = "description";
    protected static int descriptionColumn = 1;
    
    protected static String urlHeader = "Link URL";
    protected static String urlProperty = "url";
    protected static int urlColumn = 2;
    
    protected static String imageUrlHeader = "Image URL";
    protected static String imageUrlProperty = "imageUrl";
    protected static int imageUrlColumn = 3;

    protected static String manufacturerHeader = "Manufacturer";
    protected static String manufacturerProperty = "manufacturer";
    protected static int manufacturerColumn = 4;
    
    protected static String partNumberHeader = "Part Number";
    protected static String partNumberProperty = "partNumber";
    protected static int partNumberColumn = 5;
    
    protected static String diameterHeader = "Diameter";
    protected static String diameterProperty = "diameter";
    protected static int diameterColumn = 6;

    protected static String weightHeader = "Weight";
    protected static String weightProperty = "weight";
    protected static int weightColumn = 7;
    
    protected static String conductorsHeader = "Conductors";
    protected static String conductorsProperty = "conductors";
    protected static int conductorsColumn = 8;
    
    protected static String insulationHeader = "Insulation";
    protected static String insulationProperty = "insulation";
    protected static int insulationColumn = 9;
    
    protected static String jacketColorHeader = "Jacket Color";
    protected static String jacketColorProperty = "jacketColor";
    protected static int jacketColorColumn = 10;
    
    protected static String voltageRatingHeader = "Voltage Rating";
    protected static String voltageRatingProperty = "voltageRating";
    protected static int voltageRatingColumn = 11;
    
    protected static String fireLoadHeader = "Fire Load";
    protected static String fireLoadProperty = "fireLoad";
    protected static int fireLoadColumn = 12;
    
    protected static String heatLimitHeader = "Heat Limit";
    protected static String heatLimitProperty = "heatLimit";
    protected static int heatLimitColumn = 13;
    
    protected static String bendRadiusHeader = "Voltage Rating";
    protected static String bendRadiusProperty = "bendRadius";
    protected static int bendRadiusColumn = 14;
    
    protected static String teamHeader = "Team";
    protected static String teamProperty = "team";
    protected static int teamColumn = 15;
    
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
        columns.add(new ColumnModel(descriptionHeader, descriptionProperty));
        columns.add(new ColumnModel(urlHeader, urlProperty));
        columns.add(new ColumnModel(imageUrlHeader, imageUrlProperty));
        columns.add(new ColumnModel(manufacturerHeader, manufacturerProperty));
        columns.add(new ColumnModel(partNumberHeader, partNumberProperty));
        columns.add(new ColumnModel(diameterHeader, diameterProperty));
        columns.add(new ColumnModel(weightHeader, weightProperty));
        columns.add(new ColumnModel(conductorsHeader, conductorsProperty));
        columns.add(new ColumnModel(insulationHeader, insulationProperty));
        columns.add(new ColumnModel(jacketColorHeader, jacketColorProperty));
        columns.add(new ColumnModel(voltageRatingHeader, voltageRatingProperty));
        columns.add(new ColumnModel(fireLoadHeader, fireLoadProperty));
        columns.add(new ColumnModel(heatLimitHeader, heatLimitProperty));
        columns.add(new ColumnModel(bendRadiusHeader, bendRadiusProperty));
        columns.add(new ColumnModel(teamHeader, teamProperty));
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
    }

    @Override
    public boolean parseRow(Row row) {

        String cableType = "";
        String description = "";
        String url = "";
        String imageUrl = "";
        String manufacturer = "";
        String partNumber = "";
        String diameter = "";
        String weight = "";
        String conductors = "";
        String insulation = "";
        String jacketColor = "";
        String voltageRating = "";
        String fireLoad = "";
        String heatLimit = "";
        String bendRadius = "";
        String team = "";
        
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

        cell = row.getCell(descriptionColumn);
        if (cell == null) {
            description = "";
        } else {
            cell.setCellType(CellType.STRING);
            description = cell.getStringCellValue();
        }

        cell = row.getCell(urlColumn);
        if (cell == null) {
            url = "";
        } else {
            XSSFHyperlink urlLink = (XSSFHyperlink)cell.getHyperlink();
            if (urlLink != null) {
                url = urlLink.getAddress();
                if (url.length() > 256) {
                    isValid = false;
                    validString = "url length exceeds 256 characters";
                }
            }
        }
        
        cell = row.getCell(imageUrlColumn);
        if (cell == null) {
            imageUrl = "";
        } else {
            XSSFHyperlink imageLink = (XSSFHyperlink)cell.getHyperlink();
            if (imageLink != null) {
                imageUrl = imageLink.getAddress();
                if (imageUrl.length() > 256) {
                    isValid = false;
                    validString = "imageUrl length exceeds 256 characters";
                }
            }
        }
        
        cell = row.getCell(manufacturerColumn);
        if (cell == null) {
            manufacturer = "";
        } else {
            cell.setCellType(CellType.STRING);
            manufacturer = cell.getStringCellValue();
        }

        cell = row.getCell(partNumberColumn);
        if (cell == null) {
            partNumber = "";
        } else {
            cell.setCellType(CellType.STRING);
            partNumber = cell.getStringCellValue();
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

        cell = row.getCell(conductorsColumn);
        if (cell == null) {
            conductors = "";
        } else {
            cell.setCellType(CellType.STRING);
            conductors = cell.getStringCellValue();
        }
        
        cell = row.getCell(insulationColumn);
        if (cell == null) {
            insulation = "";
        } else {
            cell.setCellType(CellType.STRING);
            insulation = cell.getStringCellValue();
        }
        
        cell = row.getCell(jacketColorColumn);
        if (cell == null) {
            jacketColor = "";
        } else {
            cell.setCellType(CellType.STRING);
            jacketColor = cell.getStringCellValue();
        }
        
        cell = row.getCell(voltageRatingColumn);
        if (cell == null) {
            voltageRating = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            voltageRating = "";
            isValid = false;
            validString = "voltageRating is not a number";
        } else {
            voltageRating = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(fireLoadColumn);
        if (cell == null) {
            fireLoad = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            fireLoad = "";
            isValid = false;
            validString = "fireLoad is not a number";
        } else {
            fireLoad = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(heatLimitColumn);
        if (cell == null) {
            heatLimit = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            heatLimit = "";
            isValid = false;
            validString = "heatLimit is not a number";
        } else {
            heatLimit = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(bendRadiusColumn);
        if (cell == null) {
            bendRadius = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            bendRadius = "";
            isValid = false;
            validString = "bendRadius is not a number";
        } else {
            bendRadius = String.valueOf(cell.getNumericCellValue());
        }

        cell = row.getCell(teamColumn);
        if (cell == null) {
            team = "";
        } else if (cell.getCellType() != CellType.NUMERIC) {
            team = "";
            isValid = false;
            validString = "team is not a number";
        } else {
            cell.setCellType(CellType.STRING);
            team = cell.getStringCellValue();
        }

        ItemDomainCableCatalogController controller = ItemDomainCableCatalogController.getInstance();
        
        ItemDomainCableCatalog newType = controller.newEntityInstance();
        newType.setCableType(cableType);
        newType.setDescription(description);
        newType.setUrl(url);
        newType.setImageUrl(imageUrl);
        newType.setManufacturer(manufacturer);
        newType.setPartNumber(partNumber);
        newType.setDiameter(diameter);
        newType.setWeight(weight);
        newType.setConductors(conductors);
        newType.setInsulation(insulation);
        newType.setJacketColor(jacketColor);
        newType.setVoltageRating(voltageRating);
        newType.setFireLoad(fireLoad);
        newType.setHeatLimit(heatLimit);
        newType.setBendRadius(bendRadius);
        newType.setTeam(team);
        
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
