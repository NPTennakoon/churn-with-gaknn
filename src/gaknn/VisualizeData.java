/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn;

/**
 *
 * @author admin
 */
import gaknn.gui.GaknnFrame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;

public class VisualizeData {

    private static String path;

    /**
     * @param args the command line arguments
     */
    public void accuracygraphing() throws IOException {
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r1 = Rengine.getMainEngine();
        if (r1 == null) {
            r1 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r1.eval("source('test2.R')"); //runs the R script to test the model accuracy

        r1.end();

        System.out.println("inside the method");

    }

    public void graphing() throws IOException {
// org.rosuda.JRI.Rengine.DEBUG = 10;

//Start R
        String newargs1[] = {"--no-save"};
//        Rengine r2 = new Rengine(newargs1, false, null);

//Do some calcs and plot the chart but save as a png in the working folder
        Rengine r2 = Rengine.getMainEngine();
        if (r2 == null) {
            r2 = new Rengine(newargs1, false, null);
        }

        r2.eval("source('boxplots.R')"); // to give an anaysis on the data distribution showing the outliers
        r2.eval("source('BarPlots.R')");
        r2.eval("source('vviz.R')"); //to visualize the data in a data file

        r2.end();

    }

    public void fitnessDistribution() throws IOException {

//Start R
        String newargs1[] = {"--no-save"};

        Rengine r3 = Rengine.getMainEngine();
        if (r3 == null) {
            r3 = new Rengine(newargs1, false, null);
        }

        //Do some calcs and plot the chart but save as a png in the working folder
        r3.eval("source('fitness.R')"); //runs the R script to test the model accuracy

        r3.eval("source('VisualizeWeight.R')"); //runs the R script to check weight distribution

        r3.end();

    }

    public void summary() throws IOException {

        //GaknnFrame frame2 = new GaknnFrame();
        //path = GaknnFrame.arffFilePath;
        //  path="E:\\Project\\TestFiles\\churntrain2.arff";
        //Start R
        String newargs1[] = {"--no-save"};

        Rengine r5 = Rengine.getMainEngine();
        if (r5 == null) {
            r5 = new Rengine(newargs1, false, null);
        }
//Do some calcs

//        r5.eval("source('stats.R')"); // to give a statistical anaysis on the data
        r5.eval("source('SummaryStats.R')"); // to give a statistical anaysis on the data

        // r5.eval("readfilesum(\"" + path + "\")");
        //    REXP valueReturned=r5.eval("readfilesum(path)");
        //   System.out.println(valueReturned);
        r5.end();

    }

    public void outlierRemover() throws IOException {

        GaknnFrame frame2 = new GaknnFrame();
        //  path = frame2.arffFilePath;
        //String path1="E:\\Project\\TestFiles\\churntrain2.arff";
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r8 = Rengine.getMainEngine();
        if (r8 == null) {
            r8 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r8.eval("source('outlierRemoval.R')"); //runs the R script to remove outliers

//        r3.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script
        // r8.eval("readfile(\"" + path1 + "\")");
        r8.end();
    }

    public void outlierRemoverMC() throws IOException {

        GaknnFrame frame2 = new GaknnFrame();
        // path = frame2.arffFilePath;

//Start R
        String newargs1[] = {"--no-save"};

        Rengine r5 = Rengine.getMainEngine();
        if (r5 == null) {
            r5 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r5.eval("source('outlierRemovalMC.R')"); //runs the R script to remove outliers

        //   r5.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script
        r5.end();

    }
}
