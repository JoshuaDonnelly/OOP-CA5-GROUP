package DAOs;

import DTOs.productDTO;
import Database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class productDAO implements productDAOInterface {
    private Connection conn;

    public productDAO() {
        conn = DatabaseConnection.getConnection();
    }

    @Override
    public List<productDTO> getAllProducts() {
        List<productDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public productDTO getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteProductById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public productDTO insertProduct(productDTO p) {
        String sql = "INSERT INTO products (name, price, description, category_id, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getName());
            pstmt.setFloat(2, p.getPrice());
            pstmt.setString(3, p.getDescription());
            pstmt.setInt(4, p.getCategoryId());
            pstmt.setInt(5, p.getStock());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    p.setId(generatedKeys.getInt(1));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // **NEW: Update an existing product by ID**
    @Override
    public boolean updateProduct(int id, productDTO updatedProduct) {
        String sql = "UPDATE products SET name = ?, price = ?, description = ?, category_id = ?, stock = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updatedProduct.getName());
            pstmt.setFloat(2, updatedProduct.getPrice());
            pstmt.setString(3, updatedProduct.getDescription());
            pstmt.setInt(4, updatedProduct.getCategoryId());
            pstmt.setInt(5, updatedProduct.getStock());
            pstmt.setInt(6, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // **NEW: Find products by keyword (search by name or description)**
    @Override
    public List<productDTO> findProductsByKeyword(String keyword) {
        List<productDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            pstmt.setString(2, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
