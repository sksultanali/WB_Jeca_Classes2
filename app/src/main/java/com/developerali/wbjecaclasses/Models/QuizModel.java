package com.developerali.wbjecaclasses.Models;

public class QuizModel {

    String id, title, apiKey;
    Long date;

    public QuizModel(String title, String apiKey) {
        this.title = title;
        this.apiKey = apiKey;
    }

    public QuizModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
