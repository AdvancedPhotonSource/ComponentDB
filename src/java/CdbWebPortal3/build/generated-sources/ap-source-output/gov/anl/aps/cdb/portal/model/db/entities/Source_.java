package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Source.class)
public class Source_ { 

    public static volatile SingularAttribute<Source, String> contactInfo;
    public static volatile ListAttribute<Source, ItemSource> itemSourceList;
    public static volatile SingularAttribute<Source, String> name;
    public static volatile SingularAttribute<Source, String> description;
    public static volatile SingularAttribute<Source, Integer> id;
    public static volatile SingularAttribute<Source, String> url;

}