package gov.anl.aps.cdb.portal.view.jsf.utilities;

import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class UiComponentUtility {

    static public final String EXPORT_VALUE_ATTR_KEY = "exportValue";

    public static UIComponent findComponent(String id) {
        UIComponent component = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            UIComponent root = facesContext.getViewRoot();
            component = findComponent(root, id);
        }

        return component;
    }

    public static UIComponent findComponent(UIComponent parentComponent, String id) {
        if (id.equals(parentComponent.getId())) {
            return parentComponent;
        }

        Iterator childIterator = parentComponent.getFacetsAndChildren();
        while (childIterator.hasNext()) {
            UIComponent childComponent = (UIComponent) childIterator.next();
            if (id.equals(childComponent.getId())) {
                return childComponent;
            }
            UIComponent targetComponent = findComponent(childComponent, id);
            if (targetComponent != null) {
                return targetComponent;
            }
        }
        return null;
    }

    static public String getParentComponentExportValue(UIComponent component) {
        if (component == null) {
            return null;
        }
        UIComponent parent = component.getParent();
        if (parent == null) {
            return null;
        }
        Map<String, Object> attrs = parent.getAttributes();
        Object exportObject = attrs.get(EXPORT_VALUE_ATTR_KEY);
        if (exportObject == null) {
            return null;
        }
        String exportValue = exportObject.toString();
        return exportValue;
    }
}
