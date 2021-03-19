package me.MrWener.StaticEffects.plugin.storage.utils;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ResponseDecoder {

    public static enum ResponseType {
        SUCCESS("undefined"), FAIL("undefined");

        String message;

        ResponseType(@NotNull String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(@Nullable String message) {
            if (message == null) {
                this.message = "N/A";
            } else {
                this.message = message;
            }
        }

        public static ResponseType getByName(@NotNull String name) {
            ResponseType[] declaredEnums = values();

            for (ResponseType one : declaredEnums) {
                if (one.name().equalsIgnoreCase(name)) return one;
            }
            return null;
        }

        public static <T extends Exception> ResponseType fromException(T exception) {
            ResponseType type = FAIL;
            type.setMessage(exception.toString());
            return type;
        }

        @Override
        public String toString() {
            return "Status: '" + name() + "', " +
                    "Message: '" + getMessage() + "'.";
        }
    }

    public static boolean isValidFormat(String jsonString) {
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(jsonString);
            String raw = (String) object.get("status");
            System.out.println(raw);
            if (raw == null) {
                return false;
            }
            return true;
        } catch (Exception x) {
            return false;
        }
    }

    public static ResponseType decodeResponse(@NotNull String response) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(response);

            String status = (String) json.get("status");
            String message = (String) json.get("message");
            ResponseType type = ResponseType.getByName(status);
            if (type == null) {
                ResponseType responseType = ResponseType.FAIL;
                responseType.setMessage("Unknown ResponseType: " + status);
                return responseType;
            }
            type.setMessage(message);

            return type;
        } catch (ParseException x) {
            ResponseType responseType = ResponseType.FAIL;
            responseType.setMessage(x.toString());
            return responseType;
        }
    }


}
