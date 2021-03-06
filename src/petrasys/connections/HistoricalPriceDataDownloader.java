package petrasys.connections;

import com.ib.client.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.*;
import java.util.*;
import javax.swing.JTable;
import petrasys.ContinuousContractDlg;
import petrasys.PeTraSys;
import petrasys.entities.FuturesContractDetails;
import petrasys.utils.BarSize;
import petrasys.utils.MsgBox;
import petrasys.instruments.PriceBar;
import petrasys.instruments.QuoteHistory;
import petrasys.utils.ContractFactory;
import petrasys.utils.DBops;

/**
 *
 * Retrieves historical price and volume data for a specified security and saves
 * it as date- and time-stamped OHLCV price bars. Up to one year of intraday
 * historical data can be downloaded for stocks, futures, forex, and indices.
 */
public class HistoricalPriceDataDownloader implements Runnable {

  /*
   * rpc - 3/2/10 10:59 AM See pacing violations in IB-TWS Reference
   * "Do not make more than 60 historical data requests in any ten-minute period."
   * so, 6 a minute, or every 10 seconds is ok. add 100 millis for buffer.
   */
  private static final int MAX_REQUEST_FREQUENCY_MILLIS = 10100;
  private static final String lineSep = System.getProperty("line.separator");
  private int tickerId;
  //private Trader trader;
  //private Report eventReport;
  private String fileName;
  private BarSize barSize;
  private QuoteHistory qh;
  private List<PriceBar> priceBars;
  //private BackDataDialog backDataDialog;
  private ContinuousContractDlg ccdlg;
  private Contract contract;
  private PrintWriter writer;
  private boolean rthOnly;
  private boolean firstBarReached;
  private boolean isCancelled;
  private Calendar firstDate, lastDate;
  private Connection quotes1minConnection;
  private PreparedStatement stmtForQuotes;
  private MySocket socket;
  private long lastRequestTime;
  private HistoricalFromTWS histFromTWS;
  private boolean guard;
  FuturesContractDetails fcd;
  List<petrasys.entities.FuturesContractDetails> futuresContractDetailsList;
  JTable ContractInfoTable;

  public HistoricalPriceDataDownloader(ContinuousContractDlg ccdlg,
          List<FuturesContractDetails> futuresContractDetailsList, JTable ContractInfoTable) {
    this.ccdlg = ccdlg;
    this.fcd = fcd;
    this.futuresContractDetailsList = futuresContractDetailsList;
    this.ContractInfoTable = ContractInfoTable;
    histFromTWS = new HistoricalFromTWS();
    qh = new QuoteHistory();
  }

  public HistoricalPriceDataDownloader(HistoricalFromTWS histFrom) {
    try {
      qh = new QuoteHistory();
      histFromTWS = histFrom;
      ccdlg = null;
      connect();
      int i = 1;
    } catch (Exception ex) {
      MsgBox.err2(ex);
    }
  }

  public HistoricalPriceDataDownloader(HistoricalFromTWS histFrom, MySocket socket) {
    try {
      qh = new QuoteHistory();
      histFromTWS = histFrom;
      ccdlg = null;
      this.socket = socket;
      int i = 1;
    } catch (Exception ex) {
      MsgBox.err2(ex);
    }
  }

  private synchronized void blockOnGuard() {
    //This guard only loops once for each special event, which may not
    //be the event we're waiting for.
    while (guard) {
      try {
        wait();
        int i = 1;
      } catch (InterruptedException e) {
      }
    }
  }

  public synchronized void setGuard() {
    guard = true;
  }

  public synchronized void releaseGuard() {
    guard = false;
    notifyAll();
  }

  public HistoricalFromTWS getHistFromTWS() {
    return histFromTWS;
  }

  public void setHistFromTWS(HistoricalFromTWS histIn) {
    this.histFromTWS = histIn;
  }

  public QuoteHistory getQh() {
    return qh;
  }

  public void setQh(QuoteHistory qh) {
    this.qh = qh;
  }

  public void connect() {
    socket = IBConnectionManager.connect(histFromTWS);
  }

  public void setupDownloader(Contract contract, Calendar startDate, Calendar endDate, BarSize barSize) {
    this.barSize = barSize;
    this.rthOnly = false;
    this.contract = contract;
    tickerId = contract.m_conId;
    priceBars = new ArrayList<PriceBar>();
    firstDate = startDate;
    lastDate = endDate;
    contract.m_includeExpired = true;
  }

  public void reqHistoricalData(int strategyId, Contract contract, String endDateTime, String duration,
          String barSize, String whatToShow, int useRTH, int formatDate) throws InterruptedException {
    //rpc - 3/2/10 8:12 AM - This is where the pacing is taken care of
    try {
      long elapsedSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
      long remainingTime = MAX_REQUEST_FREQUENCY_MILLIS - elapsedSinceLastRequest;
      if (remainingTime > 0) {
        Thread.sleep(remainingTime);
      }
      qh.setIsHistRequestCompleted(false);

      String msg = qh.getStrategyName() + ": " + "Req Hist data for. " + contract.m_symbol;
      msg += "-" + contract.m_expiry + " End time: " + endDateTime;
      PeTraSys.writeToReport(msg);
      lastRequestTime = System.currentTimeMillis();
      socket.reqHistoricalData(strategyId, contract, endDateTime, duration, barSize, 
              whatToShow, useRTH, formatDate);
      //int i = 1;
    } catch (Exception ex) {
      MsgBox.err("Error in reqHistoricalData: ", "Error");
      MsgBox.err2(ex);
    }
  }

  private void runContract() {
    try {
      //setGuard();
      download();
      //blockOnGuard();
      if (!isCancelled) {
        dataToDatabase();
      }
    } catch (Exception ex) {
      MsgBox.err2(ex);
    }
  }

  private void doContinuousContractDownload() {
    histFromTWS.setMyMate(this);
    connect();
    String sym, exchange, expiry;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    int rc = ContractInfoTable.getRowCount();
    for (int rownum = 0; rownum < rc; rownum++) {
      fcd = futuresContractDetailsList.get(ContractInfoTable.convertRowIndexToModel(rownum));
      sym = (String) ContractInfoTable.getValueAt(rownum, 1);
      exchange = (String) ContractInfoTable.getValueAt(rownum, 6);
      expiry = Integer.toString((Integer) ContractInfoTable.getValueAt(rownum, 2)); //Integer.toString(expiration)
      ccdlg.DownloadWaitLabelSetText("Downloading " + sym + "-" + expiry + "....");
      contract = ContractFactory.makeContract(sym, "FUT", exchange, expiry, "USD");
      startDate.setTime((Date) ContractInfoTable.getValueAt(rownum, 3));
      endDate.setTime((Date) ContractInfoTable.getValueAt(rownum, 4));
      int yr = endDate.get(Calendar.YEAR);
      Calendar tempcal = Calendar.getInstance();
      tempcal.setTime(new Date());
      if ((endDate.get(Calendar.YEAR) == 1900) || endDate.after(tempcal)) {
        endDate.setTime(new Date());
      }
      setupDownloader(contract, startDate, endDate, BarSize.Min1);
      runContract();
    }
    ccdlg.DownloadWaitLabelSetText("Done");
    socket.disConnect();
  }

  @Override
  public void run() {
    try {
      if (ccdlg == null) {
        runContract();
        int j = 1;
      } else { // ccdlg is set to calling ContinuousContractDlg.
        doContinuousContractDownload();
      }
    } catch (Exception ex) {
      MsgBox.err2(ex);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  public void error(int errorCode, String errorMsg) {
    firstBarReached = (errorCode == 162 && errorMsg.contains("HMDS query returned no data"));
    if (firstBarReached) {
      firstBarReached = true;
      qh.setIsHistRequestCompleted(true);
      PeTraSys.writeToReport("In HistoricalPriceDataDownloader.error() - firstBarReached");
      return;
    }
    if (errorCode == 162 || errorCode == 200 || errorCode == 321) {
      cancel();
      String msg = "Could not complete back data download." + lineSep + "Cause: " + errorMsg;
      PeTraSys.writeToReport(msg);
    }
  }

  /**
   * rpc - 3/4/10 9:14 AM - Does the loop when more than one
   * call for historical data is necessary, call reqHistoricalData()
   * @throws InterruptedException
   */
  private void download() throws InterruptedException {
    int onlyRTHPriceBars = rthOnly ? 1 : 0;
    String infoType = contract.m_exchange.equalsIgnoreCase("IDEALPRO") ? "MIDPOINT" : "TRADES";
    Calendar cal = (Calendar) lastDate.clone();
    isCancelled = false;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    String endTime = dateFormat.format(cal.getTime());
    long totalMillis = cal.getTimeInMillis() - firstDate.getTimeInMillis();
    long lastDateMillis = cal.getTimeInMillis();
    //rpc - 3/2/10 12:14 PM - This is loop for historic, get all dates for sym
    while (cal.after(firstDate)) {
      reqHistoricalData(tickerId, contract, endTime, barSize.getHistRequestDuration(),
              barSize.toIBText(), infoType, onlyRTHPriceBars, 2);
      //rpc - 3/2/10 2:34 PM - Synching - HistoricalFromTWS releases guard
      setGuard();
      blockOnGuard();
      if (firstBarReached || isCancelled || qh.size() <= 1) {
        return;
      }
      // Use the timestamp of the first bar in the received
      // block of bars as the "end time" for the next historical data
      // request.
      long firstBarMillis = qh.getFirstPriceBar().getDate();
      cal.setTimeInMillis(firstBarMillis);
      endTime = dateFormat.format(cal.getTime());
      // Add the just received block of bars to the cumulative set of
      // bars and clear the current block for the next request
      List<PriceBar> allBars = qh.getAll();
      priceBars.addAll(0, allBars);
      allBars.clear();
      qh.setIsHistRequestCompleted(false);
      long firstBar = firstDate.getTimeInMillis();
      while (priceBars.size() > 0 && priceBars.get(0).getDate() < firstBar) {
        priceBars.remove(0);
      }
    }
    //socket.disConnect();
  }

  /**
   *
   */
  public void cancel() {
    isCancelled = true;
    qh.setIsHistRequestCompleted(true);
  }

  public void setupQuotes1minConnection() {
    try {
      quotes1minConnection = DBops.setuptradesConnection();
      stmtForQuotes = quotes1minConnection.prepareStatement(
              "INSERT INTO quotes1min VALUES (?, ? , ?, ?, ?, ?, ?, ?)");
    } catch (SQLException sqlex) {
      System.err.println("SQLException: " + sqlex.getMessage());
    }
  }

  private void dataToDatabase() {
    setupQuotes1minConnection();
    try {
      int size = priceBars.size();
      for (PriceBar priceBar : priceBars) {
        java.sql.Timestamp dateIn = new java.sql.Timestamp(priceBar.getDate());
        //java.sql.Date dateIn = new java.sql.Date(udatein);
        stmtForQuotes.setString(1, contract.m_symbol);
        stmtForQuotes.setInt(2, Integer.parseInt(contract.m_expiry));
        stmtForQuotes.setTimestamp(3, dateIn);
        stmtForQuotes.setDouble(4, priceBar.getOpen());
        stmtForQuotes.setDouble(5, priceBar.getHigh());
        stmtForQuotes.setDouble(6, priceBar.getLow());
        stmtForQuotes.setDouble(7, priceBar.getClose());
        stmtForQuotes.setLong(8, priceBar.getVolume());
        stmtForQuotes.addBatch();
      }
      int[] updateCounts = stmtForQuotes.executeBatch();
      stmtForQuotes.close();
      quotes1minConnection.close();
    } catch (SQLException sqlex) {
      System.err.println("SQLException: " + sqlex.getMessage());
    }
  }

  private void writeBars() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy,HH:mm:ss,");
    long barsWritten = 0;
    int size = priceBars.size();
    for (PriceBar priceBar : priceBars) {
      String dateTime = dateFormat.format(new java.util.Date(priceBar.getDate()));
      String line = dateTime + priceBar.getOpen() + "," + priceBar.getHigh() + ",";
      line += priceBar.getLow() + "," + priceBar.getClose();
      line += "," + priceBar.getVolume();
      writer.println(line);
      barsWritten++;

      if (barsWritten % 100 == 0) {
        double progress = (100 * barsWritten) / size;
      }
    }
  }
}
