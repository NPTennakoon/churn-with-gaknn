/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ParentSet.java
 * Copyright (C) 2001-2012 University of Waikato, Hamilton, New Zealand
 *
 */
package gaknn.search;

import java.io.Serializable;

import gaknn.core.Instance;
import gaknn.core.Instances;

/**
 * Helper class for Bayes Network classifiers. Provides datastructures to
 * represent a set of parents in a graph.
 *
 * @author Remco Bouckaert (rrb@xm.co.nz)
 * @version $Revision: 8034 $
 */
public class ParentSets
        implements Serializable {

    /**
     * for serialization
     */
    static final long serialVersionUID = 4155021284407181838L;
    /**
     * Holds indexes of parents
     */
    private int[] m_nParents;

    /**
     * returns index parent of parent specified by index
     *
     * @param iParent Index of parent
     * @return index of parent
     */
    public int getParent(int iParent) {
        return m_nParents[iParent];
    }

    public int[] getParents() {
        return m_nParents;
    }

    /**
     * sets index parent of parent specified by index
     *
     * @param iParent Index of parent
     * @param nNode index of the node that becomes parent
     */
    public void SetParent(int iParent, int nNode) {
        m_nParents[iParent] = nNode;
    } // SetParent
    /**
     * Holds number of parents
     */
    private int m_nNrOfParents = 0;

    /**
     * returns number of parents
     *
     * @return number of parents
     */
    public int getNrOfParents() {
        return m_nNrOfParents;
    }

    /**
     * test if node is contained in parent set
     *
     * @param iNode node to test for
     * @return number of parents
     */
    public boolean contains(int iNode) {
        for (int iParent = 0; iParent < m_nNrOfParents; iParent++) {
            if (m_nParents[iParent] == iNode) {
                return true;
            }
        }
        return false;
    }
    /**
     * Holds cardinality of parents (= number of instantiations the parents can
     * take)
     */
    private int m_nCardinalityOfParents = 1;

    /**
     * returns cardinality of parents
     *
     * @return the cardinality
     */
    public int getCardinalityOfParents() {
        return m_nCardinalityOfParents;
    }

    /**
     * returns cardinality of parents after recalculation
     *
     * @return the cardinality
     */
    public int getFreshCardinalityOfParents(Instances _Instances) {
        m_nCardinalityOfParents = 1;
        for (int iParent = 0; iParent < m_nNrOfParents; iParent++) {
            m_nCardinalityOfParents *= _Instances.Attribute(m_nParents[iParent]).NumValues();
        }
        return m_nCardinalityOfParents;
    }

    /**
     * default constructor
     */
    public ParentSets() {
        m_nParents = new int[10];
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
    }    // ParentSet

    /**
     * constructor
     *
     * @param nMaxNrOfParents upper bound on nr of parents
     */
    public ParentSets(int nMaxNrOfParents) {
        m_nParents = new int[nMaxNrOfParents];
        m_nNrOfParents = 0;
        m_nCardinalityOfParents = 1;
    }    // ParentSet

    /**
     * copy constructor
     *
     * @param other other parent set
     */
    public ParentSets(ParentSets other) {
        m_nNrOfParents = other.m_nNrOfParents;
        m_nCardinalityOfParents = other.m_nCardinalityOfParents;
        m_nParents = new int[m_nNrOfParents];

        for (int iParent = 0; iParent < m_nNrOfParents; iParent++) {
            m_nParents[iParent] = other.m_nParents[iParent];
        }
    }    // ParentSet

    /**
     * reserve memory for parent set
     *
     * @param nSize maximum size of parent set to reserver memory for
     */
    public void maxParentSetSize(int nSize) {
        m_nParents = new int[nSize];
    }    // MaxParentSetSize

    /**
     * Add parent to parent set and update internals (specifically the
     * cardinality of the parent set)
     *
     * @param nParent parent to add
     * @param _Instances used for updating the internals
     */
    public void addParent(int nParent, Instances _Instances) {
//        System.out.println("m_nParents.length" + m_nParents.length);
//        System.out.println("m_nNrOfParents" + m_nNrOfParents);

        if (m_nNrOfParents == m_nParents.length) { // 10) {
            // reserve more memory
            int[] nParents = new int[2 * m_nParents.length]; // 50];
            for (int i = 0; i < m_nNrOfParents; i++) {
                nParents[i] = m_nParents[i];
            }
            m_nParents = nParents;
        }
        m_nParents[m_nNrOfParents] = nParent;
        m_nNrOfParents++;
//        System.out.println("nParent" + nParent);
//        System.out.println("_Instances.Attribute(nParent)" + _Instances.Attribute(nParent - 1).NumValues());
        m_nCardinalityOfParents *= _Instances.Attribute(nParent - 1).NumValues();
//        System.out.println("cardina" + m_nCardinalityOfParents);
    }    // AddParent

    /**
     * Add parent to parent set at specific location and update internals
     * (specifically the cardinality of the parent set)
     *
     * @param nParent parent to add
     * @param iParent location to add parent in parent set
     * @param _Instances used for updating the internals
     */
    public void addParent(int nParent, int iParent, Instances _Instances) {
        if (m_nNrOfParents == m_nParents.length) { // 10) {
            // reserve more memory
            int[] nParents = new int[2 * m_nParents.length]; // 50];
            for (int i = 0; i < m_nNrOfParents; i++) {
                nParents[i] = m_nParents[i];
            }
            m_nParents = nParents;
        }
        for (int iParent2 = m_nNrOfParents; iParent2 > iParent; iParent2--) {
            m_nParents[iParent2] = m_nParents[iParent2 - 1];
        }
        m_nParents[iParent] = nParent;
        m_nNrOfParents++;
        m_nCardinalityOfParents *= _Instances.Attribute(nParent).NumValues();
    } // AddParent

    /**
     * delete node from parent set
     *
     * @param nParent node number of the parent to delete
     * @param _Instances data set
     * @return location of the parent in the parent set. This information can be
     * used to restore the parent set using the addParent method.
     */
    public int deleteParent(int nParent, Instances _Instances) {
        int iParent = 0;
        while ((m_nParents[iParent] != nParent) && (iParent < m_nNrOfParents)) {
            iParent++;
        }
        int iParent2 = -1;
        if (iParent < m_nNrOfParents) {
            iParent2 = iParent;
        }
        if (iParent < m_nNrOfParents) {
            while (iParent < m_nNrOfParents - 1) {
                m_nParents[iParent] = m_nParents[iParent + 1];
                iParent++;
            }
            m_nNrOfParents--;
            m_nCardinalityOfParents /= _Instances.Attribute(nParent).NumValues();
        }
        return iParent2;
    } // DeleteParent

    /**
     * Delete last added parent from parent set and update internals
     * (specifically the cardinality of the parent set)
     *
     * @param _Instances used for updating the internals
     */
    public void deleteLastParent(Instances _Instances) {
        m_nNrOfParents--;
        m_nCardinalityOfParents
                = m_nCardinalityOfParents / _Instances.Attribute(m_nParents[m_nNrOfParents]).NumValues();
    }    // DeleteLastParent

    /**
     * Copy makes current parents set equal to other parent set
     *
     * @param other : parent set to make a copy from
     */
    public void copy(ParentSets other) {
        m_nCardinalityOfParents = other.m_nCardinalityOfParents;
        m_nNrOfParents = other.m_nNrOfParents;
        for (int iParent = 0; iParent < m_nNrOfParents; iParent++) {
            m_nParents[iParent] = other.m_nParents[iParent];
        }
    } // Copy
}      // class ParentSet

