package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.HotWaterMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HotWaterRepository extends CrudRepository<HotWaterMetrics, Long> {
    List<HotWaterMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);

}
