package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(List.class)
public class List_ { 

    public static volatile ListAttribute<List, ItemElement> itemElementList;
    public static volatile ListAttribute<List, UserInfo> userInfoList;
    public static volatile SingularAttribute<List, EntityInfo> entityInfo;
    public static volatile SingularAttribute<List, String> name;
    public static volatile SingularAttribute<List, String> description;
    public static volatile ListAttribute<List, UserGroup> userGroupList;
    public static volatile SingularAttribute<List, Integer> id;

}