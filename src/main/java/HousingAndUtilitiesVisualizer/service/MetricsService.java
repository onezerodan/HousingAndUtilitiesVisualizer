package HousingAndUtilitiesVisualizer.service;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public void save(MetricsBuilder metricsBuilder) {
        coldWaterRepository.save(metricsBuilder.getColdWaterMetrics());
        hotWaterRepository.save(metricsBuilder.getHotWaterMetrics());
        heatingRepository.save(metricsBuilder.getHeatingMetrics());
        electricPowerRepository.save(metricsBuilder.getElectricPowerMetrics());
    }
}
