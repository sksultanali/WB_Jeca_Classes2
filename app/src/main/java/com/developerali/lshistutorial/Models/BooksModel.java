package com.developerali.lshistutorial.Models;

import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class BooksModel {

    String id, fileUrl, sem, type, title, description;
    Long sellingPrice, normalPrice;
    ArrayList<SlideModel> images;
    ArrayList<String> imageString;
    Boolean readBook;

    public BooksModel(String fileUrl, String title, String description) {
        this.fileUrl = fileUrl;
        this.title = title;
        this.description = description;
    }

    public BooksModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Long getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(Long normalPrice) {
        this.normalPrice = normalPrice;
    }

    public ArrayList<SlideModel> getImages() {
        return images;
    }

    public void setImages(ArrayList<SlideModel> images) {
        this.images = images;
    }

    public ArrayList<String> getImageString() {
        return imageString;
    }

    public void setImageString(ArrayList<String> imageString) {
        this.imageString = imageString;
    }

    public Boolean getReadBook() {
        return readBook;
    }

    public void setReadBook(Boolean readBook) {
        this.readBook = readBook;
    }
}
