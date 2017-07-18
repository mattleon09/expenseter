package com.mattleo.finance.api.requests;

import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.backend.endpoint.categories.Categories;
import com.mattleo.finance.backend.endpoint.categories.model.CategoriesBody;
import com.mattleo.finance.backend.endpoint.categories.model.CategoryEntity;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.model.Category;

import java.util.ArrayList;
import java.util.List;

public class PostCategoriesRequest extends PostRequest<CategoriesBody> {
    private final Categories categoriesService;
    private final List<Category> categories;

    public PostCategoriesRequest(GcmRegistration gcmRegistration, Categories categoriesService, List<Category> categories) {
        super(null, gcmRegistration);
        Preconditions.notNull(categoriesService, "Categories service cannot be null.");
        Preconditions.notNull(categories, "Categories list cannot be null.");

        this.categoriesService = categoriesService;
        this.categories = categories;
    }

    @Override protected CategoriesBody createBody() {
        return new CategoriesBody();
    }

    @Override protected void onAddPostData(CategoriesBody body) {
        final List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (Category category : categories) {
//            categoryEntities.add(category.asEntity());
        }
        body.setCategories(categoryEntities);
    }

    @Override protected boolean isPostDataEmpty(CategoriesBody body) {
        return body.getCategories().isEmpty();
    }

    @Override protected void performRequest(CategoriesBody body) throws Exception {
        categoriesService.save(body);
    }
}
