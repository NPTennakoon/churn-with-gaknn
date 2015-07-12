/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import javax.swing.border.*;

import java.awt.*;

import gaknn.OptimizeKNN;

/**
 *
 * @author admin
 */
public class ChurnPrediction {

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
        {
            try {
                // TODO add your handling code here:
                System.out.print("it works");
                OptimizeKNN.runOptimization();
            } catch (Exception ex) {
                Logger.getLogger(ChurnPrediction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void run() {

        // TODO code application logic here
        JFrame myFrame = new JFrame("gaKnn Churn Prediction");

        myFrame.setBounds(10, 20, 850, 500);

        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        myFrame.setVisible(true);

        //Panels
        JPanel panelTop, panelLeft, panelRight, panelBottom, panelCenter, panelMain;

        //Layouts
        BorderLayout border;

        FlowLayout fl1, fl2;

        GridLayout gl1, gl2, gl3;

        JLabel lblHeader, lblUserName, lblPassword, lblMusic, lblSports, lblBusiness;

        JTextField txtUserName, txtPassword;

        JButton btnAdd, btnUpdate, btnDelete, btnLogin, btnLogout, btnEmail;

        //CREATE PANELS
        panelMain = new JPanel();

        panelTop = new JPanel();

        panelLeft = new JPanel();

        panelRight = new JPanel();

        panelBottom = new JPanel();

        panelCenter = new JPanel();

        Border etchedBdr = BorderFactory.createEtchedBorder();

        panelTop.setBorder(etchedBdr);

        panelLeft.setBorder(new TitledBorder(null, "Options", TitledBorder.LEFT, TitledBorder.TOP));

        panelBottom.setBorder(BorderFactory.createEtchedBorder());

        panelRight.setBorder(BorderFactory.createEtchedBorder());

        //CREATE LAYOUTS
        border = new BorderLayout();

        fl1 = new FlowLayout();

        fl2 = new FlowLayout();

        gl1 = new GridLayout(3, 1);

        gl2 = new GridLayout(3, 1);

        gl3 = new GridLayout(2, 2);

        //SET LAYOUTS TO PANELS
        panelMain.setLayout(border);

        panelTop.setLayout(fl1);

        panelLeft.setLayout(gl1);

        panelRight.setLayout(gl2);

        panelBottom.setLayout(fl2);

        panelCenter.setLayout(gl3);

        //CREATE CONTROLS
        lblHeader = new JLabel("gaKnn Churn Prediction Tool");

        lblHeader.setForeground(Color.BLUE);

        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));

//        lblUserName=new JLabel("User Name : ");
//
//   lblPassword=new JLabel("Password :");
/*
         lblMusic = new JLabel("Music ");

         lblSports=new JLabel("Sports ");

         lblBusiness=new JLabel("Bussiness ");
         */
//         txtUserName= new JTextField(10);
//   txtPassword= new JTextField(10);
        btnAdd = new JButton("Predict Churn");

        btnUpdate = new JButton("View Data");

        btnDelete = new JButton("Exit");

//       btnLogin=new JButton("Login");
//        btnLogout=new JButton("Logout");
//     btnEmail=new JButton("Email");
        //ADD CONTROLS TO TOP PANEL
        panelTop.add(lblHeader);

          //ADD CONTROLS TO LEFT PANEL
/*
         panelLeft.add(lblMusic);

         panelLeft.add(lblSports);


         panelLeft.add(lblBusiness);

         */
        //ADD CONTROLS TO RIGHT PANEL
        panelRight.add(btnAdd);

        panelRight.add(btnUpdate);

        panelRight.add(btnDelete);

          //ADD CONTROLS TO BOTTOM PANEL
//       panelBottom.add(btnLogin);
//      panelBottom.add(btnEmail);
//     panelBottom.add(btnLogout);
        //ADD CONTROLS TO CENTER PANEL
/*
         panelCenter.add(lblUserName);

         panelCenter.add(txtUserName);

         panelCenter.add(lblPassword);

         panelCenter.add(txtPassword);

         */
        //ADD SUB PANELS TO MAIN PANEL
        panelMain.add(panelTop, "North");

        panelMain.add(panelLeft, "West");

        panelMain.add(panelRight, "East");

        panelMain.add(panelBottom, "South");

        panelMain.add(panelCenter, "Center");

        //ADD MAIN PANEL TO CONTENT PANE
        myFrame.getContentPane().add(panelMain);

    }
}
