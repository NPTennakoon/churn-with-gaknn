/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.datapreprocess;

/**
 *
 * @author admin
 */
import gaknn.gui.GaknnFrame;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class testOutliers {

    private static String path = "E:\\Project\\TestFiles\\churntrain2.arff";

    public void outlierRemover() throws IOException {

//        GaknnFrame frame2 = new GaknnFrame();
//        path = frame2.arffFilePath;
        // path="E:\\Project\\TestFiles\\churntrain2.arff";
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r3 = Rengine.getMainEngine();
        if (r3 == null) {
            r3 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r3.eval("source('outlierRemoval.R')"); //runs the R script to remove outliers

//        r3.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script
        r3.eval("readfile(\"" + path + "\")");
        r3.end();
    }

    public void outlierHandler() throws IOException {

//        GaknnFrame frame2 = new GaknnFrame();
//        path = frame2.arffFilePath;
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r4 = Rengine.getMainEngine();
        if (r4 == null) {
            r4 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r4.eval("source('outlierReplace.R')"); //runs the R script to replace outliers

        r4.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script

        r4.end();

    }

    public void outlierRemoverMC() throws IOException {

//        GaknnFrame frame2 = new GaknnFrame();
//        path = frame2.arffFilePath;
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r5 = Rengine.getMainEngine();
        if (r5 == null) {
            r5 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r5.eval("source('outlierRemovalMC.R')"); //runs the R script to remove outliers

        r5.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script

        r5.end();

    }

    public void outlierHandlerMC() throws IOException {

//        GaknnFrame frame2 = new GaknnFrame();
//        path = frame2.arffFilePath;
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r6 = Rengine.getMainEngine();
        if (r6 == null) {
            r6 = new Rengine(newargs1, false, null);
        }
//Do some calcs and plot the chart but save as a png in the working folder

        r6.eval("source('outlierReplaceMC.R')"); //runs the R script to replace outliers

        r6.eval("readfile(\"" + path + "\")"); //execute the relevant function in the selected R script

        r6.end();

    }

    public static void main(String args[]) throws IOException {

        testOutliers obj = new testOutliers();
        obj.outlierRemover();
//           obj.outlierHandler();

        /*
         String newargs1[] = {"--no-save"};
         Rengine r3 = new Rengine(newargs1, false, null);


         REXP x = r3.eval("R.version.string");
         System.out.println(x.asString());

         double a=r3.eval("3+3").asDouble();
         System.out.println("a"+a);

         REXP y = r3.eval("a<-read.arff('churntrain2.arff')");
         //System.out.println(y.asString());
         r3.eval("c<-a$accountlength");
         REXP ym=r3.eval("quantile(c,0.25)");

         System.out.println(ym.asString());
         r3.end();
         */
    }
}
