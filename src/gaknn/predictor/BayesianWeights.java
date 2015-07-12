/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.predictor;

import gaknn.core.Attribute;
import gaknn.core.FastVector;
import gaknn.core.Instance;
import gaknn.Statistics;

/**
 *
 * @author admin
 */
public class BayesianWeights {

    /* Naive Bayesian conditional probability is calculated
     * as a confidence level on the prediction.
     * Hence, for numberic attributes (continuos variables) the relevant probablity density function
     * (pdf) is calculated.
     * When calculating the pdf, it assumed a gaussian distrubuiton in the data of
     * our interest.
     * g(x,mu,gemma)=
     */
    private static FastVector m_Attributes;
    private static double[][] t0Set;
    private static double[][] t1Set;
    private static double[] onemean;
    private static double[] zeromean;
    private static double[] oneSTD;
    private static double[] zeroSTD;
    private double priorzero;
    private double priorone;

    public BayesianWeights(FastVector attributes, Instance[] train) {
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

    /* This method better be called as same as the 'Predict' method is called.
     * for each instance in the test data set (only one instance is passed to the method once)
     * values obtain for mean when class is zero and one are passed in
     */
    public double CalculateProbs(double[] instance, double[] means, double[] stds, double[][] tSet) {

        double vote = 1.0;
        double pdf = 1.0, p = 0.0, a = 0.0, b = 0.0;

        //   System.out.println("instance.length" + instance.length);

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
                p = a / b;
                // pdf = pdf * p;
//                System.out.println("a  " + a);
//                System.out.println("b  " + b);
                //System.out.println("p    " + p);
                if (a > 0 && b > 0) {
                    p = a / b;
                } else {
                    a = a + 1;
                    b = b + 1;
                    p = a / b;
                }

            } else {
                //  System.out.println("att nominal");
                double cato = Statistics.Count(tSet, i, instance[i]);
                p = cato;
                // System.out.println("cat0 " + cato);
            }
        }
        vote = vote * p;
        // System.out.println("vote" + vote);
        return vote;
    }
}
