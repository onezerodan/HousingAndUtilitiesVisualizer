package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ColdWaterMetrics;
import HousingAndUtilitiesVisualizer.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ColdWaterRepository extends CrudRepository<ColdWaterMetrics, Long> {
    List<ColdWaterMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);
}
