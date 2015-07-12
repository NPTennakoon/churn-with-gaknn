/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import gaknn.OptimizeKNN;
import gaknn.core.FastVector;
import gaknn.core.Instances;
import gaknn.datapreprocess.DiscretizeData;
import gaknn.search.BayesianNetwork;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class GetBayesForBN {

    private static String m_disDataFilePath = "Discritized.arff";
    private static FastVector m_Attributes;
    //private static Instances m_Data;

    public void GetAllValues() throws IOException, Exception {
        //    System.out.println("hey u");

        DiscretizeData Discretize = new DiscretizeData();
        Discretize.Discretizing();

        OptimizeKNN.ReadData(m_disDataFilePath);
        double[][] trainSet = OptimizeKNN.ReadTrainData(m_disDataFilePath);
        int ClassAttribIndex = OptimizeKNN.m_ClassAttribIndex;
        System.out.println("ClassAttribIndex for disc" + ClassAttribIndex);
        int[] trainIndex = OptimizeKNN.ReadTrainIndex(m_disDataFilePath);

        //build and learn the DAG
        BayesianNetwork bn = new BayesianNetwork(trainSet, m_Attributes);
        // System.out.println("m_Data si" + OptimizeKNN.m_Data);
        bn.buildClassifier(trainSet, ClassAttribIndex, OptimizeKNN.m_Data);
        bn.bnValues(ClassAttribIndex, trainSet, trainIndex, OptimizeKNN.m_Data);
    }
//    public static void main(String args[]) throws IOException{
//
//    }
}
