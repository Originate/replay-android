package io.replay.framework;

import android.text.TextUtils;

/** Public utility class containing useful, reuseable methods.
 */
class Util {

    /** Returns true if the string is null, or empty (when trimmed). */
    static boolean isNullOrEmpty(String text) {
        // Rather than using text.trim().length() == 0, use getTrimmedLength avoids allocating an
        // extra String object
        return TextUtils.isEmpty(text) || TextUtils.getTrimmedLength(text) == 0;
    }
}
