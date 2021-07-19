package NeuralNetwork;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harry
 */
public class NeuralNetwork {

    private int numOfInputs; //number of inputs the network expects
    private int numOfOutputs; //the number of outputs the network will return

    private Matrix inputs; //the inputs from the user

    private double lr = 0.05; //the learning rate of the network

    private boolean returnProbabilities = true;

    Matrix[] weightMatrices; //the matrecies between layers
    Matrix[] biasMatrices; //the matrecies between each layer and the bias
    Matrix[] nodeMatrices; //the values of all the nodes in the network. The first matrix is the inputs and the last matrix is the outputs

    /**
     * takes an integer array relating to the number of nodes in each layer of
     * the network. the first element is the number of inputs, the last element
     * is the number of outputs, and the elements in between are for hidden
     * layers.
     *
     * @param nodes - the integer array containing how many nodes there are in
     * each layer
     */
    public NeuralNetwork(int[] nodes) {

        //makes all the matricies between layers
        this.numOfInputs = nodes[0];
        this.numOfOutputs = nodes[nodes.length - 1];
        weightMatrices = new Matrix[nodes.length - 1];
        Matrix matrix;

        //loops over the hidden layer matrix array and creates all the necessary hidden matricies
        for (int i = 0; i < weightMatrices.length; i++) {
            matrix = new Matrix(nodes[i + 1], nodes[i]);
            matrix.randVal();
            weightMatrices[i] = matrix;
        }

        //makes the matrices between the bias and the hidden layers
        biasMatrices = new Matrix[nodes.length - 1];
        //loops over the bias matrix array and creates all the necessary bias matricies
        for (int i = 0; i < biasMatrices.length; i++) {
            matrix = new Matrix(nodes[i + 1], 1);
            matrix.fill(0);
            biasMatrices[i] = matrix;
        }

        nodeMatrices = new Matrix[nodes.length];

    }

    /**
     *
     * passes inputs through the neural network
     *
     * @param inp the array of inputs to pass through the network
     * @return the output of the neural network
     */
    public double[] feedforward(double[] inp) throws Exception {
        //the array to output
        double[] output = new double[this.numOfOutputs];
        //makes sure the number of inputs is correct
        if (inp.length != this.numOfInputs) {
            throw new Exception("there should be " + this.numOfInputs + " input(s)");
        } else {
            //sets up the matrices for the inputs, hiddens and ouptuts
            double[][] a = {inp};
            inputs = new Matrix(a);

            //gets the values for the hidden nodes
            nodeMatrices[0] = inputs; //puts the inputs into the node matrix
            //loops over all the other hidden layers and works out their values
            for (int i = 1; i < nodeMatrices.length; i++) {

                nodeMatrices[i] = Matrix.staticMultiply(weightMatrices[i - 1], nodeMatrices[i - 1]);
                //adds the bias to the values
                nodeMatrices[i].add(biasMatrices[i - 1]);

                //applies the activation function to the nodes values
                nodeMatrices[i].map((double inp1) -> activation(inp1));

            }
            //loop over the last matrix (oututs) and put them into the output array
            for (int i = 0; i < output.length; i++) {
                output[i] = nodeMatrices[nodeMatrices.length - 1].valAt(i, 0);
            }
        }
        return output;
    }

    public void train(double[] inputs, double[] expectations) throws Exception {
        if (expectations.length != this.numOfOutputs) {
            throw new Exception("the network's number of outputs and the number of expectations do not match");
        } else {
            //array to store all the network errors
            Matrix[] networkErrors = new Matrix[nodeMatrices.length - 1];
            //turns the outputs of the network into a matrix
            double[] outputs = feedforward(inputs);
            double[][] a = {outputs};
            Matrix guesses = new Matrix(a);
            //turns the expecations into a matrix
            double[][] b = {expectations};
            Matrix correctAnswers = new Matrix(b);
            //calculates each of the errors in the networks output
            Matrix errors = Matrix.staticSubtract(correctAnswers, guesses);
            networkErrors[networkErrors.length - 1] = errors;
            //calculate the errors in each layer and corrects the weight matrices
            for (int i = weightMatrices.length - 1; i >= 0; i--) {
                if (i > 0) {
                    Matrix tw = Matrix.staticTranspose(weightMatrices[i]);
                    Matrix hiddenErrors = Matrix.staticMultiply(tw, networkErrors[i]);
                    networkErrors[i - 1] = hiddenErrors;
                }
                Matrix gradient = Matrix.staticMap(nodeMatrices[i + 1], (double inp) -> activationDiriv(inp));
                gradient.hadamardProduct(networkErrors[i]);
                gradient.multiply(lr);
                
                //calculate deltas
                Matrix transposedHiddens = Matrix.staticTranspose(nodeMatrices[i]);
                Matrix weightDeltas = Matrix.staticMultiply(gradient, transposedHiddens);
                weightMatrices[i].add(weightDeltas);
                biasMatrices[i].add(gradient);
                
            }
            
        }
    }

    /**
     * saves the neural network to a binary file with the given name
     *
     * @param name the name of the file you want to save to
     */
    public void saveNetwork(String name) {

        try {
            for (int i = 0; i < weightMatrices.length; i++) {
                String fileName = name + "weights_" + i;
                double[] weights = twoDArrayToOneDArray(weightMatrices[i].returnAs2DArray());
                byte[] data = convertDoubleArrayToByteArray(weights); //converts the colomns of the matrix to byte arrays
                saveData(fileName, data);
            }
            for (int i = 0; i < biasMatrices.length; i++) {
                String fileName = name + "bias_" + i;
                double[] bias = twoDArrayToOneDArray(biasMatrices[i].returnAs2DArray());
                byte[] data = convertDoubleArrayToByteArray(bias); //converts the colomns of the matrix to byte arrays
                saveData(fileName, data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * takes a file name and array of byte data and writes the array f bytes to a binary file with the file name inputted
     * @param name - name of the file to save the data in
     * @param data - the data t save to the file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void saveData(String name, byte[] data) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        BufferedOutputStream writer;

        fos = new FileOutputStream(new File(name));
        writer = new BufferedOutputStream(fos);
        writer.write(data, 0, data.length); //write the data to the file
        // flush remaining bytes
        writer.flush();
        // close the writer
        writer.close();

    }

    /**
     * loads the neural network weight values from files
     *
     * @param name - name of the neural network
     * @throws IOException
     */
    public void loadNetwork(String name) throws IOException {

        try {
            //loads the weights
            for (int i = 0; i < weightMatrices.length; i++) {
                String fileName = name + "weights_" + i;
                double[][] weights = weightMatrices[i].returnAs2DArray();
                weightMatrices[i] = new Matrix(loadData(fileName, weights));
            }
            //loads the biases
            for (int i = 0; i < biasMatrices.length; i++) {
                String fileName = name + "bias_" + i;
                double[][] bias = biasMatrices[i].returnAs2DArray();
                biasMatrices[i] = new Matrix(loadData(fileName, bias));
            }
        } catch (Exception e) {
            System.out.println(e);

        }

    }

    /**
     * loads data from a given file name into an array passed in by the user.
     *
     * @param name - name of file to load
     * @param weights - the array to load it into
     * @return - the array with the data in
     * @throws IOException
     */
    private double[][] loadData(String name, double[][] weights) throws IOException {
        FileInputStream fis = new FileInputStream(name);//creates a new input stream
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);
        byte[] buffer = new byte[weights[0].length * 8]; //makes a byte oarray the same size as one column in the matrix
        int line = 0;
        //loops over each amount of data in the file and puts it into columns in the 2D array
        while (dis.read(buffer) != -1) {
            double[] data = convertByteArrayToDoubleArray(buffer);
            weights[line] = data;
            line++;
        }

        return weights;
    }

    public void returnProbabilities(boolean n) {

        this.returnProbabilities = n;

    }

    /**
     *
     * the activation function used to squash any given value to a number
     * between negative one and the specific equation name is the sigmoid
     * function
     *
     * @param inp the value to squash
     * @return the result of the sigmoid function applied to inp
     */
    private double activation(double inp) {

        double output = 0;

        if (returnProbabilities == true) {

            //output = (1 / (1 + (Math.pow(Math.E, (-inp)))));
            output =  1.0 / (1.0 + Math.exp(-inp));

        } else {

            output = Math.max(0.0, inp);

        }

        return output;

    }

    /**
     *
     * takes a number that has been put through the activation function and
     * returns the original value
     *
     * @param a the number you want to revert
     * @return the original number before the activation was applied
     */
    private double activationDiriv(double a) {

        double output = 0;

        if (returnProbabilities == true) {
            output = a*(1 - a);

        } else {

            output = 0;
            if (a > 0) {
                output = 1;
            } else {
                output = 0;
            }

        }

        return output;

    }

    private byte[] convertDoubleArrayToByteArray(double[] data) {
        if (data == null) {
            return null;
        }
        // ----------
        byte[] byts = new byte[data.length * Double.BYTES];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(convertDoubleToByteArray(data[i]), 0, byts, i * Double.BYTES, Double.BYTES);
        }
        return byts;
    }

    private byte[] convertDoubleToByteArray(double number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(number);
        return byteBuffer.array();
    }

    public double[] convertByteArrayToDoubleArray(byte[] data) {
        if (data == null || data.length % Double.BYTES != 0) {
            return null;
        }
        // ----------
        double[] doubles = new double[data.length / Double.BYTES];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = (convertByteArrayToDouble(new byte[]{
                data[(i * Double.BYTES)],
                data[(i * Double.BYTES) + 1],
                data[(i * Double.BYTES) + 2],
                data[(i * Double.BYTES) + 3],
                data[(i * Double.BYTES) + 4],
                data[(i * Double.BYTES) + 5],
                data[(i * Double.BYTES) + 6],
                data[(i * Double.BYTES) + 7],}));
        }
        return doubles;
    }

    private double convertByteArrayToDouble(byte[] doubleBytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.put(doubleBytes);
        byteBuffer.flip();

        return byteBuffer.getDouble();
    }

    /**
     * takes two arrays and joins them together to make one long array
     * @param <T>
     * @param a - the first array
     * @param b - the second array
     * @return - a + b
     */
    private <T> T concatenate(T a, T b) {

        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        T output = (T) Array.newInstance(a.getClass().getComponentType(), Array.getLength(a) + Array.getLength(b));
        System.arraycopy(a, 0, output, 0, Array.getLength(a));
        System.arraycopy(b, 0, output, Array.getLength(a), Array.getLength(b));

        return output;
    }

    private <T> T twoDArrayToOneDArray(T[] inp) {

        if (!inp.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        Class type = Array.get(inp, 0).getClass().getComponentType();
        T output = (T) Array.newInstance(type, Array.getLength(inp) * Array.getLength(Array.get(inp, 0)));

        for (int i = 0; i < Array.getLength(inp); i++) {
            try {
                System.arraycopy(Array.get(inp, i), 0, output, i * Array.getLength(Array.get(inp, i)), Array.getLength(Array.get(inp, i)));
            } catch (ArrayStoreException e) {
            }

        }

        return output;
    }

}
