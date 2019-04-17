package com.gymtrainer.gymuserapp.Model;

/**
 * Created by haroonpc on 3/14/2019.
 */

public class Category
{
    public String categoryName,categoryId,categoryImageUrl;


    public Category(){};

    public Category(String categoryName, String categoryId,String categoryImageUrl)
    {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}
