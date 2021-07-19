/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handdrawdigitrecognition;

/**
 *
 * @author Harry
 */
public class TrainingGroup {
    //array to store the straining data
    private TrainingData[] group;
    
    public TrainingGroup(TrainingData[] data){
    
        this.group = data;
        
    }

    public TrainingData[] getGroup() {
        return group;
    }


    
    
}
