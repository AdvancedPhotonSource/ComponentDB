package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(UserSetting.class)
public class UserSetting_ { 

    public static volatile SingularAttribute<UserSetting, Integer> id;
    public static volatile SingularAttribute<UserSetting, String> value;
    public static volatile SingularAttribute<UserSetting, UserInfo> user;
    public static volatile SingularAttribute<UserSetting, SettingType> settingType;

}