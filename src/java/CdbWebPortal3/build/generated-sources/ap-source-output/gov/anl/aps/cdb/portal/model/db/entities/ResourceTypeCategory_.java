package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ResourceTypeCategory.class)
public class ResourceTypeCategory_ { 

    public static volatile SingularAttribute<ResourceTypeCategory, String> name;
    public static volatile SingularAttribute<ResourceTypeCategory, String> description;
    public static volatile ListAttribute<ResourceTypeCategory, ResourceType> resourceTypeList;
    public static volatile SingularAttribute<ResourceTypeCategory, Integer> id;

}