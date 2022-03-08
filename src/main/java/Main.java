import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8989;
        String pathName = "C:\\Users\\Валерий Королев\\IdeaProjects\\pcs-jd-diplom\\pcs-jd-diplom\\pdfs";

        System.out.println("Starting server at port " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        BooleanSearchEngine engine = new BooleanSearchEngine(new File(pathName));

        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                System.out.println("New connection accepted");
                String word = in.readLine();
                if (word.equals("0")) break;

                List<PageEntry> searchResult = engine.search(word);
                Collections.sort(searchResult);
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                out.println("{" + word + "} ->");
                for (PageEntry pe : searchResult) {
                    out.println(gson.toJson(pe));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}