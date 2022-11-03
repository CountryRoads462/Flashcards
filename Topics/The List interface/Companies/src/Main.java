import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        List<String> stringList = new ArrayList<>(Arrays.asList(userInput.split(" ")));
        System.out.println(stringList);
    }
}