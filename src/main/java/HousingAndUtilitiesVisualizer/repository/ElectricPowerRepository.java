package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ElectricPowerMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricPowerRepository extends CrudRepository<ElectricPowerMetrics, Long> {
}
