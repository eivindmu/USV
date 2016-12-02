/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author vegard
 * Klasse som lager et trendplott basert på sendte verdier
 */
public class TrendPlot {
  
    private ChartPanel label;
    private TimeSeries timeSeries;
    private String plotName;
 

    /**
     * Konstruktøren kaller initialize()
     *
     * @param plotName
     * 
     */
    public TrendPlot(String data, String plotName) {

        this.plotName = plotName;
        timeSeries = new TimeSeries(data, Millisecond.class);

        initialize();
    }

    /**
     * initialiser plottet
     */
    private void initialize() {
        TimeSeriesCollection dataset = new TimeSeriesCollection(timeSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(plotName,
                "Time",
                "Error Value",
                dataset,
                true,
                true,
                false);

        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(20000.0);
        label = new ChartPanel(chart);
    }

    /**
     * Oppdaterer plottet
     * @param errorValue
     */

    public void updatePlot(double errorValue) {
        //metode i timeseries som oppdaterer plottet
        timeSeries.addOrUpdate(new Millisecond(), errorValue);

    }

    /**
     * Getter som brukes av GUI for å hente plottet
     *
     * @return
     */
    public ChartPanel getChartPanel() {
        return label;
    }

}

