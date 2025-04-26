package org.example.oopca5jfx.Utils;

import org.example.oopca5jfx.DTOs.productDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
        jsonObject.put("image_filename", product.getImageFilename());

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
            productJson.put("image_filename", product.getImageFilename());
            jsonArray.put(productJson);
        }

        return jsonArray.toString(4); // 4 spaces for pretty-print
    }

    public static productDTO jsonToProduct(String json) {
        JSONObject jsonObject = new JSONObject(json);
        productDTO product = new productDTO();
        product.setId(jsonObject.getInt("id"));
        product.setName(jsonObject.getString("name"));
        product.setPrice((float) jsonObject.getDouble("price"));
        product.setDescription(jsonObject.getString("description"));
        product.setCategoryId(jsonObject.getInt("categoryId"));
        product.setStock(jsonObject.getInt("stock"));
        product.setImageFilename(jsonObject.optString("image_filename", null)); // Handle missing image_filename
        return product;
    }

    public static List<productDTO> jsonToProductList(String json) {
        List<productDTO> products = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                productDTO product = new productDTO();
                product.setId(jsonObject.getInt("id"));
                product.setName(jsonObject.getString("name"));
                product.setPrice((float) jsonObject.getDouble("price"));
                product.setDescription(jsonObject.getString("description"));
                product.setCategoryId(jsonObject.getInt("categoryId"));
                product.setStock(jsonObject.getInt("stock"));
                product.setImageFilename(jsonObject.optString("image_filename", null));
                products.add(product);
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON array: " + e.getMessage());
        }
        return products;
    }
}