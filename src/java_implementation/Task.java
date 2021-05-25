// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the abstract base class for all tasks that have to be implemented.
 */
public abstract class Task {
    String inFilename;
    String oracleInFilename;
    String oracleOutFilename;
    String outFilename;

    public abstract void solve() throws IOException, InterruptedException;

    public abstract void readProblemData() throws IOException;

    public void formulateOracleQuestion() throws IOException {}

    public void decipherOracleAnswer() throws IOException {}

    public abstract void writeAnswer() throws IOException;

    /**
     * Stores the files paths as class attributes.
     *
     * @param inFilename         the file containing the problem input
     * @param oracleInFilename   the file containing the oracle input
     * @param oracleOutFilename  the file containing the oracle output
     * @param outFilename        the file containing the problem output
     */
    public void addFiles(String inFilename, String oracleInFilename, String oracleOutFilename, String outFilename) {
        this.inFilename = inFilename;
        this.oracleInFilename = oracleInFilename;
        this.oracleOutFilename = oracleOutFilename;
        this.outFilename = outFilename;
    }

    /**
     * Asks the oracle for an answer to the formulated question.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void askOracle() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command("python3", "sat_oracle.py", oracleInFilename, oracleOutFilename);
        Process process = builder.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String buffer;
        StringBuilder output = new StringBuilder();

        while ((buffer = in.readLine()) != null) {
            output.append(buffer).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Error encountered while running oracle");
            System.err.println(output.toString());
            System.exit(-1);
        }
    }

    /**
     * Transforms a line read from the inputFile into a list of integers.
     *
     * @param contentLine
     * @return
     */
    public List<Integer> getValues(String contentLine) {
        List<Integer> values = new ArrayList<>();
        Integer auxiliaryInt = 0;
        boolean isNegative = false;
        for(int index = 0; index < contentLine.length(); index++) {
            char number = contentLine.charAt(index);
            if (number != 32 && number != 45) {
                auxiliaryInt = auxiliaryInt * 10 + Integer.parseInt(String.valueOf(number));
            } else if (number == 45) {
                isNegative = true;
            }
            if (index == contentLine.length() - 1 || number == 32){
                if (isNegative) {
                    auxiliaryInt *= -1;
                }
                values.add(auxiliaryInt);
                auxiliaryInt = 0;
                isNegative = false;
            }
        }

        return values;
    }
}
