package NeuralNetwork;

public class Vector {

    private double[] vector;

    //constructors
    //------------------------------------------------------------------------------
    /**
     *
     * creates a new vector based on a given length
     *
     * @param length the length of the vector
     */
    public Vector(int length) {

        vector = new double[length];

    }

    /**
     *
     * creates a new vector based on a given array of values
     *
     * @param val
     */
    public Vector(double[] values) {

        vector = values;

    }

    //----------------------------------------------------------------------------
    /**
     *
     * adds the given value to this vector
     *
     * @param n the value to add
     */
    public void add(double n) {

        for (int i = 0; i < vector.length; i++) {

            vector[i] += n;

        }

    }

    /**
     *
     * uses elementwise addition to add a given vector to this vector requires
     * the given vector to be the same length as this vector
     *
     * @param v the vector to add on
     */
    public void add(Vector v) throws Exception {
        if (vector.length != v.length()) {

            throw new Exception("given vector must be the same length as this vector");

        } else {

            for (int i = 0; i < this.length(); i++) {

                vector[i] += v.valAt(i);

            }
        }

    }

    /**
     *
     * adds a given value to every element of a given vector
     *
     * @param v the vector to add
     * @param n the value to add on
     * @return the result of v + n
     */
    public static Vector staticAdd(Vector v, double n) {

        Vector newV = new Vector(v.length());
        for (int i = 0; i < v.length(); i++) {

            newV.set(i, (v.valAt(i) + n));

        }

        return newV;

    }

    /**
     *
     * uses elementwise addition to add together two given vectors requires both
     * given vectors to be of equal length
     *
     * @param v1 the first vector to add
     * @param v2 the second vector to add
     * @return the result of v1 + v2
     * @throws Exception v1 and v2 must be of equal length
     */
    public static Vector staticAdd(Vector v1, Vector v2) throws Exception {

        Vector newV = new Vector(v1.length());

        if (v1.length() != v2.length()) {

            throw new Exception("v1 and v2 must be of equal length");

        } else {

            for (int i = 0; i < v1.length(); i++) {

                newV.set(i, (v1.valAt(i) + v2.valAt(i)));

            }

        }

        return newV;
    }

    /**
     *
     * subtracts a given value from every element in this vector
     *
     * @param n the value to subtract
     */
    public void subtract(double n) {

        for (int i = 0; i < vector.length; i++) {

            vector[i] -= n;

        }

    }
    
    /**
     * 
     * uses elementwise subtraction to subtract each element in a given vector from its corresponding element in this vector
     * requires the given vector to be of equal length to this vector
     * 
     * @param v the vector to subtract
     * @throws Exception given vector must be the same length as this vector
     */
    public void  subtract(Vector v) throws Exception{
    
        if(vector.length != v.length()){
        
            throw new Exception("given vector must be the same length as this vector");
            
        }
        else{
        
            for(int i = 0; i < vector.length; i++){
            
                vector[i] -= v.valAt(i);
                
            }
            
        }
        
    }
    
    /**
     * 
     * subtracts a value from a given vector and returns the result
     * 
     * @param v the vector to subtract from
     * @param n the value to subtract
     * @return the result of v - n
     */
    public static Vector staticSubtract(Vector v, double n){

        Vector newV = new Vector(v.length());
        for (int i = 0; i < v.length(); i++) {

            newV.set(i, (v.valAt(i) - n));

        }

        return newV;
    }
    

    /**
     * 
     * uses elementwise subtraction to subtract a given vector from another given vector
     * require both vectors to be of equal length
     * 
     * @param v1 the vector to subtract from
     * @param v2 the vector to subtract
     * @return the result of v1 - v2
     * @throws Exception v1 and v2 must be of equal length
     */
    public static Vector staticSubtract(Vector v1, Vector v2) throws Exception{

        Vector newV = new Vector(v1.length());

        if (v1.length() != v2.length()) {

            throw new Exception("v1 and v2 must be of equal length");

        } else {

            for (int i = 0; i < v1.length(); i++) {

                newV.set(i, (v1.valAt(i) - v2.valAt(i)));

            }

        }

        return newV;
    }
    
    
    
    /**
     *
     * calculates the dot product of two given vectors requires the length of
     * both vectors to be equal
     *
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the result of v1.v2
     */
    public static double dotProduct(Vector v1, Vector v2) throws Exception {

        double product = 0;

        if (v1.length() != v2.length()) {

            throw new Exception("vectors must have equal length");

        } else {

            for (int i = 0; i < v1.length(); i++) {

                product += v1.valAt(i) * v2.valAt(i);

            }
        }

        return product;

    }
    
    /**
     * 
     * turns this vector into a unit vector
     * 
     * @throws Exception vector must be of length 2
     */
    public void normalize() throws Exception{
    
        Vector newV = new Vector(2);
        if(vector.length != 2){
        
            throw new Exception("vector must be of length 2");
            
        }else{
        
            double magnitude = Math.sqrt((Math.pow(vector[0], 2) + Math.pow(vector[1], 2)));
            
            for(int i = 0; i < newV.length(); i++){
            
                newV.set(i, vector[i] / magnitude);
                
            }
            
            vector = newV.returnAsArray();
            
        }
        
    }
    
    /**
     * 
     * turns a given vector into a unit vector
     * 
     * @param v the vector to normalise
     * @return the normalised vector
     * @throws Exception vector must be of length 2
     */
        public static Vector staticNormalize(Vector v) throws Exception{
    
        Vector newV = new Vector(2);
        if(v.length() != 2){
        
            throw new Exception("vector must be of length 2");
            
        }else{
        
            double magnitude = Math.sqrt((Math.pow(v.valAt(0), 2) + Math.pow(v.valAt(1), 2)));
            
            for(int i = 0; i < newV.length(); i++){
            
                newV.set(i, v.valAt(i) / magnitude);
                
            }
            
            
        }
        
        return newV;
        
    }
        
        /**
         * 
         * fills every element of the vector with a random value between 1 and 100
         * 
         */
        public void randVal(){
        
            for(int i = 0; i < vector.length; i++){
            
                vector[i] = (int) (Math.random() * (100 - 1 + 1) + 1);
                
            }
            
        }

    /**
     *
     * returns how many elements the vector has
     *
     * @return the length of the vector
     */
    public int length() {

        return vector.length;

    }
    
    /**
     * returns this vector as an array
     * 
     * @return the array containing the values of the vector
     */
    public double[] returnAsArray(){
    
        return vector;
        
    }

    /**
     *
     * returns the value in the vector at a given element
     *
     * @param element the element you want the value of
     * @return the value of the given element
     */
    public double valAt(int element) {

        return vector[element];

    }

    /**
     *
     * sets the value at the given index equal to the given calue
     *
     * @param index the index in the vector to set
     * @param value the value to set
     */
    public void set(int index, double value) {

        vector[index] = value;

    }

}
