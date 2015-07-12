/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.core;

import java.io.BufferedReader;
import java.lang.*;
import java.util.ArrayList;

/**
 *
 * @author Niro
 */
public class Instances {

    public final static int CAPACITY = 5000;

    /**
     * The dataset's name.
     */
//    protected String m_RelationName;
    public String m_RelationName;

    /**
     * The attribute information.
     */
    protected FastVector m_Attributes;
    protected /*@spec_public non_null@*/ FastVector m_Instances;
    protected double[][] m_DataSet;
//    protected String  m_DataSet1;
    protected int[] m_ClassIdList;
    private int m_Capacity;

    /**
     * The class attribute's index
     */
    protected int m_ClassIndex;

    protected int m_Lines = 0;
    protected int m_NumAttributes = 0;
    protected int m_NumClases;
    protected String[] m_ClassArray;
    protected Instance m_instance;//BN

    protected int[] WeightVector;

    public Instances(/*@non_null@*/String name,
            /*@non_null@*/ FastVector attInfo) {

        m_RelationName = name;
        m_ClassIndex = -1;
        m_Attributes = attInfo;

        for (int i = 0; i < attInfo.size(); i++) {
            if (!Attribute(i).IsClassAttribute() && Attribute(i).Selected()) {
                m_NumAttributes++;
            }

        }

        if (m_NumAttributes > 0) {
            m_DataSet = new double[CAPACITY][m_NumAttributes];
        }
        m_ClassIdList = new int[CAPACITY];
        m_Capacity = CAPACITY;
    }

    public Instances(Instances m_plotInstances) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Instances(BufferedReader bufferedReader) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void SetWeightVector(int[] weights) {
        this.WeightVector = weights;
    }

    public int NumAttributes() {
        return m_NumAttributes;
    }

    public /*@pure@*/ int NumAllAttributes() {
        return m_Attributes.size();
    }

    public /*@pure@*/ Attribute Attribute(int index) {
        return (Attribute) m_Attributes.elementAt(index);
    }

    public void RemoveAttribute(int index) {
        m_Attributes.removeElementAt(index);
    }

    public void SetClassProperties() throws InvalidClassIndexException {
        if (m_ClassIndex >= 0) {
            Attribute attr = (Attribute) m_Attributes.elementAt(m_ClassIndex);

            m_NumClases = attr.NumValues();
            m_ClassArray = new String[m_NumClases];

            for (int i = 0; i < m_NumClases; i++) {
                m_ClassArray[i] = attr.Value(i);
            }

            m_Attributes.removeElementAt(m_ClassIndex);
        } else {
            throw new InvalidClassIndexException("Invalid Class Index : " + m_ClassIndex);
        }
    }

    public int NumberofClasses() {
        return m_NumClases;
    }

    public String[] ClassArray() {
        return m_ClassArray;
    }

    public double[][] DataSet() {
        return m_DataSet;
    }

    public void DataSet(double[][] dataSet) {
        m_DataSet = dataSet;
    }

    public FastVector Attributes() {
        return m_Attributes;
    }

    public int Size() {
        return m_Lines;
    }

    public int[] ClassIdList() {
        return m_ClassIdList;
    }

    public void AddElement(int RecNo, double[] numValues, int classIndex) {
        if (RecNo >= m_Capacity) {
            m_Capacity = m_Capacity + Double.valueOf(CAPACITY * 0.5).intValue();
            if (m_NumAttributes > 0) {
                m_DataSet = (double[][]) ResizeArray(m_DataSet, m_Capacity);
            }

            m_ClassIdList = (int[]) ResizeArray(m_ClassIdList, m_Capacity);
        }

        if (m_NumAttributes > 0) {
            m_DataSet[RecNo] = numValues;
        }
        m_ClassIdList[RecNo] = classIndex;
        m_Lines++;
    }

    public void Compact() {
        m_DataSet = (double[][]) ResizeArray(m_DataSet, m_Lines);
        m_ClassIdList = (int[]) ResizeArray(m_ClassIdList, m_Lines);
    }

    public void SetClassIndex(int classIndex) {
        if (classIndex >= NumAllAttributes()) {
            throw new IllegalArgumentException("Invalid class index: " + classIndex);
        }
        m_ClassIndex = classIndex;
    }

    public int GetClassIndex() {
        return m_ClassIndex;
    }

    private static Object ResizeArray(Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return newArray;
    }

}
