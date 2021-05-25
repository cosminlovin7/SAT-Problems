// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    private Integer numberOfFamilies;
    private Integer numberOfRelationships;
    private Integer task2NumberOfRelationships;
    private List<Relationship> relationshipList = new ArrayList<>();
    private List<Relationship> task2RelationshipList = new ArrayList<>();
    private List<Integer> familiesAdded = new ArrayList<>();
    private List<Integer> minimumArrests = new ArrayList<>();
    private String task2OracleDecision;
    private List<Integer> task2OracleResult = new ArrayList<>();


    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();
        Integer familyDimension = numberOfFamilies;

        reduceToTask2(familyDimension);
        task2Solver.solve();
        while(task2Solver.getOracleDecision().equalsIgnoreCase("FALSE")) {
            reduceToTask2(familyDimension);
            task2Solver.solve();
            familyDimension--;
        }

        task2OracleDecision = task2Solver.getOracleDecision();
        task2OracleResult = task2Solver.getExtendedFamily();

        extractAnswerFromTask2();
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

        generateNonEdges();
    }

    /**
     * The function generates all the non Edges that DOES NOT exist
     * between two vertices.
     */
    protected void generateNonEdges() {
        Relationship currentRelationship;
        for (int family1 = 1; family1 <= numberOfFamilies; family1++) {
            for (int family2 = family1 + 1; family2 <= numberOfFamilies; family2++) {
                currentRelationship = new Relationship(family1, family2);
                if (!relationshipList.contains(currentRelationship)) {
                    task2RelationshipList.add(currentRelationship);
                    familiesAdded.add(family1);
                    familiesAdded.add(family2);
                }
            }
        }

        task2NumberOfRelationships = task2RelationshipList.size();
    }

    /**
     * This function generates the input for the k-clique problem that
     * will be used to solve this problem.
     * @param familyDimension
     * @throws IOException
     */
    public void reduceToTask2(final Integer familyDimension) throws IOException{
        String defaultSpace = " ";
        File file = new File(task2InFilename);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(task2InFilename));

        fileWriter.write(String.valueOf(numberOfFamilies));
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(task2NumberOfRelationships));
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(familyDimension));
        fileWriter.write(10);

        for (Relationship relationship : task2RelationshipList) {
            fileWriter.write(String.valueOf(relationship.getFamily1()));
            fileWriter.write(defaultSpace);
            fileWriter.write(String.valueOf(relationship.getFamily2()));
            fileWriter.write(10);
        }

        fileWriter.flush();
    }

    public void extractAnswerFromTask2() {
        for (int family = 1; family <= numberOfFamilies; family++) {
            if (!task2OracleResult.contains(family)) {
                minimumArrests.add(family);
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

        for (int index = 0; index < minimumArrests.size(); index++) {
            fileWriter.write(String.valueOf(minimumArrests.get(index)));
            fileWriter.write(defaultSpace);
        }
        fileWriter.write(10);
        fileWriter.flush();
    }
}
