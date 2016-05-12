package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.DomainHandler;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Domain.class)
public class Domain_ { 

    public static volatile ListAttribute<Domain, PropertyType> propertyTypeList;
    public static volatile SingularAttribute<Domain, String> name;
    public static volatile SingularAttribute<Domain, String> description;
    public static volatile SingularAttribute<Domain, DomainHandler> domainHandler;
    public static volatile ListAttribute<Domain, Item> itemList;
    public static volatile SingularAttribute<Domain, Integer> id;

}