/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RefObjectManager;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author craig
 */
public class ManufacturerHandler extends InputHandler {
    
    private int columnIndexMfr;
    private int columnIndexPartNum;
    
    private RefObjectManager objectManager;
    
    public ManufacturerHandler(int colIndMfr, int colIndPartNum) {
        this.columnIndexMfr = colIndMfr;
        this.columnIndexPartNum = colIndPartNum;
    }

    private int getColumnIndexMfr() {
        return columnIndexMfr;
    }

    private int getColumnIndexPartNum() {
        return columnIndexPartNum;
    }
    
    private RefObjectManager getObjectManager() {
        if (objectManager == null) {
            objectManager = new RefObjectManager(SourceController.getInstance(), null);
        }
        return objectManager;
    }

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        // get mfr and part num values parsed from spreadsheet
        String mfrString = cellValueMap.get(getColumnIndexMfr());
        String partNumString = cellValueMap.get(getColumnIndexPartNum());

        // look up the manufacturer by id or name
        Source srcObj = null;
        String info_o = null;
        if ((mfrString != null) && (!mfrString.isEmpty())) {
            
            if (mfrString.charAt(0) == '#') {
                // lookup by name
                String nameString = mfrString.substring(1);
                if (!nameString.isEmpty()) {
                    srcObj = (Source) getObjectManager().getObjectWithName(nameString.trim(), info_o);
                }
            } else {
                // lookup by id
                srcObj = (Source) getObjectManager().getObjectWithId(mfrString.trim(), info_o);
            }
            
            if (srcObj == null) {
                isValid = false;
                String msg;
                if (info_o != null) {
                    validString = info_o + " for column: Manufacturer";
                } else {
                    msg = "Unable to find object for column: Manufacturer by lookup using: "
                            + mfrString;
                }
            } else {
                // invoke domain method setManufacturerInfo(name, partNumber)
            }
        }
        
        return new ValidInfo(isValid, validString);
    }

}
