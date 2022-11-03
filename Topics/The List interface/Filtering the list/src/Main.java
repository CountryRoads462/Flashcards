import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> list = new ArrayList<>();

        while (scanner.hasNextInt()) {
            int elem = scanner.nextInt();
            list.add(elem);
        }

        List<Integer> reverseList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (i % 2 != 0) {
                reverseList.add(0, list.get(i));
            }
        }

        for (Integer elem :
                reverseList) {
            System.out.print(elem + " ");
        }
    }
}