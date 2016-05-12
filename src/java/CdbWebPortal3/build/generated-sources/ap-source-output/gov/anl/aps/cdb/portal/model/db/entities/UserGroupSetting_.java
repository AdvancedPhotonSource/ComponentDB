package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(UserGroupSetting.class)
public class UserGroupSetting_ { 

    public static volatile SingularAttribute<UserGroupSetting, Integer> id;
    public static volatile SingularAttribute<UserGroupSetting, String> value;
    public static volatile SingularAttribute<UserGroupSetting, UserGroup> userGroup;
    public static volatile SingularAttribute<UserGroupSetting, SettingType> settingType;

}