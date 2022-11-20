package HousingAndUtilitiesVisualizer.service;

import HousingAndUtilitiesVisualizer.builder.MetricsBuilder;
import HousingAndUtilitiesVisualizer.model.ColdWaterMetrics;
import HousingAndUtilitiesVisualizer.model.ElectricPowerMetrics;
import HousingAndUtilitiesVisualizer.model.HeatingMetrics;
import HousingAndUtilitiesVisualizer.model.HotWaterMetrics;
import HousingAndUtilitiesVisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

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


        ColdWaterMetrics coldWaterMetrics = metricsBuilder.getColdWaterMetrics();
        HotWaterMetrics hotWaterMetrics = metricsBuilder.getHotWaterMetrics();
        HeatingMetrics heatingMetrics = metricsBuilder.getHeatingMetrics();
        ElectricPowerMetrics electricPowerMetrics = metricsBuilder.getElectricPowerMetrics();

        if (coldWaterMetrics != null) coldWaterRepository.save(coldWaterMetrics);
        if (hotWaterMetrics != null) hotWaterRepository.save(hotWaterMetrics);
        if (heatingMetrics != null) heatingRepository.save(heatingMetrics);
        if (electricPowerMetrics != null) electricPowerRepository.save(electricPowerMetrics);
    }
}
