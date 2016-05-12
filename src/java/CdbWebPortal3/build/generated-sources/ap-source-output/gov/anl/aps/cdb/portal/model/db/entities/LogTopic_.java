package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(LogTopic.class)
public class LogTopic_ { 

    public static volatile SingularAttribute<LogTopic, String> name;
    public static volatile ListAttribute<LogTopic, Log> logList;
    public static volatile SingularAttribute<LogTopic, String> description;
    public static volatile SingularAttribute<LogTopic, Integer> id;

}