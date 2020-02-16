package baconetworks.npcbattlelimiter.config;

import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ConfigLoader {

    public static File configFile;
    public static ConfigurationLoader<CommentedConfigurationNode> configManager;
    public static CommentedConfigurationNode config;
    private static ConfigLoader instance = new ConfigLoader();
    public static LinkedHashMap<String, BattleLimitObject> BattleLimits = new LinkedHashMap<>();
    public static ArrayList<String> values = new ArrayList<>();

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
        ensureBoolean(config.getNode("MainConfig", "LowerMax", "Enabled"), true);
        ensureNumber(config.getNode("MainConfig", "LowerMax", "Threshold"), 5);
        ensureNumber(config.getNode("MainConfig", "LowerMax", "GiveLimit"), 250);
        config.getNode("MainConfig", "BattleLimit").setComment("Set the max battle limit");
        config.getNode("MainConfig", "LowerMax").setComment("Lower the max amount of money received after hitting the defined threshold");
        config.getNode("MainConfig", "LowerMax", "Enabled").setComment("Enabled or disabled");
        config.getNode("MainConfig", "LowerMax", "Threshold").setComment("Set the threshold (Anything above this will have money caped)");
        config.getNode("MainConfig", "LowerMax", "GiveLimit").setComment("Set the max money given after reaching the threshold");
        config.getNode(new Object[]{"BattleLimits"}).setValue(values.add("f7723a00-d600-41c9-b907-5054b1ffb94b"));
        config.getNode(new Object[]{"BattleLimits", "f7723a00-d600-41c9-b907-5054b1ffb94b", "Username"}).setValue("kristi71111");
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
                            commandNode.getNode("Username").getString(),
                            commandNode.getNode("BattleLimit").getInt()
                    );
                    BattleLimits.put(Objects.requireNonNull(commandNode.getKey()).toString(), BattleLimit);
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

    public static void ensureBoolean(CommentedConfigurationNode node, Boolean def) {
        if (!(node.getValue() instanceof Boolean)) {
            node.setValue(def);
        }
    }

    public static LinkedHashMap<String, BattleLimitObject> BattleLimitGet() {
        return BattleLimits;
    }

    public static CommentedConfigurationNode getNode(String... path) {
        return config.getNode(path);
    }
}