package com.mattleo.finance.backend.endpoint.body;

import com.mattleo.finance.backend.entity.TagEntity;

import java.util.List;

public class TagsBody extends EntitiesBody<TagEntity> {
    private final List<TagEntity> tags;

    public TagsBody(List<TagEntity> tags, String deviceRegId) {
        super(deviceRegId);
        this.tags = tags;
    }

    public List<TagEntity> getTags() {
        return tags;
    }
}
