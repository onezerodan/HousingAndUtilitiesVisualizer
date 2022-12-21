package HousingAndUtilitiesVisualizer.service;


import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.*;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;


@Service
public class ChartService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MetricsService metricsService;

    Logger log = LogManager.getLogger(ChartService.class);

    public File getChart(Long userId, Period period) throws IOException, NotFoundException {
        String periodStr = "";
        switch (period) {
            case YEAR -> periodStr = "за 1 год";
            case ALL_TIME -> periodStr = "за всё время";
            case MONTH -> periodStr = "за 2 месяца";
            case SIX_MONTHS -> periodStr = "за полгода";
            case THREE_MONTHS -> periodStr = "за 3 месяца";
        }

        TimeSeriesCollection dataset = createDataset(userId, period);
        JFreeChart chart = drawChart(dataset, "Потребление ЖКУ за " + periodStr);
        return saveChart(chart, userId);
    }
    private TimeSeriesCollection createDataset(Long userId, Period period) throws NotFoundException {

        User user = userRepository.findByChatId(userId).orElseThrow(() -> new NotFoundException("User not found"));

        TimeSeries coldWaterSeries = new TimeSeries("Холодная вода");
        TimeSeries hotWaterSeries = new TimeSeries("Горячая вода");
        TimeSeries heatingSeries = new TimeSeries("Отопление");
        TimeSeries powerDaySeries = new TimeSeries("Электроэнергия день");
        TimeSeries powerNightSeries = new TimeSeries("Электроэнергия ночь");

        for (Metrics metrics : metricsService.getMetricsForPeriod(period, user)) {

            if (metrics instanceof ElectricPowerMetrics electricMetrics) {
                powerDaySeries.add(
                        new Day(electricMetrics.getDateAdded()), electricMetrics.getValueDay());
                powerNightSeries.add(
                        new Day(electricMetrics.getDateAdded()), electricMetrics.getValueNight());
                continue;
            }

            if (metrics instanceof ColdWaterMetrics) {
                coldWaterSeries.add(new Day(metrics.getDateAdded()), metrics.getValue());
            } else if (metrics instanceof  HotWaterMetrics) {
                hotWaterSeries.add(new Day(metrics.getDateAdded()), metrics.getValue());
            } else if (metrics instanceof HeatingMetrics) {
                heatingSeries.add(new Day(metrics.getDateAdded()), metrics.getValue());
            }
        }

        TimeSeriesCollection timeSeries = new TimeSeriesCollection();

        if (coldWaterSeries.getItemCount() > 0) timeSeries.addSeries(coldWaterSeries);
        if (hotWaterSeries.getItemCount() > 0) timeSeries.addSeries(hotWaterSeries);
        if (heatingSeries.getItemCount() > 0) timeSeries.addSeries(heatingSeries);
        if (powerDaySeries.getItemCount() > 0) timeSeries.addSeries(powerDaySeries);
        if (powerNightSeries.getItemCount() > 0) timeSeries.addSeries(powerNightSeries);

        timeSeries.setDomainIsPointsInTime(true);
        return timeSeries;
    }

    private JFreeChart drawChart(TimeSeriesCollection dataset, String title) throws IOException {



        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, // Chart title
                "Дата", // X-Axis Label
                "Показание", // Y-Axis Label
                dataset,
                true,
                true,
                false
        );

        XYPlot plot =  chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            String name = (String) dataset.getSeriesKey(i);
            int index = dataset.indexOf(name);
            switch (name) {
                case "Холодная вода" -> renderer.setSeriesPaint(index,Color.BLUE);
                case "Горячая вода" -> renderer.setSeriesPaint(index, Color.RED);
                case "Отопление" -> renderer.setSeriesPaint(index, Color.YELLOW);
                case "Электроэнергия день" -> renderer.setSeriesPaint(index, Color.GREEN);
                case "Электроэнергия ночь" -> renderer.setSeriesPaint(index, Color.CYAN);
            }
        }

        DateAxis dateAxis = new DateAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM yyyy", new Locale("ru")));
        dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
        dateAxis.setVerticalTickLabels(true);

        plot.setDomainAxis(dateAxis);

        chart.setAntiAlias(false);

        renderer.setBaseShapesVisible(true);
        renderer.setBaseLinesVisible(true);

        renderer.setAutoPopulateSeriesShape(false);
        renderer.setBaseShape(new Ellipse2D.Double(-2, -2, 5, 5));

        return chart;
    }

    public File saveChart(JFreeChart chart, Long userId) throws IOException {
        File file = new File("data/charts/stat-" + userId +".png");
        ChartUtilities.saveChartAsPNG(file, chart, 900, 600);
        return file;
    }

    public void deleteFile(File file) {
        file.delete();
    }


}
