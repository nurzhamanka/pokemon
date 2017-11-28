package Model;

public class Pokemon {
    private String name;
    private int aggressiveness;
    private int stamina;
    private String area;
    private String trainer;
    private String nickname;
    private int date_time;

    void Pokemon(int date_time, String name, int aggressiveness, int stamina, String area) {
        this.setDate_time(date_time);
        this.setName(name);
        this.setAggressiveness(aggressiveness);
        this.setStamina(stamina);
        this.setArea(area);
    }

    void Pokemon(String name, String trainer, String nickname) {
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

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDate_time() {
        return date_time;
    }

    public void setDate_time(int date_time) {
        this.date_time = date_time;
    }
}
