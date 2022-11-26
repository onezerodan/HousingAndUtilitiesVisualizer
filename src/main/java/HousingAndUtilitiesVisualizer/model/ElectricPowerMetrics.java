package HousingAndUtilitiesVisualizer.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "electric_power_metrics")
public class ElectricPowerMetrics extends Metrics {
    @Id
    @GeneratedValue
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyy")
    private Date dateAdded;

    @Column(name = "value_day")
    private Double valueDay;

    @Column(name = "value_night")
    private Double valueNight;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public ElectricPowerMetrics(Date dateAdded, Double valueDay, Double valueNight, User user) {
        this.dateAdded = dateAdded;
        this.valueDay = valueDay;
        this.valueNight = valueNight;
        this.user = user;
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

    public Double getValueDay() {
        return valueDay;
    }

    public void setValueDay(Double valueDay) {
        this.valueDay = valueDay;
    }

    public Double getValueNight() {
        return valueNight;
    }

    public void setValueNight(Double valueNight) {
        this.valueNight = valueNight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
