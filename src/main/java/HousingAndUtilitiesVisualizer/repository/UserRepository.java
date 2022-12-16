package HousingAndUtilitiesVisualizer.repository;

import HousingAndUtilitiesVisualizer.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    //User findByChatId(Long chatId);
    //boolean existsByChatId(Long chatId);
    Optional<User> findByChatId(Long userId);
}
