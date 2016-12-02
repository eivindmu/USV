package application;

import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicArrowButton;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Albert
 *
 */
public class GUI extends javax.swing.JFrame implements Runnable {

    private final Client r;
    // Antall desimaler som vises
    DecimalFormat df1 = new DecimalFormat("#.######");
    DecimalFormat df2 = new DecimalFormat("#.###");
    private CoordinateSystem frame;
    private DataStorage storage;
    private float headingRef;
    private TrendPlot swayPlot;
    private TrendPlot surgePlot;
    private TrendPlot yawPlot;
    private int guiCommand;

    /**
     * Creates new form GUI
     */
    public GUI(Client r, DataStorage storage) {
        this.r = r;
        this.storage = storage;
        guiCommand = 0;
        initComponents();
        df1.setRoundingMode(RoundingMode.CEILING);
        frame = (CoordinateSystem) coordinateSystemFrame;
        // Trend plot for avviksverdier
        swayPlot = new TrendPlot("Sway error", "Sway");
        surgePlot = new TrendPlot("Surge error", "Surge");
        yawPlot = new TrendPlot("Yaw error", "Yaw");
        showPlot();
        // Intitialiser vinduet uten reelle verdier 
        frame.dataUpdated(new float[]{0f, 0f, 0f, 0f});
        this.setVisible(true);
        headingRef = 0f;
    }

    // Oppdater navigasjonsdata
    private void updateNavDataFields() {
        float[] data = storage.getDataArray();
        double[] latLon = storage.getLatLonFromUSV();
        boolean egnosEnabled = storage.getEgnosEnabled();

        latitudeLabel.setText("Latitude USV: " 
                + df1.format((double) latLon[0]));
        longitudeLabel.setText("Longitude USV: " 
                + df1.format((double) latLon[1]));

        latRefLabel.setText("Latitude Reference: " 
                + df1.format((double) data[8]));
        longRefLabel.setText("Longitude Reference: " 
                + df1.format((double) data[9]));

        surgeLabel.setText("Deviation North: " 
                + df2.format((double) data[0]));
        swayLabel.setText("Deviation East: " 
                + df2.format((double) data[1]));
        yawLabel.setText("Heading: " 
                + df2.format((double) data[2]));

        speedLabel.setText("Speed: " 
                + df2.format((double) data[3]));
        rotSpeedLabel.setText("Rotational Speed: " 
                + df2.format((double) data[22]));
        windSpeedLabel.setText("Wind Speed: " 
                + df2.format((double) data[5]));
        windDirLabel.setText("Wind Direction: " 
                + df2.format((double) data[6]));
        connectedLabel.setText("Connected to USV: " 
                + r.isConnected());
        dGPSlabel.setText("EGNOS enabled: " + egnosEnabled);
        
        // Sett status tekst, basert på modus
        switch(guiCommand) {
            case 0: 
                if(r.isConnected()) statusLabel.setText("Status: Idle");
                else statusLabel.setText("Status: Disconnected");
                break;
            case 1:
                statusLabel.setText("Status: Dynamic Positioning");
                break;
            case 2:
                statusLabel.setText("Status: Remote Control");
                break;
            case 3:
                if(!r.isConnected()) statusLabel.setText("Status: Connecting");
                else statusLabel.setText("Status: Idle");
                break;
            case 4:
                statusLabel.setText("Status: Disconnected");                
        }

        swayPlot.updatePlot(data[1]);
        surgePlot.updatePlot(data[0]);
        yawPlot.updatePlot(headingRef - data[2]);

        // Oppdater koordinatsystemet 
        // (data = heading ref, actual heading, surge, sway, X, Y
        frame.dataUpdated(new float[]
        {headingRef, data[2], data[0], data[1], data[19], data[20]});
    }

    // Oppdater tekstfelt
    private void updateTextFields() {
        float[] data = storage.getDataArray();

        proportionalSurgeTextField.setText("Proportional Gain: " 
                + df2.format((double) data[10]).replace(",", "."));
        integralSurgeTextField.setText("Integral Gain: " 
                + df2.format((double) data[11]).replace(",", "."));
        derivativeSurgeTextField.setText("Derivative Gain: " 
                + df2.format((double) data[12]).replace(",", "."));

        proportionalSwayTextField.setText("Proportional Gain: " 
                + df2.format((double) data[13]).replace(",", "."));
        integralSwayTextField.setText("Integral Gain: " 
                + df2.format((double) data[14]).replace(",", "."));
        derivativeSwayTextField.setText("Derivative Gain: " 
                + df2.format((double) data[15]).replace(",", "."));

        proportionalYawTextField.setText("Proportional Gain: " 
                + df2.format((double) data[16]).replace(",", "."));
        integralYawTextField.setText("Integral Gain: " 
                + df2.format((double) data[17]).replace(",", "."));
        derivativeYawTextField.setText("Derivative Gain: " 
                + df2.format((double) data[18]).replace(",", "."));

        headingReferenceTextField.setText("Heading Ref (deg): " 
                + df2.format((double) headingRef));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        northButton = new BasicArrowButton(BasicArrowButton.NORTH);
        southButton = new BasicArrowButton(BasicArrowButton.EAST);
        eastButton = new BasicArrowButton(BasicArrowButton.WEST);
        eastButton1 = new BasicArrowButton(BasicArrowButton.SOUTH);
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        navDataPanel = new javax.swing.JPanel();
        latitudeLabel = new javax.swing.JLabel();
        latRefLabel = new javax.swing.JLabel();
        windSpeedLabel = new javax.swing.JLabel();
        longitudeLabel = new javax.swing.JLabel();
        longRefLabel = new javax.swing.JLabel();
        windDirLabel = new javax.swing.JLabel();
        surgeLabel = new javax.swing.JLabel();
        swayLabel = new javax.swing.JLabel();
        yawLabel = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();
        rotSpeedLabel = new javax.swing.JLabel();
        connectedLabel = new javax.swing.JLabel();
        navDataLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        proportionalSurgeTextField = new javax.swing.JTextField();
        integralSurgeTextField = new javax.swing.JTextField();
        derivativeSurgeTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        proportionalSwayTextField = new javax.swing.JTextField();
        integralSwayTextField = new javax.swing.JTextField();
        derivativeSwayTextField = new javax.swing.JTextField();
        proportionalYawTextField = new javax.swing.JTextField();
        integralYawTextField = new javax.swing.JTextField();
        derivativeYawTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        trendPanelSurge = new javax.swing.JPanel(new BorderLayout(1,1));
        trendPanelSway = new javax.swing.JPanel(new BorderLayout(1,1));
        trendPanelYaw = new javax.swing.JPanel(new BorderLayout(1,1));
        coordinateSystemFrame = new CoordinateSystem(500,450);
        setpointLabel2 = new javax.swing.JPanel();
        refNorthButton = new BasicArrowButton(BasicArrowButton.NORTH);
        refSouthButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        refEastButton = new BasicArrowButton(BasicArrowButton.EAST);
        refWestButton = new BasicArrowButton(BasicArrowButton.WEST);
        setpointLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        dGPSlabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        remoteButton = new javax.swing.JButton();
        dynPosButton = new javax.swing.JButton();
        idleButton = new javax.swing.JButton();
        headingReferenceTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        northButton.setText("jButton1");

        southButton.setText("jButton1");

        eastButton.setText("jButton1");

        eastButton1.setText("jButton1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(eastButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(northButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eastButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(southButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(eastButton))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(northButton)
                .addGap(18, 18, 18)
                .addComponent(eastButton1))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(southButton))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        navDataPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        latitudeLabel.setText("Latitude USV:");

        latRefLabel.setText("Latitude Reference:");

        windSpeedLabel.setText("Wind Speed:");

        longitudeLabel.setText("Longitude USV:");

        longRefLabel.setText("Longitude Reference:");

        windDirLabel.setText("Wind Direction:");

        surgeLabel.setText("Deviation North:");

        swayLabel.setText("Deviation East");

        yawLabel.setText("Heading:");

        speedLabel.setText("Speed:");

        rotSpeedLabel.setText("Rotational speed:");

        connectedLabel.setText("Connected to USV:");

        javax.swing.GroupLayout navDataPanelLayout = new javax.swing.GroupLayout(navDataPanel);
        navDataPanel.setLayout(navDataPanelLayout);
        navDataPanelLayout.setHorizontalGroup(
            navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(latitudeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(longitudeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                    .addComponent(swayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(surgeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(latRefLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(longRefLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(yawLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(speedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotSpeedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(navDataPanelLayout.createSequentialGroup()
                        .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(windSpeedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(windDirLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(connectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
        navDataPanelLayout.setVerticalGroup(
            navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(latitudeLabel)
                    .addComponent(latRefLabel)
                    .addComponent(windSpeedLabel))
                .addGap(18, 18, 18)
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(longitudeLabel)
                    .addComponent(longRefLabel)
                    .addComponent(windDirLabel))
                .addGap(18, 18, 18)
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(surgeLabel)
                    .addComponent(yawLabel)
                    .addComponent(rotSpeedLabel))
                .addGap(18, 18, 18)
                .addGroup(navDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(swayLabel)
                    .addComponent(speedLabel)
                    .addComponent(connectedLabel))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        navDataLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        navDataLabel.setText("Navigational Data");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navDataLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(navDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(navDataLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(navDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PID Controller Gains");

        proportionalSurgeTextField.setText("Proportional Gain:");
        proportionalSurgeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                proportionalSurgeTextFieldFocusLost(evt);
            }
        });
        proportionalSurgeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proportionalSurgeTextFieldActionPerformed(evt);
            }
        });

        integralSurgeTextField.setText("Integral Gain:");
        integralSurgeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                integralSurgeTextFieldFocusLost(evt);
            }
        });
        integralSurgeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                integralSurgeTextFieldActionPerformed(evt);
            }
        });

        derivativeSurgeTextField.setText("Derivative Gain:");
        derivativeSurgeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                derivativeSurgeTextFieldFocusLost(evt);
            }
        });
        derivativeSurgeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derivativeSurgeTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Surge");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Sway");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Yaw");

        proportionalSwayTextField.setText("Proportional Gain:");
        proportionalSwayTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                proportionalSwayTextFieldFocusLost(evt);
            }
        });
        proportionalSwayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proportionalSwayTextFieldActionPerformed(evt);
            }
        });

        integralSwayTextField.setText("Integral Gain:");
        integralSwayTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                integralSwayTextFieldFocusLost(evt);
            }
        });
        integralSwayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                integralSwayTextFieldActionPerformed(evt);
            }
        });

        derivativeSwayTextField.setText("Derivative Gain:");
        derivativeSwayTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                derivativeSwayTextFieldFocusLost(evt);
            }
        });
        derivativeSwayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derivativeSwayTextFieldActionPerformed(evt);
            }
        });

        proportionalYawTextField.setText("Proportional Gain:");
        proportionalYawTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                proportionalYawTextFieldFocusLost(evt);
            }
        });
        proportionalYawTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proportionalYawTextFieldActionPerformed(evt);
            }
        });

        integralYawTextField.setText("Integral Gain:");
        integralYawTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                integralYawTextFieldFocusLost(evt);
            }
        });
        integralYawTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                integralYawTextFieldActionPerformed(evt);
            }
        });

        derivativeYawTextField.setText("Derivative Gain:");
        derivativeYawTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                derivativeYawTextFieldFocusLost(evt);
            }
        });
        derivativeYawTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derivativeYawTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(integralSurgeTextField)
                            .addComponent(derivativeSurgeTextField)
                            .addComponent(proportionalSurgeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(derivativeSwayTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(integralSwayTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(proportionalSwayTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(derivativeYawTextField)
                            .addComponent(integralYawTextField)
                            .addComponent(proportionalYawTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proportionalSurgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proportionalSwayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proportionalYawTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(integralSurgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(integralSwayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(integralYawTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(derivativeSurgeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(derivativeSwayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(derivativeYawTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        trendPanelSurge.setMaximumSize(new java.awt.Dimension(600, 120));
        trendPanelSurge.setMinimumSize(new java.awt.Dimension(0, 120));

        javax.swing.GroupLayout trendPanelSurgeLayout = new javax.swing.GroupLayout(trendPanelSurge);
        trendPanelSurge.setLayout(trendPanelSurgeLayout);
        trendPanelSurgeLayout.setHorizontalGroup(
            trendPanelSurgeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        trendPanelSurgeLayout.setVerticalGroup(
            trendPanelSurgeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        trendPanelSway.setMaximumSize(new java.awt.Dimension(32767, 120));
        trendPanelSway.setMinimumSize(new java.awt.Dimension(0, 120));

        javax.swing.GroupLayout trendPanelSwayLayout = new javax.swing.GroupLayout(trendPanelSway);
        trendPanelSway.setLayout(trendPanelSwayLayout);
        trendPanelSwayLayout.setHorizontalGroup(
            trendPanelSwayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        trendPanelSwayLayout.setVerticalGroup(
            trendPanelSwayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        trendPanelYaw.setMaximumSize(new java.awt.Dimension(32767, 120));
        trendPanelYaw.setMinimumSize(new java.awt.Dimension(0, 120));

        javax.swing.GroupLayout trendPanelYawLayout = new javax.swing.GroupLayout(trendPanelYaw);
        trendPanelYaw.setLayout(trendPanelYawLayout);
        trendPanelYawLayout.setHorizontalGroup(
            trendPanelYawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        trendPanelYawLayout.setVerticalGroup(
            trendPanelYawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(trendPanelSurge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(trendPanelSway, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(trendPanelYaw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(trendPanelSurge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trendPanelSway, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trendPanelYaw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        coordinateSystemFrame.setMaximumSize(new java.awt.Dimension(502, 502));
        coordinateSystemFrame.setMinimumSize(new java.awt.Dimension(502, 502));
        coordinateSystemFrame.setPreferredSize(new java.awt.Dimension(502, 502));

        javax.swing.GroupLayout coordinateSystemFrameLayout = new javax.swing.GroupLayout(coordinateSystemFrame);
        coordinateSystemFrame.setLayout(coordinateSystemFrameLayout);
        coordinateSystemFrameLayout.setHorizontalGroup(
            coordinateSystemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        coordinateSystemFrameLayout.setVerticalGroup(
            coordinateSystemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );

        setpointLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        refNorthButton.setText("jButton1");
        refNorthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refNorthButtonActionPerformed(evt);
            }
        });

        refSouthButton.setText("jButton2");
        refSouthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refSouthButtonActionPerformed(evt);
            }
        });

        refEastButton.setText("jButton3");
        refEastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refEastButtonActionPerformed(evt);
            }
        });

        refWestButton.setText("jButton4");
        refWestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refWestButtonActionPerformed(evt);
            }
        });

        setpointLabel1.setText("Use arrows to move reference setpoint");

        jLabel7.setText("in 0.5 meter increments");

        javax.swing.GroupLayout setpointLabel2Layout = new javax.swing.GroupLayout(setpointLabel2);
        setpointLabel2.setLayout(setpointLabel2Layout);
        setpointLabel2Layout.setHorizontalGroup(
            setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setpointLabel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(setpointLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(refWestButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refSouthButton)
                    .addComponent(refNorthButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refEastButton))
        );
        setpointLabel2Layout.setVerticalGroup(
            setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setpointLabel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(refNorthButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refSouthButton)
                .addGap(0, 11, Short.MAX_VALUE))
            .addGroup(setpointLabel2Layout.createSequentialGroup()
                .addGroup(setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(setpointLabel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(setpointLabel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(refEastButton)
                            .addComponent(refWestButton)))
                    .addGroup(setpointLabel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(setpointLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        dGPSlabel.setText("EGNOS enabled:");

        statusLabel.setText("Status:");

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        remoteButton.setText("Remote Control");
        remoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteButtonActionPerformed(evt);
            }
        });

        dynPosButton.setText("Dynamic Positioning");
        dynPosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dynPosButtonActionPerformed(evt);
            }
        });

        idleButton.setText("Idle");
        idleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idleButtonActionPerformed(evt);
            }
        });

        headingReferenceTextField.setText("Heading Ref (deg):");
        headingReferenceTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                headingReferenceTextFieldFocusLost(evt);
            }
        });
        headingReferenceTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headingReferenceTextFieldActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Set heading before DP");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(connectButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dGPSlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5)
                                        .addGap(12, 12, 12))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(headingReferenceTextField))))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(idleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(dynPosButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(44, 44, 44)
                        .addComponent(remoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusLabel)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(headingReferenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dGPSlabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remoteButton)
                    .addComponent(dynPosButton)
                    .addComponent(idleButton))
                .addGap(28, 28, 28)
                .addComponent(connectButton))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(setpointLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coordinateSystemFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(setpointLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(coordinateSystemFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
    ActionListenere for knappene
    Aksjoner avhenger av hvilken knapp som trykkes
    */
    private void dynPosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynPosButtonActionPerformed

        r.setGUIcommand(1);
        guiCommand = 1;
    }//GEN-LAST:event_dynPosButtonActionPerformed

    private void idleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idleButtonActionPerformed
        r.setGUIcommand(0);
        guiCommand = 0;
    }//GEN-LAST:event_idleButtonActionPerformed

    private void remoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteButtonActionPerformed
        r.setGUIcommand(2);
        guiCommand = 2;
    }//GEN-LAST:event_remoteButtonActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        if (!r.isConnected()) {
            r.setGUIcommand(3);
            guiCommand = 3;
            connectButton.setText("Disconnect");
        } else {
            try {
                r.setGUIcommand(4);
                guiCommand = 4;
                connectButton.setText("Connect");
                Thread.sleep(1000);
                // r.setGUIcommand(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(GUI.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void proportionalSurgeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proportionalSurgeTextFieldActionPerformed
        r.gainChanged(1, getValueFromTextInput(proportionalSurgeTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_proportionalSurgeTextFieldActionPerformed

    private void integralSurgeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_integralSurgeTextFieldActionPerformed
        r.gainChanged(2, getValueFromTextInput(integralSurgeTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_integralSurgeTextFieldActionPerformed

    private void derivativeSurgeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derivativeSurgeTextFieldActionPerformed
        r.gainChanged(3, getValueFromTextInput(derivativeSurgeTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_derivativeSurgeTextFieldActionPerformed

    private void proportionalSwayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proportionalSwayTextFieldActionPerformed
        r.gainChanged(4, getValueFromTextInput(proportionalSwayTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_proportionalSwayTextFieldActionPerformed

    private void integralSwayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_integralSwayTextFieldActionPerformed
        r.gainChanged(5, getValueFromTextInput(integralSwayTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_integralSwayTextFieldActionPerformed

    private void derivativeSwayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derivativeSwayTextFieldActionPerformed
        r.gainChanged(6, getValueFromTextInput(derivativeSwayTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_derivativeSwayTextFieldActionPerformed

    private void proportionalYawTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proportionalYawTextFieldActionPerformed
        r.gainChanged(7, getValueFromTextInput(proportionalYawTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_proportionalYawTextFieldActionPerformed

    private void integralYawTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_integralYawTextFieldActionPerformed
        r.gainChanged(8, getValueFromTextInput(integralYawTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_integralYawTextFieldActionPerformed

    private void derivativeYawTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derivativeYawTextFieldActionPerformed
        r.gainChanged(9, getValueFromTextInput(derivativeYawTextField.getText()));
        this.requestFocus();
    }//GEN-LAST:event_derivativeYawTextFieldActionPerformed

    private void proportionalSurgeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_proportionalSurgeTextFieldFocusLost

    }//GEN-LAST:event_proportionalSurgeTextFieldFocusLost

    private void integralSurgeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_integralSurgeTextFieldFocusLost

    }//GEN-LAST:event_integralSurgeTextFieldFocusLost

    private void derivativeSurgeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_derivativeSurgeTextFieldFocusLost

    }//GEN-LAST:event_derivativeSurgeTextFieldFocusLost

    private void proportionalSwayTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_proportionalSwayTextFieldFocusLost

    }//GEN-LAST:event_proportionalSwayTextFieldFocusLost

    private void integralSwayTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_integralSwayTextFieldFocusLost

    }//GEN-LAST:event_integralSwayTextFieldFocusLost

    private void derivativeSwayTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_derivativeSwayTextFieldFocusLost

    }//GEN-LAST:event_derivativeSwayTextFieldFocusLost

    private void proportionalYawTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_proportionalYawTextFieldFocusLost

    }//GEN-LAST:event_proportionalYawTextFieldFocusLost

    private void integralYawTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_integralYawTextFieldFocusLost

    }//GEN-LAST:event_integralYawTextFieldFocusLost

    private void derivativeYawTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_derivativeYawTextFieldFocusLost

    }//GEN-LAST:event_derivativeYawTextFieldFocusLost

    private void headingReferenceTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headingReferenceTextFieldActionPerformed
        headingRef = getValueFromTextInput(headingReferenceTextField.getText());
        r.setHeadingReference(headingRef);
        updateTextFields();
    }//GEN-LAST:event_headingReferenceTextFieldActionPerformed

    private void headingReferenceTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_headingReferenceTextFieldFocusLost
        updateTextFields();
    }//GEN-LAST:event_headingReferenceTextFieldFocusLost

    private void refNorthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refNorthButtonActionPerformed
        r.incrementNorth(1);
        updateNavDataFields();
    }//GEN-LAST:event_refNorthButtonActionPerformed

    private void refEastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refEastButtonActionPerformed
        r.incrementEast(1);
        updateNavDataFields();
    }//GEN-LAST:event_refEastButtonActionPerformed

    private void refSouthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refSouthButtonActionPerformed
        r.incrementNorth(-1);
        updateNavDataFields();
    }//GEN-LAST:event_refSouthButtonActionPerformed

    private void refWestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refWestButtonActionPerformed
        r.incrementEast(-1);
        updateNavDataFields();
    }//GEN-LAST:event_refWestButtonActionPerformed
/*
    Metode for å konvertere string til desimaltall hentet fra java api
    */
    private float getValueFromTextInput(String text) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex
                = ("[\\x00-\\x20]*"
                + // Optional leading "whitespace"
                "[+-]?("
                + // Optional sign character
                "NaN|"
                + // "NaN" string
                "Infinity|"
                + // "Infinity" string
                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                //
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from section 3.10.2 of
                // The Java™ Language Specification.
                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"
                + // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.(" + Digits + ")(" + Exp + ")?)|"
                + // Hexadecimal strings
                "(("
                + // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|"
                + // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")"
                + ")[pP][+-]?" + Digits + "))"
                + "[fFdD]?))"
                + "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if (Pattern.matches(fpRegex, text)) {
            return Float.valueOf(text); // Will not throw NumberFormatException
        } else {
            String[] s = text.split(":");
            return Float.valueOf(s[s.length - 1].trim());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // initialiser objekter
        DataStorage st = new DataStorage();
        //</editnavDataLabel>
        JoystickReader jReader = new JoystickReader();
        Timer t = new Timer();
        // Start joystickleseren
        t.scheduleAtFixedRate(jReader, 1000, 100);
        // start klienten
        Client r = new Client(jReader, st);
        r.start();
        GUI gui = new GUI(r, st);
        // Start gui
        Thread t1 = new Thread(gui);
        t1.start();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel connectedLabel;
    private javax.swing.JPanel coordinateSystemFrame;
    private javax.swing.JLabel dGPSlabel;
    private javax.swing.JTextField derivativeSurgeTextField;
    private javax.swing.JTextField derivativeSwayTextField;
    private javax.swing.JTextField derivativeYawTextField;
    private javax.swing.JButton dynPosButton;
    private javax.swing.JButton eastButton;
    private javax.swing.JButton eastButton1;
    private javax.swing.JTextField headingReferenceTextField;
    private javax.swing.JButton idleButton;
    private javax.swing.JTextField integralSurgeTextField;
    private javax.swing.JTextField integralSwayTextField;
    private javax.swing.JTextField integralYawTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel latRefLabel;
    private javax.swing.JLabel latitudeLabel;
    private javax.swing.JLabel longRefLabel;
    private javax.swing.JLabel longitudeLabel;
    private javax.swing.JLabel navDataLabel;
    private javax.swing.JPanel navDataPanel;
    private javax.swing.JButton northButton;
    private javax.swing.JTextField proportionalSurgeTextField;
    private javax.swing.JTextField proportionalSwayTextField;
    private javax.swing.JTextField proportionalYawTextField;
    private javax.swing.JButton refEastButton;
    private javax.swing.JButton refNorthButton;
    private javax.swing.JButton refSouthButton;
    private javax.swing.JButton refWestButton;
    private javax.swing.JButton remoteButton;
    private javax.swing.JLabel rotSpeedLabel;
    private javax.swing.JLabel setpointLabel1;
    private javax.swing.JPanel setpointLabel2;
    private javax.swing.JButton southButton;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel surgeLabel;
    private javax.swing.JLabel swayLabel;
    private javax.swing.JPanel trendPanelSurge;
    private javax.swing.JPanel trendPanelSway;
    private javax.swing.JPanel trendPanelYaw;
    private javax.swing.JLabel windDirLabel;
    private javax.swing.JLabel windSpeedLabel;
    private javax.swing.JLabel yawLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        while (true) {
            updateNavDataFields();
            // Hvis GUI har fokus
            if (this.isFocusOwner()) {
                // Hvis ny data
                if (storage.updated()) {
                    // oppdater tekstfelt
                    updateTextFields();
                }
            }
        }
    }
/*
    Sett opp og vis trendplot i gui
    */
    private void showPlot() {
        ChartPanel p1 = swayPlot.getChartPanel();
        ChartPanel p2 = surgePlot.getChartPanel();
        ChartPanel p3 = yawPlot.getChartPanel();

        trendPanelSway.setBorder(javax.swing.BorderFactory.
                createLineBorder(new java.awt.Color(0, 0, 0)));
        trendPanelSurge.setBorder(javax.swing.BorderFactory.
                createLineBorder(new java.awt.Color(0, 0, 0)));
        trendPanelYaw.setBorder(javax.swing.BorderFactory.
                createLineBorder(new java.awt.Color(0, 0, 0)));
        
        p1.setDomainZoomable(true);
        p2.setDomainZoomable(true);
        p3.setDomainZoomable(true);
        p1.setSize(trendPanelSurge.getWidth()-1, trendPanelSurge.getHeight());
        p2.setSize(trendPanelYaw.getWidth()-1, trendPanelYaw.getHeight());
        p3.setSize(trendPanelSway.getWidth()-1, trendPanelSway.getHeight());
        
        trendPanelYaw.add(p3, BorderLayout.CENTER);
        trendPanelSurge.add(p2, BorderLayout.CENTER);
        trendPanelSway.add(p1, BorderLayout.CENTER);
 
        

    }
}
