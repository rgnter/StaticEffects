package me.MrWener.StaticEffects.plugin.storage.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataDecoder {
    /**
     * Decodes JSON data string to HashMap
     *
     * @param resultString
     * @return
     */
    public static Map<UUID, UUID> decodeResult(String resultString) {
        try {
            JSONObject resultJson = (JSONObject) new JSONParser().parse(resultString);
            String resultsString = (String) resultJson.get("result");
            return jsonToMap(resultsString);
        } catch (ParseException x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * This method processes LOOOONG JSON datas
     *
     * @param json Json to be processed
     * @return Data
     */
    public static Map<UUID, UUID> jsonToMap(String json) {
        String striped = json;
        striped = striped.replace("[", "");
        striped = striped.replace("]", "");
        striped = striped.replace("{", "");
        striped = striped.replace("}", "");

        Map<UUID, UUID> data = new HashMap<>();
        String[] keyAndValueRoots = striped.split(",");


        /*
           Every odd Number is key, so i need to find them, and get their vlaues
         */
        for (int i = 0; i < keyAndValueRoots.length; i++) {
            String section = keyAndValueRoots[i];
            if (isOdd(i)) {
                String key = section.split(":")[1];
                String value = keyAndValueRoots[i + 1].split(":")[1];

                key = stripJSONValue(key);
                value = stripJSONValue(value);

                try {
                    data.put(UUID.fromString(key), UUID.fromString(value));
                } catch (Exception x) {

                }
            }
        }
        return data;
    }

    /**
     * Checks if num is odd
     *
     * @param num Num
     * @return boolean
     */
    private static boolean isOdd(int num) {
        if ((num & 1) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Strips " and " (yay) from JSON
     *
     * @param key key to be striped
     * @return Striped string
     */
    private static String stripJSONValue(String key) {
        StringBuffer keyBuf = new StringBuffer(key);
        if (keyBuf.charAt(0) == '"') {
            keyBuf.setCharAt(0, '\0');
        }
        if (keyBuf.charAt(key.length() - 1) == '"') {
            keyBuf.setCharAt(key.length() - 1, '\0');
        }
        key = keyBuf.toString().trim();

        return key;
    }


}
