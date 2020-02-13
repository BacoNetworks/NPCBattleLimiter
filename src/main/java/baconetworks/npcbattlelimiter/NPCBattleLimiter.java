package baconetworks.npcbattlelimiter;

import baconetworks.npcbattlelimiter.Events.BattleEvent;
import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.task.ResetBattleLimit;
import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private File rootDir;

    @Inject
    private Logger logger;

    //Inject the config dir
    @Inject
    @ConfigDir(sharedRoot = true)
    private File defaultConfigDir;
    public static final String PLUGIN_NAME = "NPCBattleLimiter";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(12).withMinute(0).withSecond(0);

        if (zonedNow.compareTo(zonedNext) > 0) {
            zonedNext = zonedNext.plusDays(1);
        }

        long initalDelay = Duration.between(zonedNow, zonedNext).getSeconds();
        Pixelmon.EVENT_BUS.register(new BattleEvent());

        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(new ResetBattleLimit())
                .delay(initalDelay, TimeUnit.SECONDS)
                .interval(1, TimeUnit.DAYS)
                .async()
                .submit(this);

        logger.info("I started just fine. Running on version " + "@VERSION@" + " of NPCBattleLimiter");
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        rootDir = new File(defaultConfigDir, "NPCBattleLimiter");
        ConfigLoader.init(rootDir);
        ConfigLoader.loadBattleLimit();
        ConfigLoader.load();
    }
}
