package org.example.oopca5jfx.DTOs;

public class productDTO {
    private int id;
    private String name;
    private float price;
    private String description;
    private int categoryId;
    private int stock;
    private String imageFilename;

    public productDTO() {}

    public productDTO(int id, String name, float price, String description, int categoryId, int stock, String imageFilename) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.stock = stock;
        this.imageFilename = imageFilename;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public void setImageFilename(String imageFilename) {this.imageFilename = imageFilename;}
    public String getImageFilename() {return imageFilename;}

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    @Override
    public String toString() {
        return "productDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", categoryId=" + categoryId +
                ", stock=" + stock +
                ", imageFilename='" + imageFilename + '\'' +
                '}';
    }
}

