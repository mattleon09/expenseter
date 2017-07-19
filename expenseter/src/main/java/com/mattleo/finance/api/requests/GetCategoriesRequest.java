package com.mattleo.finance.api.requests;

import android.content.Context;
import android.net.Uri;

import com.mattleo.finance.api.User;
import com.mattleo.finance.backend.endpoint.categories.Categories;
import com.mattleo.finance.backend.endpoint.categories.model.CategoryEntity;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.data.providers.CategoriesProvider;

import java.util.List;

public class GetCategoriesRequest extends GetRequest<CategoryEntity> {
    private final Categories categoriesService;

    public GetCategoriesRequest(Context context, User user, Categories categoriesService) {
        super(null, context, user);
        Preconditions.notNull(categoriesService, "Categories cannot be null.");

        this.categoriesService = categoriesService;
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getCategoriesTimestamp();
    }

    @Override protected List<CategoryEntity> performRequest(long timestamp) throws Exception {
        return categoriesService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(CategoryEntity entity) {
        return null;
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setCategoriesTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return CategoriesProvider.uriCategories();
    }
}
