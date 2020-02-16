package baconetworks.npcbattlelimiter.task;

import baconetworks.npcbattlelimiter.NPCBattleLimiter;
import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResetBattleLimit implements Runnable {
    public void run() {
        final Game game = Sponge.getGame();

        LinkedHashMap<String, BattleLimitObject> BattleLimit = ConfigLoader.BattleLimitGet();
        NPCBattleLimiter.LOGGER.info("Reset Battle Limit schedule triggered!");

        for (Map.Entry<String, BattleLimitObject> BattleLimits : BattleLimit.entrySet()) {
            if (BattleLimits.getValue().GetLimit() != 0) {
                ConfigLoader.config.getNode(new Object[]{"BattleLimits", BattleLimits.getKey(), "BattleLimit"}).setValue(0);
                BattleLimits.getValue().SetLimit(0);
            }
        }
        if (game.getServer().getOnlinePlayers().size() != 0) {
            for (Player OnlinePlayer : game.getServer().getOnlinePlayers()) {
                OnlinePlayer.sendMessage(Text.of(TextColors.GREEN, "The daily battle limit has been reset!", Text.NEW_LINE, "This means that you can now gain money from trainers again!"));
            }
        }
        ConfigLoader.saveConfig();
    }
}



