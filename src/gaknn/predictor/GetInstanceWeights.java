/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.predictor;

import gaknn.OptimizeKNN;
import java.io.IOException;

/**
 *
 * @author admin
 */
public class GetInstanceWeights {

    private static String DataFilePath = "E:\\Project\\TestFiles\\churntrain2.arff";
    private static int weighttype = 1;

    public static void CalWeights() throws IOException, Exception {

        OptimizeKNN.ReadData(DataFilePath);
        //get the file from file chooser
        int test = 0;
        double[][] trainSet = OptimizeKNN.ReadTrainData(DataFilePath);
        double[][] testSet = OptimizeKNN.ReadTestData(DataFilePath);
        int[] testIndex = OptimizeKNN.ReadTestIndex(DataFilePath);
        int[] trainIndex = OptimizeKNN.ReadTrainIndex(DataFilePath);

//                OptimizeKNN.instanceWeighter(trainSet, testSet, testIndex, trainIndex, weighttype);
        OptimizeKNN.weightselector(trainSet, testSet, testIndex, trainIndex, weighttype);
        OptimizeKNN.createTrainingdataSetsDouble(trainSet, trainIndex);
        //   System.out.println("OptimizeKNN.m_TestSetd.length"+(int) OptimizeKNN.m_TestSetd[4][18]);

//                for (int i = 0; i < OptimizeKNN.m_TestSetd.length-1; i++) {
//                    test = (int) OptimizeKNN.m_TestSetd[i][6];
//                    System.out.println("test v"+test);
//                }
    }

    public static void main(String args[]) throws Exception {
        System.out.println("get inst");
        CalWeights();

        System.out.println("priorone====" + OptimizeKNN.priorone);

    }
}
