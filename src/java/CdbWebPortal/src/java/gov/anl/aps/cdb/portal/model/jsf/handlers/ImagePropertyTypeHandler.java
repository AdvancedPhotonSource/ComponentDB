package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;

/**
 *
 * @author sveseli
 */
public class ImagePropertyTypeHandler extends AbstractPropertyTypeHandler 
{
    public static final String HANDLER_NAME = "Image";
    
    public ImagePropertyTypeHandler() {
        super(HANDLER_NAME);
    }
   
    @Override
    public String getEditActionOncomplete() {
        return "propertyValueImageUploadDialogWidget.show()";
    }
    
    @Override
    public String getEditActionIcon() {
        return "ui-icon-circle-arrow-n";
    }
    
    @Override
    public String getEditActionBean() {
        return "propertyValueImageUploadBean";
    }   
    
    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.IMAGE;
    }
}
