package HousingAndUtilitiesVisualizer;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.ColdWaterRepository;
import HousingAndUtilitiesVisualizer.repository.UserRepository;
import HousingAndUtilitiesVisualizer.service.ChartService;
import HousingAndUtilitiesVisualizer.service.MetricsService;
import HousingAndUtilitiesVisualizer.service.Period;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class DatabaseTest {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ColdWaterRepository coldWaterRepository;

    @Autowired
    ChartService chartService;

    @Test
    void saveMetricsTest() {
        User user = new User(1L, "some_address");
        userRepository.save(user);
        MetricsBuilder metricsBuilder = new MetricsBuilder((new ColdWaterMetrics(Calendar.getInstance().getTime(), 12345D, user)),null, null, null);
        metricsService.save(metricsBuilder);



    }

    @Test
    void findMetricsOfUserTest() {
        User user = new User(203298389L);
        //userRepository.save(user);
        System.out.println(metricsService.getMetricsForPeriod(Period.ALL_TIME, user));
        //metricsService.save(new ColdWaterMetrics(Calendar.getInstance().getTime(), 111D, user));



    }


    @Test
    void getMetricsForPeriodTest() {
        User user = new User(1L, "some_address");
        User user2 = new User(2L);
        userRepository.save(user);
        userRepository.save(user2);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        metricsService.save(new ColdWaterMetrics(calendar.getTime(), 123D, user));
        metricsService.save(new ColdWaterMetrics(calendar.getTime(), 123D, user2));

        calendar.add(Calendar.DATE, -2);

        System.out.println(metricsService.getMetricsForPeriod(Period.MONTH, user));
        System.out.println(metricsService.getMetricsForPeriod(Period.ALL_TIME, user2));
    }

    @Test
    void getMetricsForPeriodChartTest() {
        User user = new User(1L, "some_address");
        User user2 = new User(2L);
        userRepository.save(user);
        userRepository.save(user2);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        metricsService.save(new ColdWaterMetrics(calendar.getTime(), 123D, user));
        metricsService.save(new HotWaterMetrics(calendar.getTime(), 222D, user));
        metricsService.save(new HeatingMetrics(calendar.getTime(), 10D, user));
        metricsService.save(new ElectricPowerMetrics(calendar.getTime(), 1000D, 700D, user));
        //calendar.add(Calendar.DATE, -2);
        metricsService.save(new ColdWaterMetrics(calendar.getTime(), 321D, user));
        metricsService.save(new HotWaterMetrics(calendar.getTime(), 333D, user));
        metricsService.save(new HeatingMetrics(calendar.getTime(), 20D, user));
        metricsService.save(new ElectricPowerMetrics(calendar.getTime(), 1200D, 850D, user));

        calendar.add(Calendar.DATE, -3);
        metricsService.save(new ColdWaterMetrics(calendar.getTime(), 111D, user));
        metricsService.save(new HotWaterMetrics(calendar.getTime(), 444D, user));
        metricsService.save(new HeatingMetrics(calendar.getTime(), 30D, user));
        metricsService.save(new ElectricPowerMetrics(calendar.getTime(), 890D, 470D, user));


        //chartService.createDataset(user.getId(), Period.THREE_MONTHS);

    }

    @Test
    public void fillDatabaseWithData() {

        int moths = 23;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        User user = new User(203298389L);

        for (int i = 0; i < 24; i++) {
            double value = ThreadLocalRandom.current().nextDouble(200, 600);
            metricsService.save(new ColdWaterMetrics(calendar.getTime(), value, user));

            value = ThreadLocalRandom.current().nextDouble(200, 600);
            metricsService.save(new HotWaterMetrics(calendar.getTime(), value, user));

            value = ThreadLocalRandom.current().nextDouble(200, 600);
            metricsService.save(new HeatingMetrics(calendar.getTime(), value, user));

            value = ThreadLocalRandom.current().nextDouble(200, 600);
            double value1 = ThreadLocalRandom.current().nextDouble(100, 400);
            metricsService.save(new ElectricPowerMetrics(calendar.getTime(), value, value1, user));

            calendar.add(Calendar.MONTH, -1);
        }
    }


}
