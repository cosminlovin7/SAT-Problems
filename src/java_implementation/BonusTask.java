// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    private Integer numberOfFamilies;
    private Integer numberOfRelationships;
    private Integer numberOfClauses;
    private Integer hard = 0;
    private Integer soft = 1;
    private List<Integer> oracleResult = new ArrayList<>();
    private List<Relationship> relationshipList = new ArrayList<>();
    private List<Integer> minimumVertex = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(inFilename));
        String contentLine = fileReader.readLine();
        List<Integer> values = getValues(contentLine);
        this.numberOfFamilies = values.get(0);
        this.numberOfRelationships = values.get(1);
        contentLine = fileReader.readLine();
        while(contentLine != null) {
            values.clear();
            values = getValues(contentLine);
            relationshipList.add(new Relationship(values.get(0), values.get(1)));
            contentLine = fileReader.readLine();
        }
        numberOfClauses = relationshipList.size() + numberOfFamilies;
        hard = numberOfFamilies + 1;
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        String defaultString = "p wcnf";
        String defaultSpace = " ";
        File file = new File(oracleInFilename);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(oracleInFilename));

        fileWriter.write(defaultString);
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(numberOfFamilies));
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(numberOfClauses));
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(hard));
        fileWriter.write(10);

        //HardClauses
        for (Relationship relationship : relationshipList) {
            fileWriter.write(String.valueOf(hard));
            fileWriter.write(defaultSpace);
            fileWriter.write(String.valueOf(relationship.getFamily1()));
            fileWriter.write(defaultSpace);
            fileWriter.write(String.valueOf(relationship.getFamily2()));
            fileWriter.write(defaultSpace);
            fileWriter.write(48);
            fileWriter.write(10);
        }

        //SoftClauses
        for (int value = 1; value <= numberOfFamilies; value++) {
            fileWriter.write(String.valueOf(soft));
            fileWriter.write(defaultSpace);
            fileWriter.write(String.valueOf(value * (-1)));
            fileWriter.write(defaultSpace);
            fileWriter.write(48);
            fileWriter.write(10);
        }
        fileWriter.flush();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(oracleOutFilename));
        String contentLine = fileReader.readLine(); // just skip
        contentLine = fileReader.readLine();
        List<Integer> values = getValues(contentLine);
        for (Integer value : values) {
            if (value > 0) {
                minimumVertex.add(value);
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        String defaultSpace = " ";
        File file = new File(outFilename);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outFilename));

        for (Integer vertex : minimumVertex) {
            fileWriter.write(String.valueOf(vertex));
            fileWriter.write(defaultSpace);
        }
        fileWriter.write(10);
        fileWriter.flush();
    }
}
