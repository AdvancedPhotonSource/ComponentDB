package gov.anl.aps.cdb.portal.model.jsf.handlers;

/**
 *
 * @author sveseli
 */
public class ApsLinkPropertyTypeHandler extends AbstractPropertyTypeHandler 
{

    public static final String HANDLER_NAME = "APS Link";
    
    public ApsLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }
    
    @Override
    public String getEditActionOncomplete() {
        return null;
    }
}
