package platform.controller;

import entity.NewTemplate;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import constants.Result;
import constants.ResultCode;
import platform.service.TemplateService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mp/internet/wechat/template")
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @PostMapping("/list")
    public Result getTemplateList(@RequestBody Map<String, Object> templateMap) {
        String type = (String) templateMap.get("templateType");
        String name = (String) templateMap.get("templateName");
        String id = (String) templateMap.get("templateId");
        int page = templateMap.get("page") == null ? 1 : Integer.parseInt(templateMap.get("page").toString());
        int pageSize = templateMap.get("pageSize") == null ? 10 : Integer.parseInt(templateMap.get("pageSize").toString());

        Map<String, Object> templateList = new HashMap<>();
        templateList.put("templateList", templateService.getTemplates(page, pageSize, type, name, id));
        int totalTemplate = templateService.totalTemplate(type, name, id);
        int totalPage = totalTemplate%pageSize == 0?totalTemplate/pageSize:totalTemplate/pageSize+1;
        templateList.put("totalTemplate", totalTemplate);
        templateList.put("totalPage", totalPage);
        return new Result(ResultCode.SUCCESS,"success", templateList);
    }

    @PostMapping("/add")
    public Result addTemplate(@RequestBody NewTemplate newTemplate, HttpServletRequest request) {
        String token = request.getHeader("token");
        templateService.addTemplate(newTemplate, token);
        return new Result(ResultCode.SUCCESS,"success");
    }
}
