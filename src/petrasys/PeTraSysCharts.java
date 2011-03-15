/*********************************************************************
 * File path:     /share/JavaDev/PeTraSys/src/petrasys/PeTraSysCharts.java
 * Version:       
 * Description:   
 * Author:        Rick Charon <rickcharon@gmail.com>
 * Created at:    Wed Nov 24 12:10:02 2010
 * Modified at:   Wed Nov 24 13:49:43 2010
 ********************************************************************/

/*
 * ChartsForm.java
 *
 * Created on Apr 2, 2010, 5:19:20 AM
 */
package petrasys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.swing.JList;
import petrasys.instruments.RunState;
import petrasys.utils.DBops;
import petrasys.utils.DateOps;
import petrasys.utils.MsgBox;

/**
 *
 * @author rickcharon
 */
public class PeTraSysCharts extends javax.swing.JFrame {

  private Vector<String> symbols;

  /** Creates new form ChartsForm */
  public PeTraSysCharts() {
    symbols = new Vector();
    getDistinctSymbolNames();
    initComponents();
  }

  public Vector<String> getSymbols() {
    return symbols;
  }

  public JList getSymbolsList() {
    return symbolsList;
  }

  public void getDistinctSymbolNames() {
    //return symbols;   Vector<String>
    try {
      ResultSet res = DBops.distinctSymsProc().executeQuery();
      int rowSize = res.getRow();
      res.last();
      rowSize = res.getRow();
      res.first();
      rowSize = res.getRow();
      res.previous();
      rowSize = res.getRow();
      symbols.clear();
      while (res.next()) {
        symbols.add(res.getString("symbol"));
      }
      res.close();
    } catch (SQLException sqlex) {
      System.err.println("SQLException: " + sqlex.getMessage());
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    symbolsList = new javax.swing.JList(symbols);
    jLabel5 = new javax.swing.JLabel();
    beginDateLabel = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    endDateLabel = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    beginDateChooser = new com.toedter.calendar.JDateChooser();
    jLabel2 = new javax.swing.JLabel();
    endDateChooser = new com.toedter.calendar.JDateChooser();
    chartButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("PeTraSys - Charts");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
      public void windowOpened(java.awt.event.WindowEvent evt) {
        formWindowOpened(evt);
      }
    });

    jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("petrasys/Bundle"); // NOI18N
    jLabel1.setText(bundle.getString("PeTraSysTopFrame1.jLabel1.text")); // NOI18N

    symbolsList.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    symbolsList.setModel(new javax.swing.AbstractListModel() {
      public int getSize() { return symbols.size(); }
      public Object getElementAt(int i) { return symbols.elementAt(i); }
    });
    symbolsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    symbolsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        symbolsListValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(symbolsList);

    jLabel5.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    jLabel5.setText(bundle.getString("PeTraSysTopFrame1.jLabel5.text")); // NOI18N

    beginDateLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 16));
    beginDateLabel.setText(bundle.getString("PeTraSysTopFrame1.beginDateLabel.text")); // NOI18N

    jLabel3.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    jLabel3.setText(bundle.getString("PeTraSysTopFrame1.jLabel3.text")); // NOI18N

    endDateLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 16));
    endDateLabel.setText(bundle.getString("PeTraSysTopFrame1.endDateLabel.text")); // NOI18N

    jLabel6.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    jLabel6.setText(bundle.getString("PeTraSysTopFrame1.jLabel6.text")); // NOI18N

    beginDateChooser.setDateFormatString("MM/dd/yyyy HH:mm:ss");
    beginDateChooser.setDoubleBuffered(false);
    beginDateChooser.setFont(new java.awt.Font("DejaVu Sans", 0, 18));
    beginDateChooser.setName("beginDateChooser"); // NOI18N
    //beginDateChooser.setVisible(false);

    jLabel2.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    jLabel2.setText(bundle.getString("PeTraSysTopFrame1.jLabel2.text")); // NOI18N

    endDateChooser.setDateFormatString("MM/dd/yyyy HH:mm:ss");
    endDateChooser.setFont(new java.awt.Font("DejaVu Sans", 0, 18));
    //endDateChooser.setVisible(false);

    chartButton.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
    chartButton.setText(bundle.getString("PeTraSysTopFrame1.chartButton.text")); // NOI18N
    chartButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chartButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(13, 13, 13)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel5)
              .addComponent(jLabel3)
              .addComponent(beginDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
              .addComponent(endDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(beginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(endDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(35, 35, 35))
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(0, 249, Short.MAX_VALUE)
          .addComponent(chartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGap(0, 249, Short.MAX_VALUE)))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addGap(6, 6, 6)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(beginDateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endDateLabel))
              .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel6)
            .addGap(3, 3, 3)
            .addComponent(beginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10)
            .addComponent(jLabel2)
            .addGap(3, 3, 3)
            .addComponent(endDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(203, Short.MAX_VALUE))
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(0, 218, Short.MAX_VALUE)
          .addComponent(chartButton)
          .addGap(0, 218, Short.MAX_VALUE)))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void symbolsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_symbolsListValueChanged
      String sym = (String) symbolsList.getSelectedValue();
      try {
        ResultSet res = DBops.minMaxDatesBySym(sym).executeQuery();
        if (res.next()) {
          Timestamp minD = res.getTimestamp(1);
          Timestamp maxD = res.getTimestamp(2);
          Calendar minCal = new GregorianCalendar();
          minCal.setTime(minD);
          Calendar maxCal = new GregorianCalendar();
          maxCal.setTime(maxD);
          SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
          String beginTime = dateFormat.format(minCal.getTime());
          String endTime = dateFormat.format(maxCal.getTime());
          beginDateChooser.setCalendar(minCal);
          endDateChooser.setCalendar(maxCal);
          beginDateLabel.setText(DateOps.dbFormatString(minD));
          endDateLabel.setText(DateOps.dbFormatString(maxD));
        }
        //int i = 1;
      } catch (SQLException ex) {
        MsgBox.err2(ex);
      }
}//GEN-LAST:event_symbolsListValueChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
      setLocationRelativeTo(null);
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      dispose();
    }//GEN-LAST:event_formWindowClosing

    private void chartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chartButtonActionPerformed
      PeTraSys.getTopFrame().getSymbolsList().setSelectedValue(symbolsList.getSelectedValue(), true);
      PeTraSys.getTopFrame().getBeginDateChooser().setCalendar(beginDateChooser.getCalendar());
      PeTraSys.getTopFrame().getEndDateChooser().setCalendar(endDateChooser.getCalendar());
      PeTraSys.getTopFrame().setupForActionsOnInstrument("Chart ", RunState.Chart, PeTraSys.getTopFrame().getChartPanel());
}//GEN-LAST:event_chartButtonActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    PeTraSysTopFrame1 topFrame = PeTraSys.getTopFrame();
    java.awt.EventQueue.invokeLater(new Runnable() {

      public void run() {
        new PeTraSysCharts().setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.toedter.calendar.JDateChooser beginDateChooser;
  private javax.swing.JLabel beginDateLabel;
  private javax.swing.JButton chartButton;
  private com.toedter.calendar.JDateChooser endDateChooser;
  private javax.swing.JLabel endDateLabel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList symbolsList;
  // End of variables declaration//GEN-END:variables
}
