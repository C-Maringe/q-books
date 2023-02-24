package com.qbook.app.domain.models;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "batch_email_meta_info_collection")
public class BatchEmailMetaInfo {
    @Id
    @Expose
    private ObjectId id;
    private Integer totalToBeSentTo;
    private Integer totalActualSentTo;
    private Long durationInMinutes;
}
