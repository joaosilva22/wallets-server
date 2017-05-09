package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class APIUtils {
    public static void sendResponse(HttpExchange httpExchange, int code, String response) throws IOException {
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static Map<String, String> getGETparams(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getQuery();
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    public static Map<String, String> getPOSTparams(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(query, type);
    }
}
