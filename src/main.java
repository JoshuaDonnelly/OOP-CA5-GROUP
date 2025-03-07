

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

                    break;
                case 4:

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
}
