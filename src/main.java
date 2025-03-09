import DTOs.productDTO;
import DAOs.productDAO;

import java.util.List;
import java.util.Scanner;

public class main {
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
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input! Enter a number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllProducts();
                    break;
                case 2:
                    findProductById();
                    break;
                case 3:
                    deleteProductById();
                    break;
                case 4:
                    insertNewProduct();
                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 0:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
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
        scanner.nextLine(); // Consume newline

        productDTO product = productDAO.getProductById(id);
        if (product != null) {
            System.out.println("Product found: " + product);
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void deleteProductById() {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean product = productDAO.deleteProductById(id);
        if (product) {
            System.out.println("Product found");
            System.out.println("Deleting...");
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
        scanner.nextLine(); // Consume newline

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
}
