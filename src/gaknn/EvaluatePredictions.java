/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn;

import gaknn.evaluator.Evaluator;
import gaknn.predictor.Predictor;
import org.jgap.*;
import gaknn.similarity.AbstractSimilarity;
import java.io.*;
import java.util.*;

/**
 *
 * @author Niro
 */
public class EvaluatePredictions extends FitnessFunction implements Serializable {

    int m_ChromosomeLength;
    AbstractSimilarity m_Similarity;
    Predictor m_Predictor;
    Evaluator m_Evaluator;
    int m_K;
    boolean m_FindK = true;
//    boolean m_FindK = false;

    public EvaluatePredictions(AbstractSimilarity sim, Predictor pred, Evaluator eval) {
        super();
        m_Similarity = sim;
        m_Predictor = pred;
        m_Evaluator = eval;
    }

    public void FindK(boolean find) {
        m_FindK = find;
    }

    public void SetChromosomeLength(int length) {
        m_ChromosomeLength = length;
    }

    public void SetSimilarity(AbstractSimilarity sim) {
        m_Similarity = sim;
    }

    public void SetPredictor(Predictor pred) {
        m_Predictor = pred;
    }

    public void SetK(int k) {
        m_K = k;
    }

    public double evaluate(IChromosome a_chrom) {

        int weightVecSize;
        double fitness = 0.0;

        double testfit;

        if (m_FindK) {
            weightVecSize = a_chrom.size() - 1;
            m_K = (Integer) a_chrom.getGene(weightVecSize).getAllele();
        } else {
            weightVecSize = a_chrom.size();
        }

        double[] weights = new double[weightVecSize];

        for (int i = 0; i < weightVecSize; i++) {
            weights[i] = ((Double) a_chrom.getGene(i).getAllele());
        }

        m_Predictor.setK(m_K);
        m_Similarity.SetWeights(weights);
        m_Predictor.setSimilarity(m_Similarity);
        m_Evaluator.SetPredictor(m_Predictor);

        try {
            fitness = m_Evaluator.Evaluate();
        } catch (Exception e) {
            System.out.println(" " + e.toString());
        }

//        System.out.println("fitness value: "+fitness); // 06-08-2013
        //testfit =( 1.0 / fitness)*100.0;
//System.out.println("fitness value from evaluate: "+testfit);
        return ((1.0 / fitness) * 100.0);
    }

}
