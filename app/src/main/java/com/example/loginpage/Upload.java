package com.example.loginpage;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String status;
    private String category;

    public Upload(){
//empty
    }
    public Upload(String name,String imageUrl, String status, String category){
        if(name.trim().equals("")){
            name ="No Name";
        }
        mName=name;
        mImageUrl=imageUrl;
        this.status = status;
        this.category = category;
    }
    public String getName(){
        return mName;
    }
    public void setName(String name){
        mName=name;
    }
    public String getImageUrl(){
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }
}


