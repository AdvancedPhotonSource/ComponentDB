package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.List;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(EntityInfo.class)
public class EntityInfo_ { 

    public static volatile SingularAttribute<EntityInfo, Date> lastModifiedOnDateTime;
    public static volatile SingularAttribute<EntityInfo, Item> item;
    public static volatile SingularAttribute<EntityInfo, UserInfo> createdByUser;
    public static volatile SingularAttribute<EntityInfo, List> list;
    public static volatile SingularAttribute<EntityInfo, Date> obsoletedOnDateTime;
    public static volatile SingularAttribute<EntityInfo, Date> createdOnDateTime;
    public static volatile SingularAttribute<EntityInfo, UserInfo> ownerUser;
    public static volatile SingularAttribute<EntityInfo, UserGroup> ownerUserGroup;
    public static volatile SingularAttribute<EntityInfo, UserInfo> obsoletedByUser;
    public static volatile SingularAttribute<EntityInfo, Boolean> isGroupWriteable;
    public static volatile SingularAttribute<EntityInfo, ItemElement> itemElement;
    public static volatile SingularAttribute<EntityInfo, Integer> id;
    public static volatile SingularAttribute<EntityInfo, UserInfo> lastModifiedByUser;

}