package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.ElectricPowerMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ElectricPowerRepository extends CrudRepository<ElectricPowerMetrics, Long> {
    List<ElectricPowerMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);
    long deleteByUserChatId(Long userId);
    Optional<ElectricPowerMetrics> findByDateAddedAndUserChatId(Date dateAdded, Long userId);
}
