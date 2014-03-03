package gov.anl.aps.irmis.apps.pv.cfw;

import java.util.regex.*;

public class PVUtils {

    private static final Pattern pvRegEx = Pattern.compile("^[0-9#@ ]+.*");
    /**
     * Checks to make sure pvName is a valid pv string.
     * The strings in DBF_XXXLINK fields may or may not be an
     * actual pv reference, so we need to verify.
     */
    public static boolean isPurePV(String pvName) {
        if (pvName == null ||
            pvName.length() == 0)
            return false;
        Matcher matcher = pvRegEx.matcher(pvName);
        return !matcher.matches();
    }

    public static String basePVName(String pvString) {

        int delimiter = pvString.indexOf('.');
        if (delimiter == -1)
            delimiter = pvString.indexOf(' ');
        if (delimiter == -1)
            return pvString;
        else
            return pvString.substring(0,delimiter);
    }

}
