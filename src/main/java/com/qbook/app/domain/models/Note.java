package com.qbook.app.domain.models;

import lombok.Data;

@Data
public class Note {
    private String title;
    private String description;
    private long dateCreated;
    private long dateUpdated;
    private String uuid;
    private String createdByName;
    private String createdById;
}
