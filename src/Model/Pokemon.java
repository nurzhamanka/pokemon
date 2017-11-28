package Model;

public class Pokemon {
    private String name;
    private String area;
    private Trainer trainer;
    private String nickname;
    private int aggressiveness;
    private int stamina;

    // encountering a wild Pokemon
    public Pokemon(String name, int aggressiveness, int stamina, String area) {
        this.setName(name);
        this.setAggressiveness(aggressiveness);
        this.setStamina(stamina);
        this.setArea(area);
    }

    // taming a Pokemon
    public Pokemon(String name, Trainer trainer, String nickname) {
        this.setName(name);
        this.setTrainer(trainer);
        this.setNickname(nickname);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(int aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
