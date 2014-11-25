
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;

/**
 *
 * @author sveseli
 */
public interface PropertyTypeHandlerInterface 
{   
    public abstract String getEditActionOncomplete();
 
    public abstract String getEditActionIcon();

    public abstract String getEditActionBean();
    
    public abstract Boolean getDisplayEditActionButton();
    
    public abstract DisplayType getValueDisplayType();
}
