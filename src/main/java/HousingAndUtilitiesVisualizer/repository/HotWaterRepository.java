package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.HotWaterMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotWaterRepository extends CrudRepository<HotWaterMetrics, Long> {
}
