package gov.anl.aps.cdb.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility
{
    private static final SimpleDateFormat DateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    public static String getCurrentDateTime() {
        return DateTimeFormat.format(new Date());
    }
}
