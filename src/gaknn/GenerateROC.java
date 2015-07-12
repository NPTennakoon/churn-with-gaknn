/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn;

/**
 *
 * @author admin
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.rosuda.JRI.Rengine;

public class GenerateROC {

    /**
     * @param args the command line arguments
     */
    public void GenROC() throws IOException {

//Set some labels for the plot
        String title = "R Plot in JFrame";
        String xlab = "X Label";
        String ylab = "Y Label";
//Start R
        String newargs1[] = {"--no-save"};

        Rengine r2 = new Rengine(newargs1, false, null);
//Do some calcs and plot the chart but save as a png in the working folder

        r2.eval("png(file=\"ROC.png\",width=1600,height=1600,res=400)");
        r2.eval("source('test2.R')");

        r2.eval("dev.off()");
        r2.eval("png(file=\"data.png\",width=1500,height=1500,res=400)");
        r2.eval("source('Visualize.R')");

        //r.eval("plot(a,b,type='o',col=\"Blue\",main=\"" + title + "\",xlab=\""
        // + xlab + "\",ylab=\"" + ylab + "\")");
        r2.eval("dev.off()");
        r2.eval("source('stats.R')");

//get the image and create a new imagepanel
        File file = new File("ROC.png");
        Image image = ImageIO.read(file);
        imagePanel myPanel = new imagePanel(image);

        File file1 = new File("data.png");
        Image image1 = ImageIO.read(file1);
        imagePanel myPanel1 = new imagePanel(image1);

//Create a new frame and add the imagepanel
        JFrame aFrame = new JFrame();
        aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aFrame.getContentPane().add(myPanel1, BorderLayout.CENTER);
        aFrame.pack();
        aFrame.setVisible(true);
        aFrame.setSize(new Dimension(600, 600));

        JFrame bFrame = new JFrame();
        bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bFrame.getContentPane().add(myPanel, BorderLayout.CENTER);
        bFrame.pack();
        bFrame.setVisible(true);
        bFrame.setSize(new Dimension(600, 600));

    }

    static class imagePanel extends JPanel {

        Image image = null;

        public imagePanel(Image image) {
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //there is a picture: draw it
            if (image != null) {
                int height = this.getSize().height;
                int width = this.getSize().width;
                g.drawImage(image, 0, 0, width, height, this);
            }
        }
    }

}

class imagePanel extends JPanel {

    Image image = null;

    public imagePanel(Image image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //there is a picture: draw it
        if (image != null) {
            int height = this.getSize().height;
            int width = this.getSize().width;
            g.drawImage(image, 0, 0, width, height, this);
        }
    }
}
