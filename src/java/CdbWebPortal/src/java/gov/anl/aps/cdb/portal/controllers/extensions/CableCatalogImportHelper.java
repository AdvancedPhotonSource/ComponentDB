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
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class CableCatalogImportHelper extends ImportHelperBase {
    
    public class CableCatalogImportInfo extends RowModel {
        
        String cableType = "";
        double weight = 0;
        double diameter = 0;
        String source = "";
        String url = "";
        
        public CableCatalogImportInfo(String type, double w, double d, String src, String u, boolean isValid, String validString) {
            cableType = type;
            weight = w;
            diameter = d;
            source = src;
            url = u;
        }
    }
    
    public void reset() {
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
    }

    @Override
    public void parseRow(Row row) {
        
        
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next();
//            switch (cell.getCellType()) {
//                case NUMERIC:
//                    System.out.print(cell.getNumericCellValue() + "\t\t");
//                    break;
//                case STRING:
//                    System.out.print(cell.getStringCellValue() + "\t\t");
//                    break;
//            }
//        }
//        System.out.println();
        
        String cableType = "";
        double weight = 0;
        double diameter = 0;
        String source = "";
        String url = "";
        boolean isValid = false;
        String validString = "";
        
        if (row.getPhysicalNumberOfCells() != 5) {
            isValid = false;
            validString = "invalid number of columns";
        } else {
            isValid = true;
        }
        
        cableType = row.getCell(0).getStringCellValue();
        weight = row.getCell(1).getNumericCellValue();
        diameter = row.getCell(2).getNumericCellValue();
        source = row.getCell(3).getStringCellValue();
        url = row.getCell(4).getStringCellValue();
        
        CableCatalogImportInfo info = new CableCatalogImportInfo(cableType, weight, diameter, source, url, isValid, validString);
        rows.add(info);
        
        System.out.println(cableType + " | " + 
                weight + " | " +
                diameter + " | " +
                source + " | " +
                url + " | " +
                isValid + " | " +
                validString);
    }
    
}
