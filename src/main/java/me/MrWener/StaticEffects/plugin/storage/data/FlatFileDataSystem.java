package me.MrWener.StaticEffects.plugin.storage.data;

import me.MrWener.StaticEffects.plugin.PluginLoader;
import me.MrWener.StaticEffects.plugin.effects.EffectGroup;
import me.MrWener.StaticEffects.plugin.storage.DataSystem;
import me.MrWener.StaticEffects.plugin.storage.utils.ResponseDecoder;
import me.MrWener.StaticEffects.plugin.utils.Logger;
import org.bukkit.util.FileUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FlatFileDataSystem implements DataSystem {
    private PluginLoader instance;

    public FlatFileDataSystem(PluginLoader instance) {
        this.instance = instance;
    }

    public boolean saveData(Map<UUID, EffectGroup> data) {
        File dataFile = new File(instance.getDataFolder(), "data/data.json");
        File dataFileFolder = new File(instance.getDataFolder(), "data");

        if (dataFile.exists()) {
            dataFile.delete();
        }

        try {
            if (!dataFile.exists()) {
                // Create file
                dataFileFolder.mkdirs();
                dataFile.createNewFile();
            }
        } catch (IOException x) {
            x.printStackTrace();
        }


        JSONObject obj = new JSONObject();

        for (UUID player : data.keySet()) {
            obj.put(player, data.get(player).getUuid().toString());
        }

        try (FileWriter writer = new FileWriter(dataFile)) {
            obj.writeJSONString(writer);
            return true;
        } catch (IOException x) {
            x.printStackTrace();
        }
        return false;
    }

    public Map<UUID, EffectGroup> loadData() {
        Map<UUID, EffectGroup> players = new HashMap<>();

        File dataFile = new File(instance.getDataFolder(), "data/data.json");
        File dataFileFolder = new File(instance.getDataFolder(), "data");

        if (!dataFile.exists()) {
            Logger.send("Any data file not found!");
            return players;
        }
        try {
            try (FileReader reader = new FileReader(dataFile)) {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(reader);

                for (Object entry : json.keySet()) {
                    String keyEntry = (String) entry;
                    UUID value = UUID.fromString((String) json.get(keyEntry));

                    players.put(UUID.fromString(keyEntry), PluginLoader.manager.getGroup(value));
                }
            }

        } catch (ParseException ex) {
            Logger.send("Data file is corrupted!");
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("mm:hh dd/MM/yyyy");
            time.format(format);

            File corrupted = new File(dataFileFolder, "corruptedDataFiles/" + time + ".json");
            try {
                FileUtil.copy(dataFile, corrupted);
                corrupted.createNewFile();
            } catch (IOException x) {
                Logger.send("Cannot save copy of corrupted file!");
                x.printStackTrace();
            }
        } catch (IOException x) {
            x.printStackTrace();
        }

        Logger.send("Loaded " + players.size() + " players from data file");
        return players;
    }

    public String saveUser(UUID uuid, UUID group) {
        File dataFile = new File(instance.getDataFolder(), "data/data.json");
        File dataFileFolder = new File(instance.getDataFolder(), "data");
        JSONObject all = new JSONObject();

        try {
            if (!dataFile.exists()) {
                // Create file
                dataFileFolder.mkdirs();
                dataFile.createNewFile();
            }
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }

        try {
            try (FileReader reader = new FileReader(dataFile)) {
                JSONParser parser = new JSONParser();
                all = (JSONObject) parser.parse(reader);
            }

        } catch (FileNotFoundException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (ParseException x) {
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
        all.put(uuid.toString(), group.toString());
        try (FileWriter writer = new FileWriter(dataFile)) {
            all.writeJSONString(writer);
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
        ResponseDecoder.ResponseType type = ResponseDecoder.ResponseType.SUCCESS;
        type.setMessage("Saved user");
        return type.toString();

    }

    public String removeUser(UUID uuid) {
        File dataFile = new File(instance.getDataFolder(), "data/data.json");

        JSONObject all;
        if (!dataFile.exists()) {
            ResponseDecoder.ResponseType type = ResponseDecoder.ResponseType.FAIL;
            type.setMessage("File does not exists");
            return type.toString();
        }
        try {
            try (FileReader reader = new FileReader(dataFile)) {
                JSONParser parser = new JSONParser();
                all = (JSONObject) parser.parse(reader);
            }
        } catch (FileNotFoundException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        } catch (ParseException x) {
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }

        PluginLoader.effects.removePlayer(uuid);
        all.remove(uuid.toString());

        try (FileWriter writer = new FileWriter(dataFile)) {
            all.writeJSONString(writer);
        } catch (IOException x) {
            x.printStackTrace();
            return ResponseDecoder.ResponseType.fromException(x).toString();
        }
        ResponseDecoder.ResponseType type = ResponseDecoder.ResponseType.SUCCESS;
        type.setMessage("Removed user");
        return type.toString();
    }

    @Override
    public String loadUser(UUID uuid) {
        return "N/A";
    }

    @Override
    public boolean containsPlayer(UUID uuid) {
        File dataFile = new File(instance.getDataFolder(), "data/data.json");

        JSONObject all;
        if (!dataFile.exists()) {
            return false;
        }
        try {
            try (FileReader reader = new FileReader(dataFile)) {
                JSONParser parser = new JSONParser();
                all = (JSONObject) parser.parse(reader);
                if (all.containsKey(uuid.toString())) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        } catch (ParseException x) {
        }

        return false;
    }

    @Override
    public DataSystemType getType() {
        return DataSystemType.FLATFILE;
    }

    @Override
    public String getDataSystemName() {
        return "FlatFileDataSystem";
    }
}
