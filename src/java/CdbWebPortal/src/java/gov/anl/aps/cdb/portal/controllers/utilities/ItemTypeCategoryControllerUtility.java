/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemTypeCategoryEntity;

/**
 *
 * @author darek
 */
public abstract class ItemTypeCategoryControllerUtility<TypeCategoryEntity extends ItemTypeCategoryEntity, TypeCategoryFacade extends CdbEntityFacade<TypeCategoryEntity>> extends CdbEntityControllerUtility<TypeCategoryEntity, TypeCategoryFacade> {

}
