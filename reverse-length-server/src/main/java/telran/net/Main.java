package telran.net;

import java.net.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONException;

public class Main {
    private static final int PORT = 4000;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            runSession(socket);
        }
    }

    private static void runSession(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream())) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                JSONObject jsonInput;
                try {
                    jsonInput = new JSONObject(line);
                } catch (JSONException e) {
                    writer.println("Wrong JSON format.");
                    continue;
                }

                String word = jsonInput.optString("word", null);
                String operation = jsonInput.optString("operation", null);

                if (word == null || operation == null) {
                    writer.println("JSON must contain 'word' and 'operation' fields.");
                    continue;
                }

                String answer;
                if ("length".equals(operation)) {
                    answer = String.valueOf(word.length());
                } else if ("reverse".equals(operation)) {
                    answer = new StringBuilder(word).reverse().toString();
                } else {
                    answer = "Wrong operation: " + operation;
                }
                writer.printf("Echo server on %s , port: %d sends back %s\n", socket.getLocalAddress().getHostAddress(),
                        socket.getLocalPort(), answer);
            }
        } catch (Exception e) {
            System.out.println("client closed connection abnormally");
        }
    }
}