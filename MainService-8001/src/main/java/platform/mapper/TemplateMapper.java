package platform.mapper;

import entity.NewTemplate;
import entity.Template;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateMapper {
    List<Template> selectTemplates(int start, int pageSize, String templateType, String templateName, String templateId);
    int totalTemplate(String templateType, String templateName, String templateId);
    void insertTemplate(NewTemplate newTemplate);
}
