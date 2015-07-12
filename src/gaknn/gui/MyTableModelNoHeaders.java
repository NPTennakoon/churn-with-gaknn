/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

import gaknn.core.FastVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

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
public class MyTableModelNoHeaders extends AbstractTableModel {

//    File myfile = new File("E:\\test1\\output_churns.csv");
    FileReader myfileReader;
    List<String[]> myEntries;
    //  String[] columnNames = null;
//  String[] columnNames = {"Column 1", "Column 2", "Column 3", "Column 4"};
    FastVector cols;
    FastVector data2;
    Object[][] data;

    public MyTableModelNoHeaders(File myfiles) throws IOException {
        File myfile = myfiles;
        FileInputStream fin = null;
        try {
//            try {
//                myfileReader = new FileReader(myfile);
//            } catch (FileNotFoundException ex) {
//                System.out.println("exception: " + ex.toString());
//            }
//            CSVReader reader = new CSVReader(myfileReader);
//            try {
//                myEntries = reader.readAll();
//            } catch (IOException ex) {
//                System.out.println("exception: " + ex.toString());
//            }
//            data = myEntries.toArray(new String[0][]);

            ///////////////////////////////
            // get the column headers//
            String aLine;
            cols = new FastVector();
            data2 = new FastVector();
            fin = new FileInputStream(myfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            // extract column names
            StringTokenizer st1 = new StringTokenizer(br.readLine(), ",");
            while (st1.hasMoreTokens()) {
                cols.addElement(st1.nextToken());
            }
            // extract data
            while ((aLine = br.readLine()) != null) {
                StringTokenizer st2
                        = new StringTokenizer(aLine, ",");
                while (st2.hasMoreTokens()) {
                    data2.addElement(st2.nextToken());
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////////////////////////////
    }

    public int getColumnCount() {
        //return columnNames.length;
        return cols.size();
    }

    public int getRowCount() {
//        return data.length;
        return data2.size() / getColumnCount();
    }

//    public void assign(){
//        for(int i=0;i<columnNames.length;i++){
//           columnNames[i] ="aaa";
//        }
//    }
//    public String getColumnName(int col) {
//        //return columnNames[col];
//
//        String colName = "";
//
//        if (col <= getColumnCount()) {
//            colName = (String) cols.elementAt(col);
//        }
//
//        return colName;
//    }
    public Object getValueAt(int row, int col) {
        // return data[row][col];

        return (String) data2.elementAt((row * getColumnCount()) + col);
    }

    public static void main(String[] args) throws IOException {
        // this.assign();
        File myfile1 = new File("E:\\test1\\output_churns.csv");
        //   MyTableModel mtm = new MyTableModel(myfile1);
    }
}
