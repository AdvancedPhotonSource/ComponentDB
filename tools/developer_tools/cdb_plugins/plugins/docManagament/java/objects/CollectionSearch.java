/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;

/**
 *
 * @author djarosz
 */
public class CollectionSearch extends CdbObject{
    
    private String collectionId; 
    private CollectionObject collection; 
    private String documentId;
    private String systemId;
    private String projectId; 
    

    public CollectionSearch(String collectionId, String collectionLabel) {
        this.collectionId = collectionId;
        this.collection = new CollectionObject(collectionLabel); 
    }

    public CollectionSearch(String collectionId, String collectionLabel, String documentId, String systemId, String projectId, UserInfo owner) {
        this.collectionId = collectionId;
        this.collection = collection;
        this.documentId = documentId;
        this.systemId = systemId;
        this.projectId = projectId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public CollectionObject getCollection() {
        return collection;
    }
    
    public class CollectionObject {
        
        private String collectionLabel; 
        private UserInfo owner; 

        public CollectionObject(String collectionLabel) {
            this.collectionLabel = collectionLabel;
        }

        public void setCollectionLabel(String collectionLabel) {
            this.collectionLabel = collectionLabel;
        }
    }
    
    
}
