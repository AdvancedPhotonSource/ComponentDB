/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMAARCSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.GalleryUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author djarosz
 */
@Named("itemDomainMAARCController")
@SessionScoped
public class ItemDomainMAARCController extends ItemController<ItemDomainMAARC, ItemDomainMAARCFacade, ItemDomainMAARCSettings> {

    protected final String FILE_ENTITY_TYPE_NAME = "File";
    public static final String MAARC_CONNECTION_RELATIONSHIP_TYPE_NAME = "MAARC Connection";
    protected final String FILE_PROPERTY_TYPE_NAME = "File";

    protected final String FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY = "cdbViewableUUID";

    private final String VIEWABLE_UUID_REMOTE_COMMAND_KEY = "imageUUID";

    private static final Logger logger = Logger.getLogger(ItemDomainMAARCController.class.getName());

    private Integer filePropertyTypeId = null;
    private boolean attemptedFetchFilePropertyType = false;

    private Boolean loadGallery = null;
    private String currentViewableUUIDToDownload = null;

    private List<ItemElementRelationship> relatedRelationshipsForCurrent = null;

    private MAARCListDataModel maarcListDataModel;

    @EJB
    ItemDomainMAARCFacade itemDomainMAARCFacade;

    @EJB
    PropertyTypeFacade propertyTypeFacade;

    @EJB
    PropertyMetadataFacade propertyMetadataFacade;

    @Override
    protected ItemDomainMAARC instenciateNewItemDomainEntity() {
        return new ItemDomainMAARC();
    }

    @Override
    protected ItemDomainMAARCSettings createNewSettingObject() {
        return new ItemDomainMAARCSettings(this);
    }

    @Override
    protected ItemDomainMAARCFacade getEntityDbFacade() {
        return itemDomainMAARCFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "itemMAARC";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.maarc.getValue();
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "MAARC Item";
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return true;
    }

    /**
     * Used to fetch a list of relationships for items that are related to maarc
     * item.
     *
     * @param item
     * @return
     */
    public static List<ItemElementRelationship> getRelatedMAARCRelationshipsForItem(Item item) {
        List<ItemElementRelationship> relatedMAARCRelationshipsForItem = new ArrayList<>();

        List<ItemElementRelationship> itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();

        for (ItemElementRelationship ier : itemElementRelationshipList) {
            if (ier.getRelationshipType().getName().equals(MAARC_CONNECTION_RELATIONSHIP_TYPE_NAME)) {
                relatedMAARCRelationshipsForItem.add(ier);
            }
        }

        return relatedMAARCRelationshipsForItem;
    }

    public boolean isCollapsedRelatedItemsForCurrent() {
        return getRelatedRelationshipsForCurrent().size() < 1;
    }

    public void destroyRelationship(ItemElementRelationship ier) {
        ItemElementRelationshipController ierc = ItemElementRelationshipController.getInstance();
        ierc.destroy(ier);

        relatedRelationshipsForCurrent.remove(ier);
    }

    /**
     * Destroys a full file reference from a study
     *
     * @param itemElement
     */
    public void destroyFile(ItemElement itemElement) {
        ItemDomainMAARC containedItem = (ItemDomainMAARC) itemElement.getContainedItem();

        ItemElementController instance = ItemElementController.getInstance();
        instance.destroy(itemElement);

        ItemDomainMAARC currentItem = getCurrent();
        destroy(containedItem);
        current = currentItem;
    }

    @Override
    protected void prepareEntityDestroy(ItemDomainMAARC item) throws CdbException {
        if (isEntityTypeFile(item)) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();

            // A file should be a member of 1 Study 
            if (itemElementMemberList.size() == 1) {
                // Destroy the element before proceeding to destroy the file. 
                ItemElement itemElement = itemElementMemberList.get(0);
                ItemElementController instance = ItemElementController.getInstance();
                instance.destroy(itemElement);
                itemElementMemberList.clear();
            } else if (itemElementMemberList.size() == 0) {
                // Do Nothing. 
            } else {
                throw new CdbException("File should be a member of one study. Something went wrong! please notify Admin of this error.");
            }
        } else {
            // Study
            List<ItemElement> itemElementDisplayList = item.getItemElementDisplayList();
            while (itemElementDisplayList.size() > 0) {
                ItemElement ie = itemElementDisplayList.get(0);
                itemElementDisplayList.remove(0);

                destroyFile(ie);
            }

            // Clear Relationships
            List<ItemElementRelationship> ierList = item.getItemElementRelationshipList1();
            if (ierList.size() > 0) {
                ItemElementRelationshipController ierc = ItemElementRelationshipController.getInstance();

                while (ierList.size() > 0) {
                    ItemElementRelationship ier = ierList.get(0);
                    ierList.remove(0);

                    ierc.destroy(ier);
                }

            }
        }
        super.prepareEntityDestroy(item);
    }

    public List<ItemElementRelationship> getRelatedRelationshipsForCurrent() {
        if (relatedRelationshipsForCurrent == null) {
            List<ItemElementRelationship> itemElementRelationshipList1 = getCurrent().getSelfElement().getItemElementRelationshipList1();
            relatedRelationshipsForCurrent = new ArrayList<>();

            for (ItemElementRelationship ier : itemElementRelationshipList1) {
                if (ier.getRelationshipType().getName().equals(MAARC_CONNECTION_RELATIONSHIP_TYPE_NAME)) {
                    relatedRelationshipsForCurrent.add(ier);
                }
            }
        }

        return relatedRelationshipsForCurrent;
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();
        relatedRelationshipsForCurrent = null;
        loadGallery = null;
    }

    @Override
    public boolean getEntityDisplayItemElementsForCurrent() {
        boolean result = super.getEntityDisplayItemElementsForCurrent();
        if (getCurrent() == null
                || getCurrent().getEntityTypeList() == null
                || getCurrent().getEntityTypeList().isEmpty()) {
            return result;
        }
        if (isCurrentEntityTypeFile()) {
            result = false;
        }

        return result;
    }

    public boolean isCurrentEntityTypeFile() {
        return isEntityTypeFile(getCurrent());
    }

    public boolean isEntityTypeFile(ItemDomainMAARC item) {
        List<EntityType> entityTypeList = item.getEntityTypeList();
        for (EntityType entityType : entityTypeList) {
            if (entityType.getName().equals(FILE_ENTITY_TYPE_NAME)) {
                return true;

            }
        }

        return false;
    }

    public Integer getFilePropertyTypeId() {
        if (filePropertyTypeId == null && !attemptedFetchFilePropertyType) {
            PropertyType filePropertyType = propertyTypeFacade.findByName(FILE_PROPERTY_TYPE_NAME);
            if (filePropertyType != null) {
                filePropertyTypeId = filePropertyType.getId();
            }
            attemptedFetchFilePropertyType = true;
        }
        return filePropertyTypeId;
    }

    public boolean getCollapsedFilePreview() {
        return collapsedPreviewPanelForItem(getCurrent());
    }

    public boolean getCollapsedStudyGallery() {
        for (ItemElement itemElement : getCurrent().getItemElementDisplayList()) {
            Item containedItem = itemElement.getContainedItem();
            if (!collapsedPreviewPanelForItem(containedItem)) {
                return false;
            }
        }

        return true;
    }

    private boolean collapsedPreviewPanelForItem(Item item) {
        PropertyValue maarcFilePropertyValueFromItem = getMAARCFilePropertyValueFromItem(item);

        if (maarcFilePropertyValueFromItem != null) {
            if (GalleryUtility.viewableFileName(maarcFilePropertyValueFromItem.getValue())) {
                return false;
            }
        }

        return true;
    }

    /*/ Used to delete viewableUUID key from each item in a study. Not used in production. 
    public void deletePreviewKey() {
        for (ItemElement itemElement : getCurrent().getItemElementDisplayList()) {
            Item file = itemElement.getContainedItem();

            PropertyValue pv = getMAARCFilePropertyValueFromItem(file);
            if (pv != null) {
                PropertyMetadata propertyMetadataForKey = pv.getPropertyMetadataForKey(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY);
                if (propertyMetadataForKey != null) {
                    propertyMetadataFacade.remove(propertyMetadataForKey);
                }
            }

        }
    }
    //*/
    public List<String> getPreviewsForAllElements() {
        if (isCurrentEntityTypeFile()) {
            return null;
        }

        List<String> streamedContentsList = new ArrayList<>();
        for (ItemElement itemElement : getCurrent().getItemElementDisplayList()) {
            Item file = itemElement.getContainedItem();

            String viewableFileName = getPreviewPath((ItemDomainMAARC) file);
            if (viewableFileName != null) {
                streamedContentsList.add(viewableFileName);
            }
        }

        return streamedContentsList;

    }

    public String getPreviewPath() {
        return getPreviewPath(getCurrent(), false);
    }

    public String getPreviewPath(ItemDomainMAARC item) {
        return getPreviewPath(item, false);
    }

    public String getPreviewPath(ItemDomainMAARC item, boolean storedOnly) {
        if (!isEntityTypeFile(item)) {
            return null;
        }

        PropertyValue pv = getMAARCFilePropertyValueFromItem(item);

        if (pv == null) {
            return null;
        }

        if (!GalleryUtility.viewableFileName(pv.getValue())) {
            return null;
        }

        PropertyMetadata metadata = pv.getPropertyMetadataForKey(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY);

        if (metadata != null) {
            String metadataValue = metadata.getMetadataValue();
            return metadataValue;
        }

        if (storedOnly) {
            return null;
        }

        try {
            // Prepare storage directory 
            String basePath = StorageUtility.getFileSystemMAARCPreviewsDirectory();
            Path uploadPath = Paths.get(basePath);
            if (Files.notExists(uploadPath)) {
                logger.debug("Creating inidial maarc previews directory: " + basePath);
                Files.createDirectory(uploadPath);
            }

            PropertyTypeHandlerInterface handler = PropertyTypeHandlerFactory.getHandler(pv);

            StreamedContent contentStream;
            try {
                contentStream = handler.fileDownloadActionCommand(pv);
            } catch (ExternalServiceError ex) {
                SessionUtility.addErrorMessage("Error Download File", ex.getMessage());
                return null;
            }

            InputStream stream = contentStream.getStream();

            byte[] originalData = IOUtils.toByteArray(stream);

            String fileName = pv.getValue();
            int extStart = fileName.lastIndexOf(".") + 1;
            String imageFormat = fileName.substring(extStart);

            if (imageFormat.equalsIgnoreCase("pdf")) {
                imageFormat = "png";
                originalData = GalleryUtility.createPNGFromPDF(originalData);
                if (originalData == null) {
                    return null;
                }
            }

            UUID randomUUID = UUID.randomUUID();
            String uniqueFileName = randomUUID.toString();
            uniqueFileName += "." + imageFormat;

            GalleryUtility.storePreviewsFromViewableData(originalData, imageFormat, basePath, uniqueFileName);

            pv.setPropertyMetadataValue(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY, uniqueFileName);

            // System save the metadata value
            PropertyMetadata viewableKeyMetadata = pv.getPropertyMetadataForKey(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY);
            propertyMetadataFacade.create(viewableKeyMetadata);
            logger.debug("Created cdb viewable metadata id: " + viewableKeyMetadata.getId());

            return uniqueFileName;
        } catch (IOException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return null;
        }
    }

    public String getScaledPreviewPath(String uuidPreviewPath) {
        String scaledImage = StorageUtility.getMAARCPreviewsPath(uuidPreviewPath) + CdbPropertyValue.SCALED_IMAGE_EXTENSION;

        if (StorageUtility.isFileExist(scaledImage)) {
            return scaledImage;
        }

        return StorageUtility.getMissingImageDefaultPreview(uuidPreviewPath, scaledImage, CdbPropertyValue.SCALED_IMAGE_EXTENSION);
    }

    @Override
    public String getThumbnailImageForDownloadablePropertyValue(PropertyValue propertyValue) {
        String uuidPreviewPath = propertyValue.getPropertyMetadataValueForKey(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY);
        String thumbnailImage = StorageUtility.getMAARCPreviewsPath(uuidPreviewPath) + CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION;

        if (StorageUtility.isFileExist(thumbnailImage)) {
            return thumbnailImage;
        }

        return super.getThumbnailImageForDownloadablePropertyValue(propertyValue);
    }

    private PropertyValue getMAARCFilePropertyValueFromItem(Item item) {
        List<PropertyValue> propertyValueList = item.getPropertyValueList();
        if (propertyValueList.size() > 0) {
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyType propertyType = propertyValue.getPropertyType();
                if (propertyType != null) {
                    if (propertyType.getName().equals(FILE_PROPERTY_TYPE_NAME)) {
                        return propertyValue;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Do not render gallery if it is not yet ready. The view will activate
     * loading screen.
     *
     * @return
     */
    public boolean isLoadGallery() {
        if (loadGallery == null) {
            if (isEntityTypeFile(getCurrent())) {
                // File
                loadGallery = loadGalleryForItemNeeded(getCurrent());
            } else {
                // Study                 
                for (ItemElement itemElement : getCurrent().getItemElementDisplayList()) {
                    ItemDomainMAARC containedItem = (ItemDomainMAARC) itemElement.getContainedItem();
                    if (loadGalleryForItemNeeded(containedItem)) {
                        loadGallery = true;
                        return true;
                    }
                }

                loadGallery = false;
            }
        }
        return loadGallery;
    }

    public void setLoadGallery(boolean renderGallery) {
        this.loadGallery = renderGallery;
    }

    private boolean loadGalleryForItemNeeded(ItemDomainMAARC item) {
        PropertyValue maarcFilePropertyValueFromItem = getMAARCFilePropertyValueFromItem(item);
        if (maarcFilePropertyValueFromItem != null) {
            String value = maarcFilePropertyValueFromItem.getValue();
            if (GalleryUtility.viewableFileName(value)) {
                String res = getPreviewPath(item, true);
                return (res == null);
            }
        }

        return false;
    }

    public void updateCurrentViewableUUIDToDownload() {
        currentViewableUUIDToDownload = SessionUtility.getRequestParameterValue(VIEWABLE_UUID_REMOTE_COMMAND_KEY);
    }

    public StreamedContent downloadCurrentViewableUUIDToDownload() {
        PropertyValue pv = null;
        if (isCurrentEntityTypeFile()) {
            pv = getMAARCFilePropertyValueFromItem(getCurrent());
        } else {
            for (ItemElement itemElement : getCurrent().getItemElementDisplayList()) {
                ItemDomainMAARC containedItem = (ItemDomainMAARC) itemElement.getContainedItem();
                PropertyValue pvCheck = getMAARCFilePropertyValueFromItem(containedItem);
                if (pvCheck != null) {
                    if (GalleryUtility.viewableFileName(pvCheck.getValue())) {
                        String metadataKey = pvCheck.getPropertyMetadataValueForKey(FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY);
                        if (metadataKey != null) {
                            if (metadataKey.equals(currentViewableUUIDToDownload)) {
                                pv = pvCheck;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (pv != null) {
            PropertyTypeHandlerInterface handler = PropertyTypeHandlerFactory.getHandler(pv);
            try {
                return handler.fileDownloadActionCommand(pv);
            } catch (ExternalServiceError ex) {
                SessionUtility.addErrorMessage("Error Download File", ex.getMessage());
            }
        }

        return null;
    }

    public MAARCListDataModel getMAARCListDataModel() {
        if (maarcListDataModel == null) {           
            maarcListDataModel = new MAARCListDataModel(itemDomainMAARCFacade);
        }

        return maarcListDataModel;
    } 

    @Override
    public void resetListDataModel() {
        super.resetListDataModel(); 
        maarcListDataModel = null; 
    }

    @Override
    public String getItemEntityTypeTitle() {
        return "MAARC Type";
    }

    @Override
    public String getItemElementsListTitle() {
        return "Files";
    }

    @Override
    public boolean getRenderItemElementList() {
        return !isCurrentEntityTypeFile();
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "maarc";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return null;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }

    public class MAARCListDataModel extends LazyDataModel {

        List<ItemDomainMAARC> itemList;
        ItemDomainMAARCFacade facade;

        Map lastFilterMap = null;

        private final String ENTITY_TYPE_FILTER = "entityTypeString";
        private final String NAME_FILTER = "name";
        private final String DESCRIPTION_FILTER = "description";
        private final String ITEM_ID_FILTER = "id";
        private final String CREATED_ON_DATE_TIME_FILTER = "createdOnDateTime";
        private final String LAST_MODIFIED_ON_DATE_TIME_FILTER = "lastModifiedOnDateTime";
        private final String OWNER_GROUP_FILTER = "ownerUserGroup.name";
        private final String CREATED_BY_USERNAME_FILTER = "createdByUser.username";
        private final String MODIFIED_BY_USERNAME_FILTER = "lastModifiedByUser.username";
        private final String OWNED_BY_USERNAME_FILTER = "ownerUser.username";

        public MAARCListDataModel(ItemDomainMAARCFacade facade) {
            this.facade = facade;
            updateItemList(new ArrayList<>());
        }

        private void updateItemList(List<ItemDomainMAARC> itemList) {
            this.itemList = itemList;
            setRowCount(itemList.size());
        }

        @Override
        public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filterBy) {
            if (filterBy.size() == 0) {
                // Innitial set
                lastFilterMap = filterBy;
            }

            if (lastFilterMap.equals(filterBy) == false) {
                String entityTypeFilter = readFilterValue(filterBy, ENTITY_TYPE_FILTER);
                String nameFilter = readFilterValue(filterBy, NAME_FILTER);
                String descriptionFilter = readFilterValue(filterBy, DESCRIPTION_FILTER);
                String itemIdFilter = readFilterValue(filterBy, ITEM_ID_FILTER);
                String ownerFilter = readFilterValue(filterBy, OWNED_BY_USERNAME_FILTER); 
                String createdUserFilter = readFilterValue(filterBy, CREATED_BY_USERNAME_FILTER); 
                String modifiedUserFilter = readFilterValue(filterBy, MODIFIED_BY_USERNAME_FILTER);
                String ownerGroupFilter = readFilterValue(filterBy, OWNER_GROUP_FILTER);
                String createdOnDateFilter = readFilterValue(filterBy, CREATED_ON_DATE_TIME_FILTER);
                String modifiedOnDateFilter = readFilterValue(filterBy, LAST_MODIFIED_ON_DATE_TIME_FILTER);

                List<ItemDomainMAARC> results = facade.findByDataTableFilterQueryBuilder(
                        getDefaultDomain(),
                        entityTypeFilter,
                        nameFilter,
                        descriptionFilter,
                        itemIdFilter,
                        ownerFilter,
                        createdUserFilter,
                        modifiedUserFilter,
                        ownerGroupFilter,
                        createdOnDateFilter,
                        modifiedOnDateFilter);
                updateItemList(results);

            }

            return paginate(first, pageSize);
        }

        private String readFilterValue(Map filterMap, String key) {
            if (filterMap.containsKey(key)) {
                // New version of primefaces utilizes FilterMeta older version is equal to filterValue. 
                //FilterMeta filter = (FilterMeta) filterMap.get(key);
                //Object filterValue = filter.getFilterValue();
                
                Object filterValue = filterMap.get(key);
                if (filterValue != null) {
                    return filterValue.toString();
                }
            }
            return null;
        }

        private List paginate(int first, int pageSize) {
            int size = itemList.size();

            int last = first + pageSize;
            if (size < last) {
                last = size;
            }

            return itemList.subList(first, last);
        }

    }

}
