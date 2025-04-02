
import DTOs.productDTO;
import DAOs.productDAO;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final productDAO productDAO = new productDAO();

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== Guitar E-Commerce System =====");
            System.out.println("1. View All Products");
            System.out.println("2. Find Product by ID");
            System.out.println("3. Insert New Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Search Products by Keyword");
            System.out.println("7. View All Products (JSON)");
            System.out.println("8. Find Product by ID (JSON)");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAllProducts();
                case 2 -> findProductById();
                case 3 -> insertNewProduct();
                case 4 -> updateProduct();
                case 5 -> deleteProductById();
                case 6 -> searchProductsByKeyword();
                case 0 -> System.out.println("Exiting program...");
                case 7 -> viewAllProductsJson();
                case 8 -> findProductByIdJson();
                default -> System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 0);
        scanner.close();
    }

    private static void viewAllProducts() {
        System.out.println("\n--- All Products ---");
        List<productDTO> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (productDTO product : products) {
                System.out.println(product);
            }
        }
    }

    private static void findProductById() {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        productDTO product = productDAO.getProductById(id);
        if (product != null) {
            System.out.println("Product found: " + product);
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void insertNewProduct() {
        System.out.println("\n--- Insert New Product ---");

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Product Price: ");
        while (!scanner.hasNextFloat()) {
            System.out.print("Invalid input! Enter a valid price: ");
            scanner.next();
        }
        float price = scanner.nextFloat();
        scanner.nextLine();

        System.out.print("Enter Product Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter Category ID: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Enter a valid category ID: ");
            scanner.next();
        }
        int categoryId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Stock Quantity: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Enter a valid stock quantity: ");
            scanner.next();
        }
        int stock = scanner.nextInt();
        scanner.nextLine();

        productDTO newProduct = new productDTO(0, name, price, description, categoryId, stock);
        productDTO insertedProduct = productDAO.insertProduct(newProduct);

        if (insertedProduct != null) {
            System.out.println("Product inserted successfully! Assigned ID: " + insertedProduct.getId());
        } else {
            System.out.println("Failed to insert product.");
        }
    }

    private static void updateProduct() {
        System.out.print("\nEnter Product ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        productDTO existingProduct = productDAO.getProductById(id);
        if (existingProduct == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("Updating Product: " + existingProduct);

        System.out.print("Enter New Product Name (leave blank to keep current): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = existingProduct.getName();

        System.out.print("Enter New Product Price (or -1 to keep current): ");
        float price = scanner.nextFloat();
        if (price == -1) price = existingProduct.getPrice();
        scanner.nextLine();

        System.out.print("Enter New Product Description (leave blank to keep current): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = existingProduct.getDescription();

        System.out.print("Enter New Category ID (or -1 to keep current): ");
        int categoryId = scanner.nextInt();
        if (categoryId == -1) categoryId = existingProduct.getCategoryId();
        scanner.nextLine();

        System.out.print("Enter New Stock Quantity (or -1 to keep current): ");
        int stock = scanner.nextInt();
        if (stock == -1) stock = existingProduct.getStock();
        scanner.nextLine();

        productDTO updatedProduct = new productDTO(id, name, price, description, categoryId, stock);
        boolean success = productDAO.updateProduct(id, updatedProduct);

        if (success) {
            System.out.println("Product updated successfully.");
        } else {
            System.out.println("Failed to update product.");
        }
    }

    private static void deleteProductById() {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        boolean success = productDAO.deleteProductById(id);
        if (success) {
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void searchProductsByKeyword() {
        System.out.print("\nEnter keyword: ");
        String keyword = scanner.nextLine();

        List<productDTO> results = productDAO.findProductsByKeyword(keyword);
        if (results.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (productDTO product : results) {
                System.out.println(product);
            }
        }
    }
    private static void viewAllProductsJson() {
        System.out.println("\n--- All Products (JSON) ---");
        String json = productDAO.getAllProductsJson();
        System.out.println(json);
    }

    private static void findProductByIdJson() {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String json = productDAO.getProductJsonById(id);
        System.out.println("Product JSON:");
        System.out.println(json);
    }

}
