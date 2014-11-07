package io.replay.framework;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Public utility class containing useful, reuseable methods.
 */
public class Util {

    /** Returns true if the string is null, or empty (when trimmed). */
    public static boolean isNullOrEmpty(String s) {
        // Rather than using s.trim().length() == 0, use getTrimmedLength avoids allocating an
        // extra String object
        return TextUtils.isEmpty(s) || TextUtils.getTrimmedLength(s) == 0;
    }

    /** Returns true if the specified collection is null or empty **/
    public static boolean isNullOrEmpty(Collection c){
        return c == null || c.isEmpty();
    }

    /** Returns true if the specified Map is null or empty **/
    public static boolean isNullOrEmpty(Map m){
        return m == null || m.isEmpty();
    }

    /** Returns true if the specified Array is null or empty **/
    public static boolean isNullOrEmpty(Object [] obj){
        return obj == null || obj.length == 0;
    }

    /**
     * Takes an array of objects where the even index is a String and the odd index is an Object of some type and
     * returns a Map.
     * The "key" should be castable to String, else this method will throw a {@link ClassCastException}.
     *
     * @param keyValuePairs a (varargs) array of Objects that follow the pattern: ["key", val, "key2", val2]
     * @return the varargs list converted to a Map
     */
    public static Map<String, ?> varArgsToMap(Object... keyValuePairs) {
        if (isNullOrEmpty(keyValuePairs) || (keyValuePairs.length % 2 != 0)) {
            throw new IllegalArgumentException("\"keyValuePairs\" must be non-null and even");
        }

        final int length = keyValuePairs.length;
        Map<String, Object> map;
        if(VERSION.SDK_INT >= VERSION_CODES.KITKAT){
            map = new ArrayMap<String, Object>(length/2 +1); //attempts to conserve memory with ArrayMap when possible.
        }else{
            map = new HashMap<String, Object>(length / 2 + 1);
        }
        for (int i = 0; i < length; i += 2) {
            map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }

}
