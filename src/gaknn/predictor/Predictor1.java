/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.predictor;

import gaknn.core.Instance;
import gaknn.similarity.*;
import gaknn.core.FastVector;
import gaknn.core.Pair;
import java.util.Arrays;

/**
 *
 * @author Niro
 */
public class Predictor1 extends Predictor {

    public Predictor1(AbstractSimilarity sim, Instance[] trSet) {
        super(sim, trSet);
    }

    public double Predict(Instance instance) {
        int dataLength = trainSet.length;
        // System.out.println("dataLength"+dataLength);

        Pair[] simList = new Pair[dataLength];

        for (int i = 0; i < dataLength; i++) {
            double simValue = similarityMeasure.GetSimilarity(instance.GetElements(), trainSet[i].GetElements());
//            System.out.print("simvalue: "+simValue+", ");
            simList[i] = new Pair(i, simValue);

        }

        Arrays.sort(simList);

        double[] vote = new double[m_ClassList.length];
        int ClassIndex = 0;

        for (int i = 0; i < m_K; i++) {
            int index = ((Pair) simList[i]).Index();
            ClassIndex = trainSet[index].GetClassIndex();
            vote[ClassIndex] += ((Pair) simList[i]).Value();
        }

        int clsId = (int) instance.GetClassIndex();
        double val = CalculateClassConf(vote, clsId);

        if (val < Double.MIN_VALUE) {
            val = 0.0;
        } else if (val > Double.MAX_VALUE) {
            val = Double.MAX_VALUE;
        } else if (Double.isNaN(val)) {
            val = 0.0;
        }

        return val;
    }

    public double Predict(Instance instance, double[] values) {
        int dataLength = trainSet.length;
        // System.out.println("dataLength"+dataLength);

        Pair[] simList = new Pair[dataLength];

        for (int i = 0; i < dataLength; i++) {
            double simValue = similarityMeasure.GetSimilarity(instance.GetElements(), trainSet[i].GetElements());
//            System.out.print("simvalue: "+simValue+", ");
            simList[i] = new Pair(i, simValue);

        }

        Arrays.sort(simList);

        double[] vote = new double[m_ClassList.length];
        int ClassIndex = 0;

        for (int i = 0; i < m_K; i++) {
            int index = ((Pair) simList[i]).Index();
            ClassIndex = trainSet[index].GetClassIndex();
            vote[ClassIndex] += ((Pair) simList[i]).Value();
            vote[ClassIndex] = vote[ClassIndex] * values[index];
        }

        int clsId = (int) instance.GetClassIndex();
        double val = CalculateClassConf(vote, clsId);

        if (val < Double.MIN_VALUE) {
            val = 0.0;
        } else if (val > Double.MAX_VALUE) {
            val = Double.MAX_VALUE;
        } else if (Double.isNaN(val)) {
            val = 0.0;
        }

        return val;
    }

    public double Predict(Instance instance, double vote0, double vote1) {
        int dataLength = trainSet.length;
        // System.out.println("dataLength"+dataLength);

        Pair[] simList = new Pair[dataLength];

        for (int i = 0; i < dataLength; i++) {
            double simValue = similarityMeasure.GetSimilarity(instance.GetElements(), trainSet[i].GetElements());
//            System.out.print("simvalue: "+simValue+", ");
            simList[i] = new Pair(i, simValue);

        }

        Arrays.sort(simList);

        double[] vote = new double[m_ClassList.length];
        int ClassIndex = 0;

        for (int i = 0; i < m_K; i++) {
            int index = ((Pair) simList[i]).Index();
            ClassIndex = trainSet[index].GetClassIndex();
            vote[ClassIndex] += ((Pair) simList[i]).Value();
        }

        if (ClassIndex == 0) {
            vote[ClassIndex] = vote[ClassIndex] * vote0;
        } else {
            vote[ClassIndex] = vote[ClassIndex] * vote1;
        }

        int clsId = (int) instance.GetClassIndex();
        double val = CalculateClassConf(vote, clsId);

        if (val < Double.MIN_VALUE) {
            val = 0.0;
        } else if (val > Double.MAX_VALUE) {
            val = Double.MAX_VALUE;
        } else if (Double.isNaN(val)) {
            val = 0.0;
        }

        return val;
    }

    //this method is called for each instance of  the data set
    public Pair Predict(double[] instance, double vote0, double vote1) {
        Pair[] simList = new Pair[trainSet.length];
        int dataLength = trainSet.length;

//        System.out.println("trainSet"+trainSet.length);
        for (int i = 0; i < dataLength; i++) {
            double simValue = similarityMeasure.GetSimilarity(instance, trainSet[i].GetElements());
            simList[i] = new Pair(i, simValue);
        }

        Arrays.sort(simList);
        double[] vote = new double[m_ClassList.length];
        int ClassIndex = 0;

        for (int i = 0; i < m_K; i++) {
            int index = ((Pair) simList[i]).Index();
            ClassIndex = trainSet[index].GetClassIndex();
            vote[ClassIndex] += ((Pair) simList[i]).Value();
        }

        // vote to lessen the effect of imbalancedness in the dataset
        if (ClassIndex == 0) {
            vote[ClassIndex] = vote[ClassIndex] * vote0;
        } else {
            vote[ClassIndex] = vote[ClassIndex] * vote1;
        }

        int clsIndex = 0;
        for (int i = 1; i < m_ClassList.length; i++) {
            if (vote[clsIndex] < vote[i]) {
                clsIndex = i;
            }
        }

        double confidence = CalculateClassConf(vote, clsIndex);

        return new Pair(clsIndex, confidence);
    }

    public Pair Predict(double[] instance, double[] values) {
        Pair[] simList = new Pair[trainSet.length];
        int dataLength = trainSet.length;

        double[] ccwList = new double[trainSet.length];

//        System.out.println("trainSet"+trainSet.length);
        for (int i = 0; i < dataLength; i++) {
            double simValue = similarityMeasure.GetSimilarity(instance, trainSet[i].GetElements());
            simList[i] = new Pair(i, simValue);
        }

        //to calculate ccw for training data
        /*
         for (int i = 0; i < dataLength; i++) {
         System.out.println("confweights "+i+" "+values[i]);
         }
         */
        Arrays.sort(simList);
        double[] vote = new double[m_ClassList.length];
        int ClassIndex = 0;

        for (int i = 0; i < m_K; i++) {
            int index = ((Pair) simList[i]).Index();
            ClassIndex = trainSet[index].GetClassIndex();
            vote[ClassIndex] += ((Pair) simList[i]).Value();
            vote[ClassIndex] = vote[ClassIndex] * values[index];
        }
        //ccw weight for each training tuple should be gained & should incorporate in the above

        // vote to lessen the effect of imbalancedness in the dataset
//        if (ClassIndex == 0) {
//            vote[ClassIndex] = vote[ClassIndex] * ccw;
//        } else {
//            vote[ClassIndex] = vote[ClassIndex] * ccw;
//        }
        int clsIndex = 0;
        for (int i = 1; i < m_ClassList.length; i++) {
            if (vote[clsIndex] < vote[i]) {
                clsIndex = i;
            }
        }

        double confidence = CalculateClassConf(vote, clsIndex);

        return new Pair(clsIndex, confidence);
    }

    public double CalculateClassConf(double[] vote, int clsId) {
        double conf;
        double totconf = 0.0;

        for (int i = 0; i < vote.length; i++) {
            totconf = vote[i] + totconf;
        }

        if (vote[clsId] > Double.MAX_VALUE) {
            conf = 1.0;
        } else {
            //System.out.println("vote[clsId],totconf "+ vote[clsId]+" ,"+totconf);
            conf = (vote[clsId] / totconf);
            if (Double.isNaN(conf)) {
                conf = 0.0;
            }
        }

        return conf;
    }
}
