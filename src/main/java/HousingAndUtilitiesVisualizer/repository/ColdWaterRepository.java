package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ColdWaterMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColdWaterRepository extends CrudRepository<ColdWaterMetrics, Long> {

}
