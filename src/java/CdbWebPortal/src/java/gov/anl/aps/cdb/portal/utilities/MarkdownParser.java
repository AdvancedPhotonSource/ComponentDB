/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 *
 * @author djarosz
 */
public class MarkdownParser {
    
    private static MutableDataSet options = new MutableDataSet();
    private static Parser parser = Parser.builder(options).build();
    private static HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    
    public static String parseMarkdownAsHTML(String text) {
        Node document = parser.parse(text); 
        String html = renderer.render(document); 
        
        return html;
    }

    
}
