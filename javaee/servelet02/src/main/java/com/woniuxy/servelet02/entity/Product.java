package com.woniuxy.servelet02.entity;


public class Product {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String mainPic;

    public Product() {
    }

    public Product(Long id, String title, String description, Double price, String mainPic) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.mainPic = mainPic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", mainPic='" + mainPic + '\'' +
                '}';
    }
}
