package DAOs;
import DTOs.productDTO;
import java.util.List;

public interface productDAOInterface {
    List<productDTO> getAllProducts();
    productDTO getProductById(int id);
    boolean deleteProductById(int id);
    productDTO insertProduct(productDTO p);
}
