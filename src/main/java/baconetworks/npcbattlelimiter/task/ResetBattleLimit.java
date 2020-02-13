package baconetworks.npcbattlelimiter.task;

import baconetworks.npcbattlelimiter.NPCBattleLimiter;
import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ResetBattleLimit implements Runnable {
    public void run() {
        int limit;
        UUID PlayerUUID;
        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Map<String, BattleLimitObject> BattleLimit = ConfigLoader.BattleLimitGet();
        NPCBattleLimiter.LOGGER.info("Reset Battle Limit schedule triggered!");

        for (BattleLimitObject BattleLimits : BattleLimit.values()) {
            limit = BattleLimits.GetLimit();
            PlayerUUID = BattleLimits.GetPlayerUUID();
            if (limit > 0) {
                ConfigLoader.config.getNode(new Object[]{"BattleLimits", PlayerUUID.toString(), "BattleLimit"}).setValue(0);
                BattleLimits.SetLimit(0);
                Optional<User> oUser = uss.get(PlayerUUID);
                if (oUser.isPresent()) {
                    User user = oUser.get();
                    Optional<Player> player = user.getPlayer();
                    if (player.isPresent()) {
                        Player PresentPlayer = player.get();
                        if (PresentPlayer.isOnline()) {
                            PresentPlayer.sendMessage(Text.of(TextColors.GREEN, "The daily battle limit has been reset!", Text.NEW_LINE, "This means that you can now gain money from trainers again!"));
                        }
                    }
                }
                ConfigLoader.saveConfig();
            }
        }
    }
}
