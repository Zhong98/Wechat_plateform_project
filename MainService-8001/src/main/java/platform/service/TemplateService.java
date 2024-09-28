package platform.service;

import entity.NewTemplate;
import entity.Template;

import java.util.List;

public interface TemplateService {
    List<Template> getTemplates(int page, int pageSize, String type, String name, String id);
    int totalTemplate(String type, String name, String id);
    void addTemplate(NewTemplate newTemplate, String token);
}
