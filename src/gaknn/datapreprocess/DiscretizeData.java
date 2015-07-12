/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.datapreprocess;

import gaknn.gui.GaknnFrame;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author admin
 */
public class DiscretizeData {

    private static String path1;

    public void Discretizing() throws IOException {
        GaknnFrame frame2 = new GaknnFrame();
        //  path1 = frame2.relationName;
        // path1= "E:\\Project\\TestFiles\\churntrain2.arff";
        //   System.out.println("path1"+path1);
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r7 = Rengine.getMainEngine();
        if (r7 == null) {
            r7 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r7.eval("source('Discretize.R')"); //runs the R script to remove outliers

        // r7.eval("readfiled(\"" + path1 + "\")"); //execute the relevant function in the selected R script
        //    r7.eval("readfiled(\"" +path1+ "\")"); //execute the relevant function in the selected R script
        r7.end();

    }

    public static void main(String[] args) throws Exception {

        DiscretizeData obj = new DiscretizeData();
        obj.Discretizing();
    }
}
