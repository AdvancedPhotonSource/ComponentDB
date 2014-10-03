package gov.anl.aps.cdb.portal.utilities;


public class StringUtility
{
    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == null && cs2 == null) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        return cs1.equals(cs2);
    }
}
