package com.mattleo.finance.backend.endpoint.body;

import com.mattleo.finance.backend.entity.CategoryEntity;

import java.util.List;

public class CategoriesBody extends EntitiesBody<CategoryEntity> {
    private final List<CategoryEntity> categories;

    public CategoriesBody(List<CategoryEntity> categories, String deviceRegId) {
        super(deviceRegId);
        this.categories = categories;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }
}
