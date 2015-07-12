/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import gaknn.VisualizeData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class StatSummary {

//private rad getRadioButton=new rad();
    public StatSummary() {
        // int result = JOptionPane.showConfirmDialog( getRadioButton);
    }

    public static void showStats() {
        try {
            VisualizeData vsum = new VisualizeData();
            vsum.summary();
            FileReader reader = new FileReader("stats_summary.txt");
            BufferedReader br = new BufferedReader(reader);
            Frame10.jSummary.read(br, null);
            br.close();
            Frame10.jSummary.requestFocus();
        } catch (IOException ex) {
        }
    }

}
