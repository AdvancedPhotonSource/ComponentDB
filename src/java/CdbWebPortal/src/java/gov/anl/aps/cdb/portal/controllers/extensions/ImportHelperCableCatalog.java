/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase {

    public class CableCatalogImportInfo extends RowModel {

        private String cableType = "";
        private double weight = 0;
        private double diameter = 0;
        private String source = "";
        private String url = "";

        public CableCatalogImportInfo(String type, double w, double d, String src, String u, boolean v, String vs) {
            super(v, vs);
            cableType = type;
            weight = w;
            diameter = d;
            source = src;
            url = u;
        }

        public String getCableType() {
            return cableType;
        }

        public void setCableType(String cableType) {
            this.cableType = cableType;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public double getDiameter() {
            return diameter;
        }

        public void setDiameter(double diameter) {
            this.diameter = diameter;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
    public void parseRow(Row row) {

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

        CableCatalogImportInfo info = new CableCatalogImportInfo(cableType, weight, diameter, source, url, isValid, validString);
        rows.add(info);
    }

}
