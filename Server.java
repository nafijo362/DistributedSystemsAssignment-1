import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final Map<String, List<ErrorCode>> errorCodes = new HashMap<>();

    static {
        // Define error codes for each level with expected correct fixes
        errorCodes.put("beginner", Arrays.asList(
                new ErrorCode("int x = ;  // Syntax error: missing initializer", "int x = 0;"),
                new ErrorCode("String name = \"Alice;  // Syntax error: missing closing quote", "String name = \"Alice\";"),
                new ErrorCode("if (x = 5) { System.out.println(\"Equals\"); }  // Syntax error: assignment in condition", "if (x == 5) { System.out.println(\"Equals\"); }"),
                new ErrorCode("int[] arr = new int[5]; arr[5] = 10;  // Logical error: index out of bounds", "int[] arr = new int[5]; arr[4] = 10;"),
                new ErrorCode("System.out.println(\"Hello World\"  // Syntax error: missing parenthesis", "System.out.println(\"Hello World\");")
        ));

        errorCodes.put("intermediate", Arrays.asList(
                new ErrorCode("int[] nums = {1, 2, 3}; System.out.println(nums[3]);  // Logical error: index out of bounds", "int[] nums = {1, 2, 3}; System.out.println(nums[2]);"),
                new ErrorCode("for (int i=0; i<10; i--) { System.out.println(i); }  // Logical error: infinite loop", "for (int i=0; i<10; i++) { System.out.println(i); }"),
                new ErrorCode("int result = 100 / (10 - 10);  // Logical error: division by zero", "int result = 100 / (10 - 1);"),
                new ErrorCode("String[] words = {\"apple\", \"banana\"}; System.out.println(words[2]);  // Index out of bounds", "String[] words = {\"apple\", \"banana\", \"cherry\"}; System.out.println(words[2]);"),
                new ErrorCode("boolean flag = 0;  // Incompatible types", "boolean flag = false;")
        ));

        errorCodes.put("advanced", Arrays.asList(
                new ErrorCode("int result = 5 / 0;  // Division by zero", "int result = 5 / 1;"),
                new ErrorCode("if (x = 10) { System.out.println(\"Equal\"); }  // Assignment in condition", "if (x == 10) { System.out.println(\"Equal\"); }"),
                new ErrorCode("double d = 10.0 / 0.0; System.out.println(d);  // Logical error: infinity", "double d = 10.0 / 1.0; System.out.println(d);"),
                new ErrorCode("String text = null; System.out.println(text.toUpperCase());  // NullPointerException", "String text = \"\"; System.out.println(text.toUpperCase());"),
                new ErrorCode("double d = 3 / ;  // Syntax error: missing operand", "double d = 3 / 1;")
        ));
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345...");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                // Ask for user level
                output.println("Select your level (beginner/intermediate/advanced): ");
                String level = input.readLine().toLowerCase();

                if (!errorCodes.containsKey(level)) {
                    output.println("Invalid level selected. Please restart the game.");
                    return;
                }

                // Randomly select an error for the user to fix
                List<ErrorCode> errors = errorCodes.get(level);
                Random random = new Random();
                ErrorCode selectedError = errors.get(random.nextInt(errors.size()));

                output.println("Here is your error to fix:");
                output.println(selectedError.getErrorMessage()); // Display the error

                // Ask the user for their fixed code
                output.println("Fix this error and enter your code:");
                String userFix = input.readLine();

                // Validate the user's fix
                if (userFix.trim().equals(selectedError.getCorrectFix())) {
                    output.println("Correct! Your fix is valid.");
                } else {
                    output.println("Incorrect. Your fix did not solve the error.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ErrorCode {
        private final String errorMessage;
        private final String correctFix;

        public ErrorCode(String errorMessage, String correctFix) {
            this.errorMessage = errorMessage;
            this.correctFix = correctFix;
        }

        public String getErrorMessage() {
            return errorMessage.split(" //")[0]; // Return only the code snippet part
        }

        public String getCorrectFix() {
            return correctFix;
        }
    }
}
