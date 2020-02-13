package baconetworks.npcbattlelimiter.config;

import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigLoader {

    public static File configFile;
    public static ConfigurationLoader<CommentedConfigurationNode> configManager;
    public static CommentedConfigurationNode config;
    private static ConfigLoader instance = new ConfigLoader();
    public static Map<String, BattleLimitObject> BattleLimits = new HashMap<>();
    public static ArrayList<String> values = new ArrayList<String>();

    public static void init(File rootDir) {
        configFile = new File(rootDir, "limits.conf");
        configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
    }

    public static ConfigLoader getInstance() {
        return instance;
    }

    public static void load() {
        // load file
        try {
            configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                loadConfig();
                makeConfig();
                saveConfig();
            }
            config = configManager.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeConfig() {
        ensureNumber(config.getNode("MainConfig", "BattleLimit"), 5);
        config.getNode("MainConfig", "BattleLimit").setComment("Set the max battle limit");
        config.getNode(new Object[]{"BattleLimits"}).setValue(values.add("f7723a00-d600-41c9-b907-5054b1ffb94b"));
        config.getNode(new Object[]{"BattleLimits", "f7723a00-d600-41c9-b907-5054b1ffb94b", "UUID"}).setValue("f7723a00-d600-41c9-b907-5054b1ffb94b");
        config.getNode(new Object[]{"BattleLimits", "f7723a00-d600-41c9-b907-5054b1ffb94b", "BattleLimit"}).setValue(0);
    }

    public static void saveConfig() {
        try {
            configManager.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            config = configManager.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadBattleLimit() {
        BattleLimits.clear();
        try {
            ConfigurationNode node = configManager.load();

            ConfigurationNode commandsNode = node.getNode("BattleLimits");

            for (ConfigurationNode commandNode : commandsNode.getChildrenMap().values()) {
                try {
                    BattleLimitObject BattleLimit = new BattleLimitObject(
                            commandNode.getNode("UUID").getString(),
                            commandNode.getNode("BattleLimit").getInt()
                    );
                    BattleLimits.put(commandNode.getNode("UUID").getString().split(" ")[0], BattleLimit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void ensureNumber(CommentedConfigurationNode node, Number def) {
        if (!(node.getValue() instanceof Number)) {
            node.setValue(def);
        }
    }
    public static Map<String, BattleLimitObject> BattleLimitGet() {
        return BattleLimits;
    }

    public static CommentedConfigurationNode getNode(String... path) {
        return config.getNode(path);
    }
}