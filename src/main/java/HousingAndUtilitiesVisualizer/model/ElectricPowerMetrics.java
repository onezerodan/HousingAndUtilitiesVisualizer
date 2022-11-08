package HousingAndUtilitiesVisualizer.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "electric_power_metrics")
public class ElectricPowerMetrics {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    @Column(name = "value_day")
    private Long valueDay;

    @Column(name = "value_night")
    private Long valueNight;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public ElectricPowerMetrics(Date dateAdded, Long valueDay, Long valueNight) {
        this.dateAdded = dateAdded;
        this.valueDay = valueDay;
        this.valueNight = valueNight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Long getValueDay() {
        return valueDay;
    }

    public void setValueDay(Long valueDay) {
        this.valueDay = valueDay;
    }

    public Long getValueNight() {
        return valueNight;
    }

    public void setValueNight(Long valueNight) {
        this.valueNight = valueNight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
