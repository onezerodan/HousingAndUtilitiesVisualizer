package HousingAndUtilitiesVisualizer.builder;

import HousingAndUtilitiesVisualizer.model.*;

/**
 * The type Metrics builder.
 */
public class MetricsBuilder {
    private User user;
    private ColdWaterMetrics coldWaterMetrics;
    private HotWaterMetrics hotWaterMetrics;
    private ElectricPowerMetrics electricPowerMetrics;
    private HeatingMetrics heatingMetrics;


    /**
     * Instantiates a new Metrics builder with all metrics included.
     *
     * @param coldWaterMetrics     the cold water metrics
     * @param hotWaterMetrics      the hot water metrics
     * @param electricPowerMetrics the electric power metrics
     * @param heatingMetrics       the heating metrics
     */
    public MetricsBuilder(ColdWaterMetrics coldWaterMetrics, HotWaterMetrics hotWaterMetrics, ElectricPowerMetrics electricPowerMetrics, HeatingMetrics heatingMetrics) {
        this.coldWaterMetrics = coldWaterMetrics;
        this.hotWaterMetrics = hotWaterMetrics;
        this.electricPowerMetrics = electricPowerMetrics;
        this.heatingMetrics = heatingMetrics;
    }

    public ColdWaterMetrics getColdWaterMetrics() {
        return coldWaterMetrics;
    }

    public void setColdWaterMetrics(ColdWaterMetrics coldWaterMetrics) {
        this.coldWaterMetrics = coldWaterMetrics;
    }

    public HotWaterMetrics getHotWaterMetrics() {
        return hotWaterMetrics;
    }

    public void setHotWaterMetrics(HotWaterMetrics hotWaterMetrics) {
        this.hotWaterMetrics = hotWaterMetrics;
    }

    public ElectricPowerMetrics getElectricPowerMetrics() {
        return electricPowerMetrics;
    }

    public void setElectricPowerMetrics(ElectricPowerMetrics electricPowerMetrics) {
        this.electricPowerMetrics = electricPowerMetrics;
    }

    public HeatingMetrics getHeatingMetrics() {
        return heatingMetrics;
    }

    public void setHeatingMetrics(HeatingMetrics heatingMetrics) {
        this.heatingMetrics = heatingMetrics;
    }
}
