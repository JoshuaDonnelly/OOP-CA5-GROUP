// ImageDAO.java
package org.example.oopca5jfx.Networking;

import org.example.oopca5jfx.DAOs.ImageDAOInterface;
import org.example.oopca5jfx.Database.DatabaseConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO implements ImageDAOInterface {
    private static final String IMAGE_DIRECTORY = "images/"; // Directory where images are stored

    @Override
    public List<String> getAllImageNames() {
        List<String> imageNames = new ArrayList<>();

        // First try to get from database if stored there
        String sql = "SELECT image_filename FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                imageNames.add(rs.getString("image_filename")); // Match the selected column
            }


            // If no images in database, check filesystem
            if (imageNames.isEmpty()) {
                try {
                    Files.list(Paths.get(IMAGE_DIRECTORY))
                            .filter(Files::isRegularFile)
                            .forEach(path -> imageNames.add(path.getFileName().toString()));
                } catch (IOException e) {
                    System.out.println("Error reading image directory: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error getting image names: " + e.getMessage());
        }

        return imageNames;
    }

    @Override
    public byte[] getImageData(String imageName) {
        try {
            Path imagePath = Paths.get(IMAGE_DIRECTORY + imageName);
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            System.out.println("Error reading image file: " + e.getMessage());
            return null;
        }
    }
}