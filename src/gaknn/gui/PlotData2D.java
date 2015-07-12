/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

/**
 *
 * @author nipunika_t
 */
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import gaknn.core.Instances;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.Add;

/**
 * This class is a container for plottable data. Instances form the primary
 * data. An optional array of classifier/clusterer predictions (associated 1 for
 * 1 with the instances) can also be provided.
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class PlotData2D implements Serializable {

    /**
     * For serialization
     */
    private static final long serialVersionUID = -3979972167982697979L;

    /**
     * The instances
     */
    protected Instances m_plotInstances = null;

    /**
     * The name of this plot
     */
    protected String m_plotName = "new plot";

    /**
     * The name of this plot (possibly in html) suitable for using in a tool tip
     * text.
     */
    protected String m_plotNameHTML = null;

    /**
     * Custom colour for this plot
     */
    public boolean m_useCustomColour = false;
    public Color m_customColour = null;

    /**
     * Display all points (ie. those that map to the same display coords)
     */
    public boolean m_displayAllPoints = false;

    /**
     * If the shape size of a point equals this size then always plot it (i.e.
     * even if it is obscured by other points)
     */
    public int m_alwaysDisplayPointsOfThisSize = -1;

    /**
     * Panel coordinate cache for data points
     */
    protected double[][] m_pointLookup;

    /**
     * Additional optional information to control the size of points. The
     * default is shape size 2
     */
    protected int[] m_shapeSize;

    /**
     * Additional optional information to control the point shape for this data.
     * Default is to allow automatic assigning of point shape on the basis of
     * plot number
     */
    protected int[] m_shapeType;

    /**
     * Additional optional information to control the drawing of lines between
     * consecutive points. Setting an entry in the array to true indicates that
     * the associated point should have a line connecting it to the previous
     * point.
     */
    protected boolean[] m_connectPoints;

    /**
     * These are used to determine bounds
     */
    /**
     * The x index
     */
    private int m_xIndex;

    /**
     * The y index
     */
    private int m_yIndex;

    /**
     * The colouring index
     */
    private int m_cIndex;

    /**
     * Holds the min and max values of the x, y and colouring attributes for
     * this plot
     */
    protected double m_maxX;
    protected double m_minX;
    protected double m_maxY;
    protected double m_minY;
    protected double m_maxC;
    protected double m_minC;

    /**
     * Construct a new PlotData2D using the supplied instances
     *
     * @param insts the instances to use.
     */
    public PlotData2D(Instances insts) {
        m_plotInstances = insts;
        m_xIndex = m_yIndex = m_cIndex = 0;
        m_pointLookup = new double[m_plotInstances.Size()][4];
        m_shapeSize = new int[m_plotInstances.Size()];
        m_shapeType = new int[m_plotInstances.Size()];
        m_connectPoints = new boolean[m_plotInstances.Size()];
        for (int i = 0; i < m_plotInstances.Size(); i++) {
//      m_shapeSize[i] = Plot2D.DEFAULT_SHAPE_SIZE; // default shape size
//      m_shapeType[i] = Plot2D.CONST_AUTOMATIC_SHAPE; // default (automatic shape
            // assignment)
            m_shapeSize[i] = 2; // default shape size
            m_shapeType[i] = -1; // default (automatic shape
            // assignment)
        }
        determineBounds();
    }

    /**
     * Adds an instance number attribute to the plottable instances,
     */
    public void addInstanceNumberAttribute() {
        String originalRelationName = m_plotInstances.m_RelationName;
        int originalClassIndex = m_plotInstances.GetClassIndex();
        try {
            // Add addF = new Add();
            // addF.setAttributeName("Instance_number");
            // addF.setAttributeIndex("first");
            // addF.setInputFormat(m_plotInstances);
            // m_plotInstances = Filter.useFilter(m_plotInstances, addF);
            m_plotInstances.SetClassIndex(originalClassIndex + 1);
            for (int i = 0; i < m_plotInstances.Size(); i++) {
                //   m_plotInstances..setValue(0, i);
            }
            // m_plotInstances.setRelationName(originalRelationName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the instances for this plot
     *
     * @return the instances for this plot
     */
    public Instances getPlotInstances() {
        return new Instances(m_plotInstances);
    }

    /**
     * Set the name of this plot
     *
     * @param name the name for this plot
     */
    public void setPlotName(String name) {
        m_plotName = name;
    }

    /**
     * Get the name of this plot
     *
     * @return the name of this plot
     */
    public String getPlotName() {
        return m_plotName;
    }

    /**
     * Set the plot name for use in a tool tip text.
     *
     * @param name the name of the plot for potential use in a tool tip text
     * (may use html).
     */
    public void setPlotNameHTML(String name) {
        m_plotNameHTML = name;
    }

    /**
     * Get the name of the plot for use in a tool tip text. Defaults to the
     * standard plot name if it hasn't been set.
     *
     * @return the name of this plot (possibly in html) for use in a tool tip
     * text.
     */
    public String getPlotNameHTML() {
        if (m_plotNameHTML == null) {
            return m_plotName;
        }

        return m_plotNameHTML;
    }

    /**
     * Set the shape type for the plot data
     *
     * @param st an array of integers corresponding to shape types (see
     * constants defined in Plot2D)
     */
    public void setShapeType(int[] st) throws Exception {
        m_shapeType = st;
        if (m_shapeType.length != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: Shape type array must have the same "
                    + "number of entries as number of data points!");
        }
        /*
         * for (int i = 0; i < st.length; i++) { if (m_shapeType[i] ==
         * Plot2D.ERROR_SHAPE) { m_shapeSize[i] = 3; } }
         */
    }

    /**
     * Get the shape types for the plot data
     *
     * @return the shape types for the plot data
     */
    public int[] getShapeType() {
        return m_shapeType;
    }

    /**
     * Set the shape type for the plot data
     *
     * @param st a FastVector of integers corresponding to shape types (see
     * constants defined in Plot2D)
     */
    public void setShapeType(ArrayList<Integer> st) throws Exception {
        if (st.size() != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: Shape type vector must have the same "
                    + "number of entries as number of data points!");
        }
        m_shapeType = new int[st.size()];
        for (int i = 0; i < st.size(); i++) {
            m_shapeType[i] = st.get(i).intValue();
            /*
             * if (m_shapeType[i] == Plot2D.ERROR_SHAPE) { m_shapeSize[i] = 3; }
             */
        }
    }

    /**
     * Set the shape sizes for the plot data
     *
     * @param ss an array of integers specifying the size of data points
     */
    public void setShapeSize(int[] ss) throws Exception {
        m_shapeSize = ss;
        if (m_shapeType.length != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: Shape size array must have the same "
                    + "number of entries as number of data points!");
        }
    }

    /**
     * Get the shape sizes for the plot data
     *
     * @return the shape sizes for the plot data
     */
    public int[] getShapeSize() {
        return m_shapeSize;
    }

    /**
     * Set the shape sizes for the plot data
     *
     * @param ss a FastVector of integers specifying the size of data points
     */
    public void setShapeSize(ArrayList<Object> ss) throws Exception {
        if (ss.size() != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: Shape size vector must have the same "
                    + "number of entries as number of data points!");
        }
        // System.err.println("Setting connect points ");
        m_shapeSize = new int[ss.size()];
        for (int i = 0; i < ss.size(); i++) {
            m_shapeSize[i] = ((Integer) ss.get(i)).intValue();
        }
    }

    /**
     * Set whether consecutive points should be connected by lines
     *
     * @param cp an array of boolean specifying which points should be connected
     * to their preceeding neighbour.
     */
    public void setConnectPoints(boolean[] cp) throws Exception {
        m_connectPoints = cp;
        if (m_connectPoints.length != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: connect points array must have the "
                    + "same number of entries as number of data points!");
        }
        m_connectPoints[0] = false;
    }

    /**
     * Set whether consecutive points should be connected by lines
     *
     * @param cp a FastVector of boolean specifying which points should be
     * connected to their preceeding neighbour.
     */
    public void setConnectPoints(ArrayList<Boolean> cp) throws Exception {
        if (cp.size() != m_plotInstances.Size()) {
            throw new Exception("PlotData2D: connect points array must have the "
                    + "same number of entries as number of data points!");
        }
        // System.err.println("Setting connect points ");
        m_shapeSize = new int[cp.size()];
        for (int i = 0; i < cp.size(); i++) {
            m_connectPoints[i] = cp.get(i).booleanValue();
        }
        m_connectPoints[0] = false;
    }

    /**
     * Set a custom colour to use for this plot. This overides any data index to
     * use for colouring. If null, then will revert back to the default (no
     * custom colouring).
     *
     * @param c a custom colour to use for this plot or null (default---no
     * colouring).
     */
    public void setCustomColour(Color c) {
        m_customColour = c;
        if (c != null) {
            m_useCustomColour = true;
        } else {
            m_useCustomColour = false;
        }
    }

    /**
     * Set the x index of the data.
     *
     * @param x the x index
     */
    public void setXindex(int x) {
        m_xIndex = x;
        determineBounds();
    }

    /**
     * Set the y index of the data
     *
     * @param y the y index
     */
    public void setYindex(int y) {
        m_yIndex = y;
        determineBounds();
    }

    /**
     * Set the colouring index of the data
     *
     * @param c the colouring index
     */
    public void setCindex(int c) {
        m_cIndex = c;
        determineBounds();
    }

    /**
     * Get the currently set x index of the data
     *
     * @return the current x index
     */
    public int getXindex() {
        return m_xIndex;
    }

    /**
     * Get the currently set y index of the data
     *
     * @return the current y index
     */
    public int getYindex() {
        return m_yIndex;
    }

    /**
     * Get the currently set colouring index of the data
     *
     * @return the current colouring index
     */
    public int getCindex() {
        return m_cIndex;
    }

    /**
     * Determine bounds for the current x,y and colouring indexes
     */
    private void determineBounds() {
        double value = 20.0, min, max;

        if (m_plotInstances != null && m_plotInstances.NumAttributes() > 0
                && m_plotInstances.Size() > 0) {
            // x bounds
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;
            if (m_plotInstances.Attribute(m_xIndex).IsNominal()) {
                m_minX = 0;
                m_maxX = m_plotInstances.Attribute(m_xIndex).NumValues() - 1;
            } else {
                for (int i = 0; i < m_plotInstances.Size(); i++) {
                    //if (!m_plotInstances.instance(i).isMissing(m_xIndex)) {
                    //  value = m_plotInstances.instance(i).value(m_xIndex);
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                    //}
                }

                // handle case where all values are missing
                if (min == Double.POSITIVE_INFINITY) {
                    min = max = 0.0;
                }

                m_minX = min;
                m_maxX = max;
                if (min == max) {
                    m_maxX += 0.05;
                    m_minX -= 0.05;
                }
            }

            // y bounds
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;
            if (m_plotInstances.Attribute(m_yIndex).IsNominal()) {
                m_minY = 0;
                m_maxY = m_plotInstances.Attribute(m_yIndex).NumValues() - 1;
            } else {
                for (int i = 0; i < m_plotInstances.Size(); i++) {
//          if (!m_plotInstances.instance(i).isMissing(m_yIndex)) {
//            value = m_plotInstances.instance(i).value(m_yIndex);
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                    //}
                }

                // handle case where all values are missing
                if (min == Double.POSITIVE_INFINITY) {
                    min = max = 0.0;
                }

                m_minY = min;
                m_maxY = max;
                if (min == max) {
                    m_maxY += 0.05;
                    m_minY -= 0.05;
                }
            }

            // colour bounds
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < m_plotInstances.Size(); i++) {
//        if (!m_plotInstances.instance(i).isMissing(m_cIndex)) {
//          value = m_plotInstances.instance(i).value(m_cIndex);
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                //}
            }

            // handle case where all values are missing
            if (min == Double.POSITIVE_INFINITY) {
                min = max = 0.0;
            }

            m_minC = min;
            m_maxC = max;
        }
    }
}
