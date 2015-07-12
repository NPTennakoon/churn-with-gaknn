/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.datapreprocess;

/**
 *
 * @author admin
 */
import gaknn.gui.GaknnFrame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MissingValueFilter {

    public String completed;
    private static String file;

    public static void main(String[] args) {

        GaknnFrame frame2 = new GaknnFrame();
        file = frame2.relationName;
//file="A";

        //   System.out.println("file path"+file);
        MissingValueFilter obj = new MissingValueFilter();
        obj.run();
    }

    public void run() {
        String oldArffFile = "E:\\Project\\TestFiles\\" + file + ".arff";
        String newArffFile = "E:\\Project\\TestFiles\\missing_values_filtered.arff";
        String MissingValues = "E:\\Project\\TestFiles\\hasmissing_values.csv";
        BufferedReader br = null;
        PrintWriter pr = null;
        PrintWriter ms = null;
        String line;

        try {
            pr = new PrintWriter(new FileWriter(newArffFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            ms = new PrintWriter(new FileWriter(MissingValues));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            br = new BufferedReader(new FileReader(oldArffFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    // if the line is empty, print it
                    pr.println(line);
                    //   ms.println(line);
                } else if (line.charAt(0) == '@') {
                    // if the line starts with '@' sign, print that too
                    pr.println(line);
                    //  ms.println(line);
                } else {
                    // pattern matcher to find out question mark. \\? is the regex for that
                    Pattern p = Pattern.compile("\\?");
                    Matcher m = p.matcher(line);
                    boolean b = m.find();
                    if (!b) {
                        // if the pattern is not in the line, print that line too
                        pr.println(line);
                    } else {
                        ms.println(line);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        pr.close();
        ms.close();
        System.out.println("Done");
        completed = "Done";
    }
}
