package flashcards;


import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.lang.System.console;
import static java.lang.System.exit;

public class Main {
    static Integer numOfCardsInFile;
    static File flashCardsFileForImport;
    static File flashCardsFileForExport;
    static File logFile;
    static StringBuilder log = new StringBuilder();
    static Map<String, String> flashCards = new LinkedHashMap<>();
    static Map<String, Integer> termAndNumOfWAList = new HashMap<>();
    
    static void systemOutPrintF(String format, Object ... args) {
        System.out.printf(format, args);
        log.append(String.format(format, args));
    }

    static void systemOutPrintLn() {
        System.out.println();
        log.append("\n");
    }

    static void systemOutPrintLn(String arg) {
        System.out.println(arg);
        log.append(arg).append("\n");
    }

    static String scannerNextLine() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        log.append(userInput).append("\n");
        return userInput;
    }

    static int scannerNextInt() {
        Scanner scanner = new Scanner(System.in);
        int userInput = scanner.nextInt();
        log.append(userInput).append("\n");
        return userInput;
    }

    static String getTermByDefinition(String definition) {
        String term = "default";
        for (var entry :
                flashCards.entrySet()) {
            if (entry.getValue().equals(definition)) {
                term = entry.getKey();
                break;
            }
        }
        return term;
    }

    static void add() {
        systemOutPrintLn("The card:");
        String term = scannerNextLine();
        if (flashCards.containsKey(term)) {
            systemOutPrintF("The card \"%s\" already exists.\n", term);
            return;
        }

        systemOutPrintLn("The definition of the card:");
        String definition = scannerNextLine();
        if (flashCards.containsValue(definition)) {
            systemOutPrintF("The definition \"%s\" already exists.\n", definition);
            return;
        }

        flashCards.put(term, definition);
        termAndNumOfWAList.put(term, 0);
        systemOutPrintF("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
    }

    static void remove() {
        systemOutPrintLn("Which card?");
        String term = scannerNextLine();
        if (flashCards.containsKey(term)) {
            flashCards.remove(term);
            termAndNumOfWAList.remove(term);
            systemOutPrintLn("The card has been removed.");
        } else {
            systemOutPrintF("Can't remove \"%s\": there is no such card.\n", term);
        }
    }

    static void importFromFile(boolean fileIsExist) {
        if (!fileIsExist) {
            systemOutPrintLn("File name:");
            flashCardsFileForImport = new File(scannerNextLine());
        }

        try (Scanner scannerFile = new Scanner(flashCardsFileForImport)) {
            int numOfCards = 0;
            while (scannerFile.hasNextLine()) {
                String term = scannerFile.nextLine();
                String definition = scannerFile.nextLine();
                int numOfWrongAnswers = Integer.parseInt(scannerFile.nextLine());
                flashCards.put(term, definition);
                termAndNumOfWAList.put(term, numOfWrongAnswers);
                numOfCards++;
            }
            systemOutPrintF("%d cards have been loaded.\n", numOfCards);
        } catch (FileNotFoundException e) {
            systemOutPrintLn("File not found.");
        }
    }

    static void exportToFile(boolean fileIsExist) {
        if (!fileIsExist) {
            systemOutPrintLn("File name:");
            flashCardsFileForExport = new File(scannerNextLine());
        }

        try {
            boolean createdNew = flashCardsFileForExport.createNewFile();
            try (PrintWriter printWriter = new PrintWriter(flashCardsFileForExport)) {
                for (var entry :
                        flashCards.entrySet()) {
                    printWriter.println(entry.getKey() + "\n" + entry.getValue());
                    printWriter.println(termAndNumOfWAList.get(entry.getKey()));
                }
                systemOutPrintF("%d cards have been saved.\n", flashCards.size());
            } catch (IOException e) {
                systemOutPrintF("An exception occurred %s", e.getMessage());
            }
        } catch (IOException e) {
            systemOutPrintLn("Cannot create the file: " + flashCardsFileForExport.getPath());
        }
    }

    static void ask() {
        systemOutPrintLn("How many times to ask?");
        int numberOfQuestions = scannerNextInt();
        for (int i = 0; i < numberOfQuestions; i++) {
            for (var entry :
                    flashCards.entrySet()) {
                systemOutPrintF("Print the definition of \"%s\":\n", entry.getKey());
                String definition = scannerNextLine();
                if (entry.getValue().equals(definition)) {
                    systemOutPrintLn("Correct!");
                } else if (flashCards.containsValue(definition)) {
                    termAndNumOfWAList.put(entry.getKey(), termAndNumOfWAList.get(entry.getKey()) + 1);
                    systemOutPrintF("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".\n",
                            entry.getValue(), getTermByDefinition(definition));
                } else {
                    termAndNumOfWAList.put(entry.getKey(), termAndNumOfWAList.get(entry.getKey()) + 1);
                    systemOutPrintF("Wrong. The right answer is \"%s\".\n", entry.getValue());
                }
                i++;
                if (i == numberOfQuestions) {
                    break;
                }
            }
        }
    }

    static void log() {
        systemOutPrintLn("File name:");
        logFile = new File(scannerNextLine());
        try {
            boolean createdNew = logFile.createNewFile();
            try (PrintWriter printWriter = new PrintWriter(logFile)) {
                printWriter.print(log);
                systemOutPrintLn("The log has been saved.");
            } catch (IOException e) {
                systemOutPrintF("An exception occurred %s", e.getMessage());
            }
        } catch (IOException e) {
            systemOutPrintLn("Cannot create the file: " + logFile.getPath());
        }
    }

    static void hardestCard() {
        Map<String, Integer> hardestCards = new HashMap<>();
        int max = 0;

        for (var entry :
                termAndNumOfWAList.entrySet()) {
            if (entry.getValue() > max) {
                hardestCards.clear();
                hardestCards.put(entry.getKey(), entry.getValue());
                max = entry.getValue();
            } else if (entry.getValue() == max && max != 0) {
                hardestCards.put(entry.getKey(), entry.getValue());
            }
        }

        switch (hardestCards.size()) {
            case 0:
                systemOutPrintLn("There are no cards with errors.");
                break;
            case 1:
                for (var entry :
                        hardestCards.entrySet()) {
                    systemOutPrintF("The hardest card is \"%s\". You have %d errors answering it.\n", entry.getKey(), max);
                }
                break;
            default:
                StringBuilder terms = new StringBuilder();
                for (var entry :
                        hardestCards.entrySet()) {
                    terms.append(String.format(", \"%s\"", entry.getKey()));
                }
                terms.delete(0, 2);
                systemOutPrintF("The hardest cards are %s. You have %d errors answering them.\n", terms, max);
                break;
        }
    }

    static void resetStats() {
        termAndNumOfWAList.replaceAll((k, v) -> 0);
        systemOutPrintLn("Card statistics have been reset.");
    }

    static void action() {
        while (true) {
            systemOutPrintLn("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            switch (scannerNextLine()) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    importFromFile(false);
                    break;
                case "export":
                    exportToFile(false);
                    break;
                case "ask":
                    ask();
                    break;
                case "exit":
                    systemOutPrintLn("Bye bye!");
                    if (flashCardsFileForExport != null) {
                        exportToFile(true);
                    }
                    exit(0);
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    break;
            }
            systemOutPrintLn();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import")) {
                flashCardsFileForImport = new File(args[i + 1]);
            } else {
                flashCardsFileForExport = new File(args[i + 1]);
            }
        }
        if (flashCardsFileForImport != null) {
            importFromFile(true);
        }
        action();
    }
}
