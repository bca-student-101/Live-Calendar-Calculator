
package backend;

public class calculator {

    public static int calculate(int a, int b, String operation) {

        if (operation.equals("add")) {
            return a + b;

        } else if (operation.equals("subtract")) {
            return a - b;

        } else if (operation.equals("multiply")) {
            return a * b;

        } else if (operation.equals("divide")) {
            if (b == 0) {
                return 0;
            }
            return a / b;
        }
        return 0;
    }
}