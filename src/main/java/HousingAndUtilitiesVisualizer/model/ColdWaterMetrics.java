package HousingAndUtilitiesVisualizer.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cold_water_metrics")
public class ColdWaterMetrics extends CommonMetrics {


    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    private Long value;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public ColdWaterMetrics(Date dateAdded, Long value, User user) {
        this.dateAdded = dateAdded;
        this.value = value;
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
