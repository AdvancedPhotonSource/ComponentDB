package gov.anl.aps.cdb.utilities;


public class ObjectUtility
{   
    public static <Type> boolean equals(Type object1, Type object2) {
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}
