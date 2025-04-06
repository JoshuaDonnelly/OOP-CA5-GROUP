package DAOs;

import DTOs.productDTO;
import java.util.List;

public interface productDAOInterface {
    List<productDTO> getAllProducts();
    productDTO getProductById(int id);
    boolean deleteProductById(int id);
    productDTO insertProduct(String p);
    boolean updateProduct(String p);
    List<productDTO> searchProductsByKeyword(String keyword);


    // New JSON methods
    String getProductJsonById(int id);
    String getAllProductsJson();
    String getProductsJsonByKeyword(String keyword);

}
