/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.gui;

/**
 *
 * @author admin
 */
import gaknn.OptimizeKNN;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;

public class rad extends JPanel
        implements ActionListener {

    JLabel picture;
    JRadioButton[] Button;

    public rad() throws FileNotFoundException, IOException {
        super(new BorderLayout());

        AttributeTable at = new AttributeTable();
        int count = at.getColumnCount();
        //System.out.println(at.getColumnName(3));

        Button = new JRadioButton[count];
//        JRadioButton[] Button = new JRadioButton[count];
//        Button.setMnemonic(KeyEvent.VK_P);
//        Button.setActionCommand(pigString);

        JPanel radioPanel1 = new JPanel(new GridLayout(0, 1));

        ButtonGroup group1 = new ButtonGroup();

        for (int i = 0; i < count; i++) {
            Button[i] = new JRadioButton();
            String a = Integer.toString(i + 1);
            Button[i].setActionCommand(a);

            Button[i].setText(at.getColumnName(i));
            group1.add(Button[i]);
            Button[i].addActionListener(this);

            int j = i + 1;
            picture = new JLabel(createImageIcon(j + ".png"));
            radioPanel1.add(Button[i]);
        }

//        for(int i=0;i<count;i++){
//           String a=Integer.toString(i);
//            Button[i].setActionCommand(a);
//            Button[i].addActionListener(this);
//
//        //Set up the picture label.
//        picture = new JLabel(createImageIcon("E:\\test1\\"+i+".png"));
//        }
        //The preferred size is hard-coded to be the width of the
        //widest image and the height of the tallest image.
        //A real program would compute this.
        picture.setPreferredSize(new Dimension(200, 200));

        add(radioPanel1, BorderLayout.LINE_START);
        //  add(radioPanel1);
        add(picture, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Listens to the radio buttons.
     */
    public void actionPerformed(ActionEvent e) {
        picture.setIcon(createImageIcon(e.getActionCommand() + ".png"));

    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
//        java.net.URL imgURL = rad.class.getResource(path);
//        System.out.println("imgurl"+imgURL);
        if (path != null) {
            return new ImageIcon(path);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public static void createAndShowGUI() throws FileNotFoundException, IOException {
        //Create and set up the window.
        JPanel frame = new JPanel();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new rad();
        newContentPane.setOpaque(true); //content panes must be opaque
        // frame.setComponentZOrder(frame, WIDTH);
        frame.add(newContentPane);

        //Display the window.
        // frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //    createAndShowGUI();
            }
        });

    }
}
