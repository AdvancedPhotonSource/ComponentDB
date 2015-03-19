/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.view.jsf.components;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.export.CSVExporter;

/**
 * Override standard csv exporter in order to use displayed http link values.
 * 
 * See http://stackoverflow.com/questions/14411389/pdataexporter-does-not-recognize-pcelleditor/14413932#14413932
 */
public class CdbCsvExporter extends CSVExporter {

    @Override
    protected String exportValue(FacesContext context, UIComponent component) {
        if (component instanceof CellEditor) {
            // Get editor output facet
            return exportValue(context, ((CellEditor) component).getFacet("output"));
        }
        else if (component instanceof HtmlGraphicImage) {
            // Get image alt attribute
            return (String) component.getAttributes().get("alt");
        }
        else if (component instanceof HtmlOutputLink) {
            // Go over all link children and combine their exported values
            HtmlOutputLink link = (HtmlOutputLink) component;
            String value = "";
            if (link.getChildCount() > 0) {
                String delimiter = "";
                for (UIComponent childComponent : link.getChildren()) {
                    String childValue = (String) super.exportValue(context, childComponent);
                    value += delimiter + childValue;
                }
            }
            else {
               value = (String) super.exportValue(context, component);             
            }
            return value;
        }
        else {
            return super.exportValue(context, component);
        }
    }


}