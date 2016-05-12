package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Log.class)
public class Log_ { 

    public static volatile ListAttribute<Log, LogLevel> logLevelList;
    public static volatile SingularAttribute<Log, LogTopic> logTopic;
    public static volatile ListAttribute<Log, ItemElement> itemElementList;
    public static volatile ListAttribute<Log, Attachment> attachmentList;
    public static volatile SingularAttribute<Log, UserInfo> enteredByUser;
    public static volatile SingularAttribute<Log, Date> effectiveFromDateTime;
    public static volatile ListAttribute<Log, Item> itemList;
    public static volatile SingularAttribute<Log, Integer> id;
    public static volatile SingularAttribute<Log, String> text;
    public static volatile SingularAttribute<Log, Date> enteredOnDateTime;
    public static volatile SingularAttribute<Log, Date> effectiveToDateTime;

}