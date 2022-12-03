package HousingAndUtilitiesVisualizer.service;


import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.util.Comparator.comparing;

@Service
public class ChartService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MetricsService metricsService;
    @Autowired
    TimeService timeService;

    Logger log = LogManager.getLogger(ChartService.class);

    public File getChart(Long userId, Period period) {
        User user = userRepository.findByChatId(userId);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<MetricsType, List<?>> entry
                : metricsService.getMetricsForPeriod(period, user).entrySet()) {
            MetricsType metricsType = entry.getKey();
            List<Metrics> metricsList = (List<Metrics>) entry.getValue();

            metricsList.sort((o1,o2) -> o1.getDateAdded().compareTo(o2.getDateAdded()));
            for (Metrics metrics : metricsList) {
                if (metricsType == MetricsType.ELECTRIC_POWER) {
                    ElectricPowerMetrics electricMetrics = (ElectricPowerMetrics) metrics;

                    dataset.addValue(electricMetrics.getValueDay(),
                            "Электроэнергия день",
                            timeService.dateToStr(electricMetrics.getDateAdded()));
                    dataset.addValue(electricMetrics.getValueNight(),
                            "Электроэнергия ночь",
                            timeService.dateToStr(electricMetrics.getDateAdded()));
                    continue;

                }
                dataset.addValue(metrics.getValue(),
                        metricsType.label,
                        timeService.dateToStr(metrics.getDateAdded()));
            }

        }

        File result = null;
        try {
            result = drawChart(dataset, userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private File drawChart(DefaultCategoryDataset dataset, Long userId) throws IOException {
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Потребление ЖКУ", // Chart title
                "Дата", // X-Axis Label
                "Показание", // Y-Axis Label
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        lineChart.setAntiAlias(false);
        CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
        LineAndShapeRenderer r = (LineAndShapeRenderer) plot.getRenderer();
        r.setShapesVisible(true);
        File file = new File("data/charts/stat-" + userId +".png");
        ChartUtilities.saveChartAsPNG(file, lineChart, 900, 600);
        return file;

    }

    public void deleteFile(File file) {
        file.delete();
    }


}
