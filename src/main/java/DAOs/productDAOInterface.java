package DAOs;

import DTOs.productDTO;
import java.util.List;

public interface productDAOInterface {
    List<productDTO> getAllProducts();
    productDTO getProductById(int var1);
    boolean deleteProductById(int var1);
    productDTO insertProduct(productDTO var1);
    boolean updateProduct(int var1, productDTO var2);
    List<productDTO> findProductsByKeyword(String var1);

    // New JSON methods
    String getProductJsonById(int id);
    String getAllProductsJson();
    String getProductsJsonByKeyword(String keyword);
}