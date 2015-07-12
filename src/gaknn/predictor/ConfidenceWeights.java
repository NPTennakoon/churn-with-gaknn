/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.predictor;

import gaknn.core.Attribute;
import gaknn.core.FastVector;
import gaknn.core.Instance;
import gaknn.Statistics;
import gaknn.gui.Frame4;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author admin
 */
public class ConfidenceWeights {

    /**
     * Considering the k-component mixture distribution, a class confidence
     * weight is calculated based on the probability density function (pdf)
     *
     */
    private static FastVector m_Attributes;
    private static double[][] t0Set;
    private static double[][] t1Set;
    private static double[] onemean;
    private static double[] zeromean;
    private static double[] oneSTD;
    private static double[] zeroSTD;
    private static double[] updatedmean1;
    private static double[] updatedmean0;
    private static double[] updatedvariance1;
    private static double[] updatedvariance0;
    private static double mixingProb;
    private double priorzero;
    private double priorone;
    public double[][] bayesValues;

    public ConfidenceWeights(FastVector attributes, Instance[] train) {
        m_Attributes = attributes;
    }

    /*this method split the data set in to two, so that one set contains the
     * data when the class label is one (1)(true) and the other contains the data
     * when the class label is zero (0)(false)
     */
    public void splitSet(double[][] train, int[] trainIndex, int ClassAttribIndex) {
        int ind = train.length;
        //   System.out.println("to split train" + ind);

        int t1index = 0, t0index = 0;

        int count0 = 0, count1 = 0;

        for (int i = 0; i < ind; i++) {
//            System.out.println("val ind" + train[i][1]); // reads only the coulmn index 1 (2nd column)
            //     System.out.println("class value" + trainIndex[i]);

            if (trainIndex[i] == 0) {
                count0 = count0 + 1;
            } else if (trainIndex[i] == 1) {
                count1 = count1 + 1;
            }
        }// end for

        t0Set = new double[count0][ClassAttribIndex];
        t1Set = new double[count1][ClassAttribIndex];

        for (int k = 0; k < ind; k++) {
            if (trainIndex[k] == 0) {
                for (int n = 0; n < ClassAttribIndex; n++) {
                    t0Set[t0index][n] = train[k][n];

//                    System.out.println("train 0 "+train[k][n]);
                }
                t0index++;
            } else if (trainIndex[k] == 1) {
                for (int n = 0; n < ClassAttribIndex; n++) {
                    t1Set[t1index][n] = train[k][n];

//                    System.out.println("train 1 "+train[k][n]);
                }
                t1index++;
            }
        }// end for

        //  System.out.println("---------------");
        //to test if the data assigns correctly
        /*
         for (int i = 0; i < t1index; i++) {
         for (int j = 0; j < ClassAttribIndex; j++) {
         //         System.out.print(t1Set[i][j] + " ");
         }
         //     System.out.println("");
         }
         */
        priorzero = (double) count0 / (count1 + count0);
        priorone = (double) count1 / (count1 + count0);

        System.out.println("count1" + count1);
        System.out.println("count0" + count0);

        System.out.println("priorone" + priorone);
    }

    public double[][] oneset() {
        return t1Set;
    }

    public double[][] zeroset() {
        return t0Set;
    }

    public double priorone() {
        return priorone;
    }

    public double priorzero() {
        return priorzero;
    }

    public double[] oneMean(int ClassAttribIndex) {

        onemean = new double[ClassAttribIndex];

        /*along attributes(columns)*/
        for (int i = 0; i < ClassAttribIndex; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);

            //  System.out.println("attribute: " + i + " " + attribute.Name());

            /*checks if the attribute is categorical or continuos*/
            if (attribute.Type() != Attribute.NOMINAL) {
                onemean[i] = Statistics.Mean(t1Set, i);
                //     System.out.println(i + "onemean  " + onemean[i]);
            }
        }
        return onemean;
    }

    public double[] zeroMean(int ClassAttribIndex) {

        zeromean = new double[ClassAttribIndex];

        /*along attributes(columns)*/
        for (int i = 0; i < ClassAttribIndex; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);

            // System.out.println("attribute: " + i + " " + attribute.Name());

            /*checks if the attribute is categorical or continuos*/
            if (attribute.Type() != Attribute.NOMINAL) {
                zeromean[i] = Statistics.Mean(t0Set, i);
                //    System.out.println(i + "zeromean " + zeromean[i]);
            }
        }
        return zeromean;
    }

    public double[] oneSTD(int ClassAttribIndex) {

        oneSTD = new double[ClassAttribIndex];

        /*along attributes(columns)*/
        for (int i = 0; i < ClassAttribIndex; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);

            //System.out.println("attribute: " + i + " " + attribute.Name());

            /*checks if the attribute is categorical or continuos*/
            if (attribute.Type() != Attribute.NOMINAL) {
                oneSTD[i] = Statistics.STD(t1Set, i);
                //    System.out.println(i + "oneSTD  " + oneSTD[i]);
            }
        }
        return oneSTD;
    }

    public double[] zeroSTD(int ClassAttribIndex) {

        zeroSTD = new double[ClassAttribIndex];

        /*along attributes(columns)*/
        for (int i = 0; i < ClassAttribIndex; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);

            //System.out.println("attribute: " + i + " " + attribute.Name());

            /*checks if the attribute is categorical or continuos*/
            if (attribute.Type() != Attribute.NOMINAL) {
                zeroSTD[i] = Statistics.STD(t0Set, i);
                //  System.out.println(i + "zeromean " + zeroSTD[i]);
            }
        }
        return zeroSTD;
    }

    public void getBayesValues(int datarows, int attribs) throws Exception {
        int a = datarows;
        int b = attribs;
        bayesValues = new double[a][b];
        //double bayesValues;

        System.out.println("dataro" + a);
        System.out.println("attrs" + b);

        //to read in the Baysian Networks values
        //Get scanner instance
        Scanner scanner = new Scanner(new File("bayes.csv"));

        //Set the delimiter used in file
        scanner.useDelimiter(",");

        //Get all tokens and store them in some data structure
        //I am just printing them
        //while (scanner.hasNext())
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                //System.out.print(scanner.next() + "|");
                bayesValues[i][j] = Double.valueOf(scanner.next());
                //System.out.print(i + bayesValues[i][j] + " ");
            }

        }

        //Do not forget to close the scanner
        scanner.close();

    }

    /* This method better be called as same as the 'Predict' method is called.
     * for each instance in the test data set (only one instance is passed to the method once)
     * values obtain for mean when class is zero and one are passed in
     */
    public double CalculateProbs(double[] instance, double[] means, double[] stds, double[][] tSet, double[] bayes) throws IOException {

        double pdf = 1.0, p = 0.0, a = 0.0, b = 0.0;

        /*along attributes(columns)*/
        for (int i = 0; i < instance.length; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);

            //  System.out.println("attribute: " + i + " " + attribute.Name());

            /*checks if the attribute is categorical or continuos*/
            if (attribute.Type() != Attribute.NOMINAL) {
                // System.out.println("inst" + instance[i]);
                double t = Math.abs(instance[i] - means[i]);
                a = Math.exp((-1) * (t * t) / (2 * stds[i] * stds[i]));
                b = (Math.sqrt(2 * Math.PI) * stds[i] * stds[i]);

                if (a > 0 && b > 0) {
                    p = a / b;
                } else {
                    //p = 1.0;
                    a = a + 1;
                    b = b + 1;
                    p = a / b;
                }
                // pdf = pdf * p;
//                System.out.println("a  " + a);
//                System.out.println("b  " + b);
//                System.out.println("p    " + p);
            } else {
                /*values obtained from the Bayesian Networks to be incoporated here.
                 * values stored in the bayes.csvfile to be read in.
                 */
                //System.out.println("nominal found");
                //System.out.println(bayes[i]);
                if (bayes[i] != 0) {
                    p = bayes[i];
                }
            }
        }
        pdf = pdf * p;
        //   System.out.println("pdf" + pdf);
        return pdf;
    }

    public double[] updatedmean0(int Index, double[][] trainSet, double mixingpro, double[] bayes) throws IOException {
        updatedmean0 = new double[Index];

        double a = 0.0, b = 0.0;

        //along the attributes
        for (int i = 0; i < Index; i++) {
            //along the rows
            for (int j = 0; j < trainSet.length; j++) {
                a = trainSet[j][i] * evalue0(trainSet[j], t0Set, mixingpro, bayes);
                b = b + evalue0(trainSet[j], t0Set, mixingpro, bayes);
            }
            if (a == 0.0 && b == 0.0) {
                //do nothing
            } else {
                updatedmean0[i] = a / b;
            }
        }

        return updatedmean0;
    }

    public double[] updatedmean1(int Index, double[][] trainSet, double mixingpro, double[] bayes) throws IOException {
        updatedmean1 = new double[Index];

        double a = 0.0, b = 0.0;

        //along the attributes
        for (int i = 0; i < Index; i++) {
            //along the rows
            for (int j = 0; j < trainSet.length; j++) {
                a = trainSet[j][i] * evalue1(trainSet[j], t1Set, mixingpro, bayes);
                b = b + evalue1(trainSet[j], t1Set, mixingpro, bayes);
            }

            if (a == 0.0 && b == 0.0) {
                //do nothing
            } else {
                updatedmean1[i] = a / b;
            }
            // System.out.println("updatedmean1[i] in method" + updatedmean1[i]);
        }

        return updatedmean1;
    }

    public double[] updatedvariance0(int Index, double[][] trainSet, double mixingpro, double[] bayes) throws IOException {
        updatedvariance0 = new double[Index];

        double a = 0.0, b = 0.0;

        for (int i = 0; i < Index; i++) {
            for (int j = 0; j < trainSet.length; j++) {
                a = (trainSet[j][i] - updatedmean0[i]) * (trainSet[j][i] - updatedmean0[i]) * evalue0(trainSet[j], t0Set, mixingpro, bayes);
                b = b + evalue0(trainSet[j], t0Set, mixingpro, bayes);
            }
            if (a == 0.0 && b == 0.0) {
                //do nothing
            } else {
                updatedvariance0[i] = a / b;
            }
        }

        return updatedvariance0;
    }

    public double[] updatedvariance1(int Index, double[][] trainSet, double mixingpro, double[] bayes) throws IOException {
        updatedvariance1 = new double[Index];

        double a = 0.0, b = 0.0;

        for (int i = 0; i < Index; i++) {
            for (int j = 0; j < trainSet.length; j++) {
                a = (trainSet[j][i] - updatedmean1[i]) * (trainSet[j][i] - updatedmean1[i]) * evalue1(trainSet[j], t1Set, mixingpro, bayes);
                b = b + evalue1(trainSet[j], t1Set, mixingpro, bayes);
            }
            if (a == 0.0 && b == 0.0) {
                //do nothing
            } else {
                updatedvariance1[i] = a / b;
            }
        }

        return updatedvariance1;
    }

    public double mixingProb(double[][] trainSet, double[] inst, double mixingpro, double[] bayes) throws IOException {
        //mixingProb = 0.0;

        double a = 0.0;

        for (int j = 0; j < trainSet.length; j++) {
            a = a + evalue1(trainSet[j], t1Set, mixingpro, bayes);
        }
        mixingProb = a / trainSet.length;

        return mixingProb;
    }

    //calculates the evalue of a particular row(tuple)when class is 0
    public double evalue0(double[] inst, double[][] set, double mixingpro, double[] bayes) throws IOException {
        double evalue0 = 0.0;

        double x = 0.0, y = 0.0;

        double b = CalculateProbs(inst, zeromean, zeroSTD, set, bayes);
        x = (1 - mixingpro) * b;

        y = (mixingpro * CalculateProbs(inst, zeromean, zeroSTD, set, bayes)) + ((1 - mixingpro) * CalculateProbs(inst, zeromean, zeroSTD, set, bayes));

        if (x > 0 && y > 0) {
            evalue0 = x / y; //evalue for when class is 0
        } else {
            //System.out.println("0_ERROR.!!!!");
        }
        return evalue0;
    }

    //calculates the evalue of a particular row(tuple)when class is 1
    public double evalue1(double[] inst, double[][] set, double mixingpro, double[] bayes) throws IOException {
        double evalue1 = 0.0;

        double y = 0.0, z = 0.0;

        double b = CalculateProbs(inst, onemean, oneSTD, set, bayes);
        z = mixingpro * b;

        y = (mixingpro * CalculateProbs(inst, onemean, oneSTD, set, bayes)) + ((1 - mixingpro) * CalculateProbs(inst, onemean, oneSTD, set, bayes));

        if (z > 0 && y > 0) {
            evalue1 = z / y; //evalue for when class is 1
        } else {
            // System.out.println("1_ERROR.!!!!");
        }

        return evalue1;
    }

    /* Implementation of EM algorithm.
     * Done in two steps (E- step & M- step).
     * Result from the E- step is to be maximized & the corresponding results from the M-step
     * are to be used in CCW calculations.
     */
    public void EMimplementation(double[][] train, int ClassAttribIndex, double[] inst, int flag, double[] bayes) throws IOException {

        double currentValue = 0.0;
        double newValue = 0.0;

        double mixing[] = new double[2];
        mixing[0] = priorzero;
        mixing[1] = priorone;
        // System.out.println("mix: "+mixing[1]);

        if (flag == 1) {
            newValue = evalue1(inst, t1Set, mixing[1], bayes);
        } else {
            newValue = evalue0(inst, t0Set, mixing[0], bayes);
        }
        //  System.out.println("currentValue: " + newValue);

        do {
            currentValue = newValue;
            // E- Step
            mixingProb = mixingProb(train, inst, mixing[1], bayes);
            //     System.out.println("mixing prob"+mixingProb);
            // new parameters are assigned 
            if (mixing[1] != mixingProb) {
                mixing[1] = mixingProb;
                mixing[0] = 1 - mixingProb;
            }
            // System.out.println("mixing[1]" + mixing[1]);

            if (flag == 1) {
                newValue = evalue1(inst, t1Set, mixing[1], bayes);
            } else {
                newValue = evalue0(inst, t0Set, mixing[1], bayes);

            }
            // System.out.println("newValue: " + newValue);

            if (flag == 1) {
                onemean = updatedmean1(ClassAttribIndex, train, mixing[1], bayes);
                for (int i = 0; i < ClassAttribIndex; i++) {
                    // System.out.println("mean[1]" + updatedmean1[i]);
                }
                oneSTD = updatedvariance1(ClassAttribIndex, train, mixing[1], bayes);
//                for (int i = 0; i < ClassAttribIndex; i++) {
//                    //oneSTD[i] = updatedvariance1[i];
////                    System.out.println("std[i]" + updatedvariance1[i]);
//                }
            } else {
                zeromean = updatedmean0(ClassAttribIndex, train, mixing[1], bayes);

                for (int i = 0; i < ClassAttribIndex; i++) {
                    //  System.out.println("mean[o]" + updatedmean0[i]);
                }
                zeroSTD = updatedvariance0(ClassAttribIndex, train, mixing[1], bayes);
            }

        } while (newValue > currentValue);
        //System.out.println("---------------while loop ended---------------------");
// take the values obtained when newValue is the highest

        //M- Step: means and variances are updated with results obtained in the E-Step
    }


    /*Calculates Class Confidence Weight
     * In this case, number of components is considered 2,
     * being having only two classes.
     */
    public double CalConfidenceWeight(double[][] train, int ClassAttribIndex, double[] inst, int flag, double[] bayes) throws IOException {
        double CCW = 0.0;

        EMimplementation(train, ClassAttribIndex, inst, flag, bayes);
        //System.out.println("m mixingProb" + mixingProb);
        double mixing[] = new double[2];
        mixing[0] = 1 - mixingProb;
        mixing[1] = mixingProb;

        //outer for loop iterates through attributes
//        for (int i = 0; i < ClassAttribIndex; i++) {
        //inner for loop iterates through components
        for (int j = 0; j <= 1; j++) {
            if (flag == 1) {
                CCW = mixing[j] * CalculateProbs(inst, onemean, oneSTD, t1Set, bayes);
            } else {
                CCW = mixing[j] * CalculateProbs(inst, zeromean, zeroSTD, t0Set, bayes);
            }
        }
        // System.out.println("ccw : " + CCW);
//        }
        return CCW;
    }

    public double[] ClassConfidenceWeight(int ClassAttribIndex, int[] trainIndex, double[][] train) throws IOException, Exception {

        double[] ccw = new double[train.length];

        splitSet(train, trainIndex, ClassAttribIndex);
        onemean = oneMean(ClassAttribIndex);
        zeromean = zeroMean(ClassAttribIndex);
        zeroSTD = zeroSTD(ClassAttribIndex);
        oneSTD = oneSTD(ClassAttribIndex);
        priorone = priorone();
        priorzero = priorzero();

        getBayesValues(train.length, ClassAttribIndex);

        int tot = train.length;
        //goes through the train dataset, instance by instance
        for (int i = 0; i < train.length; i++) {
            Frame4.jLabelProgress.setText("weighting instance " + (i + 1) + " of " + tot);
            Frame4.jLabelProgress.paintImmediately(Frame4.jLabelProgress.getVisibleRect());
            if (trainIndex[i] == 0) {
                ccw[i] = CalConfidenceWeight(train, ClassAttribIndex, train[i], 0, bayesValues[i]);
            } else {
                ccw[i] = CalConfidenceWeight(train, ClassAttribIndex, train[i], 1, bayesValues[i]);
            }
        }
        return ccw;
    }
}
