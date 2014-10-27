package io.replay.framework;

import android.text.TextUtils;

import java.util.Collection;
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
}
