package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.List;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroupSetting;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(UserGroup.class)
public class UserGroup_ { 

    public static volatile ListAttribute<UserGroup, EntityInfo> entityInfoList;
    public static volatile ListAttribute<UserGroup, UserGroupSetting> userGroupSettingList;
    public static volatile ListAttribute<UserGroup, UserInfo> userInfoList;
    public static volatile SingularAttribute<UserGroup, String> name;
    public static volatile SingularAttribute<UserGroup, String> description;
    public static volatile SingularAttribute<UserGroup, Integer> id;
    public static volatile ListAttribute<UserGroup, List> listList;
    public static volatile ListAttribute<UserGroup, UserRole> userRoleList;

}