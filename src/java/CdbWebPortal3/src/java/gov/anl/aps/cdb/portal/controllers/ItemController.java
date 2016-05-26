package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.DomainHandler;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

public abstract class ItemController extends CdbDomainEntityController<Item, ItemFacade> implements Serializable {

    @EJB
    private ItemFacade itemFacade;
    
    protected Boolean displayItemIdentifier1 = null;
    protected Boolean displayItemIdentifier2 = null;
    protected Boolean displayItemSources = null; 
    protected Boolean displayItemType = null; 
    protected Boolean displayItemCategory = null; 
    protected Boolean displayName = null;
    protected Boolean displayDerivedFromItem = null; 
    protected Boolean displayQrId = null;
    
    private static final Logger logger = Logger.getLogger(Item.class.getName());
     

    protected String filterByItemSources = null; 
    protected String filterByQrId = null;
     
    private List<Item> parentItemList; 
    private int currentItemEntityHashCode; 
    
    private Integer qrIdViewParam = null;
    
    private TreeNode itemElementListTreeTableRootNode = null;
    
    public ItemController() {
    }
    
    public abstract boolean getEntityDisplayItemIdentifier1();
    
    public abstract boolean getEntityDisplayItemIdentifier2(); 
    
    public abstract boolean getEntityDisplayItemName();
    
    public abstract boolean getEntityDisplayItemType();
    
    public abstract boolean getEntityDisplayItemCategory(); 
    
    //TODO chagne to parent derived from item. 
    public abstract boolean getEntityDisplayDerivedFromItem(); 
    
    public abstract boolean getEntityDisplayQrId();
    
    public abstract boolean getEntityDisplayItemGallery();
    
    public abstract boolean getEntityDisplayItemLogs();
    
    public abstract boolean getEntityDisplayItemSources(); 
    
    public abstract boolean getEntityDisplayItemProperties();
    
    public abstract boolean getEntityDisplayItemElements();
    
    public abstract boolean getEntityDisplayItemsDerivedFromItem(); 
    
    public abstract boolean getEntityDisplayItemMemberships();
    
    public abstract String getItemIdentifier1Title();
    
    public abstract String getItemIdentifier2Title();
    
    public abstract String getItemsDerivedFromItemTitle(); 
    
    public abstract String getDerivedFromItemTitle(); 
    
    public abstract String getStyleName();
    
    public String getEntityListPageTitle() {
        return getDisplayEntityTypeName() + " List"; 
    }
    
    public boolean currentHasChanged(){
        Item itemEntity = getCurrent(); 
        if (currentItemEntityHashCode != itemEntity.hashCode()) {
            currentItemEntityHashCode = hashCode(); 
            return true; 
        } 
        return false; 
    }

    public Item getItem(java.lang.Integer id) {
        return itemFacade.find(id);
    }
    
    @Override
    protected ItemFacade getEntityDbFacade() {
       return itemFacade;
    }
    
    void prepareItemElementListTreeTable(Item item) {
        try {
            itemElementListTreeTableRootNode = ItemElementUtility.createItemElementRoot(item);
        } catch (CdbException ex) {
            logger.warn("Could not create item element list for tree view: " + ex.toString());
        }
    }
    
    public void prepareAddSource(Item item) {
        List<ItemSource> sourceList = item.getItemSourceList();
        ItemSource source = new ItemSource();
        source.setItem(item);
        sourceList.add(0, source);
    }

    public void saveSourceList() {
        update();
    }
    
    public void deleteSource(ItemSource itemSource) {
        Item item = getCurrent();
        List<ItemSource> itemSourceList = item.getItemSourceList();
        itemSourceList.remove(itemSource);
        updateOnRemoval();
    }

    public TreeNode getItemElementListTreeTableRootNode() {
        return itemElementListTreeTableRootNode;
    }

    public void setItemElementListTreeTableRootNode(TreeNode itemElementListTreeTableRootNode) {
        this.itemElementListTreeTableRootNode = itemElementListTreeTableRootNode;
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }
    
    public boolean getDisplayItemSourceList() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemSource> itemSourceList = item.getItemSourceList();
            return itemSourceList != null && !itemSourceList.isEmpty(); 
        }
        return false; 
    } 
    
    public boolean getDisplayItemElementList() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemElement> itemElementList = item.getItemElementDisplayList();
            return itemElementList != null && !itemElementList.isEmpty(); 
        }
        return false; 
    }
    
    public Boolean getDisplayDerivedFromItemList() {
        Item item = getCurrent();
        if (item != null) {
            List<Item> derivedFromItemList = item.getDerivedFromItemList();
            return derivedFromItemList != null && !derivedFromItemList.isEmpty();
        }
        return false;
    }
    
    public Boolean getDisplayItemMembership() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            return itemElementMemberList != null && !itemElementMemberList.isEmpty();
        }
        return false;
    }
    
    public Boolean getDisplayDerivedFromPropertyList() {
        Item item = getCurrent();
            if (item != null) {
                Item derivedFromItem = item.getDerivedFromItem(); 
                if (derivedFromItem != null) {
                    List<PropertyValue> derivedFromItemPropertyValueList = derivedFromItem.getPropertyValueList();
                    return derivedFromItemPropertyValueList != null && !derivedFromItemPropertyValueList.isEmpty();
                }
            }
            return false;
    }
    
    public List<Item> getParentItemList() {
        if (currentHasChanged()) {
            Item itemEntity = getCurrent(); 

            parentItemList = new ArrayList<>(); 

            List<ItemElement> itemElementList = itemEntity.getItemElementMemberList(); 
            for (ItemElement itemElement : itemElementList) {
                if (parentItemList.contains(itemElement.getParentItem()) == false) {
                    parentItemList.add(itemElement.getParentItem());
                }
            }
        }

        return parentItemList;
    }

    public String getFilterByQrId() {
        return filterByQrId;
    }

    public void setFilterByQrId(String filterByQrId) {
        this.filterByQrId = filterByQrId;
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null; 

        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null; 
    }

    public String getFilterByItemIdentifier1() {
        return filterByItemIdentifier1;
    }

    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterByItemIdentifier1 = filterByItemIdentifier1;
    }

    public String getFilterByItemIdentifier2() {
        return filterByItemIdentifier2;
    }

    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByItemIdentifier2 = filterByItemIdentifier2;
    } 

    public Boolean getDisplayQrId() {
        if (displayQrId == null) {
            displayQrId = getEntityDisplayQrId(); 
        }
        
        return displayQrId;
    }

    public void setDisplayQrId(Boolean displayQrId) {
        this.displayQrId = displayQrId;
    }

    public Boolean getDisplayItemIdentifier1() {
        if (displayItemIdentifier1 == null) {
            displayItemIdentifier1 = getEntityDisplayItemIdentifier1(); 
        }
        return displayItemIdentifier1;
    }

    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayItemIdentifier1 = displayItemIdentifier1;
    }

    public Boolean getDisplayItemIdentifier2() {
        if (displayItemIdentifier2 == null) {
            displayItemIdentifier2 = getEntityDisplayItemIdentifier2(); 
        }
        return displayItemIdentifier2;
    }

    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayItemIdentifier2 = displayItemIdentifier2;
    }

    public Boolean getDisplayItemSources() {
        if (displayItemSources == null) {
            displayItemSources = getEntityDisplayItemSources(); 
        }
        return displayItemSources;
    }

    public void setDisplayItemSources(Boolean displayItemSources) {
        this.displayItemSources = displayItemSources;
    }

    public Boolean getDisplayName() {
        if (displayName == null) {
            displayName = getEntityDisplayItemName(); 
        }
        return displayName;
    }

    public void setDisplayName(Boolean displayItemName) {
        this.displayName = displayItemName;
    }

    public Boolean getDisplayItemType() {
        if (displayItemType == null) {
            displayItemType = getEntityDisplayItemType(); 
        }
        return displayItemType;
    }

    public void setDisplayItemType(Boolean displayItemType) {
        this.displayItemType = displayItemType;
    }

    public Boolean getDisplayItemCategory() {
        if (displayItemCategory == null) {
            displayItemCategory = getEntityDisplayItemCategory(); 
        }
        return displayItemCategory;
    }

    public void setDisplayItemCategory(Boolean displayItemCategory) {
        this.displayItemCategory = displayItemCategory;
    }
    
    public String getFilterByItemSources() {
        return filterByItemSources;
    }

    public void setFilterByItemSources(String filterByItemSources) {
        this.filterByItemSources = filterByItemSources;
    }

    public Boolean getDisplayDerivedFromItem() {
        if (displayDerivedFromItem == null) {
            displayDerivedFromItem = getEntityDisplayDerivedFromItem(); 
        }
        return displayDerivedFromItem;
    }

    public void setDisplayDerivedFromItem(Boolean displayDerivedFromItem) {
        this.displayDerivedFromItem = displayDerivedFromItem;
    }
    
    @Override
    protected Item createEntityInstance() {
        Item item = new Item();
        item.initalizeItem();
        return item;
    }

    @Override
    public Item findById(Integer id) {
        return itemFacade.findById(id); 
    }
    
    @Override
    public Item selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            Item item = findById(idParam);
            if (item == null) {
                throw new InvalidRequest("Item id " + idParam + " does not exist.");
            }
            
            return performItemRedirection(item, "id=" + idParam, false);
            
        } else {
            // Due to bug in primefaces, we cannot have more than one
            // f:viewParam on the web page, so process qrId here
            paramValue = SessionUtility.getRequestParameterValue("qrId");
            if (paramValue != null) {
                try {
                    qrIdViewParam = Integer.parseInt(paramValue);
                    Item item = findByQrId(qrIdViewParam);
                    if (item == null) {
                        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
                        if (sessionUser != null) {
                            SessionUtility.navigateTo("/views/componentInstance/create.xhtml?faces-redirect=true");
                        } else {
                            SessionUtility.pushViewOnStack("/views/componentInstance/create.xhtml");
                            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
                        }
                        return null;
                    }
                    
                    return performItemRedirection(item, "qrId=" + qrIdViewParam, false); 
                } catch (NumberFormatException ex) {
                    throw new InvalidRequest("Invalid value supplied for QR id: " + paramValue);
                }
            } else if (current == null) {
                throw new InvalidRequest("Component instance has not been selected.");
            }
            return current;
        }
    }
    
    @Override
    public String prepareView(Item item) {
        prepareItemElementListTreeTable(item);
        return "/views/item/view.xhtml?faces-redirect=true&id=" + item.getId();
    }
    
    private Item performItemRedirection(Item item, String paramString, boolean forceRedirection) {
        String currentViewId = SessionUtility.getCurrentViewId(); 
        
        DomainHandler itemDomainHandler = item.getDomain().getDomainHandler();
        String desiredViewId;
        if(itemDomainHandler != null) {
            desiredViewId = "/views/itemDomain" + itemDomainHandler.getName() + "/view.xhtml";
        } else {
            desiredViewId = "/views/item/view.xhtml"; 
        }
        
        if (currentViewId.equals(desiredViewId)) {
            if (forceRedirection == false) {
                setCurrent(item);
                prepareView(item); 
                return item;
            }
        }
        
        SessionUtility.navigateTo(desiredViewId + "?" + paramString + "faces-redirect=true");
        return null; 
    }
    
    public String getCurrentItemDisplayTitle() {
        if (current != null) {
            if (current.getQrId() != null) {
                return "Qr: " + current.getQrIdDisplay(); 
            } else if (current.getName() != null) {
                return ": " + current.getName();
            }
        } 
        return ""; 
    } 

    public Item findByQrId(Integer qrId) {
        return itemFacade.findByQrId(qrId);
    }
    
    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getItem(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
