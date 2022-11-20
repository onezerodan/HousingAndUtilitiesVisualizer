package HousingAndUtilitiesVisualizer;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.model.ColdWaterMetrics;
import HousingAndUtilitiesVisualizer.model.User;
import HousingAndUtilitiesVisualizer.repository.UserRepository;
import HousingAndUtilitiesVisualizer.service.MetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;

@SpringBootTest
public class DatabaseTest {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveMetricsTest() {
        User user = new User(1L, "some_address");
        userRepository.save(user);
        MetricsBuilder metricsBuilder = new MetricsBuilder((new ColdWaterMetrics(Calendar.getInstance().getTime(), 12345L, user)),null, null, null);
        metricsService.save(metricsBuilder);

    }
}
