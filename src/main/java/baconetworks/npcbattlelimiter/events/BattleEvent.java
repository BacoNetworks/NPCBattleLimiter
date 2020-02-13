package baconetworks.npcbattlelimiter.events;

import baconetworks.npcbattlelimiter.config.ConfigLoader;
import baconetworks.npcbattlelimiter.config.objects.BattleLimitObject;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BattleEvent {
    @SubscribeEvent
    public void BeatTrainerEvent(BeatTrainerEvent event) throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        Player player = (Player) event.player;
        NPCTrainer npc = event.trainer;
        boolean foundPlayer = false;

        Map<String, BattleLimitObject> BattleLimit = ConfigLoader.BattleLimitGet();
        for (BattleLimitObject limit : BattleLimit.values()) {
            if (limit.GetPlayerUUID().equals(player.getUniqueId())) {
                foundPlayer = true;
                if (limit.GetLimit() >= ConfigLoader.getNode("MainConfig", "BattleLimit").getInt()) {
                    Field privateField = Class.forName("com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer").getDeclaredField("winMoney");
                    privateField.setAccessible(true);
                    privateField.setInt(npc, 0);

                    //Ugly stuff below
                    LocalDateTime localNow = LocalDateTime.now();
                    ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
                    ZonedDateTime zonedNext = zonedNow.withHour(12).withMinute(0).withSecond(0);

                    if (zonedNow.compareTo(zonedNext) > 0) {
                        zonedNext = zonedNext.plusDays(1);
                    }

                    long initalDelay = Duration.between(zonedNow, zonedNext).getSeconds();

                    int day = (int) TimeUnit.SECONDS.toDays(initalDelay);
                    int hours = (int) (TimeUnit.SECONDS.toHours(initalDelay) - (day * 24));
                    int minutes = (int) (TimeUnit.SECONDS.toMinutes(initalDelay) - (TimeUnit.SECONDS.toHours(initalDelay) * 60));
                    int seconds = (int) (TimeUnit.SECONDS.toSeconds(initalDelay) - (TimeUnit.SECONDS.toMinutes(initalDelay) * 60));

                    List<Text> texts = new ArrayList<>();

                    //Blank line
                    texts.add(Text.builder()
                            .append(Text.builder()
                                    .append(Text.of(""))
                                    .build())
                            .build());

                    //We append the actual time.
                    String time = " {h}{m}{s}.";
                    String displayedtime = time.replaceAll("\\{h}", hours + " hour" + (hours != 1 ? "s " : " "))
                            .replaceAll("\\{m}", minutes + " minute" + (minutes != 1 ? "s " : " "))
                            .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
                    if (hours <= 0) {
                        displayedtime = time.replaceAll("\\{h}", "")
                                .replaceAll("\\{m}", minutes + " minute" + (minutes != 1 ? "s " : " "))
                                .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
                    }
                    if (minutes <= 0) {
                        displayedtime = time.replaceAll("\\{h}", "")
                                .replaceAll("\\{m}", "")
                                .replaceAll("\\{s}", seconds + " second" + (seconds != 1 ? "s" : ""));
                    }
                    player.sendMessage(Text.of(TextColors.RED, "You did not receive any money from this battle due to", Text.NEW_LINE, "reaching the daily limit of " + ConfigLoader.getNode("MainConfig", "BattleLimit").getInt() + "! This will reset in", Text.NEW_LINE, "about" + displayedtime));
                } else {
                    limit.increaseLimit(1);
                    ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "BattleLimit"}).setValue(limit.GetLimit());
                    ConfigLoader.saveConfig();
                }
                break;
            }
        }
        if (!foundPlayer) {
            ConfigLoader.values.add(player.getUniqueId().toString());
            ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "UUID"}).setValue(player.getUniqueId().toString());
            ConfigLoader.config.getNode(new Object[]{"BattleLimits", player.getUniqueId().toString(), "BattleLimit"}).setValue(1);
            BattleLimitObject BattleLimited = new BattleLimitObject(
                    player.getUniqueId().toString(),
                    1
            );
            BattleLimit.put(player.getUniqueId().toString().split(" ")[0], BattleLimited);
            ConfigLoader.saveConfig();
        }
    }
}

