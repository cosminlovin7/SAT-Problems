// Copyright 2020
// Author: Matei Simtinică
// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    private Integer numberOfFamilies;
    private Integer numberOfRelationships;
    private Integer familyDimension;
    private List<Relationship> relationshipList = new ArrayList<>();
    private List<Relationship> nonRelationshipList = new ArrayList<>();
    private Integer numberOfVariables;
    private Integer numberOfClauses;
    private String oracleDecision;
    private List<Integer> oracleResult = new ArrayList<>();
    private List<Integer> extendedFamily = new ArrayList<>();

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
        this.familyDimension = values.get(2);
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
     * Function generates all Edges that DOES NOT exist
     * between two vertices read from the input.
     */
    protected void generateNonEdges() {
        Relationship currentRelationship;
        for (int family1 = 1; family1 <= numberOfFamilies; family1++) {
            for (int family2 = family1 + 1; family2 <= numberOfFamilies; family2++) {
                currentRelationship = new Relationship(family1, family2);
                if (!relationshipList.contains(currentRelationship)) {
                    nonRelationshipList.add(currentRelationship);
                }
            }
        }
    }

    public void generateClauses() throws IOException {
        String defaultString = "p cnf";
        String defaultSpace = " ";
        File file = new File(oracleInFilename);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(oracleInFilename));

        fileWriter.write(defaultString);
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(numberOfVariables));
        fileWriter.write(defaultSpace);
        fileWriter.write(String.valueOf(numberOfClauses));
        fileWriter.write(defaultSpace);
        fileWriter.write(10);

        /**
         * Type A Clauses
         */
        for (int index = 1; index <= familyDimension; index++) {
            for (int var = index; var <= numberOfVariables; var+=familyDimension) {
                fileWriter.write(String.valueOf(var));
                fileWriter.write(defaultSpace);
            }
            fileWriter.write(48);
            fileWriter.write(10);
        }

        /**
         * Type B Clauses
         */
        for (Relationship relationship : nonRelationshipList) {
            for (int index1 = 1; index1 <= familyDimension; index1++) {
                for (int index2 = 1; index2 <= familyDimension; index2++) {
                    if (index1 != index2) {
                        Integer var1 = index1 + (relationship.getFamily1() - 1) * familyDimension;
                        Integer var2 = index2 + (relationship.getFamily2() - 1) * familyDimension;

                        fileWriter.write(String.valueOf(var1 * -1));
                        fileWriter.write(defaultSpace);
                        fileWriter.write(String.valueOf(var2 * -1));
                        fileWriter.write(defaultSpace);
                        fileWriter.write(48);
                        fileWriter.write(10);
                    }
                }
            }
        }

        /**
         * Type C Clauses
         */
        for (int family = 0; family < numberOfFamilies; family++) {
            for (int index1 = 1; index1 <= familyDimension; index1 ++) {
                for (int index2 = index1 + 1; index2 <= familyDimension; index2++) {
                    Integer var1 = index1 + family * familyDimension;
                    Integer var2 = index2 + family * familyDimension;

                    fileWriter.write(String.valueOf(var1 * -1));
                    fileWriter.write(defaultSpace);
                    fileWriter.write(String.valueOf(var2 * -1));
                    fileWriter.write(defaultSpace);
                    fileWriter.write(48);
                    fileWriter.write(10);
                }
            }
        }
        fileWriter.flush();
    }

    /**
     * Giving the fact that formula for combination is n! / (n-k)! * k!
     * We always want combinations of n taken as 2.
     * So we can write it as: (n-1) * n / 2
     * @param numberOfFamilies
     */
    protected Integer calculateCombinations(final Integer numberOfFamilies) {
        if (numberOfFamilies == 0 || numberOfFamilies == 1) {
            return 1;
        } else {
            return ((numberOfFamilies - 1) * numberOfFamilies) / 2;
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        numberOfVariables = numberOfFamilies * familyDimension;
        Integer typeAClauses = familyDimension;
        Integer typeBClauses = familyDimension *
                (familyDimension - 1) * nonRelationshipList.size();
        System.out.println( nonRelationshipList.size());
        Integer typeCClauses = numberOfFamilies * calculateCombinations(familyDimension) +
                familyDimension * calculateCombinations(numberOfFamilies);
        numberOfClauses = typeAClauses + typeBClauses + typeCClauses;
        System.out.println(numberOfClauses);
        generateClauses();

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(oracleOutFilename));
        oracleDecision = fileReader.readLine();
        if (oracleDecision.equalsIgnoreCase("FALSE")) {
            return;
        }
        String currentLine = fileReader.readLine(); // just skip
        currentLine = fileReader.readLine();
        oracleResult = getValues(currentLine);

        for (int index = 0; index < oracleResult.size(); index++) {
            Integer result = oracleResult.get(index);
            if (result > 0) {
                if (result % familyDimension == 0) {
                    extendedFamily.add(oracleResult.get(index) / familyDimension);
                } else {
                    extendedFamily.add(oracleResult.get(index) / familyDimension + 1);
                }
            }
        }

        System.out.println(extendedFamily);
    }

    @Override
    public void writeAnswer() throws IOException {
        String defaultSpace = " ";
        File file = new File(outFilename);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outFilename));

        System.out.println(oracleDecision);
        fileWriter.write(oracleDecision);
        fileWriter.write(10);

        for (int index = 0; index < extendedFamily.size(); index++) {
            fileWriter.write(String.valueOf(extendedFamily.get(index)));
            fileWriter.write(defaultSpace);
        }
        fileWriter.write(10);
        fileWriter.flush();
    }

    public String getOracleDecision() {
        return oracleDecision;
    }

    public List<Integer> getExtendedFamily() {
        return extendedFamily;
    }
}
