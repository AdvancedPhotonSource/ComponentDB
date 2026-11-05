/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.fail;

public class ItemElementUtilityTest {

    @Test
    public void rejectsDirectCatalogAssemblyCycle() throws Exception {
        ItemDomainCatalog assembly = catalog(1, "Assembly");
        addChild(assembly, assembly, "self");

        assertCycleRejected(assembly);
    }

    @Test
    public void rejectsIndirectCatalogAssemblyCycle() throws Exception {
        ItemDomainCatalog first = catalog(1, "First");
        ItemDomainCatalog second = catalog(2, "Second");
        ItemDomainCatalog third = catalog(3, "Third");
        addChild(first, second, "second");
        addChild(second, third, "third");
        addChild(third, first, "first");

        assertCycleRejected(first);
    }

    private static void assertCycleRejected(ItemDomainCatalog item) throws Exception {
        try {
            ItemElementUtility.createItemElementRoot(item);
            fail("Circular catalog assembly was accepted.");
        } catch (CdbException expected) {
            // Existing tree validation is the canonical cycle check.
        }
    }

    private static ItemDomainCatalog catalog(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(id);
        item.setName(name);
        item.setDomain(new Domain(ItemDomainName.CATALOG_ID, ItemDomainName.catalog.getValue()));
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Arrays.asList(self)));
        return item;
    }

    private static void addChild(ItemDomainCatalog parent, ItemDomainCatalog child, String name) {
        ItemElement element = new ItemElement();
        element.setName(name);
        element.setParentItem(parent);
        element.setContainedItem(child);
        parent.getFullItemElementList().add(element);
        parent.resetItemElementDisplayList();
    }
}
