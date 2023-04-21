/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handdrawdigitrecognition;

import NeuralNetwork.NeuralNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static javax.swing.Spring.scale;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author Harry
 */
public class HandDrawDigitRecognition extends JPanel implements KeyListener, ActionListener {

    /**
     * @param args the command line arguments
     */
    JFrame frame;
    BufferedImage image;
    static BufferedImage lastImage;
    ArrayList<Integer[]> pointsToDraw = new ArrayList<>();
    static NeuralNetwork ir;
    int guess;

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics2D.setColor(Color.black);
        graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
        graphics2D.setColor(Color.white);

        graphics2D.drawImage(lastImage, 0, 0, null);
        int r = 40;

        graphics2D.drawString("guess: " + guess, this.getWidth() / 2, 20);

        for (int i = 0; i < pointsToDraw.size(); i++) {
            // Create a new instance of the RadialGradientPaint class.
            // Point2D center = new Point2D.Float(pointsToDraw.get(i)[0],
            // pointsToDraw.get(i)[1]);
            // float radius = r;
            // float[] dist = { 0.2f, 0.9f };
            // Color[] colors = { Color.WHITE, Color.BLACK };
            // RadialGradientPaint p = new RadialGradientPaint(center, radius, dist,
            // colors);
            // graphics2D.setPaint(p);
            graphics2D.fillOval(pointsToDraw.get(i)[0] - (r / 2), pointsToDraw.get(i)[1] - (r / 2), r, r);
        }

    }

    public HandDrawDigitRecognition() {

        super(true);

    }

    public static void main(String[] args) throws Exception {
        HandDrawDigitRecognition h = new HandDrawDigitRecognition();

        int[] nodes = { 784, 200, 80, 10 };
        ir = new NeuralNetwork(nodes);
        ir.loadNetwork("digitRecognition");
        // h.train(ir, 1);
        // System.out.println("saving");
        // ir.saveNetwork("digitRecognition");
        // System.out.println("done");

        h.setFrame();

        // int correct = 0;
        // for (int i = 50000; i < 51000; i++) {
        // TrainingData td = new TrainingData(h.readImage(i), h.readLable(i));
        // int output = h.use(ir, td.getImage());
        // if (output == td.getLabel()) {
        // correct++;
        // }
        // System.out.println(td.getLabel());
        // System.out.println(output);
        // }
        // System.out.println(correct);

        h.repaint();

    }

    private void setFrame() {

        frame = new JFrame();

        frame.add(this);

        frame.setSize(500, 500);

        // setting start position of the frame
        frame.setLocationRelativeTo(null);

        // closing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        frame.setTitle("num");

        frame.setBackground(Color.white);

        // set frame visibility
        frame.setVisible(true);

        // adding a key listner
        frame.addKeyListener(this);
        // adding mouse listner
        AddMouseHandler();

    }

    public void train(NeuralNetwork nn, int epochs) throws Exception {

        TrainingGroup[] tg = getGroups();
        for (int loop = 0; loop < epochs; loop++) {

            List<TrainingGroup> listg = Arrays.asList(tg);
            Collections.shuffle(listg);
            listg.toArray(tg);

            for (int i = 0; i < tg.length; i++) {

                TrainingData[] group = tg[i].getGroup();
                List<TrainingData> listd = Arrays.asList(group);
                Collections.shuffle(listd);
                listd.toArray(group);

                for (int j = 0; j < group.length; j++) {

                    BufferedImage image = group[j].getImage();
                    double[] pixels = imageToArray(image);
                    int lable = group[j].getLabel();

                    double[] idealAnswer = new double[10];
                    for (int p = 0; p < idealAnswer.length; p++) {

                        if (p == lable) {
                            idealAnswer[p] = 1;
                        } else {
                            idealAnswer[p] = 0;
                        }
                    }
                    nn.train(pixels, idealAnswer);
                }

            }

        }

    }

    public int use(NeuralNetwork nn, BufferedImage img) throws Exception {

        int[] answers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        double[] input = imageToArray(img);

        double[] output = nn.feedforward(input);

        int pos = 0;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > output[pos]) {

                pos = i;

            }

        }
        return answers[pos];
    }

    public double[] imageToArray(BufferedImage img) {

        double[] pixels = new double[img.getWidth() * img.getHeight()];

        int currentPixel = 0;
        for (int y = 0; y < img.getWidth(); y++) {
            for (int x = 0; x < img.getHeight(); x++) {

                Color c = new Color(img.getRGB(x, y));
                pixels[currentPixel] = (double) c.getRed() / 255;

                currentPixel++;

            }
        }

        return pixels;

    }

    public TrainingGroup[] getGroups() throws IOException {
        ArrayList<TrainingGroup> groups = new ArrayList<>();
        ArrayList<BufferedImage> images = new ArrayList<>();
        ArrayList<Integer> lables = new ArrayList<>();
        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < 50000; i++) {

            images.add(readImage(i));

            lables.add(readLable(i));

        }

        ArrayList<TrainingData> data = new ArrayList<>();
        while ((!lables.isEmpty()) && (!images.isEmpty())) {

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < lables.size(); j++) {
                    TrainingData td;
                    int lable = lables.get(j);
                    boolean contains = false;
                    for (int p = 0; p < data.size(); p++) {
                        if (data.get(p).getLabel() == lable) {
                            contains = true;
                            break;
                        }
                    }
                    if (contains == false) {
                        td = new TrainingData(images.get(j), lables.get(j));
                        lables.remove(j);
                        images.remove(j);
                        data.add(td);
                        break;
                    }

                }
            }
            TrainingGroup group = new TrainingGroup(data.toArray(new TrainingData[0]));
            data.clear();
            groups.add(group);

        }

        return groups.toArray(new TrainingGroup[0]);

    }

    public BufferedImage readImage(int i) throws FileNotFoundException, IOException {

        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
        int imageOffset = 16; // position of first pixel in byte array
        FileInputStream fis = null;

        /*
         * TRAINING SET IMAGE FILE (train-images-idx3-ubyte):
         * [imageOffset] [type] [value] [description]
         * 0000 32 bit integer 0x00000803(2051) magic number
         * 0004 32 bit integer 60000 number of images
         * 0008 32 bit integer 28 number of rows
         * 0012 32 bit integer 28 number of columns
         * 0016 unsigned byte ?? pixel
         * 0017 unsigned byte ?? pixel
         * ........
         * xxxx unsigned byte ?? pixel
         * Pixels are organized row-wise. Pixel values are 0 to 255. 0 means background
         * (white), 255 means foreground (black).
         */
        File infile = new File("train-images-idx3-ubyte");
        fis = new FileInputStream(infile);

        // byte array to store data from iamge file
        // byte array interprests value as two's complement (signed)
        byte[] buffer = new byte[(int) infile.length()];
        fis.read(buffer);

        int currentPixel = 16 + (i * 784);
        // int value = 0;
        // for(int i = 0; i < 4; i++){
        // value = value * 256 + (buffer[i]& 0xFF);
        // }
        // System.out.println("");
        // System.out.println(value);

        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {

                // & 0xFF, takes the byte into a larger register and extends the 8th bit value
                // to the rest of the following bits,
                // then ands it with FF which makes the byte unsigned because it flips the
                // extended bit
                int pixelValue = buffer[currentPixel] & 0xFF;
                Color c = new Color(pixelValue, pixelValue, pixelValue);// grey scale colour
                int rgb = c.getRGB();
                image.setRGB(x, y, rgb);
                currentPixel++;
            }
        }

        return image;

    }

    public int readLable(int i) throws FileNotFoundException, IOException {

        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        FileInputStream fis = null;

        /*
         * TRAINING SET LABEL FILE (train-labels-idx1-ubyte):
         * [imageOffset] [type] [value] [description]
         * 0000 32 bit integer 0x00000801(2049) magic number (MSB first)
         * 0004 32 bit integer 60000 number of items
         * 0008 unsigned byte ?? label
         * 0009 unsigned byte ?? label
         * ........
         * xxxx unsigned byte ?? label
         * The labels values are 0 to 9.
         */
        File infile;
        infile = new File("train-labels-idx1-ubyte");
        fis = new FileInputStream(infile);
        byte[] buffer = new byte[(int) infile.length()];
        fis.read(buffer);
        int labelOffset = 8; // position of first label in byte array
        int lable = buffer[labelOffset + i];

        return lable;

    }

    public BufferedImage scaleImage(BufferedImage img, int w2, int h2) {
        int w = img.getWidth();
        int h = img.getHeight();
        // Create a new image of the proper size
        // int w2 = 28;
        // int h2 = 28;
        double scaleW = ((double) w2 / (double) w);
        double scaleH = ((double) h2 / (double) h);
        BufferedImage scaledImage = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scaleW, scaleH);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

        scaleOp.filter(img, scaledImage);

        return scaledImage;
    }

    // adds a mouse listner to the program so the user can draw a digit
    private void AddMouseHandler() {
        MouseInputAdapter mia = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int mouseX = p.x;
                int mouseY = p.y;
                // draw
                Integer[] point = { mouseX, mouseY };
                pointsToDraw.add(point);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent md) {
                Point p = md.getPoint();
                int mouseX = p.x;
                int mouseY = p.y;
                // draw
                Integer[] point = { mouseX, mouseY };
                pointsToDraw.add(point);
                repaint();
            }

        };
        addMouseListener(mia);
        addMouseMotionListener(mia);

    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case 10:
                // pressing enter
                image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = image.createGraphics();
                this.paint(graphics2D);

                // scale the image too small, then scale it back up to 28 x 28
                // this will give the image the low quality, shitty look like the MNIST data
                image = this.scaleImage(image, 20, 20);
                image = this.scaleImage(image, 28, 28);
                lastImage = image;

            {
                try {
                    int output = use(ir, image);
                    guess = output;
                    System.out.println(output);
                    pointsToDraw.clear();
                    repaint();
                } catch (Exception ex) {
                    Logger.getLogger(HandDrawDigitRecognition.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

                break;
            default:

        }

    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    }

}
