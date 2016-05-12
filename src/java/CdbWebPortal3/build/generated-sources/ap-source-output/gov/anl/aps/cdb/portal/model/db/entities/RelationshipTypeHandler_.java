package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(RelationshipTypeHandler.class)
public class RelationshipTypeHandler_ { 

    public static volatile SingularAttribute<RelationshipTypeHandler, String> name;
    public static volatile SingularAttribute<RelationshipTypeHandler, String> description;
    public static volatile SingularAttribute<RelationshipTypeHandler, Integer> id;
    public static volatile ListAttribute<RelationshipTypeHandler, RelationshipType> relationshipTypeList;

}