package platform.service.impl;

import entity.NewTemplate;
import entity.Template;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import platform.mapper.TemplateMapper;
import platform.service.TemplateService;
import platform.util.JWTUtil;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Resource
    private TemplateMapper templateMapper;

    @Override
    public List<Template> getTemplates(int page, int pageSize, String type, String name, String id) {
        int start = (page - 1) * pageSize;
        return templateMapper.selectTemplates(start, pageSize, type, name, id);
    }

    @Override
    public int totalTemplate(String type, String name, String id) {
        return templateMapper.totalTemplate(type, name, id);
    }

    @Override
    public void addTemplate(NewTemplate newTemplate, String token) {
        String username = (String) JWTUtil.parseToken(token).get("username");
        newTemplate.setTemplateCreatedBy(username);
        templateMapper.insertTemplate(newTemplate);
    }
}
