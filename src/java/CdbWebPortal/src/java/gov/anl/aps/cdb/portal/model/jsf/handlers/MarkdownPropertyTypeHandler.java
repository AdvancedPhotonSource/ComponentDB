/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;

/**
 * Markdown property type handler.
 */
public class MarkdownPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Markdown";  

    public MarkdownPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.MARKDOWN);
    }
    
}
