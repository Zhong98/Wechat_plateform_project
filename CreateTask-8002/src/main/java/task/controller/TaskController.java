package task.controller;

import constants.Result;
import constants.ResultCode;
import entity.Task;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import task.service.TaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mp/internet/wechat")
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping("/template/sendTemplateMsg")
    public Result sendTemplateMsg(@RequestBody Map<String, Object> msgMap, HttpServletRequest request) {
        if (msgMap.get("tagId") == null || msgMap.get("templateId") == null){
            return new Result(ResultCode.FAILURE, "Tag Id and template Id can't be null");
        }
        int tagId = Integer.parseInt(msgMap.get("tagId").toString());
        String templateId = msgMap.get("templateId").toString();
        return new Result(ResultCode.SUCCESS, taskService.sendTemplateMsg(tagId, templateId, request));
    }

    @PostMapping("/task/getTaskList")
    public Result getTaskList(@RequestBody Map<String, Object> taskMap) {
        Map<String, Object> taskData = taskService.getTaskList(taskMap);
        return new Result(ResultCode.SUCCESS, "success", taskData);
    }
}
