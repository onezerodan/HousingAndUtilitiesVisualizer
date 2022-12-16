package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.HotWaterMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotWaterRepository extends CrudRepository<HotWaterMetrics, Long> {
    List<HotWaterMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);
    long deleteByUserChatId(Long userId);
    Optional<HotWaterMetrics> findByDateAddedAndUserChatId(Date dateAdded, Long userId);
}
