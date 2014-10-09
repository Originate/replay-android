package io.replay.framework.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.replay.framework.util.ReplayLogger;
import io.replay.framework.util.Util;

/**
 * Convenience class created to facilitate the mapping of String to Object. This class extends {@link org.json.JSONObject}
 * and implements {@link java.lang.Iterable}, allowing the user to non-deterministically iterate through the KeySet of this object.
 *
 */
public class ReplayJsonObject extends JSONObject implements Iterable<String>, Serializable{

    public ReplayJsonObject() {
        super();
    }

    public ReplayJsonObject(JSONObject copyFrom) {
        super();
        if(copyFrom != null && copyFrom.length() > 0)
            mergeJSON(copyFrom);
    }

    public ReplayJsonObject(Map<String, ?> map) {
        this();
        if (map == null || map.isEmpty()) return;
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            if (this.has(key)) continue;
            this.putObj(key, entry.getValue());
        }
    }

    /**Convenience method for creating a JsonObject pre-instantiated with key-value pairs.
     *
     * @param keyValuePairs
     */
    public ReplayJsonObject(Object... keyValuePairs) {
        super();
        if (keyValuePairs != null) {
            final int length = keyValuePairs.length;
            if (length % 2 == 0 && length > 0) {
                for (int i = 0; i < length; i += 2) {
                    this.putObj((String) keyValuePairs[i], keyValuePairs[i + 1]);
                }
            } else {
                throw new IllegalArgumentException("Error: ReplayJSONObject should be initialized with a non-zero, even number of" +
                                     "arguments: e.g., [key, value, key, value]. ");
            }
        }
    }

    /*package*/ void mergeJSON(JSONObject copyFrom) {
        for (Iterator<String> keys = copyFrom.keys(); keys.hasNext(); ) {
            String key = keys.next();
            if (this.has(key)) continue; //shouldn't clobber
            try {
                this.putObj(key, copyFrom.get(key));
            } catch (JSONException e) {
                ReplayLogger.w(e, "JSON object had an invalid value during merge");
            }
        }
    }

    @Override
    public JSONObject put(String name, boolean value) {
        return putObj(name, value);
    }

    @Override
    public JSONObject put(String name, double value) {
        return putObj(name, value);
    }

    @Override
    public JSONObject put(String name, int value) {
        return putObj(name, value);
    }

    @Override
    public JSONObject put(String name, long value) {
        return putObj(name, value);
    }

    @Override
    public JSONObject put(String name, Object value) {
        return putObj(name, value);
    }

    public JSONObject put(String name, String value) {
        return putObj(name, value);
    }

    public JSONObject put(String name, ReplayJsonObject value) {
        return putObj(name, value);
    }

    private JSONObject putObj(String key, Object value) {
        try {
            return super.put(key, value);
        } catch (JSONException e) {
            ReplayLogger.w(e, "Failed to add to JSON key:value [%s : %s]", key, value);
            return null;
        }
    }

    @Override
    public Object get(String name) {
        try {
            return super.get(name);
        } catch (JSONException e) {
            ReplayLogger.w(e, "");
            return null;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is a boolean or can be coerced to a boolean,
     * or false otherwise.
     *
     * @return the resulting mapping, or <code>false</code> otherwise.
     */
    @Override
    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    /**
     * Returns the value mapped by name if it exists and is a double or can be coerced to a double,
     * or {@link Double#NaN} otherwise.
     *
     * @return the resulting mapping or {@link Double#NaN} otherwise.
     */
    @Override
    public double getDouble(String name) {
        return getDouble(name, Double.NaN);
    }

    /**
     * Returns the value mapped by name if it exists and is an integer or can be coerced to an integer,
     * or {@link Integer#MIN_VALUE} otherwise.
     *
     * @return the resulting mapping or {@link Integer#MIN_VALUE} otherwise.
     */
    @Override
    public int getInt(String name) {
        return getInt(name, Integer.MIN_VALUE);
    }

    /**
     * Returns the value mapped by name if it exists and is a long or can be coerced to a long,
     * or {@link Long#MIN_VALUE} otherwise.
     *
     * @return the resulting mapping or {@link Long#MIN_VALUE} otherwise.
     */
    @Override
    public long getLong(String name) {
        return getLong(name, Long.MIN_VALUE);
    }

    /**
     * Returns the value mapped by name if it exists and is a string or can be coerced to a string,
     * or null otherwise.
     *
     * @return the resulting mapping or null otherwise.
     */
    @Override
    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defaultVal) {
        try {
            final String str = super.getString(name);
            return Util.isNullOrEmpty(str) ? defaultVal : str;
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public long getLong(String name, long defaultVal) {
        try {
            return super.getLong(name);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public boolean getBoolean(String name, boolean defaultVal) {
        try {
            return super.getBoolean(name);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public double getDouble(String name, double defaultVal) {
        try {
            return super.getDouble(name);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    public int getInt(String name, int defaultVal) {
        try {
            return super.getInt(name);
        } catch (JSONException e) {
            return defaultVal;
        }
    }

    /**
     * Returns the resulting mapping as a JSONObject or null otherwise.
     *
     * @param name
     * @return
     */
    public JSONObject getJsonObject(String name) {
        try {
            return super.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> getArray(String name) {
        try {
            JSONArray array = this.getJSONArray(name);
            final int arrLen = array.length();
            List<T> list = new ArrayList<T>(arrLen);
            for (int i = 0; i < arrLen; i++) {
                list.add((T) array.get(i));
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }


    @Override
    public int hashCode() {
        int code = 31;

        for (String k : this) {
            Object v = get(k);
            code += (k.hashCode() ^ (v != null ? v.hashCode() : 0));
        }
        return code;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JSONObject && equals(this, (JSONObject) o);
    }

    public static boolean equals(JSONObject one, JSONObject two) {
        if (one == null || two == null) return one == two;
        if (one.length() != two.length()) return false;


        Iterator<String> i1 = one.keys();
        Iterator<String> i2 = two.keys();
        while(i1.hasNext() && i2.hasNext()){
            String k1 = i1.next();
            String k2 = i2.next();
            Object v1;
            Object v2;
            try {
                v1 = one.get(k1);
                v2 = two.get(k2);
            } catch (JSONException e) {
                return false;
            }
            if (!k1.equals(k2) || !v1.equals(v2)) return false;

        }
        return true;
    }


/*    public static boolean equals(JSONArray one, JSONArray two) {
        if (one == null || two == null) return one == two;
        if (one.length() != two.length()) return false;

        final int oneLength = one.length();
        for (int i = 0; i < oneLength; i++) {
            try {
                Object oneVal = one.get(i);
                Object twoVal = two.get(i);
                //recursive equals call
                if (!equals(oneVal, twoVal)) return false;
            } catch (JSONException e) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(Object left, Object right) {
        if (left == null || right == null) return left == right;

        if (left instanceof JSONObject) {
            // it's a nested object
            if (!(right instanceof JSONObject)) return false;

            // call equals recursively
            if (!equals((JSONObject) left, (JSONObject) right)) return false;
        } else if (left instanceof JSONArray) {
            // its an array
            if (!(right instanceof JSONArray)) return false;
            JSONArray oneValArray = (JSONArray) left;
            JSONArray twoValArray = (JSONArray) right;

            // call the array equals method
            if (!equals(oneValArray, twoValArray)) return false;
        } else {
            // its a string, float, boolean, int, double, or a nested type

            if (!left.equals(right)) return false;
        }

        return true;
    }*/

    @Override
    public Iterator<String> iterator() {
        return this.keys();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.length());
        for (String key : this) {
            out.writeUTF(key);
            out.writeObject(this.get(key));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        int length = in.readInt();
        if(length <  0) throw new InvalidObjectException("ReplayJsonObject length: " + length);
        for (int i = 0; i < length; i++) {
            String key = in.readUTF();
            Object val = in.readObject();
            this.putObj(key, val);

        }

    }
}