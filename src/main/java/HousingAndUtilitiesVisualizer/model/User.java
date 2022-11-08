package HousingAndUtilitiesVisualizer.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long chatId;

    private String address;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ColdWaterMetrics> coldWaterMetrics;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<HotWaterMetrics> hotWaterMetrics;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<HeatingMetrics> heatingMetrics;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ElectricPowerMetrics> electricPowerMetrics;

    public User(Long id, String address) {
        this.chatId = id;
        this.address = address;
    }

    public User() {
    }

    public Long getId() {
        return chatId;
    }

    public void setId(Long id) {
        this.chatId = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
