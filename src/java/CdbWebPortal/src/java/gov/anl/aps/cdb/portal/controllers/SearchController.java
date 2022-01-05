/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.SearchSettings;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Search controller.
 */
@Named(SearchController.controllerNamed)
@SessionScoped
public class SearchController implements Serializable {   
        
    public static final String controllerNamed = "searchController";       

    private static final Logger logger = LogManager.getLogger(SearchController.class.getName());
    private String searchString = null;       

    private Boolean performSearch = false;
    private Boolean performExternallyInitializedSearch = false;
    
    private SearchSettings searchSettings; 
    
    private final Set<CdbEntityController> searchableControllers; 

    /**
     * Constructor.
     */
    public SearchController() {
        searchableControllers = new HashSet<>(); 
    }

    @PostConstruct
    public void initialize() {
        searchSettings = new SearchSettings(this); 
        searchSettings.updateSettings();
    }
    
    public static SearchController getInstance() {
        return (SearchController) SessionUtility.findBean(controllerNamed);
    }
    
    public void registerSearchableController(CdbEntityController entityController) {
        searchableControllers.add(entityController);         
    }

    public String performInputBoxSearch() {
        if (searchString == null || searchString.isEmpty()) {
            SessionUtility.addWarningMessage("Warning", "Please specify a search entry.");
            return null;
        }
        performExternallyInitializedSearch = true;
        return "/views/search/search.xhtml?faces-redirect=true";
    }

    public String getInputBoxSearchString() {
        return "";
    }

    public void setInputBoxSearchString(String searchString) {
        this.searchString = searchString;
    }
    
    public void prepareSearch() {
        if (searchString != null && !searchString.isEmpty()) {
            performSearch = true;
            performExternallyInitializedSearch = false;
        }
    }

    public void search() {
        if (performSearch) {
            for (CdbEntityController controller : searchableControllers) {                                                                                
                // Check if controller needs to be skipped.                               
                if (controller instanceof ItemDomainCatalogController) {
                    if (!searchSettings.getDisplayCatalogItems()) continue;
                }
                else if (controller instanceof ItemDomainInventoryController) {
                    if (!searchSettings.getDisplayInventoryItems()) continue;
                } 
                else if (controller instanceof ItemDomainMachineDesignController) {
                    if (!searchSettings.getDisplayMachineDesignItems()) continue;
                }
                else if (controller instanceof ItemDomainCableCatalogController) {
                    if (!searchSettings.getDisplayCableCatalogItems()) continue;
                } 
                else if (controller instanceof ItemDomainCableInventoryController) {
                    if (!searchSettings.getDisplayCableInventoryItems()) continue; 
                } 
                else if (controller instanceof ItemDomainCableDesignController) {
                    if(!searchSettings.getDisplayCableDesignItems()) continue;
                }
                else if (controller instanceof ItemTypeController) {
                    if (!searchSettings.getDisplayItemTypes()) continue;
                }
                else if (controller instanceof ItemCategoryController) {
                    if (!searchSettings.getDisplayItemCategories()) continue;
                }
                else if (controller instanceof ItemDomainLocationController) {
                    if (!searchSettings.getDisplayLocationItems()) continue;
                }
                else if (controller instanceof PropertyTypeController) {
                    if (!searchSettings.getDisplayPropertyTypes()) continue;
                }
                else if (controller instanceof PropertyTypeCategoryController) {
                    if (!searchSettings.getDisplayPropertyTypeCategories()) continue;
                }
                else if (controller instanceof SourceController) {
                    if (!searchSettings.getDisplaySources()) continue;
                }
                else if (controller instanceof UserGroupController) {
                    if (!searchSettings.getDisplayUserGroups()) continue;
                }
                else if (controller instanceof UserInfoController) {
                    if (!searchSettings.getDisplayUsers()) continue;                    
                } else if (controller instanceof ItemElementController) {
                    if (!searchSettings.getDisplayItemElements()) continue;
                } 
                
                controller.performEntitySearch(searchString, searchSettings.getCaseInsensitive());                
            }
        }
    }        

    public void completeSearch() {
        if (searchString == null || searchString.isEmpty()) {
            SessionUtility.addWarningMessage("Warning", "Search string is empty.");
        } else {
            performSearch = false;
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("search");
            } catch (IOException ex) {
                logger.debug(ex);
            }
        }
    }

    public boolean isDisplayResults() {
        return (searchString != null && !searchString.isEmpty()) && !performSearch;
    }

    public boolean isDisplayLoadingScreen() {
        return performSearch;
    }

    public boolean isPerformSearch() {
        return performSearch;
    }

    public boolean isPerformExternallyInitializedSearch() {
        return performExternallyInitializedSearch;
    }

    public String getCurrentViewId() {
        return SessionUtility.getCurrentViewId();
    }

    // Common customize function when settings are changed.
    public String customizeListDisplay() {
        return customizeSearch();
    }

    public String customizeSearch() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public SearchSettings getSearchSettings() {
        return searchSettings;
    }
    
    public void processPreRender() {
        searchSettings.updateSettings();
    }

}
