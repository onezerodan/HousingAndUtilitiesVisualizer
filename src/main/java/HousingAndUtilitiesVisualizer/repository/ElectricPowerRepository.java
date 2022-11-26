package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ElectricPowerMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ElectricPowerRepository extends CrudRepository<ElectricPowerMetrics, Long> {
    List<ElectricPowerMetrics> findByDateAddedBetween(Date start, Date end);

}
