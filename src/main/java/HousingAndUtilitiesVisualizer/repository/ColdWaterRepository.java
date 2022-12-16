package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ColdWaterMetrics;
import HousingAndUtilitiesVisualizer.model.Metrics;
import HousingAndUtilitiesVisualizer.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColdWaterRepository extends CrudRepository<ColdWaterMetrics, Long> {
    List<ColdWaterMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);
    long deleteByUserChatId(Long userId);
    Optional<ColdWaterMetrics> findByDateAddedAndUserChatId(Date dateAdded, Long userId);
}
