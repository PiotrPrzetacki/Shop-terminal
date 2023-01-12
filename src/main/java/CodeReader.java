import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

public class CodeReader {

    private static String scannerIp = "http://localhost:8080";

    public static Product checkCode(String scannedCode) throws RuntimeException{

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(scannerIp + "/product/" + scannedCode).get().build();
        try (Response response = client.newCall(request).execute()) {
            JSONObject productJSON = new JSONObject(
                    response
            );
            Product product = new Product(productJSON.getString("code"),productJSON.getString("name"),
                    BigDecimal.valueOf(Double.parseDouble(productJSON.getString("price"))));

            return product;

        }catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }

    }

}
