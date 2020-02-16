package baconetworks.npcbattlelimiter;

import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.event.TrainerBeatEvent;
import baconetworks.npcbattlelimiter.task.ResetBattleLimit;
import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "npcbattlelimiter",
        name = "NPCBattleLimiter",
        description = "Limit battles",
        url = "https://github.com/BacoNetworks/NPCBattleLimiter",
        authors = {
                "kristi71111"
        },
        dependencies = {
                @Dependency(id = "pixelmon")
        },
        version = "@VERSION@"
)
public class NPCBattleLimiter {
    long initalDelay = SharedFunctions.Seconds();

    @Inject
    public Logger logger;


    //Inject the config dir
    @Inject
    @ConfigDir(sharedRoot = true)
    public File defaultConfigDir;

    public static final String PLUGIN_NAME = "NPCBattleLimiter";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);
    String TimeDelay = SharedFunctions.GetTime();


    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(new ResetBattleLimit())
                .delay(initalDelay, TimeUnit.SECONDS)
                .interval(1, TimeUnit.DAYS)
                .async()
                .submit(this);

        Pixelmon.EVENT_BUS.register(new TrainerBeatEvent());
        logger.info("I started just fine. Running on version " + "@VERSION@" + " of NPCBattleLimiter");
        logger.info("The NPCBattleLimit will reset in" + TimeDelay);
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        File rootDir = new File(defaultConfigDir, "NPCBattleLimiter");
        ConfigLoader.init(rootDir);
        ConfigLoader.load();
        ConfigLoader.loadBattleLimit();
    }
}
