/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogBaseController;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author craig
 */
public abstract class ItemDomainCatalogBaseSettings<CatalogController extends ItemDomainCatalogBaseController> extends ItemSettings<CatalogController> {
    
    protected Boolean displayModelNumber = null;
    protected Boolean displayAlternateName = null;

    protected String filterByType = null;
    protected String filterByCategory = null;
    protected String filterByModelNumber = null;

    protected Boolean selectDisplayType = true;
    protected Boolean selectDisplayCategory = true;
    protected Boolean selectDisplayModelNumber = true;

    protected String selectFilterByType = null;
    protected String selectFilterByCategory = null;
    protected String selectFilterByModelNumber = null;

    protected Boolean loadComponentInstanceRowExpansionPropertyValues = null;
    protected Boolean displayComponentInstanceRowExpansion = null;
    
    private static String DEFAULT_SORT_KEY = ItemQueryBuilder.QueryTranslator.name.getValue();
    private static SortOrder DEFAULT_SORT_ORDER = SortOrder.ASCENDING; 

    public ItemDomainCatalogBaseSettings(CatalogController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        // No help fragment yet exists for cdb 3.0 catalog
        return null;
        //return DisplayListPageHelpFragmentSettingTypeKey;
    }

    public Boolean getLoadComponentInstanceRowExpansionPropertyValues() {
        return loadComponentInstanceRowExpansionPropertyValues;
    }

    public void setLoadComponentInstanceRowExpansionPropertyValues(Boolean loadComponentInstanceRowExpansionPropertyValues) {
        this.loadComponentInstanceRowExpansionPropertyValues = loadComponentInstanceRowExpansionPropertyValues;
    }

    public Boolean getDisplayComponentInstanceRowExpansion() {
        return displayComponentInstanceRowExpansion;
    }

    public void setDisplayComponentInstanceRowExpansion(Boolean displayComponentInstanceRowExpansion) {
        this.displayComponentInstanceRowExpansion = displayComponentInstanceRowExpansion;
    }

    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displayModelNumber;
    }

    @Override
    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayModelNumber = displayItemIdentifier1;
    }

    @Override
    public Boolean getDisplayItemIdentifier2() {
        return this.displayAlternateName;
    }

    @Override
    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayAlternateName = displayItemIdentifier2;
    }

    @Override
    public SortMeta getDataTableSortMeta() {
        if (dataTableSortMeta == null) {            
            SortMeta.Builder builder = new SortMeta().builder();
            dataTableSortMeta = builder
                    .field(DEFAULT_SORT_KEY)
                    .order(DEFAULT_SORT_ORDER)
                    .build();            
        }
        return dataTableSortMeta; 
    }


}
