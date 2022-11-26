package HousingAndUtilitiesVisualizer.service;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class MetricsService {

    @Autowired
    ColdWaterRepository coldWaterRepository;
    @Autowired
    HotWaterRepository hotWaterRepository;
    @Autowired
    HeatingRepository heatingRepository;
    @Autowired
    ElectricPowerRepository electricPowerRepository;

    @Autowired
    TimeService timeService;


    public void save(MetricsBuilder metricsBuilder) {


        ColdWaterMetrics coldWaterMetrics = metricsBuilder.getColdWaterMetrics();
        HotWaterMetrics hotWaterMetrics = metricsBuilder.getHotWaterMetrics();
        HeatingMetrics heatingMetrics = metricsBuilder.getHeatingMetrics();
        ElectricPowerMetrics electricPowerMetrics = metricsBuilder.getElectricPowerMetrics();

        if (coldWaterMetrics != null) coldWaterRepository.save(coldWaterMetrics);
        if (hotWaterMetrics != null) hotWaterRepository.save(hotWaterMetrics);
        if (heatingMetrics != null) heatingRepository.save(heatingMetrics);
        if (electricPowerMetrics != null) electricPowerRepository.save(electricPowerMetrics);
    }

    public void save(Metrics metrics) {
        if (metrics instanceof ColdWaterMetrics) coldWaterRepository.save((ColdWaterMetrics) metrics);
        else if (metrics instanceof HotWaterMetrics) hotWaterRepository.save((HotWaterMetrics) metrics);
        else if (metrics instanceof HeatingMetrics) heatingRepository.save((HeatingMetrics) metrics);
        else if (metrics instanceof ElectricPowerMetrics) electricPowerRepository.save((ElectricPowerMetrics) metrics);
    }

    public Map<MetricsEnum, List<?>> getMetricsForPeriod(Period period, User user) {
        Map<MetricsEnum, List<?>> result = new HashMap<>();

        Calendar calendar = Calendar.getInstance();

        Date start = null;
        Date end = end = timeService.getCurrentDate();
        calendar.setTime(end);

        switch (period) {
            case WEEK -> {
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                start = calendar.getTime();
            }

            case MONTH -> {
                calendar.add(Calendar.MONTH, -1);
                start = calendar.getTime();
            }

            case ALL_TIME -> {
                calendar.add(Calendar.YEAR, -100);
                start = calendar.getTime();
            }

            case TWO_WEEKS -> {
                calendar.add(Calendar.WEEK_OF_MONTH, -2);
                start = calendar.getTime();
            }
        }

        result.put(MetricsEnum.COLD_WATER, coldWaterRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.put(MetricsEnum.HOT_WATER, hotWaterRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.put(MetricsEnum.HEATING, heatingRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.put(MetricsEnum.ELECTRIC_POWER, electricPowerRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));

        return result;
    }

}
