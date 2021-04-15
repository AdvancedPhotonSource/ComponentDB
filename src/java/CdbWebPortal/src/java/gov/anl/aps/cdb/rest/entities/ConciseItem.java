/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darek
 */
public class ConciseItem {

    private String name;
    private Integer id;
    private String itemIdentifier1;
    private String itemIdentifier2;
    private Integer qrId;

    private Integer derivedFromItemId;
    private String derivedFromItemName;
    private List<Integer> itemProjectIdList;
    private List<Integer> itemTypeIdList;
    private List<Integer> itemCategoryIdList;
    private String primaryImageForItem;

    public ConciseItem(Item item) {
        init(item, null);
    }

    public ConciseItem(Item item, ConciseItemOptions options) {
        init(item, options);
    }

    private void init(Item item, ConciseItemOptions options) {
        name = item.getName();
        id = item.getId();
        itemIdentifier1 = item.getItemIdentifier1();
        itemIdentifier2 = item.getItemIdentifier2();
        qrId = item.getQrId();

        if (options != null) {
            if (options.isIncludeDerivedFromItemInfo()) {
                Item derivedFromItem = item.getDerivedFromItem();
                if (derivedFromItem != null) {
                    derivedFromItemId = derivedFromItem.getId();
                    derivedFromItemName = derivedFromItem.getName();
                }
            }

            if (options.isIncludeItemCategoryIdList()) {
                List<ItemCategory> itemCategoryList = item.getItemCategoryList();
                itemCategoryIdList = new ArrayList<>();
                for (ItemCategory category : itemCategoryList) {
                    Integer id = category.getId();
                    itemCategoryIdList.add(id);
                }
            }

            if (options.isIncludeItemTypeIdList()) {
                List<ItemType> itemTypeList = item.getItemTypeList();
                itemTypeIdList = new ArrayList<>();
                for (ItemType itemType : itemTypeList) {
                    Integer id = itemType.getId();
                    itemTypeIdList.add(id);
                }
            }

            if (options.isIncludeItemProjectIdList()) {
                List<ItemProject> itemProjectList = item.getItemProjectList();
                itemProjectIdList = new ArrayList<>();
                for (ItemProject itemProject : itemProjectList) {
                    Integer id = itemProject.getId();
                    itemProjectIdList.add(id);
                }
            }

            if (options.isIncludePrimaryImageForItem()) {
                primaryImageForItem = item.getPrimaryImageForItem();
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getItemIdentifier1() {
        return itemIdentifier1;
    }

    public String getItemIdentifier2() {
        return itemIdentifier2;
    }

    public Integer getQrId() {
        return qrId;
    }

    public Integer getDerivedFromItemId() {
        return derivedFromItemId;
    }

    public String getDerivedFromItemName() {
        return derivedFromItemName;
    }

    public List<Integer> getItemProjectIdList() {
        return itemProjectIdList;
    }

    public List<Integer> getItemTypeIdList() {
        return itemTypeIdList;
    }

    public List<Integer> getItemCategoryIdList() {
        return itemCategoryIdList;
    }

    public String getPrimaryImageForItem() {
        return primaryImageForItem;
    }

    public static List<ConciseItem> createList(List<Item> itemList, ConciseItemOptions options) {
        List<ConciseItem> basicItems = new ArrayList<>();

        for (Item item : itemList) {
            ConciseItem basicItem = new ConciseItem(item, options);
            basicItems.add(basicItem);
        }

        return basicItems;
    }

}
