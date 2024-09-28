package entity;

import lombok.Data;

@Data
public class NewTemplate {
    private String templateId;
    private String templateType;
    private String templateName;
    private String templateContent;
    private String templateParams;
    private String wxTemplateId;
    private String templateCreatedBy;
}
