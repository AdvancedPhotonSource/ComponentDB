package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.List;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(UserInfo.class)
public class UserInfo_ { 

    public static volatile SingularAttribute<UserInfo, String> lastName;
    public static volatile ListAttribute<UserInfo, PropertyValueHistory> propertyValueHistoryList;
    public static volatile ListAttribute<UserInfo, EntityInfo> entityInfoList;
    public static volatile SingularAttribute<UserInfo, String> description;
    public static volatile ListAttribute<UserInfo, ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    public static volatile ListAttribute<UserInfo, UserRole> userRoleList;
    public static volatile ListAttribute<UserInfo, List> listList;
    public static volatile ListAttribute<UserInfo, UserSetting> userSettingList;
    public static volatile SingularAttribute<UserInfo, String> firstName;
    public static volatile SingularAttribute<UserInfo, String> password;
    public static volatile ListAttribute<UserInfo, Log> logList;
    public static volatile ListAttribute<UserInfo, EntityInfo> entityInfoList2;
    public static volatile ListAttribute<UserInfo, EntityInfo> entityInfoList1;
    public static volatile ListAttribute<UserInfo, UserGroup> userGroupList;
    public static volatile SingularAttribute<UserInfo, String> middleName;
    public static volatile ListAttribute<UserInfo, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<UserInfo, Integer> id;
    public static volatile SingularAttribute<UserInfo, String> email;
    public static volatile ListAttribute<UserInfo, EntityInfo> entityInfoList3;
    public static volatile SingularAttribute<UserInfo, String> username;

}