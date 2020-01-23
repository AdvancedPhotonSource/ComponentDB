/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.jsf.utilities;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.inject.Named;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.wizard.Wizard;

/**
 * Utility class for assisting with common java server faces tasks.
 */
@Named("facesUtility")
@RequestScoped
public class FacesUtility {

    private Wizard wizard;

    private SelectOneMenu selectOneMenu;

    public SelectOneMenu getSelectOneMenu() {
        return selectOneMenu;
    }

    public void setSelectOneMenu(SelectOneMenu selectOneMenu) {
        this.selectOneMenu = selectOneMenu;
    }

    public void setSelectOneMenuValue(Object value) {
        selectOneMenu.setSubmittedValue(value);
    }

    public Wizard getWizard() {
        return wizard;
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    public String getWizardStep() {
        if (wizard != null) {
            return wizard.getStep();
        }
        return null;
    }

    /**
     * This binding enforce that panel is always collapsed on its refresh.
     */
    public void setAutoCollapsePanel(Panel autoCollapsePanel) {
        if (autoCollapsePanel != null) {
            autoCollapsePanel.setCollapsed(true);
        }
    }

    public Panel getAutoCollapsePanel() {
        return null;
    }

    public boolean displayFiltersForList(List<Object> dataList, int countRows) {
        if (dataList != null) {
            return dataList.size() > countRows;
        }

        return false;
    }

    public boolean displayFilters(DataModel dataModel, int countRows) {
        if (dataModel != null) {
            return dataModel.getRowCount() > countRows;
        }

        return false;
    }

    public boolean islistSizeOverMin(List<Object> someList, Integer min) {
        if (someList != null) {
            return someList.size() > min;
        }
        return false;
    }

    public void setResetDataTable(DataTable dataTable) {
        dataTable.reset();
    }

    public DataTable getResetDataTable() {
        return null;
    }

    public Object returnValueIfTrue(Boolean input, Object returnVal) {
        if (input) {
            return returnVal;
        }
        return null;
    }

    public void displayGrowlInfoMessage(String summary, String message) {
        SessionUtility.addInfoMessage(summary, message);
    }

    /**
     * In Primefaces 7.0 the filterBy attribute of a column cannot have a
     * ui:param specified directly. ui:param value is passed into this function
     * and returned to the filterBy attribute. Resolves issue with filterBy not
     * working for columns with ui:param filterBy value.
     *
     * TODO: Remove functionality when not necessary.
     *
     * @param string
     * @return
     */
    public String getFilterString(String string) {
        if (string != null) {
            return string;
        } else {
            return "";
        }
    }

    public String getToString(Object object) {
        if (object != null) {
            return object.toString();
        }
        return "";
    }

    @FacesConverter("genericSelectOneMenuObjectConverter")
    public static class ReleasedTemplateConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
            int hashCode = Integer.parseInt(string);

            List<UIComponent> children = uic.getChildren();
            for (UIComponent component : children) {
                if (component instanceof UISelectItems) {
                    UISelectItems uiSelectItems = (UISelectItems) component;
                    LinkedList<Object> value = (LinkedList<Object>) uiSelectItems.getValue();
                    LinkedList<Object> valueList = (LinkedList<Object>) value;
                    for (Object obj : valueList) {
                        if (obj.hashCode() == hashCode) {
                            return obj;
                        }
                    }
                }
            }

            return null;
        }

        @Override
        public String getAsString(FacesContext fc, UIComponent uic, Object t) {
            if (t == null) {
                return "";
            }
            return t.hashCode() + "";
        }
    }
}
