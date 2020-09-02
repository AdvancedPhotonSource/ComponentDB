/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import java.util.List;

/**
 *
 * @author craig
 */
public class InitializeInfo {

    public List<InputColumnModel> inputColumns;
    public List<InputHandler> inputHandlers;
    public List<OutputColumnModel> outputColumns;
    public ValidInfo validInfo;

    public InitializeInfo(
            List<InputColumnModel> inputColumns,
            List<InputHandler> inputHandlers,
            List<OutputColumnModel> outputColumns,
            ValidInfo validInfo) {

        this.inputColumns = inputColumns;
        this.inputHandlers = inputHandlers;
        this.outputColumns = outputColumns;
        this.validInfo = validInfo;
    }
}
