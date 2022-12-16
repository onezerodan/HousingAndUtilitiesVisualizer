package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.HeatingMetrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeatingRepository extends CrudRepository<HeatingMetrics, Long> {
    List<HeatingMetrics> findByDateAddedBetweenAndUserChatId(Date start, Date end, Long userId);
    long deleteByUserChatId(Long userId);
    Optional<HeatingMetrics> findByDateAddedAndUserChatId(Date dateAdded, Long userId);
}

