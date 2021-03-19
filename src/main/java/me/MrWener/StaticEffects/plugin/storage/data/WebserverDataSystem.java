package me.MrWener.StaticEffects.plugin.storage.data;

import me.MrWener.StaticEffects.plugin.PluginLoader;
import me.MrWener.StaticEffects.plugin.effects.EffectGroup;
import me.MrWener.StaticEffects.plugin.storage.DataSystem;
import me.MrWener.StaticEffects.plugin.storage.utils.DataDecoder;
import me.MrWener.StaticEffects.plugin.storage.utils.ResponseDecoder;
import me.MrWener.StaticEffects.plugin.utils.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebserverDataSystem implements DataSystem {
    public static final String METHOD_GET = "?method=GET&user_uuid=";
    public static final String METHOD_GET_ALL = "?method=GET";
    public static final String METHOD_GIVE = "?method=GIVE&json=";
    public static final String METHOD_REMOVE = "?method=REMOVE&user_uuid=";
    private String url;
    private FlatFileDataSystem backupSystem;

    /**
     * Default constructor
     *
     * @param url          Url of DataHandler.php
     * @param backupSystem Backup system to save data if webserver is unreachable
     */
    public <System extends DataSystem> WebserverDataSystem(String url, System backupSystem) {
        this.url = url;
        this.backupSystem = (FlatFileDataSystem) backupSystem;
    }

    /**
     * Checks if webserver is operative
     *
     * @return
     */
    public boolean isOperative() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            String[] response = readStream(connection.getInputStream()).toString().split(";-;");
            if (ResponseDecoder.isValidFormat(response[0])) {
                return true;
            }
            connection.disconnect();
            return false;
        } catch (Exception x) {
            System.err.println(x.getMessage());
            return false;
        }
    }

    /**
     * Saving user to Database trough webserver
     *
     * @param userUUID  User UUID
     * @param groupUUID User group
     */
    public String saveUser(UUID userUUID, UUID groupUUID) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("user_uuid", userUUID.toString());
        jsonData.put("group_uuid", groupUUID.toString());

        try {
            URL webUrl = new URL(url + METHOD_GIVE + jsonData.toJSONString());
            HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
            connection.connect();
            String[] response = readStream(connection.getInputStream()).toString().split(";-;");
            connection.disconnect();

            ResponseDecoder.ResponseType responseType = ResponseDecoder.decodeResponse(response[0]);
            return responseType.toString();

        } catch (MalformedURLException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (ConnectException x) {
            Logger.send("§c" + x.getMessage());
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
    }

    /**
     * Loads user from database
     *
     * @param userUUID UUID of user
     */
    public String loadUser(UUID userUUID) {
        try {
            URL webUrl = new URL(url + METHOD_GET + userUUID.toString());
            HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
            try {
                connection.connect();
                String[] response = readStream(connection.getInputStream()).toString().split(";-;");
                if (response.length > 0) {
                    JSONObject resultJson = (JSONObject) new JSONParser().parse(response[1]);

                    Map<UUID, UUID> rawData = DataDecoder.decodeResult(resultJson.toString());
                    Map<UUID, EffectGroup> data = PluginLoader.effects.getEffectedPlayers();

                    for (UUID uuid : rawData.keySet()) {
                        EffectGroup group = PluginLoader.manager.getGroup(uuid);
                        if (PluginLoader.manager.isGroupValid(group)) {
                            data.put(uuid, group);
                        }
                    }
                }
                connection.disconnect();

                ResponseDecoder.ResponseType responseType = ResponseDecoder.decodeResponse(response[0]);
                return responseType.toString();
            } catch (ParseException x) {
                return ResponseDecoder.ResponseType.fromException(x).toString();
            }
        } catch (MalformedURLException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (ConnectException x) {
            Logger.send("§c" + x.getMessage());
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
    }

    public String removeUser(UUID userUUID) {
        PluginLoader.effects.removePlayer(userUUID);
        try {
            URL webUrl = new URL(url + METHOD_REMOVE + userUUID.toString());
            HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
            try {
                connection.connect();
                String[] response = readStream(connection.getInputStream()).toString().split(";-;");
                connection.disconnect();

                ResponseDecoder.ResponseType responseType = ResponseDecoder.decodeResponse(response[0]);
                return responseType.toString();
            } catch (Exception x) {
                x.printStackTrace();
                return ResponseDecoder.ResponseType.fromException(x).toString();
            }
        } catch (MalformedURLException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (ConnectException x) {
            Logger.send("§c" + x.getMessage());
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
    }


    /**
     * Reads InputStream from web
     *
     * @param webStream Inpustream of web
     * @return Content that web returned
     */
    public StringBuilder readStream(InputStream webStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(webStream));
        StringBuilder output = new StringBuilder();
        try {
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    output.append(line + "\n");
                }
            } while (line != null);

        } catch (IOException x) {
            x.printStackTrace();

        }
        return output;
    }

    /**
     * Loads all users from database
     *
     * @return data
     */
    public Map<UUID, EffectGroup> loadUsers() {
        Map<UUID, EffectGroup> data = new HashMap<>();
        try {
            URL webUrl = new URL(url + METHOD_GET_ALL);
            HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
            try {
                String[] response = readStream(connection.getInputStream()).toString().split(";-;");

                if (response.length > 1) {
                    JSONObject resultJson = (JSONObject) new JSONParser().parse(response[1]);
                    Map<UUID, UUID> rawData = DataDecoder.decodeResult(resultJson.toString());

                    for (UUID player : rawData.keySet()) {
                        EffectGroup group = PluginLoader.manager.getGroup(rawData.get(player));
                        if (PluginLoader.manager.isGroupValid(group)) {
                            data.put(player, group);
                        }
                    }

                    return data;
                }
            } catch (ParseException x) {
                // note nothing
            } catch (java.net.ConnectException x) {
                Logger.send("§c" + x.getMessage());

            }
            connection.disconnect();
        } catch (MalformedURLException x) {
            x.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean saveData(Map<UUID, EffectGroup> data) {
        if (!isOperative()) {
            backupSystem.saveData(data);
            Logger.send("§cWebserver is not operative, saving with FlatFile!");
            return false;
        }

        for (UUID player : data.keySet()) {
            try {
                saveUser(player, data.get(player).getUuid());
            } catch (Exception x) {
                System.err.println("Cannot save user " + player + " cause: " + x.getMessage());

            }
        }
        Logger.send("Saved " + data.size() + " players to database.");
        return true;
    }

    @Override
    public Map<UUID, EffectGroup> loadData() {
        if (!isOperative()) {
            Logger.send("§cWebserver is not operative, loading with FlatFile!");
            return backupSystem.loadData();
        }

        Map<UUID, EffectGroup> data = loadUsers();
        Logger.send("Loaded " + data.size() + " players from database.");
        return data;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean containsPlayer(UUID uuid) {
        return false;
    }

    @Override
    public String getDataSystemName() {
        return "WebServerDataSystem";
    }

    @Override
    public DataSystemType getType() {
        return DataSystemType.WEBSEVRER;
    }
    public static void main(String[] args) {
        WebserverDataSystem dataSystem = new WebserverDataSystem("http://localhost/data/data_handler.p", null);
        dataSystem.isOperative();
    }
}
