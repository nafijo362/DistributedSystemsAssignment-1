import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner inputScanner = new Scanner(System.in);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Prompt the user for their level
            System.out.print("Enter your level (beginner/intermediate/advanced): ");
            String userLevel = inputScanner.nextLine().trim().toLowerCase(); // Convert to lowercase for consistency
            output.println(userLevel); // Send the level to the server

            String serverResponse;

            // Read the server's responses
            while ((serverResponse = input.readLine()) != null) {
                System.out.println(serverResponse);
                
                // Prompt for user input to fix the error
                if (serverResponse.contains("Fix this error and enter your code:")) {
                    System.out.print("Your code: ");
                    String userFix = inputScanner.nextLine();
                    output.println(userFix); // Send the user's fix to the server
                } 
                // Check for invalid level response
                else if (serverResponse.equals("Invalid level selected. Please restart the game.")) {
                    System.out.println("Exiting due to invalid input.");
                    break; // Exit the loop on invalid input
                }
                // Handle correct/incorrect feedback
                else if (serverResponse.equals("Correct! Your fix is valid.") || serverResponse.equals("Incorrect. Your fix did not solve the error.")) {
                    System.out.println(serverResponse);
                    break; // Exit the loop after receiving feedback
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
