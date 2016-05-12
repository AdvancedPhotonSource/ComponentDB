package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.UserGroupSetting;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(SettingType.class)
public class SettingType_ { 

    public static volatile ListAttribute<SettingType, UserSetting> userSettingList;
    public static volatile ListAttribute<SettingType, UserGroupSetting> userGroupSettingList;
    public static volatile SingularAttribute<SettingType, String> defaultValue;
    public static volatile SingularAttribute<SettingType, String> name;
    public static volatile SingularAttribute<SettingType, String> description;
    public static volatile SingularAttribute<SettingType, Integer> id;

}