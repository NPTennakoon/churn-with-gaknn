package gaknn.core;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.text.ParseException;
import java.util.Date;

/**
 * Class for handling attributes. Following attribute types are supported.
 *
 * Numeric String Nominal Date
 *
 * @author Niroshinie Dayaratne
 */
public class Attribute
        implements Copyable {

    /**
     * Constant set for numeric attributes.
     */
    public static final int NUMERIC = 0;

    /**
     * Constant set for nominal attributes.
     */
    public static final int NOMINAL = 1;

    /**
     * Constant set for attributes with string values.
     */
    public static final int STRING = 2;

    /**
     * Constant set for attributes with date values.
     */
    public static final int DATE = 3;

    /**
     * The attribute's name.
     */
    public String m_Name;

    private boolean m_Selected;

    /**
     * The attribute's type.
     */
    private int m_Type;
    /*@ invariant m_Type == NUMERIC || 
     m_Type == DATE || 
     m_Type == STRING || 
     m_Type == NOMINAL ||
     */

    private /*@ spec_public @*/ FastVector m_Values;

    /**
     * Mapping of values to indices (if nominal or string).
     */
    private Hashtable m_Hashtable;

    /**
     * The header information for a relation-valued attribute.
     */
//	  private Instances m_Header;
    /**
     * Date format specification for date attributes
     */
    private SimpleDateFormat m_DateFormat;

    /**
     * The attribute's index.
     */
    private int m_Index;

    private int m_ValueIndex;

    private boolean m_ClassAttribute;

    /**
     * Constructor for a numeric attribute.
     *
     * @param attributeName the name for the attribute
     * @param attribIndex, index of the attribute
     * @param attribValueIndex, Index of the value.
     */
    //@ requires attributeName != null;
    //@ requires attribIndex != null;
    //@ requires attribValueIndex != null;
    //@ ensures  m_Name == attributeName;
    public Attribute(String name, int attribIndex, int attribValueIndex) {

        m_Name = name;
        m_Index = attribIndex;
        m_ValueIndex = attribValueIndex;
        m_Values = null;
        m_Hashtable = null;
        m_ClassAttribute = false;
        m_Type = NUMERIC;
        m_Selected = true;

    }

    /**
     * Constructor for any attribute.
     *
     */
    public Attribute() {

        m_Name = "";
        m_Index = -1;
        m_ValueIndex = -1;
        m_Values = null;
        m_Hashtable = null;
        m_ClassAttribute = false;
        m_Type = 0;
        m_Selected = false;
    }

    /**
     * Constructor for an Attribute.
     *
     * @param attribute the values will be a copied fro this attribute to the
     * new one.
     */
    //@ requires attribute != null;
    public Attribute(Attribute attribute) {
        m_Name = attribute.m_Name;
        m_Index = attribute.m_Index;
        m_ValueIndex = attribute.m_ValueIndex;
        m_Values = attribute.m_Values;
        m_Hashtable = attribute.m_Hashtable;
        m_ClassAttribute = attribute.m_ClassAttribute;
        m_Type = attribute.m_Type;
        m_Selected = attribute.m_Selected;
    }

    /**
     * Constructor for a date attribute.
     *
     * @param attributeName the name for the attribute
     * @param dateFormat the format of the date attribute
     * @param attribIndex, index of the attribute
     * @param attribValueIndex, Index of the value.
     */
    //@ requires attributeName != null;
    //@ requires dateFormat != null;
    //@ requires attribIndex != null;
    //@ requires attribValueIndex != null;
    //@ ensures  m_Name == attributeName;
    public Attribute(String attributeName, String dateFormat, int attribIndex, int valueIndex) {

        m_Name = attributeName;
        m_Index = attribIndex;
        m_ValueIndex = valueIndex;
        m_Values = null;
        m_Hashtable = null;
        m_ClassAttribute = false;
        m_Selected = true;

        m_Type = DATE;
        if (dateFormat != null) {
            m_DateFormat = new SimpleDateFormat(dateFormat);
        } else {
            m_DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    /**
     * Constructor for a nominal attribute.
     *
     * @param attributeName the name for the attribute
     * @param attributeValues the format of the date attribute
     * @param attributeValues, Values of the nominal attribute
     * @param attribIndex, index of the attribute
     * @param attribValueIndex, Index of the value.
     */
    //@ requires attributeName != null;
    //@ requires attributeValues != null;
    //@ requires attribIndex != null;
    //@ requires attribValueIndex != null;
    //@ ensures  m_Name == attributeName;
    public Attribute(String attributeName,
            FastVector attributeValues, int Index, int valueIndex) {

        m_Name = attributeName;
        m_Index = Index;
        m_ValueIndex = valueIndex;

        if (attributeValues == null) {
            m_Type = STRING;
        } else {
            m_Values = new FastVector(attributeValues.size());

            for (int i = 0; i < attributeValues.size(); i++) {
                Object store = attributeValues.elementAt(i);
                if (m_Values.contains(store)) {
                    throw new IllegalArgumentException("A nominal attribute ("
                            + attributeName + ") cannot"
                            + " have duplicate labels (" + store + ").");
                }
                m_Values.addElement(store);
            }
            m_Type = NOMINAL;
        }
        m_ClassAttribute = false;
        m_Selected = true;
    }

    /**
     * Returns the index of the nominal attribute
     *
     * @param value the value of the attribute
     */
    public final int IndexOfValue(String value) {
        return m_Values.indexOf(value);
    }

    /**
     * Returns the value of the attribute given by the index for a nominal
     * attribute.
     *
     * @param index the index of the value
     */
    public final String Value(int index) {
        return String.valueOf(m_Values.elementAt(index));
    }

    /**
     * Returns the number of values of the nominal attribute.
     */
    public final int NumValues() {
        m_Values.trimToSize();
        return m_Values.capacity();
    }

    /**
     * Returns the value index of the attribute.
     */
    public final int ValueIndex() {
        return m_ValueIndex;
    }

    /**
     * Sets the value index of the attribute.
     *
     * @param index the index of the value
     */
    public final void ValueIndex(int index) {
        m_ValueIndex = index;
    }

    /**
     * Returns the type of the attribute.
     */
    public final /*@ pure @*/ int Type() {
        return m_Type;
    }

    /**
     * Returns the name of the attribute.
     */
    public final /*@ pure @*/ String Name() {
        return m_Name;
    }

    /**
     * Returns if the attribute is selected.
     */
    public final boolean Selected() {
        return m_Selected;
    }

    /**
     * Sets if the attribute is selected.
     *
     * @param select true or false.
     */
    public void Selected(boolean select) {
        m_Selected = select;
    }

    /**
     * Parses the date as depending on the format.
     *
     * @param date string.
     */
    public double ParseDate(String string) throws ParseException {
        switch (m_Type) {
            case DATE:
                long time = m_DateFormat.parse(string).getTime();
                // TODO put in a safety check here if we can't store the value in a double.
                return (double) time;
            default:
                throw new IllegalArgumentException("Can only parse date values for date"
                        + " attributes!");
        }
    }

    /**
     * formats the date according to the format.
     *
     * @param date double value of the date.
     */
    public /*@pure@*/ String FormatDate(double date) {
        switch (m_Type) {
            case DATE:
                return m_DateFormat.format(new Date((long) date));
            default:
                throw new IllegalArgumentException("Can only format date values for date"
                        + " attributes!");
        }
    }

    /**
     * Returns if the attribute is nominal.
     *
     */
    public final /*@ pure @*/ boolean IsNominal() {
        return (m_Type == NOMINAL);
    }

    /**
     * Returns if attribute is numeric.
     */
    public final /*@ pure @*/ boolean IsNumeric() {
        return ((m_Type == NUMERIC) || (m_Type == DATE));
    }

    /**
     * Returns if attribute is string.
     */
    public final /*@ pure @*/ boolean IsString() {
        return (m_Type == STRING);
    }

    /**
     * Sets if the attribute is the class atrribute.
     *
     * @param true or false.
     */
    public void SetClassAttribute(boolean val) {
        m_ClassAttribute = val;
    }

    /**
     * Returns if attribute is the class attribute.
     */
    public boolean IsClassAttribute() {
        return m_ClassAttribute;
    }

    //@ ensures \result <==> (m_Type == DATE);
    public final /*@ pure @*/ boolean IsDate() {
        return (m_Type == DATE);
    }

    /**
     * Produces a shallow copy of this attribute.
     *
     * @return a copy of this attribute with the same index
     */
    //@ also ensures \result instanceof Attribute;
    public Object Copy() {

        Attribute copy = new Attribute(m_Name, m_Index, m_ValueIndex);

        copy.m_Index = m_Index;
        copy.m_ValueIndex = m_ValueIndex;
        copy.m_Type = m_Type;
        copy.m_Values = m_Values;
        copy.m_Hashtable = m_Hashtable;
        copy.m_DateFormat = m_DateFormat;
        copy.m_ClassAttribute = m_ClassAttribute;

        return copy;
    }

}
