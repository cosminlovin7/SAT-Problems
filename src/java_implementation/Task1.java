// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    private Integer numberOfFamilies;
    private Integer numberOfRelationships;
    private Integer availableSpies;
    private Integer numberOfVariables;
    private Integer numberOfClauses;
    private List<Relationship> relationshipList = new ArrayList<>();
    private List<Result> resultsList = new ArrayList<>();
    private String oracleDecision;
    private List<Integer> oracleResult = new ArrayList<>();

    /**
     * Class is used to save the spy that every family has.
     */
    public class Result{
        private Integer family;
        private Integer assignedSpy;

        public Result(final Integer family,
                      final Integer assignedSpy) {
            this.family = family;
            this.assignedSpy = assignedSpy;
        }

        public Integer getFamily() {
            return family;
        }

        public Integer getAssignedSpy() {
            return assignedSpy;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "family=" + family +
                    ", assignedSpy=" + assignedSpy +
                    '}';
        }
    }

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
        this.availableSpies = values.get(2);
        contentLine = fileReader.readLine();
        while(contentLine != null) {
            values.clear();
            values = getValues(contentLine);
            relationshipList.add(new Relationship(values.get(0), values.get(1)));
            contentLine = fileReader.readLine();
        }
    }

    /**
     * The function calculates the factorial of a number and it is used
     * to calculate Combinations.
     * @param number
     * @return
     */
    protected Integer calculateFactorial(final Integer number) {
        if (number == 0) {
            return 1;
        }

        Integer result = 1;
        for (int i = 1; i <= number; i++) {
            result *= i;
        }

        return result;
    }

    /**
     * The function generates all clauses of type 1, 2 and 3.
     * @param type3Clauses
     * @throws IOException
     */
    protected void generateClauses(final Integer type3Clauses) throws IOException{
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
        fileWriter.write(10);

        /**
         * Type1 Clauses
         */
        for (Relationship relationship : relationshipList) {
            Integer family1 = relationship.getFamily1();
            Integer family2 = relationship.getFamily2();

            for (int counter = 1; counter <= availableSpies; counter++) {
                fileWriter.write(String.valueOf(((family1 - 1) * availableSpies + counter) * (-1)));
                fileWriter.write(defaultSpace);
                fileWriter.write(String.valueOf(((family2 - 1) * availableSpies + counter) * (-1)));
                fileWriter.write(defaultSpace);
                fileWriter.write(48);
                fileWriter.write(10);
            }
        }

        /**
         * Type2 Clauses
         */
        for (int index = 1; index <= numberOfVariables; index++) {
            if (index == numberOfVariables) {
                fileWriter.write(String.valueOf(index));
                fileWriter.write(defaultSpace);
                fileWriter.write(48);
                fileWriter.write(10);
                break;
            }
            if ((index - 1) % availableSpies == 0 && index != 1) {
                fileWriter.write(48);
                fileWriter.write(10);
            }
            fileWriter.write(String.valueOf(index));
            fileWriter.write(defaultSpace);
        }

        /**
         * Type 3 Clauses
         */
        for (int family = 0; family < numberOfFamilies; family++) {
            for (int var1 = 1; var1 <= availableSpies; var1++) {
                for (int var2 = var1 + 1; var2 <= availableSpies; var2++) {
                    fileWriter.write(String.valueOf((family * availableSpies + var1) * -1));
                    fileWriter.write(defaultSpace);
                    fileWriter.write(String.valueOf((family * availableSpies + var2) * -1));
                    fileWriter.write(defaultSpace);
                    fileWriter.write(48);
                    fileWriter.write(10);
                }
            }
        }

        fileWriter.flush();
    }

    /**
     * There are numberOfFamilies * availableSpies variables.
     * There are:
     * numberOfRelationships * availableSpies +
     * numberOfFamilies +
     * numberOfFamilies * C(availableSpies,2) clauses.
     * @throws IOException
     */
    @Override
    public void formulateOracleQuestion() throws IOException {
        numberOfVariables = numberOfFamilies * availableSpies;
        Integer type1Clauses = numberOfRelationships * availableSpies;
        Integer type2Clauses = numberOfFamilies;
        Integer type3Clauses = 0;
        if (availableSpies > 1) {
            type3Clauses = numberOfFamilies *
                    (calculateFactorial(availableSpies) / (2 * calculateFactorial(availableSpies - 2)));
        }
        numberOfClauses = type1Clauses + type2Clauses + type3Clauses;

        generateClauses(type3Clauses);
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

        Integer index;
        for (int family = 0; family < numberOfFamilies; family++) {
            for (int var = 0; var < availableSpies; var++) {
                index = family * availableSpies + var;
                if (oracleResult.get(index) > 0) {
                    resultsList.add(new Result(family + 1, var + 1));
                    break;
                }
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

        System.out.println(oracleDecision);
        fileWriter.write(oracleDecision);
        fileWriter.write(10);

        for (int result = 0; result < resultsList.size(); result++) {
            fileWriter.write(String.valueOf(resultsList.get(result).getAssignedSpy()));
            fileWriter.write(defaultSpace);
        }
        fileWriter.write(10);
        fileWriter.flush();
    }
}
