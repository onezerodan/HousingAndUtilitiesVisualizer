package HousingAndUtilitiesVisualizer.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cold_water_metrics")
public class ColdWaterMetrics extends Metrics {


    @Id
    @GeneratedValue
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyy")
    private Date dateAdded;

    private Double value;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public ColdWaterMetrics(Date dateAdded, Double value, User user) {
        this.dateAdded = dateAdded;
        this.value = value;
        this.user = user;
    }

    public ColdWaterMetrics() {
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
