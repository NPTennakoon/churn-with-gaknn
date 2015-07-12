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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author admin
 */
public class MyTableModel extends AbstractTableModel {

//    File myfile = new File("E:\\test1\\output_churns.csv");
    File file = new File("churns_Edited.csv");
    FileReader myfileReader;
    List<String[]> myEntries;
    String[] columnNames;
    //String[] columnNames = {"Column 1", "Column 2", "Column 3", "Column 4","","","","","","","","","","","","","","",""};
//    FastVector cols;
//    FastVector data2;
    Object[][] data;
    String[] nextLine;

    public MyTableModel(File myfiles) throws IOException {
        File myfile = myfiles;
        FileInputStream fin = null;
        try {
            try {
                myfileReader = new FileReader(myfile);
            } catch (FileNotFoundException ex) {
                System.out.println("exception: " + ex.toString());
            }
            CSVReader reader = new CSVReader(myfileReader);
            columnNames = reader.readNext();
            myEntries = reader.readAll();
            data = myEntries.toArray(new String[0][]);

            ///////////////////////////////
            // get the column headers//
//            String aLine;
//            cols = new FastVector();
//            data2 = new FastVector();
//            fin = new FileInputStream(myfile);
//            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
//            // extract column names
//            StringTokenizer st1 = new StringTokenizer(br.readLine(), ",");
//            while (st1.hasMoreTokens()) {
//                cols.addElement(st1.nextToken());
//            }
//            // extract data
//            while ((aLine = br.readLine()) != null) {
//                StringTokenizer st2 =
//                        new StringTokenizer(aLine, ",");
//                while (st2.hasMoreTokens()) {
//                    data2.addElement(st2.nextToken());
//                }
//            }
//            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////////////////////////////
    }

    public int getColumnCount() {
        return columnNames.length;
//        return cols.size();
    }

    public int getRowCount() {
        return data.length;
//        return data2.size() / getColumnCount();
    }

//    public void assign(){
//        for(int i=0;i<columnNames.length;i++){
//           columnNames[i] ="aaa";
//        }
//    }
    @Override
    public String getColumnName(int col) {
        return columnNames[col];

//        String colName = "";
//
//        if (col <= getColumnCount()) {
//            colName = (String) cols.elementAt(col);
//        }
//        return colName;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];

//        return (String) data2.elementAt((row * getColumnCount()) + col);
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
//     data2.setElementAt(value, col);
        fireTableCellUpdated(row, col);
    }

    public void writeCSV(File file) throws IOException {
        //   TableModel model = TestTableModel.getModel();
        FileWriter excel = new FileWriter(file);

        for (int i = 0; i < getColumnCount(); i++) {
            excel.write(getColumnName(i) + ",");
        }

        excel.write("\n");

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                excel.write(getValueAt(i, j).toString() + ",");
            }
            excel.write("\n");
        }

        excel.close();
    }

    public static void main(String[] args) throws IOException {
        // this.assign();
        File myfile1 = new File("output_churns.csv");
        MyTableModel mtm = new MyTableModel(myfile1);
    }
}
