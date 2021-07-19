/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handdrawdigitrecognition;

import java.awt.image.BufferedImage;


public class TrainingData {
    private BufferedImage image;
    private int label;
    
    
    public TrainingData(BufferedImage image, int label){
    
        this.image = image;
        this.label = label;
        
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getLabel() {
        return label;
    }
}
