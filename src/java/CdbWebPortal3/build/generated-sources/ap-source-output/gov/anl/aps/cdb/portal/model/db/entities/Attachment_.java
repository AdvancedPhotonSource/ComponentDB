package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Attachment.class)
public class Attachment_ { 

    public static volatile SingularAttribute<Attachment, String> name;
    public static volatile ListAttribute<Attachment, Log> logList;
    public static volatile SingularAttribute<Attachment, String> description;
    public static volatile SingularAttribute<Attachment, Integer> id;
    public static volatile SingularAttribute<Attachment, String> tag;

}