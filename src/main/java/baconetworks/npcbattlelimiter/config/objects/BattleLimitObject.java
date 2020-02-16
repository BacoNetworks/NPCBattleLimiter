package baconetworks.npcbattlelimiter.config.objects;

public class BattleLimitObject {
    private String Username;
    private int BattleLimit;

    public BattleLimitObject(String Username, int BattleLimit) {
        this.Username = Username;
        this.BattleLimit = BattleLimit;
    }

    public String GetUsername() {
        return this.Username;
    }

    public int GetLimit() {
        return this.BattleLimit;
    }

    public void SetLimit(int limit) {
        this.BattleLimit = limit;
    }

    public void SetUsername(String username) {
        this.Username = username;
    }

    public void increaseLimit(int limit) {
        this.BattleLimit += limit;
    }
}

