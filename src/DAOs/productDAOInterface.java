package DAOs;

import DTOs.productDTO;
import java.util.List;

public interface productDAOInterface {
    List<productDTO> getAllProducts();
    productDTO getProductById(int id);
    boolean deleteProductById(int id);
    productDTO insertProduct(productDTO p);

    // **NEW: Update an existing product by ID**
    boolean updateProduct(int id, productDTO updatedProduct);

    // **NEW: Find products by keyword (search by name or description)**
    List<productDTO> findProductsByKeyword(String keyword);
}
