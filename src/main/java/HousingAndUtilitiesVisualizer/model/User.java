package HousingAndUtilitiesVisualizer.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long chatId;

    private String name;

    private String address;

    @OneToMany(mappedBy="user")
    private Set<ColdWaterMetrics> coldWaterMetrics;

    @OneToMany(mappedBy="user")
    private Set<HotWaterMetrics> hotWaterMetrics;

    @OneToMany(mappedBy="user")
    private Set<HeatingMetrics> heatingMetrics;

    @OneToMany(mappedBy="user")
    private Set<ElectricPowerMetrics> electricPowerMetrics;

    public User(Long id, String name, String address) {
        this.chatId = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return chatId;
    }

    public void setId(Long id) {
        this.chatId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
