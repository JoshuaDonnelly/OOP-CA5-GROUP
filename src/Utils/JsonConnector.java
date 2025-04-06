package Utils;

import DTOs.productDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class JsonConnector {
    public static String productToJson(productDTO product) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", product.getId());
        jsonObject.put("name", product.getName());
        jsonObject.put("price", product.getPrice());
        jsonObject.put("description", product.getDescription());
        jsonObject.put("categoryId", product.getCategoryId());
        jsonObject.put("stock", product.getStock());

        return jsonObject.toString(4); // 4 spaces for pretty-print
    }

    public static String productListToJson(List<productDTO> products) {
        JSONArray jsonArray = new JSONArray();

        for (productDTO product : products) {
            JSONObject productJson = new JSONObject();
            productJson.put("id", product.getId());
            productJson.put("name", product.getName());
            productJson.put("price", product.getPrice());
            productJson.put("description", product.getDescription());
            productJson.put("categoryId", product.getCategoryId());
            productJson.put("stock", product.getStock());
            jsonArray.put(productJson);
        }

        return jsonArray.toString(4); // 4 spaces for pretty-print
    }
}
