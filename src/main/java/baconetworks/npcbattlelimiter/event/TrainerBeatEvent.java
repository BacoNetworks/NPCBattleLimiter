package baconetworks.npcbattlelimiter.event;

import baconetworks.npcbattlelimiter.NPCBattleLimiter;
import baconetworks.npcbattlelimiter.SharedFunctions;
import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class TrainerBeatEvent {
    @SubscribeEvent
    public void BeatTrainerEvent(BeatTrainerEvent event) throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        Player player = (Player) event.player;
        String uuid = player.getUniqueId().toString();

        NPCTrainer npc = event.trainer;

        LinkedHashMap<String, BattleLimitObject> BattleLimit = ConfigLoader.BattleLimitGet();

        if (BattleLimit.containsKey(uuid)) {
            BattleLimitObject object = BattleLimit.get(uuid);
            Field privateField = Class.forName("com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer").getDeclaredField("winMoney");

            if (!object.GetUsername().equals(player.getName())) {
                object.SetUsername(player.getName());
                ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "Username"}).setValue(player.getName());
            }

            if (object.GetLimit() >= ConfigLoader.getNode("MainConfig", "BattleLimit").getInt()) {
                privateField.setAccessible(true);
                privateField.setInt(npc, 0);
                String DisplayTime = SharedFunctions.GetTime();
                player.sendMessage(Text.of(TextColors.RED, "You did not receive any money from this battle due to", Text.NEW_LINE, "reaching the daily limit of " + ConfigLoader.getNode("MainConfig", "BattleLimit").getInt() + "! This will reset in", Text.NEW_LINE, "about" + DisplayTime));
            } else {
                object.increaseLimit(1);
                ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "BattleLimit"}).setValue(object.GetLimit());
                if (ConfigLoader.getNode("MainConfig", "LowerMax", "Enabled").getBoolean()) {
                    if (object.GetLimit() > ConfigLoader.getNode("MainConfig", "LowerMax", "Threshold").getInt()) {
                        int MaxMoney = ConfigLoader.getNode("MainConfig", "LowerMax", "GiveLimit").getInt();
                        if (npc.getWinMoney() > MaxMoney) {
                            privateField.setAccessible(true);
                            privateField.setInt(npc, MaxMoney);
                        }
                    }
                }
                ConfigLoader.saveConfig();
            }
        } else {
            ConfigLoader.values.add(player.getUniqueId().toString());
            ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "Username"}).setValue(player.getName());
            ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "BattleLimit"}).setValue(1);
            BattleLimitObject BattleLimited = new BattleLimitObject(
                    player.getName(),
                    1
            );
            BattleLimit.put(player.getUniqueId().toString(), BattleLimited);
        }
        ConfigLoader.saveConfig();
    }
}

