/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.datapreprocess;

import gaknn.core.Instances;
import gaknn.core.Attribute;
import gaknn.core.FastVector;

/**
 *
 * @author Niro
 */
public class NormalizeData {

    double[][] m_DataSet;
    public FastVector m_Attributes;

    public static double[][] Normalizea(double[][] dataSet) {
        double temparr[][] = dataSet;
        double maxValue = 0.0;
        double maxVlue[] = new double[dataSet[0].length];

        System.out.println("data set[0] length" + maxVlue.length);

        for (int j = 0; j < maxVlue.length; j++) {
            maxValue = 0.0;
            for (int i = 0; i < dataSet.length; i++) {
                if (Math.abs(temparr[i][j]) > maxValue) {
                    maxValue = temparr[i][j];
                }
            }
            maxVlue[j] = Math.abs(maxValue);
        }
        for (int j = 0; j < maxVlue.length; j++) {
            if (maxVlue[j] > 0) {
                for (int i = 0; i < dataSet.length; i++) {
                    dataSet[i][j] = ((double) (temparr[i][j] / maxVlue[j]));
                }
            }
        }
        return dataSet;
    }

//added by npt
    //min max normalization
    public static double[][] Normalize(double[][] dataSet) {

//        Attribute attribute;
//            attribute = (Attribute) m_Attributes.elementAt(i);
        double temparr[][] = dataSet;
        double maxValue = 0.0;
        double maxVlue[] = new double[dataSet[0].length];
        double minVlue[] = new double[dataSet[0].length];

        //creates the max array
        for (int j = 0; j < maxVlue.length; j++) {
            maxValue = 0.0;
            for (int i = 0; i < dataSet.length; i++) {
                if (Math.abs(temparr[i][j]) > maxValue) {
                    maxValue = temparr[i][j];
                }
            }
            maxVlue[j] = Math.abs(maxValue);
        }

        //creates the min array
        for (int j = 0; j < maxVlue.length; j++) {
            minVlue[j] = maxVlue[j];
            for (int i = 0; i < dataSet.length; i++) {
                if (Math.abs(temparr[i][j]) < minVlue[j]) {
                    minVlue[j] = temparr[i][j];
                }
            }
            minVlue[j] = Math.abs(minVlue[j]);
        }

        //calculation
        for (int j = 0; j < maxVlue.length; j++) {
            if (maxVlue[j] == 0) {
                for (int i = 0; i < dataSet.length; i++) {
                    dataSet[i][j] = ((double) ((temparr[i][j] - minVlue[j]) / (maxVlue[j] - minVlue[j])));
                }
            }
        }
        return dataSet;
    }

}
