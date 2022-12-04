package dsd.codebenders.tournament_app.responses;

public class LoadResponse {

    private Integer battleground;
    private Integer melee;
    private Integer total;

    public Integer getBattleground() {
        return battleground;
    }

    public Integer getMelee() {
        return melee;
    }

    public Integer getTotal() {
        return total;
    }

    public void setBattleground(Integer battleground) {
        this.battleground = battleground;
    }

    public void setMelee(Integer melee) {
        this.melee = melee;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
