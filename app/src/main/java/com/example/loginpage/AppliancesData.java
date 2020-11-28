package com.example.loginpage;

public class AppliancesData extends Object{

    String Title;
    String Description;
    String category;
    String imageUri;

    public AppliancesData(String title, String description, String category, String imageUri) {
        Title = title;
        Description = description;
        this.category = category;
        this.imageUri = imageUri;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getTitle() {
        return Title;
    }
}

