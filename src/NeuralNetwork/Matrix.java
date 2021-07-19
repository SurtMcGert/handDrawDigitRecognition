/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NeuralNetwork;

/**
 *
 * @author harry
 */
//an interface for the map function
interface Function {

    public double function(double a);

}

public class Matrix implements Function {

    private double[][] matrix;
    private int rows;
    private int columns;

    //constructors
    //----------------------------------------------------------------------------------
    /**
     *
     * creates matrix based off a given number of rows and columns
     *
     * @param rows the number of rows you want the matrix to have
     * @param columns the number of columns you want the matrix to have
     */
    public Matrix(int rows, int columns) {

        this.rows = rows;
        this.columns = columns;
        matrix = new double[columns][rows];

    }

    /**
     *
     * creates a clone of the given matrix
     *
     * @param m the matrix to clone
     */
    public Matrix(Matrix m) {

        this.rows = m.getRows();
        this.columns = m.getColumns();
        matrix = new double[columns][rows];

    }

    public Matrix(double[][] arr) {

        this.rows = arr[0].length;
        this.columns = arr.length;
        matrix = arr;

    }

//-------------------------------------------------------------------------------
    /**
     *
     * prints the matrix as a table to the console
     *
     */
    public void veiw() {

        for (int i = 0; i < rows; i++) {
            System.out.println("");
            for (int j = 0; j < columns; j++) {
                System.out.print("|" + matrix[j][i] + "|");

            }

        }

        System.out.println("");

    }

    /**
     * fills in matrix with random values between -1 and 1
     * including -1, excluding 1
     *
     *
     */
    public void randVal() {

        double min = -1;
        double max = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] = (Math.random() * (max - min)) + min;

            }

        }

    }

    /**
     *
     * fills the entire matrix with the given value
     *
     * @param val the value to fill the matrix with
     */
    public void fill(double val) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] = val;

            }

        }

    }

    /**
     *
     * returns the matrix as a 2D array
     *
     * @return the values of the matrix in a 2D array
     */
    public double[][] returnAs2DArray() {

        return matrix;

    }

    /**
     *
     * multiplies every value in the matrix by n
     *
     * @param n the value to multiply by
     */
    public void multiply(double n) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] *= n;

            }

        }

    }

    /**
     *
     * uses elementwise multiplication to multiply this matrix by a given matrix
     *
     * @param m the matrix to multiply by
     * @throws Exception the given matrix must have the same dimesions as this
     * matrix
     */
    public void hadamardProduct(Matrix m) throws Exception {

        if ((this.rows != m.getRows()) || (this.columns != m.columns)) {

            throw new Exception("the given matrix must have the same diensions as this matrix");

        } else {

            for (int i = 0; i < m.rows; i++) {
                for (int j = 0; j < m.columns; j++) {

                    matrix[j][i] *= m.valAt(i, j);

                }
            }
        }

    }

    /**
     *
     * multiplies this matrix by a given matrix it requires the given matrices
     * columns to be equal to this matrices rows and the given matrices rows is
     * equal to this matrices columns
     *
     *
     * @param m the matrix to multiply by
     */
    public void multiply(Matrix m) throws Exception {

        //makes sure there is the correct number of rows and columns
        if (columns != m.getRows()) {

            throw new Exception("the given matrices number of rows must be equal to this matrices number of columns");

        } else {

            //sets um the matrices
            Matrix newM = new Matrix(rows, m.getColumns()); //the final output
            Matrix a = this; //this matrix
            Matrix b = m; //the given matrix

            //loops over every cell in the new matrix
            for (int i = 0; i < newM.rows; i++) {
                for (int j = 0; j < newM.columns; j++) {

                    double[] row = new double[a.getColumns()]; //creates an array of length equal to the number of a's columns
                    double[] column = new double[b.getRows()]; //creates an array of length equal to the nymber of b's rows

                    //puts the row of a into an array
                    for (int r = 0; r < row.length; r++) {

                        row[r] = a.valAt(i, r);

                    }
                    //puts the column of b into an array
                    column = b.returnAs2DArray()[j];

                    //turns the row and column into vectors
                    Vector vRow = new Vector(row);
                    Vector vColumn = new Vector(column);

                    //sets the current element of the new matrix to the dot product of the row and column of a and b;
                    newM.set(i, j, Vector.dotProduct(vRow, vColumn));

                }

            }

            replace(newM);
        }
    }

    /**
     *
     * uses multiplication to multiply a given matrix and number together and
     * returns the new matrix
     *
     * @param m the matrix to multiply
     * @param n the value to multiply by
     * @return the result of m * n
     */
    public static Matrix staticMultiply(Matrix m, double n) {

        Matrix newM = new Matrix(m.getRows(), m.getColumns());
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                newM.set(i, j, m.valAt(i, j) * n);

            }

        }
        return newM;

    }

    /**
     * uses elementwise multiplication to multiply two given matrices together
     * @param m1 - the first matrix
     * @param m2 - the second matrix
     * @return - the result of multiplying the two matrices together
     * @throws Exception 
     */
    public Matrix staticHadamardProduct(Matrix m1, Matrix m2) throws Exception {

        Matrix newM = new Matrix(m1.getRows(), m2.getRows());

        if ((m1.getRows() != m2.getRows()) || (m1.getColumns() != m2.columns)) {

            throw new Exception("the given matrix must have the same diensions as this matrix");

        } else {

            for (int i = 0; i < newM.getRows(); i++) {
                for (int j = 0; j < newM.getColumns(); j++) {

                    newM.set(i, j, m1.valAt(i, j) * m2.valAt(i, j));

                }
            }
        }
        return newM;
    }

    /**
     *
     * multiplies two given matrices together and returns the new matrix
     * requires that both matrices are of equal dimensions
     *
     * @param m1 the first matrix to multiply by
     * @param m2 the second matrix to multiply by
     * @return the result of m1 * m2
     */
    public static Matrix staticMultiply(Matrix m1, Matrix m2) throws Exception {

        Matrix newM = new Matrix(m1.getRows(), m2.getColumns()); //the funal output
        Matrix a = m1; //the first given matrix
        Matrix b = m2; //the second given matrix

        //makes sure there is the correct number of rows and columns
        if (m1.getColumns() != m2.getRows()) {

            throw new Exception("the first matrices number of columns must be equal to the second matrices number of rows");

        } else {

            //loops over every cell in the new matrix
            for (int i = 0; i < newM.rows; i++) {
                for (int j = 0; j < newM.columns; j++) {

                    double[] row = new double[a.getColumns()]; //creates an array of length equal to the number of a's columns
                    double[] column = new double[b.getRows()]; //creates an array of length equal to the nymber of b's rows

                    //puts the row of a into an array
                    for (int r = 0; r < row.length; r++) {

                        row[r] = a.valAt(i, r);

                    }
                    //puts the column of b into an array
                    column = b.returnAs2DArray()[j];

                    //turns the row and column into vectors
                    Vector vRow = new Vector(row);
                    Vector vColumn = new Vector(column);

                    //sets the current element of the new matrix to the dotproduct of the row and column of a and b;
                    newM.set(i, j, Vector.dotProduct(vRow, vColumn));

                }

            }
        }

        return newM;
    }

    /**
     *
     * divides every value in the matrix by n
     *
     * @param n the value to divide by
     */
    public void divide(double n) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] /= n;

            }

        }

    }

    /**
     *
     * divides elementwise this matrix by a given matrix requires that the given
     * matrix is of the same dimensions as this matrix
     *
     * @param m the matrix to divide by
     */
    public void divide(Matrix m) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] /= m.valAt(i, j);

            }

        }

    }

    /**
     *
     * uses elementwise division to divide a given matrix and number together
     * and returns the new matrix
     *
     * @param m the matrix to divide
     * @param n the value to divide by
     * @return the result of m / n
     */
    public static Matrix staticDivide(Matrix m, double n) {

        Matrix newM = new Matrix(m.getRows(), m.getColumns());
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                newM.set(i, j, m.valAt(i, j) / n);

            }

        }

        return newM;

    }

    /**
     *
     * uses elementwise division to divide two matrices together and returns the
     * new matrix requires that both matrices are of equal dimensions
     *
     * @param m1 the first matrix to divide by
     * @param m2 the second matrix to divide by
     * @return the result of m1 / m2 using elementwise division
     */
    public static Matrix staticDivide(Matrix m1, Matrix m2) {

        Matrix newM = new Matrix(m1.getRows(), m1.getColumns());
        for (int i = 0; i < m1.getRows(); i++) {
            for (int j = 0; j < m1.getColumns(); j++) {
                newM.set(i, j, (m1.valAt(i, j) / m2.valAt(i, j)));

            }

        }

        return newM;

    }

    /**
     *
     * Adds n to every value in the matrix
     *
     * @param n the value to add on
     */
    public void add(double n) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] += n;

            }

        }

    }

    /**
     *
     * Adds elementwise every value of the matrix m to this matrix requires that
     * the given matrix is of equal dimensions to this matrix
     *
     * @param m the matrix to add on
     */
    public void add(Matrix m) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] += m.valAt(i, j);

            }

        }

    }

    /**
     *
     * adds a given value to every element in a given matrix and returns the
     * result as a new matrix
     *
     * @param m the matrix to add the value to
     * @param n the value to add to the matrix
     * @return the result of m + n
     */
    public static Matrix staticAdd(Matrix m, double n) {

        Matrix newM = new Matrix(m.getRows(), m.getColumns());

        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                newM.set(i, j, m.valAt(i, j) + n);

            }

        }

        return newM;

    }

    /**
     *
     * uses elementwise addition to add each element in a given matrix to its
     * corrisponding value in another given matrix and returns the result as a
     * new matrix requires that both matrices are of equal dimensions
     *
     * @param m1 the first matrix to add
     * @param m2 the second matrix to add
     * @return the result of m1 + m2
     */
    public static Matrix staticAdd(Matrix m1, Matrix m2) {

        Matrix newM = new Matrix(m1.getRows(), m1.getColumns());

        for (int i = 0; i < m1.getRows(); i++) {
            for (int j = 0; j < m1.getColumns(); j++) {
                newM.set(i, j, m1.valAt(i, j) + m2.valAt(i, j));

            }

        }

        return newM;

    }

    /**
     *
     * subtracts n from every value in the matrix
     *
     * @param n the value to subtract by
     */
    public void subtract(double n) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] -= n;

            }

        }

    }

    /**
     *
     * subtracts elementwise every value of the matrix m from this matrix
     * requires that the given matrix is of equal dimensions to this matrix
     *
     * @param m the matrix to subtract by
     */
    public void subtract(Matrix m) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[j][i] -= m.valAt(i, j);

            }

        }

    }

    /**
     *
     * subtracts a given value from every element in a given matrix and returns
     * the result as a new matrix
     *
     *
     * @param m the matrix to subtract from
     * @param n the value to subtract
     * @return the result of m - n
     */
    public static Matrix staticSubtract(Matrix m, double n) {

        Matrix newM = new Matrix(m.getRows(), m.getColumns());
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                newM.set(i, j, m.valAt(i, j) - n);

            }

        }

        return newM;

    }

    /**
     *
     * uses elementwise subtraction to subtract a given matrix from another
     * given matrix and returns the result requires that both matrices are of
     * equal dimensions
     *
     * @param m1 the matrix to subtract from
     * @param m2 the matrix to subtract
     * @return the result of m1 - m2
     */
    public static Matrix staticSubtract(Matrix m1, Matrix m2) {

        Matrix newM = new Matrix(m1.getRows(), m1.getColumns());
        for (int i = 0; i < m1.getRows(); i++) {
            for (int j = 0; j < m1.getColumns(); j++) {
                newM.set(i, j, m1.valAt(i, j) - m2.valAt(i, j));

            }

        }

        return newM;

    }

    /**
     *
     * transposes the matrix
     *
     */
    public void transpose() {

        Matrix newM = new Matrix(this.getColumns(), this.getRows());

        for (int i = 0; i < newM.getRows(); i++) {
            for (int j = 0; j < newM.getColumns(); j++) {

                newM.set(i, j, this.valAt(j, i));

            }

        }

        matrix = newM.returnAs2DArray();
        rows = newM.getRows();
        columns = newM.getColumns();

    }

    /**
     *
     * transposes a given matrix and returns it as a new matrix
     *
     * @param m the matrix to transpose
     * @return the transposition of the given matrix
     */
    public static Matrix staticTranspose(Matrix m) {

        Matrix newM = new Matrix(m.getColumns(), m.getRows());

        for (int i = 0; i < newM.getRows(); i++) {
            for (int j = 0; j < newM.getColumns(); j++) {

                newM.set(i, j, m.valAt(j, i));

            }

        }

        return newM;

    }

    /**
     *
     * applies the given function to every element in the matrix
     *
     * @param f the function to run over the matrix
     */
    public void map(Function f) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                matrix[j][i] = f.function(matrix[j][i]);

            }
        }

    }

    /**
     *
     * applies a given function to every element in a given matrix and returns
     * the result as a new matrix
     *
     * @param m the matrix to apply the function to
     * @param f the function to run over the matrix
     * @return the result of f(m);
     */
    public static Matrix staticMap(Matrix m, Function f) {

        Matrix newM = new Matrix(m.getRows(), m.getColumns());

        for (int i = 0; i < newM.getRows(); i++) {
            for (int j = 0; j < newM.getColumns(); j++) {

                newM.set(i, j, f.function(m.valAt(i, j)));

            }
        }

        return newM;
    }

    /**
     *
     * sets a specific element in the matrix to a specified value
     *
     * @param row the row to add the value to
     * @param column the column to add the value to
     * @param val the value to add to the matrix
     */
    public void set(int row, int column, double val) {

        matrix[column][row] = val;

    }

    /**
     *
     * returns the value at the given (row, column) coordinate
     *
     * @param row the row the element to return is on
     * @param column the column the element is on
     * @return the value at the specified row and column
     */
    public double valAt(int row, int column) {

        return matrix[column][row];

    }

    public void replace(Matrix m) {

        matrix = m.returnAs2DArray();
        this.rows = matrix[0].length;
        this.columns = matrix.length;

    }

    /**
     * returns the number of rows the matrix has
     *
     * @return the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * returns the number of columns the matrix has
     *
     * @return the number of columns
     */
    public int getColumns() {
        return columns;
    }

    @Override
    public double function(double a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
