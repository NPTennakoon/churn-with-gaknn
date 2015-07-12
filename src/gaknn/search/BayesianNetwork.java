/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.search;

import gaknn.core.Attribute;
import gaknn.core.FastVector;
import gaknn.core.Instance;
import java.util.ArrayList;
import gaknn.search.DiscreteEstimatorBayes;
import gaknn.search.Estimator;

import gaknn.core.Instances;
import java.io.*;
import java.util.Enumeration;

/**
 *
 * @author admin
 */
public class BayesianNetwork {

    /**
     * The parent sets.
     */
    protected ParentSets[] m_ParentSets;
    private FastVector m_Attributes;
    /**
     * Holds prior on count
     */
    protected double m_fAlpha = 0.5;
    /**
     * The attribute estimators containing CPTs.
     */
    public Estimator[][] m_Distributions;
//public DiscreteEstimatorBayes[][] m_Distributions;
    private double[] b_instance;
    private double[][] b_instances;
    /**
     * Holds upper bound on number of parents
     */
    protected int m_nMaxNrOfParents = 1;
    /**
     * probability table data *
     */
    public double[][] m_fProbs;

    public BayesianNetwork(double[][] instances, FastVector attributes) {
        b_instances = instances;
        m_Attributes = attributes;
    }

    /**
     * Generates the classifier.
     *
     * @param instances set of instances serving as training data
     * @throws Exception if the classifier has not been generated successfully
     */
//  @Override
    public void buildClassifier(double[][] instances, int clsattindex, Instances m_Data) throws Exception {

        // can classifier handle the data?
//    getCapabilities().testWithFail(instances);
        // remove instances with missing class
//    Instances instancesi = new Instances(m_Data);
        //   instances.deleteWithMissingClass();
        // ensure we have a data set with discrete variables only and with no
        // missing values
        // instances = normalizeDataSet(instances);
        // Copy the instances
        //  m_Instances = new Instances(instancesi);
        //  m_NumInstances = m_Instances.Size();
        // sanity check: need more than 1 variable in datat set
        //   m_NumClasses = instancesi.NumberofClasses();
        // build the network structure
        initStructure(clsattindex);

        // build the network structure
        buildStructure(instances, clsattindex, m_Data);

        // build the set of CPTs
        estimateCPTs(instances, clsattindex, m_Data);

//        for (int att = 0; att < clsattindex; att++) {
//            double[][] probs = getDistribution(att, m_Data);
//            m_fProbs = new double[probs.length][probs[0].length];
//            System.out.println("probs.length"+probs.length);
//            System.out.println("probs[0].length"+probs[0].length);
//            for (int i = 0; i < probs.length; i++) {
//                for (int j = 0; j < probs[0].length; j++) {
//                    m_fProbs[i][j] = probs[i][j];
////                System.out.println(" m_fProbs "+i + " " + j + " " + m_fProbs[i][j]);
//                    System.out.println(" m_fProbs " + att + " " + i + " " + j + " " + m_fProbs[i][j]);
//                }
//            }
//        }//end for (att)
    } // buildClassifier

    /*To utilize the conditional probabilties and stored in m_fProbs
     * the following method to be executed for each instance(row) of
     * the data set.
     */
    public void bnValues(int ClassAttribIndex, double[][] instances, int[] trainIndex, Instances m_Data) throws Exception {
        double cp = 0.0, cps = 1.0;
        double bnps[] = new double[trainIndex.length];
        double cpv[][] = new double[instances.length][ClassAttribIndex];

        for (int att = 0; att < ClassAttribIndex; att++) {
            double[][] probs = getDistribution(att, m_Data);
            m_fProbs = new double[probs.length][probs[0].length];
            System.out.println("probs.length" + probs.length);
            System.out.println("probs[0].length" + probs[0].length);
            for (int i = 0; i < probs.length; i++) {
                for (int j = 0; j < probs[0].length; j++) {
                    m_fProbs[i][j] = probs[i][j];
//                System.out.println(" m_fProbs "+i + " " + j + " " + m_fProbs[i][j]);
                    System.out.println(" m_fProbs " + att + " " + i + " " + j + " " + m_fProbs[i][j]);

                }
            }
            for (int i2 = 0; i2 < instances.length; i2++) {
                //   for (int j2= 0; j2 < ClassAttribIndex; j2++) {
                System.out.println("instances" + i2 + " " + att + " " + instances[i2][att]);
                int datavalue = (int) instances[i2][att];

                System.out.println("datavalue" + datavalue);
                int classvalue = trainIndex[i2];
                System.out.println("classvalue" + classvalue);
                cp = m_fProbs[classvalue][datavalue];
                System.out.println("cp" + cp);

                cpv[i2][att] = cp;
                //}
            }
//            cps=cps*cp;
//            bnps[i2]=cps;
        }//end for (att)

        PrintStream result = new PrintStream(new FileOutputStream("bayes.csv"));
        System.setOut(result);

        for (int i = 0; i < instances.length; i++) {
            for (int j = 0; j < ClassAttribIndex; j++) {
                //if(cpv[i][j] !=0)
                result.print(cpv[i][j] + ",");
//                System.out.println("cpv"+i+" "+j+" "+cpv[i][j]);
                //cps=cps*cpv[i][j];
            }
            bnps[i] = cps;
            result.println();
        }
        result.close();

    }

    /**
     * buildStructure determines the network structure/graph of the network. The
     * default behavior is creating a network where all nodes have the first
     * node as its parent (i.e., a BayesNet that behaves like a naive Bayes
     * classifier). This method can be overridden by derived classes to restrict
     * the class of network structures that are acceptable.
     *
     * @param bayesNet the network
     * @param instances the data to use
     * @throws Exception if something goes wrong
     */
    public void buildStructure(double[][] instances, int ClassAttribIndex, Instances m_Data)
            throws Exception {

        initStructure(ClassAttribIndex);

//    if (m_sInitalBIFFile != null && !m_sInitalBIFFile.equals("")) {
//      BIFReader initialNet = new BIFReader().processFile(m_sInitalBIFFile);
//      for (int iAttribute = 0; iAttribute < ClassAttribIndex; iAttribute++) {
//        int iNode = initialNet.getNode(bayesNet.getNodeName(iAttribute));
//        for (int iParent = 0; iParent < initialNet.getNrOfParents(iAttribute); iParent++) {
//          String sParent = initialNet.getNodeName(initialNet.getParent(iNode,
//            iParent));
//          int nParent = 0;
//          while (nParent < bayesNet.getNrOfNodes()
//            && !bayesNet.getNodeName(nParent).equals(sParent)) {
//            nParent++;
//          }
//          if (nParent < bayesNet.getNrOfNodes()) {
//            bayesNet.getParentSet(iAttribute).addParent(nParent, instances);
//          } else {
//            System.err
//              .println("Warning: Node "
//                + sParent
//                + " is ignored. It is found in initial network but not in data set.");
//          }
//        }//end for iparent
//      }// end for iattribute
//    } else if (m_bInitAsNaiveBayes) {
        //     int iClass = instances.GetClassIndex();
        int iClass = m_Data.GetClassIndex();
        //   System.out.println("iClass" + iClass);
        // initialize parent sets to have arrow from classifier node to
        // each of the other nodes
        for (int iAttribute = 0; iAttribute < m_Data.NumAttributes(); iAttribute++) {
            if (iAttribute != iClass) {
                //       System.out.println("inside buildstructure");
                getParentSet(iAttribute).addParent(iClass, m_Data);
            }
        }
        // }//end elseif
        // search(bayesNet, instances);
        bayesianNetwork(instances, ClassAttribIndex, m_Data);
//    if (m_bMarkovBlanketClassifier) {
//      doMarkovBlanketCorrection(bayesNet, instances);
//    }
    } // buildStructure

    //public void bayesianNetwork(Instances instances) throws Exception {
    public void bayesianNetwork(double[][] instances, int ClassAttribIndex, Instances m_Data) throws Exception {

        //    int nOrder[] = new int[instances.NumAttributes()];
        int nOrder[] = new int[ClassAttribIndex];

        //  nOrder[0] = instances.GetClassIndex();
        nOrder[0] = ClassAttribIndex;

        int nAttribute = 0;
        System.out.println("norder" + nOrder[0]);
        //initial ordering on the nodes(instances)
        for (int iOrder = 1; iOrder < ClassAttribIndex; iOrder++) {
            if (nAttribute == ClassAttribIndex) {
                nAttribute++;
            }
            // System.out.println("nAttribute"+nAttribute);
            nOrder[iOrder] = nAttribute++;
        }
//        for (int iOrder = 1; iOrder < instances.NumAttributes(); iOrder++) {
//            if (nAttribute == instances.GetClassIndex()) {
//                nAttribute++;
//            }
//            nOrder[iOrder] = nAttribute++;
//        }

        // System.out.println("instances.NumAttributes()" + ClassAttribIndex);
        // determine base scores
        double fBaseScore = calcScore(instances, ClassAttribIndex, m_Data);
        //     System.out.println("fBaseScore" + fBaseScore);

        // K2 algorithm: greedy search restricted by ordering
        for (int iOrder = 1; iOrder < m_Data.NumAttributes(); iOrder++) {
            int iAttribute = nOrder[iOrder];
            double fBestScore = fBaseScore;

            boolean bProgress = (getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents());
            while (bProgress && (getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents())) {
                int nBestAttribute = -1;
                for (int iOrder2 = 0; iOrder2 < iOrder; iOrder2++) {
                    int iAttribute2 = nOrder[iOrder2];
                    double fScore = calcScoreWithExtraParent(iAttribute, iAttribute2, instances, ClassAttribIndex, m_Data);
                    if (fScore > fBestScore) {
                        fBestScore = fScore;
                        nBestAttribute = iAttribute2;
                    }
                }
                if (nBestAttribute != -1) {
                    getParentSet(iAttribute).addParent(nBestAttribute, m_Data);
                    fBaseScore = fBestScore;
                    bProgress = true;
                } else {
                    bProgress = false;
                }
            }
        }

    }//bayesianNetwork

    public double calcScore(double[][] instances, int clsattindex, Instances m_Data) throws Exception {

        double LOCV = 0.0;
        LOCV = leaveOneOutCV(instances, clsattindex, m_Data);
        //  System.out.println("locv" + LOCV);
        return LOCV;

    } // calcScore

    public double leaveOneOutCV(double[][] instances, int clsattindex, Instances m_Data) throws Exception {

        double fAccuracy = 0.0;
        double fWeight = 0.0;

        b_instance = new double[clsattindex];
        // double[] instance = new double[clsattindex];

        estimateCPTs(instances, clsattindex, m_Data);
//        System.out.println("instances.Size()" + instances.length);

//        for (int iInstance = 0; iInstance < instances.length; iInstance++) {
//            //instance[iInstance] = instances.instance(iInstance);
//            for (int j = 0; j < clsattindex; j++) {
//                b_instance = instances[j];
//            }
////            System.out.println("instance[iInstance]" + "==" + iInstance + "--" + b_instance);
//
////           b_instance.setWeight(-b_instance.weight());
//           b_instance[iInstance].setWeight(-b_instance[iInstance].weight());
//            updateClassifier(b_instance, m_Data);
//            fAccuracy += accuracyIncrease(b_instance[iInstance]);
//            fWeight += b_instance[iInstance].weight();
//            instance[iInstance].setWeight(-instance[iInstance].weight());
////            updateClassifier(instance[iInstance]);
//            updateClassifier(b_instance, m_Data);
//        }// end for
        for (int iInstance = 0; iInstance < m_Data.Size(); iInstance++) {
            Instance instance = new Instance(m_Data.DataSet()[iInstance]);
            for (int j = 0; j < clsattindex; j++) {
                b_instance = instances[j];
            }
            //   System.out.println("-instance.weight()"+instance.weight());
            instance.setWeight(-instance.weight());
            updateClassifier(b_instance, m_Data);
            fAccuracy += accuracyIncrease(b_instance, m_Data);
            fWeight += instance.weight();
            instance.setWeight(-instance.weight());
            updateClassifier(b_instance, m_Data);
        }

//        System.out.println("fWeight" + fWeight);
//        System.out.println("fAccuracy " + fAccuracy);
        return fAccuracy / fWeight;
    } // LeaveOneOutCV

    public void estimateCPTs(double[][] instances, int clsattindex, Instances m_Data) throws Exception {
        initCPTs(instances, clsattindex, m_Data);
        /*
         // Compute counts
         Enumeration<double[]> enumInsts = enumerateInstances();
         //double[] instance;
         while (enumInsts.hasMoreElements()) {
         b_instance = (double[]) enumInsts.nextElement();

         updateClassifier(b_instance);
         }
         */
        for (int i = 0; i < instances.length; i++) {
            updateClassifier(instances[i], m_Data);
        }

    } // estimateCPTs

    /**
     * Updates the classifier with the given instance.
     *
     * @param bayesNet the bayes net to use
     * @param instance the new training instance to include in the model
     * @throws Exception if the instance could not be incorporated in the model.
     */
    public void updateClassifier(double[] instance, Instances m_Data) throws Exception {
        for (int iAttribute = 0; iAttribute < instance.length; iAttribute++) {
            double iCPT = 0;

            for (int iParent = 0; iParent < getParentSet(iAttribute).getNrOfParents(); iParent++) {
                int nParent = getParentSet(iAttribute).getParent(iParent);

//           System.out.println("iparent " + iParent + " cParent " + getParentSet(iAttribute).getNrOfParents());
                // Attribute attribute;
                // attribute = (Attribute) m_Attributes.elementAt(nParent);
                int x = m_Data.Attribute(nParent - 1).NumValues();
                // System.out.println("x: " + x);
                iCPT = iCPT * x + instance[nParent - 1];
                //  iCPT = iCPT * attribute.NumValues() + instance[nParent];
            }
            // System.out.println("icpt" + iCPT);
            m_Distributions[iAttribute][(int) iCPT].addValue(instance[iAttribute], 1.0);
            //   System.out.println("iAttribute "+iAttribute+"iCPT "+iCPT+" "+m_Distributions[iAttribute][(int) iCPT]);
        }
    } // updateClassifier

    /**
     * accuracyIncrease determines how much the accuracy estimate should be
     * increased due to the contribution of a single given instance.
     *
     * @param instance : instance for which to calculate the accuracy increase.
     * @return increase in accuracy due to given instance.
     * @throws Exception passed on by distributionForInstance and
     * classifyInstance
     */
//  double accuracyIncrease(Instance instance) throws Exception {
//    if (m_bUseProb) {
//      double[] fProb = m_BayesNet.distributionForInstance(instance);
//      return fProb[(int) instance.GetClassValue()] * instance.weight();
//    } else {
//      if (m_BayesNet.classifyInstance(instance) == instance.GetClassValue()) {
//        return instance.weight();
//      }
//    }
//    return 0;
//  } // accuracyIncrease
    /**
     * Get full set of parent sets.
     *
     * @return parent sets;
     */
    public ParentSets[] getParentSets() {
        return m_ParentSets;
    }

    /**
     * get the parent set of a node
     *
     * @param iNode index of the node
     * @return Parent set of the specified node.
     */
    public ParentSets getParentSet(int iNode) {
//        System.out.println("iNode " + iNode);
        return m_ParentSets[iNode];
    }

    /**
     * initCPTs reserves space for CPTs and set all counts to zero
     *
     * @param bayesNet the bayes net to use
     * @throws Exception if something goes wrong
     */
    public void initCPTs(double[][] instances, int clsattindex, Instances m_Data) throws Exception {

        // Reserve space for CPTs
        int nMaxParentCardinality = 1;
        for (int iAttribute = 0; iAttribute < clsattindex; iAttribute++) {
            System.out.println("cardi" + getParentSet(iAttribute).getCardinalityOfParents());
            if (getParentSet(iAttribute).getCardinalityOfParents() > nMaxParentCardinality) {
                nMaxParentCardinality = getParentSet(iAttribute).getCardinalityOfParents();
//                System.out.println("iAttribute in if" + iAttribute);
            }
//            System.out.println("iAttribute " + iAttribute);
        }

        // Reserve plenty of memory
        m_Distributions = new Estimator[clsattindex][nMaxParentCardinality];

        // estimate CPTs
        for (int iAttribute = 0; iAttribute < clsattindex; iAttribute++) {
            for (int iParent = 0; iParent < getParentSet(iAttribute).getCardinalityOfParents(); iParent++) {

                m_Distributions[iAttribute][iParent] = new DiscreteEstimatorBayes(m_Data.Attribute(iAttribute).NumValues(), m_fAlpha);
                //System.out.println("[iAttribute]"+iAttribute+"[iParent]"+iParent+" "+m_Distributions[iAttribute][iParent]);
            }
        }
    } // initCPTs

    /**
     * Returns an enumeration of all instances in the dataset.
     *
     * @return enumeration of all instances in the dataset
     */
//  public/* @non_null pure@ */Enumeration<double[]> enumerateInstances() {
//
//        return new gaknnEnumeration<double[]>(b_instances);
//  }
    /**
     * Init structure initializes the structure to an empty graph or a Naive
     * Bayes graph (depending on the -N flag).
     *
     * @throws Exception in case of an error
     */
    public void initStructure(int clsattindex) throws Exception {

        // initialize topological ordering
        // m_nOrder = new int[m_Instances.numAttributes()];
        // m_nOrder[0] = m_Instances.classIndex();
//    int nAttribute = 0;
//
//    for (int iOrder = 1; iOrder < m_Instances.NumAttributes(); iOrder++) {
//      if (nAttribute == m_Instances.GetClassIndex()) {
//        nAttribute++;
//      }
//
//      // m_nOrder[iOrder] = nAttribute++;
//    }
        // reserve memory
        m_ParentSets = new ParentSets[clsattindex];

        for (int iAttribute = 0; iAttribute < clsattindex; iAttribute++) {
            m_ParentSets[iAttribute] = new ParentSets(clsattindex);
        }
    } // initStructure

    /**
     * accuracyIncrease determines how much the accuracy estimate should be
     * increased due to the contribution of a single given instance.
     *
     * @param instance : instance for which to calculate the accuracy increase.
     * @return increase in accuracy due to given instance.
     * @throws Exception passed on by distributionForInstance and
     * classifyInstance
     */
    double accuracyIncrease(double[] instance, Instances m_Data) throws Exception {

        double[] fProb = distributionForInstance(instance, m_Data);
        int classindex = m_Data.GetClassIndex();
        return fProb[(int) instance[classindex - 1]];
//      return fProb[(int) instance.GetClassValue()] * instance.weight();
    } // accuracyIncrease

    /**
     * Calculates the class membership probabilities for the given test
     * instance.
     *
     * @param bayesNet the bayes net to use
     * @param instance the instance to be classified
     * @return predicted class probability distribution
     * @throws Exception if there is a problem generating the prediction
     */
    public double[] distributionForInstance(double[] instance, Instances m_Data)
            throws Exception {
        //Instances instances = bayesNet.m_Instances;
        int nNumClasses = m_Data.NumberofClasses();
        double[] fProbs = new double[nNumClasses];

        for (int iClass = 0; iClass < nNumClasses; iClass++) {
            fProbs[iClass] = 1.0;
        }

        for (int iClass = 0; iClass < nNumClasses; iClass++) {
            double logfP = 0;

            for (int iAttribute = 0; iAttribute < m_Data.NumAttributes(); iAttribute++) {
                double iCPT = 0;

                for (int iParent = 0; iParent < getParentSet(iAttribute).getNrOfParents(); iParent++) {
                    int nParent = getParentSet(iAttribute).getParent(iParent);

                    if (nParent == m_Data.GetClassIndex()) {
                        iCPT = iCPT * nNumClasses + iClass;
                    } else {
                        // iCPT = iCPT * instances.attribute(nParent).numValues()+ instance.value(nParent);
                        iCPT = iCPT * m_Data.Attribute(nParent).NumValues() + instance[nParent];
                    }
                }

                if (iAttribute == m_Data.GetClassIndex()) {
                    // fP *=
                    // m_Distributions[iAttribute][(int) iCPT].getProbability(iClass);
                    logfP += Math.log(m_Distributions[iAttribute][(int) iCPT].getProbability(iClass));
                } else {
                    // fP *=
                    // m_Distributions[iAttribute][(int) iCPT]
                    // .getProbability(instance.value(iAttribute));
                    logfP += Math.log(m_Distributions[iAttribute][(int) iCPT].getProbability(instance[iAttribute]));
                }
            }

            // fProbs[iClass] *= fP;
            fProbs[iClass] += logfP;
        }

        // Find maximum
        double fMax = fProbs[0];
        for (int iClass = 0; iClass < nNumClasses; iClass++) {
            if (fProbs[iClass] > fMax) {
                fMax = fProbs[iClass];
            }
        }
        // transform from log-space to normal-space
        for (int iClass = 0; iClass < nNumClasses; iClass++) {
            fProbs[iClass] = Math.exp(fProbs[iClass] - fMax);
        }

        return fProbs;
    } // distributionForInstance

    /**
     * Sets the max number of parents
     *
     * @param nMaxNrOfParents the max number of parents
     */
    public void setMaxNrOfParents(int nMaxNrOfParents) {
        m_nMaxNrOfParents = nMaxNrOfParents;
    }

    /**
     * Gets the max number of parents.
     *
     * @return the max number of parents
     */
    public int getMaxNrOfParents() {
        return m_nMaxNrOfParents;
    }

    /**
     * Calc Node Score With Added Parent
     *
     * @param nNode node for which the score is calculate
     * @param nCandidateParent candidate parent to add to the existing parent
     * set
     * @return log score
     * @throws Exception if something goes wrong
     */
    public double calcScoreWithExtraParent(int nNode, int nCandidateParent, double[][] instances, int clsattindex, Instances m_Data)
            throws Exception {
        ParentSets oParentSet = getParentSet(nNode);
        // Instances instances = b_instance;

        // sanity check: nCandidateParent should not be in parent set already
        for (int iParent = 0; iParent < oParentSet.getNrOfParents(); iParent++) {
            if (oParentSet.getParent(iParent) == nCandidateParent) {
                return -1e100;
            }
        }

        // set up candidate parent
        oParentSet.addParent(nCandidateParent, m_Data);

        // calculate the score
        double fAccuracy = calcScore(instances, clsattindex, m_Data);

        // delete temporarily added parent
        oParentSet.deleteLastParent(m_Data);

        return fAccuracy;
    } // calcScoreWithExtraParent

    /**
     * returns distribution of a node in matrix form with matrix representing
     * distribution with P[i][j] = P(node = j | parent configuration = i)
     *
     * @param nTargetNode index of the node to get distribution from
     */
    public double[][] getDistribution(int nTargetNode, Instances m_Data) {
        int nParentCard = m_ParentSets[nTargetNode].getCardinalityOfParents();
        int nCard = m_Data.Attribute(nTargetNode).NumValues();
//        int nCard = 2;
        double[][] P = new double[nParentCard][nCard];
        for (int iParent = 0; iParent < nParentCard; iParent++) {
            for (int iValue = 0; iValue < nCard; iValue++) {
                P[iParent][iValue] = m_Distributions[nTargetNode][iParent].getProbability(iValue);
            }
        }
        return P;
    } // getDistribution
}
