/* ----------------------
 * HighLowChartDemo3.java
 * ----------------------
 * (C) Copyright 2007, 2008, by Object Refinery Limited.
 *
 */
package petrasys.charts.experiments;

import java.awt.BasicStroke;
import java.awt.Color;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a high-low-open-close chart with a candlestick chart overlaid
 * against a second range axis.
 */
public class HighLowChartDemo3 extends ApplicationFrame {

  ChartPanel chartPanel;
  Crosshair crosshairDomain;
  Crosshair crosshairRange;

  /**
   * A demonstration application showing a high-low-open-close chart.
   *
   * @param title  the frame title.
   */
  public HighLowChartDemo3(String title) {
    super(title);
    chartPanel = createDemoPanel();
    CrosshairOverlay overlay = new CrosshairOverlay();
    crosshairDomain = new Crosshair(new Day(25, 9, 2007).getFirstMillisecond());
    crosshairDomain.setPaint(Color.BLACK);
    crosshairRange = new Crosshair(50.0);
    crosshairRange.setPaint(Color.BLACK);
    overlay.addDomainCrosshair(crosshairDomain);
    overlay.addRangeCrosshair(crosshairRange);
    chartPanel.addOverlay(overlay);
    crosshairDomain.setLabelVisible(true);
    crosshairDomain.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
    crosshairDomain.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
    crosshairRange.setLabelVisible(true);
    crosshairRange.setLabelBackgroundPaint(new Color(255, 255, 0, 100));
    chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
    setContentPane(chartPanel);
  }

  /**
   * Creates a sample high low dataset.
   *
   * @return a sample high low dataset.
   */
  public OHLCDataset createDataset1() {

    OHLCSeries s1 = new OHLCSeries("Series 1");
    s1.add(new Day(24, 9, 2007), 50.5, 53.2, 49.8, 50.1);
    s1.add(new Day(25, 9, 2007), 50.2, 51.2, 47.8, 48.1);
    s1.add(new Day(26, 9, 2007), 48.0, 49.2, 45.3, 47.4);
    s1.add(new Day(27, 9, 2007), 47.5, 48.3, 46.8, 46.8);
    s1.add(new Day(28, 9, 2007), 46.6, 47.0, 45.1, 46.0);
    s1.add(new Day(1, 10, 2007), 46.6, 47.0, 45.1, 46.0);
    s1.add(new Day(2, 10, 2007), 47.5, 48.3, 46.8, 46.8);
    s1.add(new Day(3, 10, 2007), 48.0, 49.2, 45.3, 47.4);
    s1.add(new Day(4, 10, 2007), 50.2, 51.2, 47.8, 48.1);
    s1.add(new Day(5, 10, 2007), 50.5, 53.2, 49.8, 50.1);
    OHLCSeriesCollection dataset = new OHLCSeriesCollection();
    dataset.addSeries(s1);
    return dataset;
  }

  /**
   * Creates a sample high low dataset.
   *
   * @return a sample high low dataset.
   */
  public static OHLCDataset createDataset2() {

    OHLCSeries s2 = new OHLCSeries("Series 2");
    s2.add(new Day(24, 9, 2007), 5.5, 6.2, 4.8, 5.9);
    s2.add(new Day(25, 9, 2007), 6.0, 6.9, 6.0, 6.7);
    s2.add(new Day(26, 9, 2007), 6.8, 7.5, 6.4, 7.1);
    s2.add(new Day(27, 9, 2007), 7.2, 8.2, 7.0, 7.9);
    s2.add(new Day(28, 9, 2007), 7.8, 8.5, 7.7, 8.2);
    s2.add(new Day(1, 10, 2007), 8.2, 8.5, 7.7, 7.8);
    s2.add(new Day(2, 10, 2007), 7.9, 8.2, 7.0, 7.2);
    s2.add(new Day(3, 10, 2007), 7.1, 7.5, 6.4, 6.8);
    s2.add(new Day(4, 10, 2007), 6.0, 6.9, 6.0, 6.7);
    s2.add(new Day(5, 10, 2007), 5.5, 6.2, 4.8, 5.9);
    OHLCSeriesCollection dataset = new OHLCSeriesCollection();
    dataset.addSeries(s2);
    return dataset;
  }

  /**
   * Creates a sample chart.
   *
   * @param dataset  a dataset.
   *
   * @return a sample chart.
   */
  private JFreeChart createChart(OHLCDataset dataset) {

    JFreeChart chart = ChartFactory.createHighLowChart(
            "OHLC Demo 3",
            "Time",
            "Price",
            dataset,
            true);
    XYPlot plot = (XYPlot) chart.getPlot();
    HighLowRenderer renderer = (HighLowRenderer) plot.getRenderer();
    renderer.setBaseStroke(new BasicStroke(20.0f));
    renderer.setSeriesPaint(0, Color.blue);
    //renderer.setSeriesStroke(0, new BasicStroke(10.0f));

    DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
    yAxis1.setAutoRangeIncludesZero(false);

    NumberAxis yAxis2 = new NumberAxis("Price 2");
    yAxis2.setAutoRangeIncludesZero(false);
    plot.setRangeAxis(1, yAxis2);
    plot.setDataset(1, createDataset2());
    CandlestickRenderer cr = new CandlestickRenderer(10.0);
    cr.setBaseStroke(new BasicStroke(10.0f));
    //cr.setSeriesStroke(0, new BasicStroke(10.0f));
    //cr.setSeriesOutlineStroke(1, new BasicStroke(10.0f));

    plot.setRenderer(1, cr);
    plot.getRenderer(1).setBaseStroke(new BasicStroke(10.0f));
    plot.mapDatasetToRangeAxis(1, 1);
    plot.setRangeCrosshairStroke(new BasicStroke(1));
    plot.setDomainCrosshairStroke(new BasicStroke(1));
    plot.setDomainCrosshairLockedOnData(true);
    plot.setRangeCrosshairLockedOnData(true);
    plot.setDomainCrosshairVisible(true);
    plot.setRangeCrosshairVisible(true);
    plot.setDomainPannable(true);

    //ChartUtilities.applyCurrentTheme(chart);
    return chart;

  }

  /**
   * Creates a panel for the demo (used by SuperDemo.java).
   *
   * @return A panel.
   */
  public ChartPanel createDemoPanel() {
    JFreeChart chart = createChart(createDataset1());

    return new ChartPanel(chart);
  }

  /**
   * Starting point for the demonstration application.
   *
   * @param args  ignored.
   */
  public static void main(String[] args) {
    HighLowChartDemo3 demo = new HighLowChartDemo3(
            "JFreeChart: HighLowChartDemo3.java");
    demo.pack();
    RefineryUtilities.centerFrameOnScreen(demo);
    demo.setVisible(true);
  }
}
