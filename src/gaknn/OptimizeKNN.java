/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn;

import gaknn.core.Attribute;
import gaknn.core.FastVector;
import gaknn.core.Instance;
import gaknn.core.Instances;
import gaknn.core.Pair;
import gaknn.dataaccess.ArffFileReader;
import gaknn.dataaccess.DataFileReader;
import gaknn.dataaccess.ParameterReader;
import gaknn.dataaccess.ParameterWriter;
import gaknn.datapreprocess.BasicValueHandler;
import gaknn.datapreprocess.NormalizeData;
import gaknn.evaluator.Evaluator;
import gaknn.evaluator.SimpleWeightEvaluator;
import gaknn.predictor.BayesianWeights;
import gaknn.predictor.ConfidenceWeights;
import gaknn.predictor.Predictor;
import gaknn.predictor.Predictor1;
import gaknn.similarity.AbstractSimilarity;
import gaknn.similarity.BasicSimilarity;
import gaknn.similarity.EuclideanSimilarity;
import gaknn.search.BayesianNetwork;
import gaknn.VisualizeData;

//import java.io.IOException;
import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;

import java.io.*;
import java.util.*;
import java.lang.*;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author Niro
 */
public class OptimizeKNN {

    private static int m_NumEvolutions;
//    private static int m_NumEvolutions = 1;
    private static int m_Population;
//    private static int m_Population = 20;
    private static int m_Mutation = 1000;
//    private static int m_Mutation = 100;
    private static double m_MinDoubleGeneVal = 0.0;
    private static double m_MaxDoubleGeneVal = 10.0;
    private static int m_MaxIntGeneVal = 10;
    private static String m_DataFilePath = "";
//    private static String m_DataFilePath = "E:\\Project\\TestFiles\\A.arff";
    private static String m_TestFilePath = "";
//    private static String m_TestFilePath = "E:\\Project\\TestFiles\\B.arff";
    public static Instance[] m_TrainingSet;
    private static Instance[] m_TrainingSet1; //for stratification
    public static Instance[] m_TestSet;
    private static FastVector m_Attributes;
    private static FastVector ms_Attributes;
    public static Instances m_Data;
    private static int k = 2;
    private static Instances ms_Data;
    private static int genNo;
//    private static boolean m_FindK = false;
    private static boolean m_FindK = true;
//    public static int m_ClassAttribIndex = 3;
    public static int m_ClassAttribIndex;
//    private static String m_ParameterFile = "E:\\Project\\TestFiles\\A.prm";
    public static String m_ParameterFile;
    private static double[] m_Weights;
    private static String m_task = "p";
    private static int test_option = 2;
    private static int confidenceType;
//added by Asanka
    private static ArffFileReader fileData;
    private static double DefaultValue = Double.NaN;
    public static DataFileReader fileReaderObject;
    public static DataFileReader fileReaderObjectSec;
    public static Pair[] predictions = null;
    private static double bestFitnessSoFarGlobalFold = 0.0;
    public static double[][] m_TrainingSetd;
    public static double[][] m_TestSetd;
    public static int[] m_TrainingSetdIndex;
    public static int[] m_TestSetdIndex;
    public static Pair[] predictionsd = null;
    public static File outputcsvfile; // this is the variable used to save the return file of creatfile method
    //used in instance weigthing
    public static double[] oneM = null;
    public static double[] zeroM = null;
    public static double[] zeroS = null;
    public static double[] oneS = null;
    public static double priorone = 0.0;
    public static double priorzero = 0.0;
    public static double[] values = null;

    public void setparameters(int evolutions, int population, String ParameterFile, String DataFilePath, String TestFilePath) {
        m_NumEvolutions = evolutions;
        m_Population = population;
        m_ParameterFile = ParameterFile;
        m_DataFilePath = DataFilePath;
        m_TestFilePath = TestFilePath;
    }

    public static void runOptimization() throws Exception {

        System.out.println("ro" + m_ParameterFile);

        //ReadData(m_DataFilePath);
        String[] ClassArray = m_Data.ClassArray();

        Configuration.reset();
        Configuration conf = new DefaultConfiguration();
        MutationOperator muteOp = new MutationOperator(conf, m_Mutation);
        conf.addGeneticOperator(muteOp);

        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);
//         AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);
        //System.out.println("similarity value:       "+Similarity.simValue);

        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        Evaluator evaluator = new SimpleWeightEvaluator(predictor, m_TestSet);

        System.out.println("int runop" + m_TestSetd[1].length);
        predictionsd = PredictInstances(m_TestSetd);

//        System.out.println("m_TestSetd"+m_TestSet[0].GetElementAt(1));
//        System.out.println("m_TestSetd"+m_TestSet[0].GetClassIndex());
//        System.out.println("predictions"+predictionsd[1].Index());
        PrintStream outval = new PrintStream(new FileOutputStream("output.csv"));
        System.setOut(outval);

        System.out.println("Actual" + "," + "Predicted");
        for (int i = 0; i < m_TestSet.length; i++) {
            System.out.print(m_TestSet[i].GetClassIndex() + ",");
            System.out.println(predictionsd[i].Index());
        }

        outval.close();

        VisualizeData visualizeaccv = new VisualizeData();
        visualizeaccv.accuracygraphing();

        EvaluatePredictions fitFunc = new EvaluatePredictions(simMeas, predictor, evaluator);
//    	fitFunc.SetK(k);
        fitFunc.FindK(m_FindK);
        conf.setFitnessFunction(fitFunc);

        int numAttributes = m_Data.NumAttributes();
        Gene[] sampleGenes;

        //System.out.println(" num Attribur" + numAttributes);
        int g;
        if (m_FindK) {
            sampleGenes = new Gene[numAttributes + 1];
        } else {
            sampleGenes = new Gene[numAttributes];
        }

        for (g = 0; g < numAttributes; g++) {
            sampleGenes[g] = new DoubleGene(conf, m_MinDoubleGeneVal, m_MaxDoubleGeneVal);
        }

        if (m_FindK) {
            sampleGenes[g] = new IntegerGene(conf, 1, m_MaxIntGeneVal);
        }

        IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        conf.setPopulationSize(m_Population);
        Genotype population = Genotype.randomInitialGenotype(conf);

        IChromosome bestSolutionSoFarGlobal = sampleChromosome;
        double bestFitnessSoFarGlobal = 0.0;
        double[] weights = new double[numAttributes];

        PrintStream graphfitness = new PrintStream(new FileOutputStream("fitness.csv"));
        System.setOut(graphfitness);

        System.out.println("gen" + "," + "Fitness");

        for (int i = 0; i < m_NumEvolutions; i++) {
            genNo = 0;
            population.evolve();
            IChromosome bestSolutionSoFar = population.getFittestChromosome();

            double fit = 0.0;
            fit = bestSolutionSoFar.getFitnessValue();

//            System.out.println("gen#"+i+" Fit "+bestSolutionSoFar.getFitnessValue());
            g = 0;
            while (g < numAttributes) {
                weights[g] = (Double) bestSolutionSoFar.getGene(g).getAllele();

                //	System.out.println("Weight value W" + g + ": " + weights[g]);
                g++;
            }

            if (m_FindK) {
                k = (Integer) bestSolutionSoFar.getGene(g).getAllele();
                // System.out.println(" Value k test: " + k);
            }

            if (fit > bestFitnessSoFarGlobal) {
                bestFitnessSoFarGlobal = fit;
                bestSolutionSoFarGlobal = bestSolutionSoFar;

            }
            // System.out.println("global "+i+" Fit "+ bestSolutionSoFarGlobal.getFitnessValue());
            System.out.println(i + "," + bestSolutionSoFarGlobal.getFitnessValue());
        }
        graphfitness.close();

        g = 0;
        PrintStream result = new PrintStream(new FileOutputStream("result.csv"));
        System.setOut(result);
        result.println("attribute" + "," + "weights");
        while (g < numAttributes) {
            weights[g] = (Double) bestSolutionSoFarGlobal.getGene(g).getAllele();
            // System.out.println("Weight value W" + g + ": " + weights[g]);
//            result.println("Weight value W" + g + ": " + weights[g]);

            result.println("W" + g + "," + weights[g]);
            g++;
        }

        result.close();

        if (m_FindK) {
            k = (Integer) bestSolutionSoFarGlobal.getGene(g).getAllele();
            // System.out.println(" Value k: " + k);
        }

        String parameterFileName = m_DataFilePath;
        parameterFileName.replaceFirst(ParameterWriter.FILE_EXTENSION, ArffFileReader.FILE_EXTENSION);
        int i = parameterFileName.lastIndexOf(ArffFileReader.FILE_EXTENSION);
        parameterFileName = parameterFileName.substring(0, i).concat(ParameterWriter.FILE_EXTENSION);
        //parameterFileName.replaceAll(ArffFileReader.FILE_EXTENSION,ParameterWriter.FILE_EXTENSION);
        ParameterWriter pWriter = new ParameterWriter(m_Attributes, parameterFileName);
        pWriter.Write(weights, k);
        pWriter = null;

        VisualizeData visualize = new VisualizeData();
        visualize.fitnessDistribution();
    }

    public static void crossValidate(int numFolds, double[][] trainSet, int weight, int[] trainIndexFull) throws Exception {
        int startpoint = 1;

        for (int numFold = 1; numFold <= numFolds; numFold++) {
            System.out.println("At Fold " + numFold);
            System.out.println("startpoint" + startpoint);
            startpoint = CreateFolds(numFolds, startpoint, numFold, trainSet, trainIndexFull);

            weightselector(m_TrainingSetd, m_TestSetd, OptimizeKNN.m_TestSetdIndex, OptimizeKNN.m_TrainingSetdIndex, weight);
            runOptimizationforCV();
        }
    }

    public static void runOptimizationforCV() throws Exception {

        //ReadData(m_DataFilePath);
        String[] ClassArray = m_Data.ClassArray();

        Configuration.reset();
        Configuration conf = new DefaultConfiguration();
        MutationOperator muteOp = new MutationOperator(conf, m_Mutation);
        conf.addGeneticOperator(muteOp);

        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);
//         AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);
        //System.out.println("similarity value:       "+Similarity.simValue);

        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        Evaluator evaluator = new SimpleWeightEvaluator(predictor, m_TestSet);

        predictionsd = PredictInstances(m_TestSetd);

        FileWriter fw = new FileWriter(outputcsvfile, true);

        for (int i = 0; i < m_TestSet.length; i++) {
            fw.append(m_TestSet[i].GetClassIndex() + ",");
            // fw.append(m_TestSet[i].GetElementAt(1) + ",");
            fw.append(Integer.toString(predictionsd[i].Index()));
            fw.append("\n");
        }

        fw.close();

//        System.out.println("m_TestSetd" + m_TestSet[0].GetElementAt(1));
//        System.out.println("m_TestSetd"+m_TestSet[0].GetClassIndex());
//        System.out.println("predictions"+predictionsd[1].Index());
//        PrintStream outval = new PrintStream(new FileOutputStream("output.csv"));
//        System.setOut(outval);
//
//        System.out.println("Actual" + "," + "Predicted");
//        for (int i = 0; i < m_TestSet.length; i++) {
//            System.out.print(m_TestSet[i].GetClassIndex() + ",");
//            System.out.println(predictionsd[i].Index());
//        }
//
//        outval.close();
        VisualizeData visualizeaccv = new VisualizeData();
        visualizeaccv.accuracygraphing();

        EvaluatePredictions fitFunc = new EvaluatePredictions(simMeas, predictor, evaluator);
//    	fitFunc.SetK(k);
        fitFunc.FindK(m_FindK);
        conf.setFitnessFunction(fitFunc);

        int numAttributes = m_Data.NumAttributes();
        Gene[] sampleGenes;

        //System.out.println(" num Attribur" + numAttributes);
        int g;
        if (m_FindK) {
            sampleGenes = new Gene[numAttributes + 1];
        } else {
            sampleGenes = new Gene[numAttributes];
        }

        for (g = 0; g < numAttributes; g++) {
            sampleGenes[g] = new DoubleGene(conf, m_MinDoubleGeneVal, m_MaxDoubleGeneVal);
        }

        if (m_FindK) {
            sampleGenes[g] = new IntegerGene(conf, 1, m_MaxIntGeneVal);
        }

        IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        conf.setPopulationSize(m_Population);
        Genotype population = Genotype.randomInitialGenotype(conf);

        IChromosome bestSolutionSoFarGlobal = sampleChromosome;
        double bestFitnessSoFarGlobal = 0.0;
        double[] weights = new double[numAttributes];

//        PrintStream graphfitness = new PrintStream(new FileOutputStream("fitness.csv"));
//        System.setOut(graphfitness);
//
//        System.out.println("gen" + "," + "Fitness");
        for (int i = 0; i < m_NumEvolutions; i++) {
            genNo = 0;
            population.evolve();
            IChromosome bestSolutionSoFar = population.getFittestChromosome();

            double fit = 0.0;
            fit = bestSolutionSoFar.getFitnessValue();

//            System.out.println("gen#"+i+" Fit "+bestSolutionSoFar.getFitnessValue());
            g = 0;
            while (g < numAttributes) {
                weights[g] = (Double) bestSolutionSoFar.getGene(g).getAllele();

                //	System.out.println("Weight value W" + g + ": " + weights[g]);
                g++;
            }

            if (m_FindK) {
                k = (Integer) bestSolutionSoFar.getGene(g).getAllele();
                // System.out.println(" Value k test: " + k);
            }

            if (fit > bestFitnessSoFarGlobal) {
                bestFitnessSoFarGlobal = fit;
                bestSolutionSoFarGlobal = bestSolutionSoFar;

            }
            // System.out.println("global "+i+" Fit "+ bestSolutionSoFarGlobal.getFitnessValue());
//            System.out.println(i + "," + bestSolutionSoFarGlobal.getFitnessValue());
        }
//        graphfitness.close();

//should repeat for each fold up to this point
        if (bestFitnessSoFarGlobalFold < bestFitnessSoFarGlobal) {
            bestFitnessSoFarGlobalFold = bestFitnessSoFarGlobal;
            System.out.println("bestFitnessSoFarGlobalFold : " + bestFitnessSoFarGlobalFold);
            g = 0;
//        PrintStream result = new PrintStream(new FileOutputStream("result.csv"));
//        System.setOut(result);
//        result.println("attribute" + "," + "weights");
            while (g < numAttributes) {
                weights[g] = (Double) bestSolutionSoFarGlobal.getGene(g).getAllele();
                // System.out.println("Weight value W" + g + ": " + weights[g]);
//            result.println("Weight value W" + g + ": " + weights[g]);

//            result.println("W" + g + "," + weights[g]);
                g++;
            }

//        result.close();
            if (m_FindK) {
                k = (Integer) bestSolutionSoFarGlobal.getGene(g).getAllele();
                // System.out.println(" Value k: " + k);
            }

            String parameterFileName = m_DataFilePath;
            parameterFileName.replaceFirst(ParameterWriter.FILE_EXTENSION, ArffFileReader.FILE_EXTENSION);
            int i = parameterFileName.lastIndexOf(ArffFileReader.FILE_EXTENSION);
            parameterFileName = parameterFileName.substring(0, i).concat(ParameterWriter.FILE_EXTENSION);
            //parameterFileName.replaceAll(ArffFileReader.FILE_EXTENSION,ParameterWriter.FILE_EXTENSION);
            ParameterWriter pWriter = new ParameterWriter(m_Attributes, parameterFileName);
            pWriter.Write(weights, k);
            pWriter = null;
        }//end if(bestFitnessSoFarGlobalFold<bestFitnessSoFarGlobal)

//        VisualizeData visualize = new VisualizeData();
//        visualize.fitnessDistribution();
    }

    public static void ReadData(String filePath) throws IOException {
        try {
            if (filePath.length() == 0) {
                throw new IOException("Missing file name");
            }

            ArffFileReader dataFileReader = new ArffFileReader(filePath);
            dataFileReader.SetValueHandler(new BasicValueHandler());

            // System.out.println("m_Data.NumAttributes()"+m_Data.NumAttributes());
            //int[] attr = new int[]{1,2,3,6}; // to test atttibute selection
            dataFileReader.ReadHeader();
            //System.out.println("header read");

            dataFileReader.SetClassIndex(m_ClassAttribIndex);
            System.out.println("m_ClassAttribIndex set " + m_ClassAttribIndex);
            dataFileReader.CreateDataSet();
            //   System.out.println("created dataset");
            dataFileReader.LoadData();
            System.out.println("data loaded");
            m_Data = dataFileReader.GetData();
            m_Data.Compact();
            System.out.println("size h       " + m_Data.Size());
            //m_Data.Normalize();

            m_Data.SetClassProperties();

            m_Attributes = m_Data.Attributes();
            fileReaderObject = dataFileReader;

            dataFileReader = null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void ReadDataSecondFile(String filePath) throws IOException {
        try {
            if (filePath.length() == 0) {
                throw new IOException("Missing file name");
            }

            ArffFileReader dataFileReader = new ArffFileReader(filePath);
            dataFileReader.SetValueHandler(new BasicValueHandler());

            //int[] attr = new int[]{1,2,3,6}; // to test atttibute selection
            dataFileReader.ReadHeader();

            //   dataFileReader.setSelectedAttributes(attr); // to test atttibute selection
            dataFileReader.SetClassIndex(m_ClassAttribIndex);
            dataFileReader.CreateDataSet();

            dataFileReader.LoadData();

            ms_Data = dataFileReader.GetData();
            ms_Data.Compact();
            System.out.println("size h sec       " + ms_Data.Size());
            //m_Data.Normalize();

            ms_Data.SetClassProperties();

            ms_Attributes = ms_Data.Attributes();
            fileReaderObjectSec = dataFileReader;

            dataFileReader = null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static double[][] ReadTestData(String testfile) throws IOException {
        if (testfile.length() == 0) {
            throw new IOException("Missing file name");
        }

        Instances testdata;
        double[][] Ntdata;
        ArffFileReader dataFileReader = new ArffFileReader(testfile);
        dataFileReader.SetValueHandler(new BasicValueHandler());

        dataFileReader.ReadHeader();
        dataFileReader.SetClassIndex(m_ClassAttribIndex);

        //dataFileReader.setSelectedAttributes(attr);
        dataFileReader.CreateDataSet();
        dataFileReader.LoadData();

        testdata = dataFileReader.GetData();
        testdata.Compact();
        System.out.println("size w       " + testdata.Size());
        //m_Data.Normalize();

        Ntdata = NormalizeData.Normalize(testdata.DataSet()); //npt

        dataFileReader.ReadDataRow();
        //m_Data.SetClassProperties();

        //added by Asanka
        fileData = dataFileReader;

        //m_Attributes = testdata.Attributes();
        dataFileReader = null;

//        return testdata.DataSet();
        return Ntdata;
    }

    //added by npt: can use to display the train dataset
    public static double[][] ReadTrainData(String trainfile) throws IOException {
        if (trainfile.length() == 0) {
            throw new IOException("Missing file name");
        }

        Instances traindata;
        double[][] Ndataset; //npt
        ArffFileReader dataFileReader = new ArffFileReader(trainfile);
        dataFileReader.SetValueHandler(new BasicValueHandler());

        dataFileReader.ReadHeader();
        dataFileReader.SetClassIndex(m_ClassAttribIndex);
        System.out.println("mcla" + m_ClassAttribIndex);
        //dataFileReader.setSelectedAttributes(attr);

        dataFileReader.CreateDataSet();
        dataFileReader.LoadData();

        traindata = dataFileReader.GetData();
        traindata.Compact();
        System.out.println("size w       " + traindata.Size());
//    	m_Data.Normalize();

        Ndataset = NormalizeData.Normalize(traindata.DataSet()); //npt

        dataFileReader.ReadDataRow();
        //m_Data.SetClassProperties();

        //added by Asanka
        fileData = dataFileReader;

        //m_Attributes = testdata.Attributes();
        dataFileReader = null;

        //return traindata.DataSet();
        return Ndataset; //npt
    }

    /* the following method is to read the index of the testdata*/
    public static int[] ReadTestIndex(String testfile) throws IOException {
        if (testfile.length() == 0) {
            throw new IOException("Missing file name");
        }

        Instances testdata;

        ArffFileReader dataFileReader = new ArffFileReader(testfile);
        dataFileReader.SetValueHandler(new BasicValueHandler());

        dataFileReader.ReadHeader();
        dataFileReader.SetClassIndex(m_ClassAttribIndex);

        //dataFileReader.setSelectedAttributes(attr);
        dataFileReader.CreateDataSet();
        dataFileReader.LoadData();

        testdata = dataFileReader.GetData();
        testdata.Compact();
        System.out.println("size w       " + testdata.Size());
//    	m_Data.Normalize();

        //m_Data.SetClassProperties();
        //m_Attributes = testdata.Attributes();
        dataFileReader = null;

        /*
         for (int y=0; y < testdata.Size(); y++)
         {
         System.out.println(testdata);
         }// to print the test set
         */
        return testdata.ClassIdList();
    }

    public static int[] ReadTrainIndex(String trainfile) throws IOException {
        if (trainfile.length() == 0) {
            throw new IOException("Missing file name");
        }

        Instances traindata;
        ArffFileReader dataFileReader = new ArffFileReader(trainfile);
        dataFileReader.SetValueHandler(new BasicValueHandler());

        dataFileReader.ReadHeader();
        dataFileReader.SetClassIndex(m_ClassAttribIndex);

        //dataFileReader.setSelectedAttributes(attr);
        dataFileReader.CreateDataSet();
        dataFileReader.LoadData();

        traindata = dataFileReader.GetData();
        traindata.Compact();
        System.out.println("size w       " + traindata.Size());
//    	m_Data.Normalize();

        //m_Data.SetClassProperties();
        //m_Attributes = testdata.Attributes();
        dataFileReader = null;

        return traindata.ClassIdList();
    }

    private Pair PredictInstance(double[] instance) throws IOException {
        ReadData(m_DataFilePath);
        int recNo = m_Data.Size();

        for (int i = 0; i < recNo; i++) {
            m_TrainingSet[i] = new Instance(m_Data.DataSet()[i]);
            m_TrainingSet[i].SetClassIndex(m_Data.ClassIdList()[i]);
        }

        String[] ClassArray = m_Data.ClassArray();
//        AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);
        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);

        ParameterReader paramReader = new ParameterReader(m_Attributes, m_ParameterFile);
        m_Weights = paramReader.ReadWeights();
        k = paramReader.ReadK();
        simMeas.SetWeights(m_Weights);

        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        predictor.setK(k);

        return (predictor.Predict(instance, 1, 1));
    }

    private static Pair[] PredictInstances(double[][] instances, double[] oneM, double[] zeroM, double[] oneS, double[] zeroS, double priorone, double priorzero) throws IOException {

        Pair[] predictions = new Pair[instances.length];
        int recNo = m_Data.Size(); //size of the training dataset
        m_TrainingSet = new Instance[recNo];

        //ReadData(m_DataFilePath);
        System.out.println("instanceslength" + instances.length);
        System.out.println("rec no : " + m_Data.Size());

        for (int i = 0; i < recNo; i++) {
            m_TrainingSet[i] = new Instance(m_Data.DataSet()[i]);
            m_TrainingSet[i].SetClassIndex(m_Data.ClassIdList()[i]);
        }

        String[] ClassArray = m_Data.ClassArray();
        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);
//        AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);

        ParameterReader paramReader = new ParameterReader(m_Attributes, m_ParameterFile);
        m_Weights = paramReader.ReadWeights();
        k = paramReader.ReadK();
        simMeas.SetWeights(m_Weights);

        //BayesianWeights.CalculateProbs(m_TrainingSet, m_Attributes, recNo);
        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        predictor.setK(k);

        BayesianWeights bwe = new BayesianWeights(m_Attributes, m_TrainingSet);
        double[][] oneset = bwe.oneset();
        double[][] zeroset = bwe.zeroset();

//        ConfidenceWeights ccw = new ConfidenceWeights(m_Attributes, m_TrainingSet);
//        double[][] oneset = ccw.oneset();
//        double[][] zeroset = ccw.zeroset();
        //go instance by instance of the test set
        for (int i = 0; i < instances.length; i++) {
            /* Bayesian weight to be calculated here. the probability for getting
             * class 1 and class 0 are to be separtely paased to the Predict(in) method
             * coming below
             */
            double vote1 = priorone * bwe.CalculateProbs(instances[i], oneM, oneS, oneset);
            double vote0 = priorzero * bwe.CalculateProbs(instances[i], zeroM, zeroS, zeroset);

//double cw1 = ccw.ClassConfidenceWeight(m_ClassAttribIndex, instances[i], oneM, oneS, oneset, instances, priorone, priorzero,1);
//            double cw0 = ccw.ClassConfidenceWeight(m_ClassAttribIndex, instances[i], zeroM, zeroS, zeroset, instances, priorone, priorzero,0);
            predictions[i] = predictor.Predict(instances[i], vote0, vote1);//when using Bayesian Weights
            //  predictions[i] = predictor.Predict(instances[i],m_ClassAttribIndex);//when using Class Confidence Weights
            // predictions[i] = predictor.Predict(instances[i], 1.0, 1.0); //no weight is used
            //System.out.println("preds : " +predictions[i].toString());
        }

        return predictions;
    }

    private static Pair[] PredictInstances(double[][] instances, double[] values) throws IOException {

        Pair[] predictions = new Pair[instances.length];
        int recNo = m_Data.Size(); //size of the training dataset
        m_TrainingSet = new Instance[recNo];

        //ReadData(m_DataFilePath);
        System.out.println("instanceslength" + instances.length);
        System.out.println("rec no : " + m_Data.Size());

        for (int i = 0; i < recNo; i++) {
            m_TrainingSet[i] = new Instance(m_Data.DataSet()[i]);
            m_TrainingSet[i].SetClassIndex(m_Data.ClassIdList()[i]);
        }

        String[] ClassArray = m_Data.ClassArray();
        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);
//        AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);

        ParameterReader paramReader = new ParameterReader(m_Attributes, m_ParameterFile);
        m_Weights = paramReader.ReadWeights();
        k = paramReader.ReadK();
        simMeas.SetWeights(m_Weights);

        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        predictor.setK(k);

        //go instance by instance of the test set
        for (int i = 0; i < instances.length; i++) {

            predictions[i] = predictor.Predict(instances[i], values);//when using Class Confidence Weights
            // predictions[i] = predictor.Predict(instances[i], 1.0, 1.0); //no weight is used
            //System.out.println("preds : " +predictions[i].toString());
        }

        return predictions;
    }

    private static Pair[] PredictInstances(double[][] instances) throws IOException {

        Pair[] predictions = new Pair[instances.length];
        int recNo = m_Data.Size(); //size of the training dataset
        m_TrainingSet = new Instance[recNo];

        //ReadData(m_DataFilePath);
        System.out.println("instanceslength" + instances.length);
        System.out.println("rec no : " + m_Data.Size());

        for (int i = 0; i < recNo; i++) {
            m_TrainingSet[i] = new Instance(m_Data.DataSet()[i]);
            m_TrainingSet[i].SetClassIndex(m_Data.ClassIdList()[i]);
        }

        String[] ClassArray = m_Data.ClassArray();
        AbstractSimilarity simMeas = new BasicSimilarity(m_Attributes);
//        AbstractSimilarity simMeas = new EuclideanSimilarity(m_Attributes);

        ParameterReader paramReader = new ParameterReader(m_Attributes, m_ParameterFile);
        m_Weights = paramReader.ReadWeights();
        k = paramReader.ReadK();
        simMeas.SetWeights(m_Weights);

        Predictor predictor = new Predictor1(simMeas, m_TrainingSet);
        predictor.setClassList(ClassArray);
        predictor.setK(k);

        //go instance by instance of the test set
        for (int i = 0; i < instances.length; i++) {

//          predictions[i] = predictor.Predict(instances[i],values);//when using Class Confidence Weights
            predictions[i] = predictor.Predict(instances[i], 1.0, 1.0); //no weight is used
//            System.out.println("preds : " +predictions[i].toString());
        }

        return predictions;
    }

    /*
     * This method is to create the file output.csv which used
     * to save validation data
     */
    public static File createOutputcsvFile() {
        File file = new File("output.csv");
        try {
            if (file.createNewFile()) {
                // file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                fw.write("Actual,Predicted\n");
                fw.close();
                //fw.append("Actual" + "," + "Predicted");
            } else {
                file.delete();
                createOutputcsvFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }

    public static int CreateFolds(int numFolds, int index, int numFold, double[][] trainSet, int[] trainIndexFull) {

        //int numFold = 1; //specifiy which fold is taken ex 1st,2nd,3rd etc.
        int numInstinFold = m_Data.Size() / numFolds;
        int first, offset = 0;
        int startIndex = index;
        int endIndex = 0;

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > m_Data.Size()) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }

        if (numFold < m_Data.Size() % numFolds) {
            numInstinFold++;
//      offset = numFold;
        } else {
            offset = m_Data.Size() % numFolds;
        }

//        first = numFold * (m_Data.Size() / numFolds) + offset;
//        System.out.println("first" + first);
//
//        m_TrainingSet = new Instance[m_Data.Size() - first];
//        m_TestSet = new Instance[first];
//
//        m_TrainingSetd = new double[trainSet.length - first][m_ClassAttribIndex];
//
//        m_TestSetd = new double[first][m_ClassAttribIndex];
        endIndex = startIndex + numInstinFold;
        System.out.println("endIndex" + endIndex);

        //offset values are added to the first test set
        m_TrainingSet = new Instance[m_Data.Size() - numInstinFold];
        m_TestSet = new Instance[numInstinFold];

        m_TrainingSetd = new double[trainSet.length - numInstinFold][m_ClassAttribIndex];
        m_TestSetd = new double[numInstinFold][m_ClassAttribIndex];

        m_TrainingSetdIndex = new int[trainSet.length - numInstinFold];
        m_TestSetdIndex = new int[numInstinFold];

        int trIndex = 0, tsIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            m_TestSet[tsIndex] = new Instance(m_Data.DataSet()[i - 1]);
            m_TestSet[tsIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);

            for (int j = 0; j < m_ClassAttribIndex; j++) {
                m_TestSetd[tsIndex][j] = trainSet[i - 1][j];
            }
            m_TestSetdIndex[tsIndex] = trainIndexFull[i - 1];
            tsIndex++;
        }

        System.out.println("tsindex" + tsIndex);
        System.out.println("length test" + m_TestSet.length);
//        for (int li = 0; li < m_TestSet.length; li++) {
//            for (int r = 0; r < m_ClassAttribIndex; r++) {
//                System.out.print(" " + m_TestSet[li].GetElementAt(r));
//            }
//            System.out.println();
//        }
//
//        System.out.println("calc length" + (m_Data.Size() - first));

        for (int j = endIndex; j <= m_Data.Size(); j++) {
            m_TrainingSet[trIndex] = new Instance(m_Data.DataSet()[j - 1]);
            m_TrainingSet[trIndex].SetClassIndex(m_Data.ClassIdList()[j - 1]);

            for (int n = 0; n < m_ClassAttribIndex; n++) {
                m_TrainingSetd[trIndex][n] = trainSet[j - 1][n];
            }
            m_TrainingSetdIndex[trIndex] = trainIndexFull[j - 1];
            trIndex++;
        }

//        System.out.println("trindex" + trIndex);
//        System.out.println("length" + m_TrainingSet.length);
//        for (int l = 0; l < m_TrainingSet.length; l++) {
//            for (int r = 0; r < m_ClassAttribIndex; r++) {
//                System.out.print(" " + m_TrainingSet[l].GetElementAt(r));
//            }
//            System.out.println();
//        }
        return endIndex;
    }

    public static void createTrainingdataSets() {

        try {
            int DataSize = m_Data.Size();
            int TrainSize = (DataSize / 3) * 2 + (DataSize % 3);
            int TestSize = DataSize / 3;
            System.out.println("size d" + DataSize);
            System.out.println(TrainSize);
            System.out.println(TestSize);
            m_TrainingSet = new Instance[TrainSize];
            m_TestSet = new Instance[TestSize];

            int trIndex = 0;
            int tsIndex = 0;

            for (int i = 1; i <= DataSize; i++) {
                if ((i % 3) == 0) {
                    //System.out.println(m_TestSet[tsIndex].length);
                    m_TestSet[tsIndex] = new Instance(m_Data.DataSet()[i - 1]);
                    m_TestSet[tsIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);
                    tsIndex++;
                    System.out.println("ts" + m_TestSet.length); // added for testing of codeing
                } else {
                    //System.out.println(m_TestSet[tsIndex].length);
                    m_TrainingSet[trIndex] = new Instance(m_Data.DataSet()[i - 1]);
                    m_TrainingSet[trIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);

                    trIndex++;
                    //     System.out.println("tr"+trIndex); // added for testing of codeing

                }
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("test");
            ex.printStackTrace();
        }

    }

    /* this method is used when two unkonwn sets are available for optimizing and
     * validation
     */
    public static void useTestdataSet(String SecondPath, double[][] trainSet, int[] trainIndexFull) throws IOException {
        try {
            double[][] testSet = ReadTestData(SecondPath);

            //parameters of the dataset used as the training set
            int DataSize = m_Data.Size();
            int TrainSize = DataSize;

            System.out.println("size dte" + DataSize);
            m_TrainingSet = new Instance[TrainSize];
            m_TrainingSetd = new double[TrainSize][m_ClassAttribIndex];

            m_TrainingSetdIndex = new int[TrainSize];

            //parameters of the dataset used as the test set
            ReadDataSecondFile(SecondPath);

            int SecDataSize = ms_Data.Size();
            int TestSize = SecDataSize;

            System.out.println("size tte" + DataSize);
            m_TestSet = new Instance[TestSize];
            m_TestSetd = new double[TestSize][m_ClassAttribIndex];

            m_TestSetdIndex = new int[TestSize];

            int trIndex = 0;
            int tsIndex = 0;

            for (int i = 1; i <= DataSize; i++) {
                m_TrainingSet[trIndex] = new Instance(m_Data.DataSet()[i - 1]);
                m_TrainingSet[trIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);

                for (int j = 0; j < m_ClassAttribIndex; j++) {
                    m_TrainingSetd[trIndex][j] = trainSet[i - 1][j];
                }
                m_TrainingSetdIndex[trIndex] = trainIndexFull[i - 1];
                trIndex++;
                //     System.out.println("tr"+trIndex); // added for testing of codeing
            }
            for (int i = 1; i <= SecDataSize; i++) {
                m_TestSet[tsIndex] = new Instance(ms_Data.DataSet()[i - 1]);
                m_TestSet[tsIndex].SetClassIndex(ms_Data.ClassIdList()[i - 1]);

                for (int j = 0; j < m_ClassAttribIndex; j++) {
                    m_TestSetd[tsIndex][j] = testSet[i - 1][j];
                }
                m_TestSetdIndex[tsIndex] = trainIndexFull[i - 1];
                tsIndex++;
                //System.out.println("ts"+tsIndex); // added for testing of codeing
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("test");
            ex.printStackTrace();
        }
    }

    public static void useTrainingdataSet(double[][] trainSet, int[] trainIndexFull) {

        int DataSize = m_Data.Size();
        int dDataSize = trainSet.length;

        System.out.println("size d" + DataSize);

        m_TrainingSet = new Instance[DataSize];
        m_TestSet = new Instance[DataSize];

        m_TrainingSetd = new double[dDataSize][m_ClassAttribIndex];
        m_TestSetd = new double[dDataSize][m_ClassAttribIndex];

        m_TestSetdIndex = new int[dDataSize];
        m_TrainingSetdIndex = new int[dDataSize];

        int trIndex = 0;
        int tsIndex = 0;

        for (int i = 1; i <= DataSize; i++) {

            //System.out.println(m_TestSet[tsIndex].length);
            m_TestSet[tsIndex] = new Instance(m_Data.DataSet()[i - 1]);
            m_TestSet[tsIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);

            for (int j = 0; j < m_ClassAttribIndex; j++) {
                m_TestSetd[tsIndex][j] = trainSet[i - 1][j];
            }
            m_TestSetdIndex[tsIndex] = trainIndexFull[i - 1];
            tsIndex++;
            //System.out.println("ts"+tsIndex); // added for testing of codeing

            //System.out.println(m_TestSet[tsIndex].length);
            m_TrainingSet[trIndex] = new Instance(m_Data.DataSet()[i - 1]);
            m_TrainingSet[trIndex].SetClassIndex(m_Data.ClassIdList()[i - 1]);

            for (int j = 0; j < m_ClassAttribIndex; j++) {
                m_TrainingSetd[trIndex][j] = trainSet[i - 1][j];
            }
            m_TrainingSetdIndex[trIndex] = trainIndexFull[i - 1];
            trIndex++;
            //     System.out.println("tr"+trIndex); // added for testing of codeing

        }

    }

    /**
     * Print a "usage message" that described possible command line options,
     * then exit.
     *
     * @param message a specific error message to preface the usage message by.
     */
    protected static void Usage(String message) {
        System.err.println();
        System.err.println(message);
        System.err.println();
        System.err.println();

    }

    protected static void ParseArguments(String[] argv) {
        int len = argv.length;
        int i;

        /* parse the options */
        for (i = 0; i < len; i++) {
            /* try to get the various options */
            if (argv[i].equals("-task")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-task must have a value 'o' or 'p'");
                }
                /* task */
                m_task = argv[i];
            } else if (argv[i].equals("-datafile")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-datafile must have a valid data file");
                }
                /* task */
                m_DataFilePath = argv[i];
            } else if (argv[i].equals("-testfile")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-testfile must have a valid data file");
                }
                /* task */
                m_TestFilePath = argv[i];
            } else if (argv[i].equals("-clsindex")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-clsattrib must have the attribute index of the class attribute");
                }
                /* task */
                m_ClassAttribIndex = Integer.parseInt(argv[i]);
            } else if (argv[i].equals("-population")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-population must have a valid integer value");
                }
                /* task */
                m_Population = Integer.parseInt(argv[i]);
            } else if (argv[i].equals("-mutation")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-mutation must have a valid integer value");
                }
                /* task */
                m_Mutation = Integer.parseInt(argv[i]);
            } else if (argv[i].equals("-k")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-k must have a valid integer value");
                }
                /* task */
                {
                    k = Integer.parseInt(argv[i]);
                    if (k < 1) {
                        m_FindK = false;
                    }
                }
            } else if (argv[i].equals("-params")) {
                if (++i >= len || argv[i].startsWith("-")) {
                    Usage("-prams must have a valid parameter file");
                }
                /* task */
                m_ParameterFile = argv[i];
            }
        }
    }

    public static void viewData(String DataFilePath, int ClassAttribIndex) throws IOException {
        double[][] trainSet = ReadTrainData(DataFilePath);
//        double[][] testSet = ReadTestData(m_TestFilePath);

        // int[] testIndex = ReadTestIndex(m_TestFilePath);
        int[] trainIndex = ReadTrainIndex(DataFilePath);

        PrintStream cols = new PrintStream(new FileOutputStream("ColumnsNames.csv"));
        System.setOut(cols);

        //print the header line of the file (attribute names)
        for (int c = 0; c <= ClassAttribIndex; c++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(c);

            System.out.print(currentAttribute1.m_Name + ",");
        }

        cols.close();

        PrintStream resultp = new PrintStream(new FileOutputStream("Plotting.csv"));
        System.setOut(resultp);

        //print the header line of the file (attribute names)
        for (int k = 0; k <= ClassAttribIndex; k++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(k);

            System.out.print(currentAttribute1.m_Name + ",");
        }

        System.out.println();

        //to print the trainset data for plotting
        for (int h = 0; h < trainSet.length; h++) {
            for (int ke = 0; ke < ClassAttribIndex; ke++) {
                double x = trainSet[h][ke];
                //System.out.print(" " + x);

                System.out.print(Double.toString(x) + ",");
            }
            System.out.print(trainIndex[h]);
            System.out.println();
        }
        resultp.close();

        VisualizeData datagraphs = new VisualizeData();
        datagraphs.graphing();
//        datagraphs.summary(4);
    }

    public static void summarizeData(String DataFilePath, int ClassAttribIndex) throws IOException {
        double[][] trainSet = ReadTrainData(DataFilePath);
//        double[][] testSet = ReadTestData(m_TestFilePath);

        // int[] testIndex = ReadTestIndex(m_TestFilePath);
        int[] trainIndex = ReadTrainIndex(DataFilePath);

        PrintStream cols = new PrintStream(new FileOutputStream("ColumnsNames.csv"));
        System.setOut(cols);

        //print the header line of the file (attribute names)
        for (int c = 0; c <= ClassAttribIndex; c++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(c);

            System.out.print(currentAttribute1.m_Name + ",");
        }

        cols.close();

        PrintStream resultp = new PrintStream(new FileOutputStream("Summarize.csv"));
        System.setOut(resultp);

        //print the header line of the file (attribute names)
        for (int k = 0; k <= ClassAttribIndex; k++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(k);

            if (k == ClassAttribIndex) {
                System.out.print(currentAttribute1.m_Name);
            } else {
                System.out.print(currentAttribute1.m_Name + ",");
            }
        }

        System.out.println();

        //to print the trainset data for summarizing
        for (int h = 0; h < trainSet.length; h++) {
            for (int ke = 0; ke < ClassAttribIndex; ke++) {
                double x = trainSet[h][ke];
                //  System.out.print(x + ",");
                Attribute currentAttribute2 = fileData.GetData().Attribute(ke);
                int atrType = currentAttribute2.Type();
                //value of the attrbute in Double presentation
                Double currentAttributeValue2 = trainSet[h][ke];

                //System.out.print(predictedClassValue2+",");
                switch (atrType) {

                    case Attribute.NUMERIC: {
                        //check attribute value is missing in original document
                        if (currentAttributeValue2 == DefaultValue) {
                            System.out.print("?"); //print missing value
                        } else {//if attribute is not missing
                            if (ke == ClassAttribIndex) {
                                System.out.print(currentAttributeValue2);
                            } else {
                                System.out.print(currentAttributeValue2 + ",");
                            }
                        }
                        break;
                    }

                    case Attribute.NOMINAL: {
                        //check attribute value is missing in original document
                        if (currentAttributeValue2 == DefaultValue) {
                            System.out.print("?"); //print missing value
                        } else {//if attribute is not missing
                            //this is a NOMINAL. Therefore currentAttributeValue represents the index of the NOMINAL.
                            int indexOfNominal2 = (int) currentAttributeValue2.doubleValue();
                            //now get the value regarding to the given index
                            String nominalValue2 = currentAttribute2.Value(indexOfNominal2);
                            if (ke == ClassAttribIndex) {
                                System.out.print(nominalValue2);
                            } else {
                                System.out.print(nominalValue2 + ",");
                            }
                        }
                        break;
                        // TODO: STRING and DATE cases should be implemented accordinglly.
                    }
                }// end switch

                // System.out.print(Double.toString(x) + ",");
            }
            //get the values of the class attribute
            String[] classArray = m_Data.ClassArray();

            //get the index of the original class value
            int indexOfOriginal = fileData.GetData().ClassIdList()[h];

            //get the Original class value
            String originalClassValue = classArray[indexOfOriginal];

            System.out.print(originalClassValue);
            // System.out.print(trainIndex[h]);
            System.out.println();
        }
        resultp.close();

    }

    public static void weightselector(double[][] trainSet, double[][] testSet, int[] testIndex, int[] trainIndex, int conf) throws IOException, Exception {

        confidenceType = conf;
        //confidenceType = 1;

        switch (confidenceType) {
            case 1:
                BayesianWeights bwe = new BayesianWeights(m_Attributes, m_TrainingSet);
                bwe.splitSet(trainSet, trainIndex, m_ClassAttribIndex);
                oneM = bwe.oneMean(m_ClassAttribIndex);
                zeroM = bwe.zeroMean(m_ClassAttribIndex);
                zeroS = bwe.zeroSTD(m_ClassAttribIndex);
                oneS = bwe.oneSTD(m_ClassAttribIndex);
                priorone = bwe.priorone();
                priorzero = bwe.priorzero();
                break;
            case 2:
                ConfidenceWeights ccw = new ConfidenceWeights(m_Attributes, m_TrainingSet);
                values = ccw.ClassConfidenceWeight(m_ClassAttribIndex, trainIndex, trainSet);
                break;
        }//end switch        
    }

    public static void instanceWeighter(double[][] trainSet, double[][] testSet, int[] testIndex, int[] trainIndex, int conf) throws IOException, Exception {
//        double[] oneM = null;
//        double[] zeroM = null;
//        double[] zeroS = null;
//        double[] oneS = null;
//        double priorone = 0.0;
//        double priorzero = 0.0;
//        double[] values = null;
        confidenceType = conf;
        //confidenceType = 1;

        switch (confidenceType) {
            case 1:
                BayesianWeights bwe = new BayesianWeights(m_Attributes, m_TrainingSet);
                bwe.splitSet(trainSet, trainIndex, m_ClassAttribIndex);
                oneM = bwe.oneMean(m_ClassAttribIndex);
                zeroM = bwe.zeroMean(m_ClassAttribIndex);
                zeroS = bwe.zeroSTD(m_ClassAttribIndex);
                oneS = bwe.oneSTD(m_ClassAttribIndex);
                priorone = bwe.priorone();
                priorzero = bwe.priorzero();
                break;
            case 2:
                ConfidenceWeights ccw = new ConfidenceWeights(m_Attributes, m_TrainingSet);
                values = ccw.ClassConfidenceWeight(m_ClassAttribIndex, trainIndex, trainSet);
                break;
        }//end switch

        trainSet = NormalizeData.Normalize(trainSet);
        testSet = NormalizeData.Normalize(testSet);

        switch (confidenceType) {
            case 0:
                predictions = PredictInstances(testSet);
                System.out.println("case 0");
                break;
            case 1:
                predictions = PredictInstances(testSet, oneM, zeroM, oneS, zeroS, priorone, priorzero);
                System.out.println("case 1");
                break;
            case 2:
                predictions = PredictInstances(testSet, values);
                System.out.println("case 2");
                break;
        }
    }

    public static void createPredictedSet(double[][] testSet) throws FileNotFoundException {

        PrintStream result = new PrintStream(new FileOutputStream("PredictedSet.csv"));

        System.setOut(result);

        for (int k = 0; k <= m_ClassAttribIndex; k++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(k);

            System.out.print(currentAttribute1.m_Name + ",");
        }

        System.out.println();

        for (int h = 0; h < testSet.length; h++) {
            for (int ke = 0; ke < m_ClassAttribIndex; ke++) {
                //get the Attribute we are dealing
                Attribute currentAttribute = fileData.GetData().Attribute(ke);
                //get the attribute's type
                int atrType = currentAttribute.Type();
                //value of the attrbute in Double presentation
                Double currentAttributeValue = testSet[h][ke];

                switch (atrType) {

                    case Attribute.NUMERIC: {
                        //check attribute value is missing in original document
                        if (currentAttributeValue == DefaultValue) {
                            System.out.print("?"); //pring missing value
                        } else {//if attribute is not missing
                            System.out.print(currentAttributeValue + ",");
                        }
                        break;
                    }

                    case Attribute.NOMINAL: {
                        //check attribute value is missing in original document
                        if (currentAttributeValue == DefaultValue) {
                            System.out.print("?"); //print missing value
                        } else {//if attribute is not missing
                            //this is a NOMINAL. Therefore currentAttributeValue represents the index of the NOMINAL.
                            int indexOfNominal = (int) currentAttributeValue.doubleValue();
                            //now get the value regarding to the given index
                            String nominalValue = currentAttribute.Value(indexOfNominal);
                            System.out.print(nominalValue + ",");
                        }
                        break;

                        // TODO: STRING and DATE cases should be implemented accordinglly.
                    }
                }

            }
            //System.out.println();
            //######### Added by asanka ######################
            //get index of the predicted class value
            int predictedClassIndex = predictions[h].Index();

            //get the confidence of the prediction
            double confidenceLevel = predictions[h].Value();

            //get the values of the class attribute
            String[] classArray = m_Data.ClassArray();

            //get the index of the original class value
            int indexOfOriginal = fileData.GetData().ClassIdList()[h];

            //get the Original class value
            //   String originalClassValue = classArray[indexOfOriginal];
            //get predicted class value
            String predictedClassValue = classArray[predictedClassIndex];

//                        System.out.print("  "+originalClassValue);
//                        System.out.print("Original Class value: "+originalClassValue);
//                        System.out.println();
            System.out.print(predictedClassValue + ",");
//                        System.out.print("Predicted Class value: "+predictedClassValue);
//                        System.out.println();
//                        System.out.print("Predicted Confidence: "+confidenceLevel);

            System.out.println();

        }
        result.close();
    } //end createPredictedSet()

    public static void churners(double[][] testSet) throws FileNotFoundException {

        PrintStream out = new PrintStream(new FileOutputStream("churning.csv"));
        System.setOut(out);

        for (int p = 0; p <= m_ClassAttribIndex; p++) {
            Attribute currentAttribute1 = fileData.GetData().Attribute(p);

            if (p == m_ClassAttribIndex) {
                System.out.print(currentAttribute1.m_Name);
            } else {
                System.out.print(currentAttribute1.m_Name + ",");
            }
        }

        System.out.println();

        for (int h2 = 0; h2 < testSet.length; h2++) {
            //get index of the predicted class value
            int predictedClassIndex2 = predictions[h2].Index();

            //get the confidence of the prediction
            double confidenceLevel2 = predictions[h2].Value();

            //get the values of the class attribute
            String[] classArray = m_Data.ClassArray();

            //get predicted class value
            String predictedClassValue2 = classArray[predictedClassIndex2];
            // checks if the customer is churning
            if (predictions[h2].Index() == 1) {

                for (int e = 0; e < m_ClassAttribIndex; e++) {
                    //get the Attribute we are dealing
                    Attribute currentAttribute2 = fileData.GetData().Attribute(e);
                    //get the attribute's type
                    int atrType = currentAttribute2.Type();
                    //value of the attrbute in Double presentation
                    Double currentAttributeValue2 = testSet[h2][e];

                    //System.out.print(predictedClassValue2+",");
                    switch (atrType) {

                        case Attribute.NUMERIC: {
                            //check attribute value is missing in original document
                            if (currentAttributeValue2 == DefaultValue) {
                                System.out.print("?"); //print missing value
                            } else {//if attribute is not missing
                                System.out.print(currentAttributeValue2 + ",");
                            }
                            break;
                        }

                        case Attribute.NOMINAL: {
                            //check attribute value is missing in original document
                            if (currentAttributeValue2 == DefaultValue) {
                                System.out.print("?"); //print missing value
                            } else {//if attribute is not missing
                                //this is a NOMINAL. Therefore currentAttributeValue represents the index of the NOMINAL.
                                int indexOfNominal2 = (int) currentAttributeValue2.doubleValue();
                                //now get the value regarding to the given index
                                String nominalValue2 = currentAttribute2.Value(indexOfNominal2);
                                System.out.print(nominalValue2 + ",");
                            }
                            break;
                            // TODO: STRING and DATE cases should be implemented accordinglly.
                        }
                    }// end switch

                    //System.out.print(predictedClassValue2+",");
                } //end for
                System.out.print(predictedClassValue2);
                System.out.println();
//                        System.out.print("Predicted Confidence: "+confidenceLevel);
            } //end if
//                   System.out.print(predictedClassValue2+",");
            // System.out.println();
        }

        out.close();

        PrintStream outch = new PrintStream(new FileOutputStream("prediction_summary.csv"));
        System.setOut(outch);

        System.out.println("Record ID" + "," + "Prediction" + "," + "Confidence");

        for (int i = 0; i < testSet.length; i++) {
            int j = i + 1; // to get the record ID starting from 1
            System.out.print(j + ",");

            System.out.print(predictions[i].Index() + ",");
            System.out.println(predictions[i].Value());
        }
        outch.close();
    }// end churners()

    public static void evalutePredictions(int[] testIndex) throws FileNotFoundException, IOException {

        PrintStream outa = new PrintStream(new FileOutputStream("output.csv"));
        System.setOut(outa);

        System.out.println("Actual" + "," + "Predicted");
        for (int b = 0; b < testIndex.length; b++) {
            System.out.print(testIndex[b] + ",");
            System.out.println(predictions[b].Index());
        }

        outa.close();

        VisualizeData visualizeacc = new VisualizeData();
        visualizeacc.accuracygraphing();

        PrintStream outacc = new PrintStream(new FileOutputStream("output_acc.txt"));
        System.setOut(outacc);

        System.out.println("accuracy testing");

        // the for loop gives the only the class index values of the test data file
//                for(int a=0;a<testIndex.length;a++)
//                {
//            System.out.println(" " + testIndex[a]);
//                }
        int accurate = 0, inaccurate = 0;
//                    double accurate=0.0,inaccurate=0.0;
        double accuracy, trueaccuracy;

        for (int b = 0; b < testIndex.length; b++) {
            if (testIndex[b] == predictions[b].Index()) {
                //  System.out.println(b + " accurate");
                accurate = accurate + 1;
                // System.out.println("v"+accurate);

                System.out.println(b);

            } else {
                //  System.out.println(b + " inaccurate");
                inaccurate = inaccurate + 1;
            }
        }
        System.out.println("accurate : " + accurate);
        System.out.println("inaccurate : " + inaccurate);
        accuracy = (((double) accurate) / ((double) accurate + inaccurate)) * 100.0;
        System.out.println("simple accuracy : " + accuracy + "%");

//               //sensitivity & specificity measures
//
        int t_pos = 0, t_neg = 0, pos = 0, neg = 0, f_neg = 0, f_pos = 0;
        double sensitivity, specificity;

        for (int b = 0; b < testIndex.length; b++) {
            if (testIndex[b] == 1) {
                pos = pos + 1;
            } else {
                neg = neg + 1;
            }

            if (testIndex[b] == predictions[b].Index() && testIndex[b] == 1) {
                t_pos = t_pos + 1;

            } else if (testIndex[b] == predictions[b].Index() && testIndex[b] == 0) {

                t_neg = t_neg + 1;

            }

            if (predictions[b].Index() == 0 && testIndex[b] == 1) {
                f_neg = f_neg + 1;

            } else if (predictions[b].Index() == 1 && testIndex[b] == 0) {

                f_pos = f_pos + 1;

            }

        }

        System.out.println("t pos: " + t_pos);
        System.out.println("t neg: " + t_neg);
        System.out.println("f pos: " + f_pos);
        System.out.println("f neg: " + f_neg);

        sensitivity = (double) t_pos / pos;
        specificity = (double) t_neg / neg;
        System.out.println("sensitivity: " + sensitivity);
        System.out.println("specificity: " + specificity);

        double x = (double) sensitivity * pos / (pos + neg);
        double y = (double) specificity * neg / (pos + neg);
        trueaccuracy = x + y;
        //trueaccuracy=sensitivity*pos /(pos+neg) + specificity*neg /(pos+neg);

        System.out.println("trueaccuracy: " + trueaccuracy);

    } //end  evalutePredictions()

    public static void createTrainingdataSetsDouble(double[][] trainSet, int[] trainIndexFull) {

        int DataSize = trainSet.length;
        int TrainSize = (DataSize / 3) * 2 + (DataSize % 3);
        int TestSize = DataSize / 3;

        try {
//        int DataSize = m_Data.Size();
//    	int TrainSize = (DataSize/3) * 2 + (DataSize % 3);
//    	int TestSize = DataSize/3;
            System.out.println("size d double " + DataSize);
            System.out.println(TrainSize);
            System.out.println(TestSize);

            m_TrainingSetd = new double[TrainSize][m_ClassAttribIndex];
            m_TestSetd = new double[TestSize][m_ClassAttribIndex];

            m_TrainingSetdIndex = new int[TrainSize];
            m_TestSetdIndex = new int[TestSize];

//    	m_TrainingSet = new Instance[TrainSize];
//    	m_TestSet = new Instance[TestSize];
            int trIndex = 0;
            int tsIndex = 0;

            //for (int i=1; i<=DataSize; i++){
            for (int i = 1; i <= DataSize; i++) {
                if ((i % 3) == 0) {
                    //System.out.println(m_TestSet[tsIndex].length);
                    for (int j = 0; j < m_ClassAttribIndex; j++) {
                        m_TestSetd[tsIndex][j] = trainSet[i - 1][j];
                        // System.out.println("ts " + m_TestSetd[tsIndex][j]);
                    }
                    m_TestSetdIndex[tsIndex] = trainIndexFull[i - 1];
                    tsIndex++;
                    // System.out.println("ts ind" + m_TestSetd[5].length); // added for testing of codeing
                } else {
                    //System.out.println(m_TestSet[tsIndex].length);
                    for (int j = 0; j < m_ClassAttribIndex; j++) {
                        m_TrainingSetd[trIndex][j] = trainSet[i - 1][j];
                        // System.out.println("tr " + m_TrainingSetd[tsIndex][j]);
                    }
                    m_TrainingSetdIndex[trIndex] = trainIndexFull[i - 1];
                    trIndex++;
                    //     System.out.println("tr"+trIndex); // added for testing of codeing

                }
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("test");
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // TODO code application logic here
        // ParseArguments(args);
        m_task = "o"; // o-- set to optimize
        test_option = 2;
        int confi = 2;
        try {
            if (m_task.equals("o")) {
                ReadData(m_DataFilePath);
                double[][] trainSet = ReadTrainData(m_DataFilePath);
                int[] trainIndex = ReadTrainIndex(m_DataFilePath);
                //selects the test validating option
                switch (test_option) {
                    case 1:
                        crossValidate(10, trainSet, confi, trainIndex); //for cross validation
                        break;
                    case 2:
                        createTrainingdataSets(); //for percentile split (66.66%)
                        //test code to check dataset partitioning
                        createTrainingdataSetsDouble(trainSet, trainIndex);
                        System.out.println("in main" + trainSet[2].length);
                        runOptimization(); //at the end of this method its output is written to a file
                        System.out.println("in case 2");
                }
            } else {
//System.out.println(System.getProperty("java.library.path"));
                ReadData(m_DataFilePath);
                double[][] trainSet = ReadTrainData(m_DataFilePath);
                double[][] testSet = ReadTestData(m_TestFilePath);

                int[] testIndex = ReadTestIndex(m_TestFilePath);
                int[] trainIndex = ReadTrainIndex(m_DataFilePath);

//                build and learn the DAG
//                System.out.println("DAG");
//                BayesianNetwork bn = new BayesianNetwork(trainSet, m_Attributes);
//                bn.buildClassifier(trainSet, m_ClassAttribIndex, m_Data);
//                bn.bnValues(m_ClassAttribIndex, trainSet, trainIndex, m_Data);
                ///-------------instance weighter-------------------------
                instanceWeighter(trainSet, testSet, testIndex, trainIndex, confi);

                ///--------------------end instance weighter--------------------------------
                double[][] normalSet = NormalizeData.Normalize(trainSet);

                PrintStream bettervals = new PrintStream(new FileOutputStream("Suggestions.csv"));

                System.setOut(bettervals);

                //print the header line of the file (attribute names)
                for (int k = 0; k <= m_ClassAttribIndex; k++) {
                    Attribute currentAttribute2 = fileData.GetData().Attribute(k);

                    System.out.print(currentAttribute2.m_Name + ",");
                }

                System.out.println();

                //to print the trainset data for plotting
                for (int h = 0; h < normalSet.length; h++) {
                    for (int ke = 0; ke < m_ClassAttribIndex; ke++) {
                        double x = normalSet[h][ke];
                        //System.out.print(" " + x);

                        System.out.print(Double.toString(x) + ",");
                    }
                    if (trainIndex[h] == 0) {
                        System.out.print(trainIndex[h]);
                        System.out.println();
                    }
                }

                //  System.out.println("TEST SET");
//---------------------------------------------------------------------------------
                createPredictedSet(testSet);
                System.out.println("we here");
                viewData(m_DataFilePath, m_ClassAttribIndex);
//---------------------------------------------------------------------------------

                //to print the predictions
                // System.out.println("pred length :" + predictions.length);
                churners(testSet);

                //---------------------------------------------------------------------------------
                //code to chect accuracy
                evalutePredictions(testIndex);

                //////////////////////////////////////////////////////////////////////
//         PrintStream inacc = new PrintStream(new FileInputStream("output_acc.txt"));
//                System.setIn(inacc);               
            } // end else
        } catch (Exception e) {
            //e.getMessage();
            System.out.println(e.getMessage());
            e.printStackTrace();

        }

    }
}
