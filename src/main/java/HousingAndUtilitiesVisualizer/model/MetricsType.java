package HousingAndUtilitiesVisualizer.model;

public enum MetricsType {
    COLD_WATER ("Холодная вода", 0),
    HOT_WATER ("Горячая вода", 1),
    HEATING ("Отопление", 2),
    ELECTRIC_POWER ("Электроэнергия", 3);

    public final String label;
    public final int index;

    private MetricsType(String label, int index) {
        this.label = label;
        this.index = index;
    }

}
