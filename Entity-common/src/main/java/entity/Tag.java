package entity;

import lombok.Data;

@Data
public class Tag {
    private int tagId;
    private String tagName;
    private String description;
    private int type;
    private int fansCount;
    private String createdBy;
    private String dateCreated;
}
