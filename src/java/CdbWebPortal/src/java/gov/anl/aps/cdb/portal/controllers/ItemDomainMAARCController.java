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
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMAARCControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMAARCLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
import java.util.UUID;
import java.util.function.Supplier;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author djarosz
 */
@Named("itemDomainMAARCController")
@SessionScoped
public class ItemDomainMAARCController extends ItemController<ItemDomainMAARCControllerUtility, ItemDomainMAARC, ItemDomainMAARCFacade, ItemDomainMAARCSettings> {
    
    public static final String MAARC_CONNECTION_RELATIONSHIP_TYPE_NAME = "MAARC Connection";
    protected final String FILE_PROPERTY_TYPE_NAME = "File";

    protected final String FILE_PROPERTY_VIEWABLE_UUID_METADATA_KEY = "cdbViewableUUID";

    private final String VIEWABLE_UUID_REMOTE_COMMAND_KEY = "imageUUID";

    private static final Logger logger = LogManager.getLogger(ItemDomainMAARCController.class.getName());

    private Integer filePropertyTypeId = null;
    private boolean attemptedFetchFilePropertyType = false;
    
    private String currentViewableUUIDToDownload = null;   

    private ItemDomainMAARCLazyDataModel maarcListDataModel;

    @EJB
    ItemDomainMAARCFacade itemDomainMAARCFacade;

    @EJB
    PropertyTypeFacade propertyTypeFacade;

    @EJB
    PropertyMetadataFacade propertyMetadataFacade;   

    @Override
    protected ItemDomainMAARCSettings createNewSettingObject() {
        return new ItemDomainMAARCSettings(this);
    }

    @Override
    protected ItemDomainMAARCFacade getEntityDbFacade() {
        return itemDomainMAARCFacade;
    }   

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.maarc.getValue();
    }   

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
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
        
        ItemElement selfElement = item.getSelfElement();
        if (selfElement == null) {
            return relatedMAARCRelationshipsForItem; 
        }

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
        
        ItemDomainMAARC current = getCurrent();
        List<ItemElementRelationship> relatedRelationshipsForCurrent = current.getRelatedRelationshipsForCurrent();

        relatedRelationshipsForCurrent.remove(ier);
    }   
    
    /**
     * Destroys a full file reference from a study
     *
     * @param itemElement
     */
    public void destroyFile(ItemElement itemElement) {
        UserInfo user = SessionUtility.getUser();
        
        try {
            getControllerUtility().destroyFile(itemElement, user);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "An error occurred deleting file.");
        }
    }

    public List<ItemElementRelationship> getRelatedRelationshipsForCurrent() {
        ItemDomainMAARC current = getCurrent();
        List<ItemElementRelationship> relatedRelationshipsForCurrent = current.getRelatedRelationshipsForCurrent();
        if (relatedRelationshipsForCurrent == null) {
            List<ItemElementRelationship> itemElementRelationshipList1 = getCurrent().getSelfElement().getItemElementRelationshipList1();
            relatedRelationshipsForCurrent = new ArrayList<>();
            current.setRelatedRelationshipsForCurrent(relatedRelationshipsForCurrent);

            for (ItemElementRelationship ier : itemElementRelationshipList1) {
                if (ier.getRelationshipType().getName().equals(MAARC_CONNECTION_RELATIONSHIP_TYPE_NAME)) {
                    relatedRelationshipsForCurrent.add(ier);
                }
            }
        }

        return relatedRelationshipsForCurrent;
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
        return getControllerUtility().isEntityTypeFile(getCurrent());
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
        if (!getControllerUtility().isEntityTypeFile(item)) {
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

            Supplier<InputStream> streamSupplier = contentStream.getStream();
            InputStream stream = streamSupplier.get();
            

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
        ItemDomainMAARC current = getCurrent();
        Boolean loadGallery = current.getLoadGallery();
        if (loadGallery == null) {
            if (getControllerUtility().isEntityTypeFile(getCurrent())) {
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
            current.setLoadGallery(loadGallery);
        }
        return loadGallery;
    }

    public void setLoadGallery(boolean renderGallery) {
        ItemDomainMAARC current = getCurrent();
        current.setLoadGallery(renderGallery);
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

    public ItemDomainMAARCLazyDataModel getMAARCListDataModel() {
        if (maarcListDataModel == null) {           
            maarcListDataModel = new ItemDomainMAARCLazyDataModel(itemDomainMAARCFacade, getDefaultDomain()); 
        }

        return maarcListDataModel;
    } 
    
    @Override
    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        return true;
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

    @Override
    protected ItemDomainMAARCControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMAARCControllerUtility(); 
    }

}
