/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import au.com.bytecode.opencsv.CSVReader;
import gaknn.core.FastVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author admin
 */
public class AttributeTable {

    File myfile = new File("ColumnsNames.csv");
    FileReader myfileReader;
    List<String[]> myEntries;
    String[] columnNames = {"", "Attribute Name"};
    Object[][] data;

    FastVector cols;
    FastVector data2;

    public AttributeTable() {

        FileInputStream fin = null;
        try {
            cols = new FastVector();

            fin = new FileInputStream(myfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            // extract column names
            StringTokenizer st1 = new StringTokenizer(br.readLine(), ",");
            while (st1.hasMoreTokens()) {
                cols.addElement(st1.nextToken());
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getColumnCount() {
        return cols.size();
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        // return columnNames[col];

        String colName = "";

        if (col <= getColumnCount()) {
            colName = (String) cols.elementAt(col);
        }

        return colName;
    }

    public static void main(String[] args) throws IOException {
        // this.assign();
        AttributeTable at = new AttributeTable();
        System.out.println(at.getColumnCount());
        System.out.println(at.getColumnName(3));
    }

}
