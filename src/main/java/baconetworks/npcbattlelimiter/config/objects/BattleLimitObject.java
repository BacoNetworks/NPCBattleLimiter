package baconetworks.npcbattlelimiter.config.objects;

import java.util.UUID;

public class BattleLimitObject {
    private UUID PlayerUUID;
    private int BattleLimit;

    public BattleLimitObject(String PlayerUUID, int BattleLimit) {
        this.PlayerUUID = UUID.fromString(PlayerUUID);
        this.BattleLimit = BattleLimit;
    }

    public UUID GetPlayerUUID() {
        return this.PlayerUUID;
    }

    public int GetLimit() {
        return this.BattleLimit;
    }

    public void SetLimit(int limit) {
        this.BattleLimit = limit;
    }

    public void increaseLimit(int limit) {
        this.BattleLimit += limit;
    }
}

