/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.core;

/**
 *
 * @author Niro
 */
public class Instance
        implements Copyable {

    protected Instances m_Dataset;
    private double weight;

    /**
     * The instance's attribute values.
     */
    public /*@spec_public non_null@*/ double[] m_AttValues;
    //protected /*@spec_public non_null@*/ double[] m_AttValues;
    protected String m_Class;
    protected int m_ClassIndex;

    protected static final double DEFAULT_NUMVALUE = Double.NaN;

    public Instance(/*@non_null@*/Instance instance) {

        m_AttValues = instance.m_AttValues;
        m_Dataset = null;
    }

    public Instance(double[] attValues) {

        m_AttValues = attValues;
        m_Dataset = null;
    }

    public Instance(int numAttributes) {
        m_AttValues = new double[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            m_AttValues[i] = DEFAULT_NUMVALUE;
        }

        m_Dataset = null;
    }

    public /*@pure@*/ Instances dataset() {
        return m_Dataset;
    }

    public void AddElement(double value, int index) {
        m_AttValues[index] = value;
    }

    public double GetElementAt(int index) {
        return m_AttValues[index];
    }

    public double[] GetElements() {
        return m_AttValues;
    }

    public void SetClassVlaue(String cls) {
        m_Class = cls;
    }

    public String GetClassValue() {
        return m_Class;
    }

    public void SetClassIndex(int clsIndex) {
        m_ClassIndex = clsIndex;
    }

    public int GetClassIndex() {
        return m_ClassIndex;
    }

    //added for the requirment to design & learn Bayesian Networks
    /**
     * Sets the weight of an instance.
     *
     * @param weight the weight
     */
    public void setWeight(double weightp) {
        weight = weightp;
    }

    /**
     * Returns the instance's weight.
     *
     * @return the instance's weight as a double
     */
    public double weight() {
        weight = 1.0;
        return weight;
    }

    public /*@pure@*/ Object Copy() {
        Instance result = new Instance(this);
        result.m_Dataset = m_Dataset;
        return result;
    }

}
