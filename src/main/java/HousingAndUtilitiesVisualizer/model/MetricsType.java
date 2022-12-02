package HousingAndUtilitiesVisualizer.model;

public enum MetricsType {
    COLD_WATER ("Холодная вода"),
    HOT_WATER ("Горячая вода"),
    HEATING ("Отопление"),
    ELECTRIC_POWER ("Электроэнергия");

    public final String label;

    private MetricsType(String label) {
        this.label = label;
    }
}
