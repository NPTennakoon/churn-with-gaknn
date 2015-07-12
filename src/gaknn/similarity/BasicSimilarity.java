/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.similarity;

import gaknn.core.Attribute;
import gaknn.core.FastVector;

/**
 *
 * @author Niro
 */
public class BasicSimilarity extends AbstractSimilarity {

    public BasicSimilarity(FastVector attributes) {
        super(attributes);
    }

    public double GetSimilarityN(double[] attrbuteSet1, double[] attrbuteSet2) {
        double simValue = 0.0;
        double dif = 0.0;
        int iWeightPos = 0;
        int numAttributes = attrbuteSet1.length;

        for (int i = 0; i < numAttributes; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);
            //System.out.println("attribute: "+i+" "+ attribute.Name()); //2013-04-06

            switch (attribute.Type()) {
                case Attribute.NOMINAL:
                    if (attrbuteSet1[i] == attrbuteSet2[i]) {
                        dif = 0.0;
                    } //                    dif = m_Weights[iWeightPos];
                    else {
                        dif = m_Weights[iWeightPos];
                    }
//                    dif = 0.0; //npt

                case Attribute.STRING:
                    if (attrbuteSet1[i] == attrbuteSet2[i]) {
                        dif = 0.0;
                    } else {
                        dif = m_Weights[iWeightPos];
                    }

                default:
//			dif = m_Weights[iWeightPos] * Math.abs(attrbuteSet1[i] - attrbuteSet2[i]);
                    // dif=m_Weights[iWeightPos] * Math.sqrt(Math.abs(attrbuteSet1[i]*attrbuteSet1[i] - attrbuteSet2[i]*attrbuteSet2[i]));
                    dif = m_Weights[iWeightPos] * Math.pow((attrbuteSet1[i] - attrbuteSet2[i]), 2.0);//npt
            }
            iWeightPos++;
            simValue += dif;
        }

        if (simValue < Double.MIN_VALUE) {
            simValue = Double.MAX_VALUE;
        } else //            simValue = 1.0/Math.sqrt(simValue);//npt
        {
            simValue = 1.0 / (simValue);
        }

        if (Double.isInfinite(simValue)) {
            simValue = Double.MAX_VALUE;
        }

//        System.out.println("similarity value:       "+simValue); //2013-04-06
        return simValue;
    }

    public double GetSimilarity(double[] attrbuteSet1, double[] attrbuteSet2) {
        double simValue = 0.0;
        double difno = 0.0, difnu = 0.0, dif = 0.0, difde = 0.0;
        int iWeightPos = 0;
        int sum = 0, match = 0;

        int numAttributes = attrbuteSet1.length;

        for (int i = 0; i < numAttributes; i++) {

            Attribute attribute;
            attribute = (Attribute) m_Attributes.elementAt(i);
//            System.out.println("attribute: "+i+" "+ attribute.Name()); //2013-04-06

            switch (attribute.Type()) {
                case Attribute.NOMINAL:
                    if (attrbuteSet1[i] == attrbuteSet2[i]) {
                        //match=match+1;
                        difno = 0.0;
//                    dif = m_Weights[iWeightPos];
                    } else {
                        difno = m_Weights[iWeightPos];
//                        System.out.println("att is nominal"+attribute.m_Name);
                    }
                    //   sum=sum+1;
                    break;

                case Attribute.NUMERIC:
                    difnu = difnu + (m_Weights[i] * Math.pow(Math.abs(attrbuteSet1[i] - attrbuteSet2[i]), 2.0));
//                        System.out.println("att is numeric "+attribute.m_Name);
//                    m=1;
                    break;

                case Attribute.STRING:
                    if (attrbuteSet1[i] == attrbuteSet2[i]) {
                        difno = 0.0;
                    } else {
                        difno = m_Weights[iWeightPos];
                    }
                    break;

                default: {
                    difde = m_Weights[iWeightPos] * Math.abs(attrbuteSet1[i] - attrbuteSet2[i]);
//                    dif = m_Weights[iWeightPos] * Math.sqrt(Math.abs(attrbuteSet1[i]*attrbuteSet1[i] - attrbuteSet2[i]*attrbuteSet2[i])); //npt
//            dif = m_Weights[iWeightPos] *(attrbuteSet1[i] - attrbuteSet2[i])*(attrbuteSet1[i] - attrbuteSet2[i]); //npt

//                      System.out.println("att is unknown other "+attribute.m_Name);
                }
                break;
            }
            iWeightPos++;
            dif = difno + difde;
            //simValue +=  dif;
//             sum=sum+m;
        } // end for
//         System.out.println("sum"+sum+"match"+match);
        //difno=(double)(sum-match)/sum;
        difnu = (double) Math.sqrt(difnu);

        dif = dif + difnu;
        // dif = difno + difnu + difde;
//         System.out.println("difno"+difno+"difnu"+difnu+"dif"+dif);
//	System.out.println("simValue: "+simValue);
//		simValue= Math.sqrt(simValue); //npt

//         System.out.println("sum: "+sum);
        simValue = dif;

        if (simValue < Double.MIN_VALUE) {
            simValue = Double.MAX_VALUE;
        } else {
            simValue = 1.0 / simValue;
        }
        // simValue = simValue/sum;

        if (Double.isInfinite(simValue)) {
            simValue = Double.MAX_VALUE;
        }

        // System.out.println("similarity value:       "+simValue); //2013-04-06
        return simValue;
    }
}
