package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(RoleType.class)
public class RoleType_ { 

    public static volatile SingularAttribute<RoleType, String> name;
    public static volatile SingularAttribute<RoleType, String> description;
    public static volatile SingularAttribute<RoleType, Integer> id;
    public static volatile ListAttribute<RoleType, UserRole> userRoleList;

}