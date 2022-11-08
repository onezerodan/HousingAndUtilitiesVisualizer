package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.HeatingMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeatingRepository extends CrudRepository<HeatingMetrics, Long> {
}
