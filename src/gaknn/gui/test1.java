/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import gaknn.OptimizeKNN;
import java.io.IOException;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author admin
 */
public class test1 {

    private static String path;
    private static String DataFilePath1 = "E:\\Project\\TestFiles\\churntrain2Reduced.arff";

    public static void main(String args[]) throws IOException {

        OptimizeKNN.ReadData(DataFilePath1);
        String relationName = OptimizeKNN.fileReaderObject.m_RelationName;
        System.out.println("relationName" + relationName);
        //      RelationLabel.setText(relationName);
        // RelationLabel.setText(dataFile.getName());
        int Instances = OptimizeKNN.fileReaderObject.numlines;
        System.out.println(" inst" + Instances);

        int attribs = OptimizeKNN.fileReaderObject.numatts;
        System.out.println("attribs" + attribs);

        summary();
    }

    public static void summary() throws IOException {

        GaknnFrame frame2 = new GaknnFrame();
        path = OptimizeKNN.fileReaderObject.m_RelationName;

        System.out.println("path for summary" + path);

        //Start R
        String newargs1[] = {"--no-save"};

        Rengine r5 = Rengine.getMainEngine();
        if (r5 == null) {
            r5 = new Rengine(newargs1, false, null);
        }
//Do some calcs

//        r5.eval("source('stats.R')"); // to give a statistical anaysis on the data
        r5.eval("source('SummaryStats.R')"); // to give a statistical anaysis on the data

        r5.eval("readfilesum(\"" + path + "\")");
        //r5.eval("readfilesum(path)");

        r5.end();

    }

}
