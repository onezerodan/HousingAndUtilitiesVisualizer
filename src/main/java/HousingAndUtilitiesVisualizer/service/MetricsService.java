package HousingAndUtilitiesVisualizer.service;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class MetricsService {

    @Autowired
    private ColdWaterRepository coldWaterRepository;
    @Autowired
    private HotWaterRepository hotWaterRepository;
    @Autowired
    private HeatingRepository heatingRepository;
    @Autowired
    private ElectricPowerRepository electricPowerRepository;

    @Autowired
    private TimeService timeService;


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
        if (metrics instanceof ColdWaterMetrics) {

            coldWaterRepository
                    .findByDateAddedAndUserChatId(metrics.getDateAdded(), metrics.getUser().getId())
                    .map(coldWaterMetrics ->
                    {
                        coldWaterMetrics.setValue(metrics.getValue());
                        return coldWaterRepository.save(coldWaterMetrics);
                    })
                    .orElseGet(() -> coldWaterRepository.save((ColdWaterMetrics) metrics));
            //if (coldWaterMetrics == null) coldWaterMetrics = (ColdWaterMetrics) metrics;
            //else coldWaterMetrics.setValue(metrics.getValue());
            //coldWaterRepository.save(coldWaterMetrics);

        }
        else if (metrics instanceof HotWaterMetrics) {
            HotWaterMetrics hotWaterMetrics = hotWaterRepository
                    .findByDateAddedAndUserChatId(metrics.getDateAdded(), metrics.getUser().getId())
                    .orElse(null);
            if (hotWaterMetrics == null) hotWaterMetrics = (HotWaterMetrics) metrics;
            else hotWaterMetrics.setValue(metrics.getValue());
            hotWaterRepository.save(hotWaterMetrics);
        }
        else if (metrics instanceof HeatingMetrics) {
            HeatingMetrics heatingMetrics = heatingRepository
                    .findByDateAddedAndUserChatId(metrics.getDateAdded(), metrics.getUser().getId())
                    .orElse(null);
            if (heatingMetrics == null) heatingMetrics = (HeatingMetrics) metrics;
            else heatingMetrics.setValue(metrics.getValue());
            heatingRepository.save(heatingMetrics);
        }
        else if (metrics instanceof ElectricPowerMetrics) {
            ElectricPowerMetrics electricPowerMetrics = electricPowerRepository
                    .findByDateAddedAndUserChatId(metrics.getDateAdded(), metrics.getUser().getId())
                    .orElse(null);
            if (electricPowerMetrics == null) electricPowerMetrics = (ElectricPowerMetrics) metrics;
            else electricPowerMetrics.setValue(metrics.getValue());
            electricPowerRepository.save(electricPowerMetrics);
        }
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        coldWaterRepository.deleteByUserChatId(userId);
        hotWaterRepository.deleteByUserChatId(userId);
        heatingRepository.deleteByUserChatId(userId);
        electricPowerRepository.deleteByUserChatId(userId);
    }

    public List<Metrics> getMetricsForPeriod(Period period, User user) {
        List<Metrics> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        Date end  = timeService.getCurrentDate();

        calendar.setTime(end);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        switch (period) {
            case MONTH -> {
                calendar.add(Calendar.MONTH, -1);
            }

            case THREE_MONTHS -> {
                calendar.add(Calendar.MONTH, -2);
            }

            case SIX_MONTHS -> {
                calendar.add(Calendar.MONTH, -5);
            }

            case YEAR -> {
                calendar.add(Calendar.YEAR, -1);
            }

            case ALL_TIME -> {
                calendar.setTime(new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime());
            }
        }

        Date start = calendar.getTime();

        result.addAll(coldWaterRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.addAll(hotWaterRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.addAll(heatingRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));
        result.addAll(electricPowerRepository.findByDateAddedBetweenAndUserChatId(start, end, user.getId()));

        return result;
    }

}
