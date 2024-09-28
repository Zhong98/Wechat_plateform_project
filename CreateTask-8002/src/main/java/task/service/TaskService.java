package task.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface TaskService {
    String sendTemplateMsg(int tagId, String templateId, HttpServletRequest request);
    void sendScheduledMsg();
    Map<String, Object> getTaskList(Map<String, Object> taskMap);
}