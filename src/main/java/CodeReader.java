import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class CodeReader {
    private ProductsData productsData;

    public CodeReader(ProductsData productsData) {
        this.productsData = productsData;
    }

    public Product getProductByCode(String code){
        return productsData.getProductByCode(code);
    }
}
