package task.service.impl;

import com.google.gson.Gson;
import entity.Task;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import task.mapper.TaskMapper;
import task.service.TaskService;
import task.util.JWTUtil;
import task.util.RedisUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    TaskMapper taskMapper;

    @Resource
    RedisUtil redisUtil;

    private static final String PREFIX = "user:send:";

    @Override
    public String sendTemplateMsg(int tagId, String templateId, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtil.parseToken(token);
        String uid = claims.getSubject();
        //if (redisUtil.get(PREFIX+1) == null) {
        if (redisUtil.get(PREFIX + uid) == null) {
            String creator = (String) claims.get("username");
            Map<String, String> taskMap = new HashMap<>();
            taskMap.put("taskId", createTask(creator, "微信推送") + "");
            taskMap.put("tagId", tagId + "");
            taskMap.put("templateId", templateId);
            String taskInfo = new Gson().toJson(taskMap);
            rocketMQTemplate.syncSend("sendMessage", taskInfo);
            redisUtil.setForTimeMIN(PREFIX + uid, claims.getSubject(), 5);
            //redisUtil.setForTimeMIN(PREFIX+1, "1", 5);
            return "创建消息推送任务成功";
        }
        Long time = redisUtil.getExpire(PREFIX + uid);
        //Long time = redisUtil.getExpire(PREFIX+1);
        return "请勿短时间多次推送，距离下一次推送还剩" + time + "s";
    }

    @Override
    @Retryable
    @Scheduled(cron = "0 0/30 * * * ?")
    public void sendScheduledMsg() {
        Map<String, String> taskMap = new HashMap<>();
        taskMap.put("taskId", createTask("系统自动推送", "新关注者微信推送") + "");
        String taskInfo = new Gson().toJson(taskMap);
        rocketMQTemplate.syncSend("sendMessage", taskInfo);
    }

    private int createTask(String creator, String taskTitle) {
        Task task = new Task();
        int taskID = new Random().nextInt(10000000);
        task.setTaskId(taskID);
        task.setCreator(creator);
        task.setTaskTitle(taskTitle);
        task.setTaskStatus("未完成");
        taskMapper.createTask(task);
        return taskID;
    }

    @Override
    public Map<String, Object> getTaskList(Map<String, Object> taskMap) {
        String creator = (String) taskMap.get("creator");
        String startTime = (String) taskMap.get("startTime");
        String endTime = (String) taskMap.get("endTime");

        if (taskMap.get("page") != null && taskMap.get("pageSize") != null) {
            int page = Integer.parseInt(taskMap.get("page").toString());
            int pageSize = Integer.parseInt(taskMap.get("pageSize").toString());
            int start = (page - 1) * pageSize;
            String taskType = null;
            if (taskMap.get("taskType") != null) {
                taskType = taskMap.get("taskType").toString();
                if (taskType.equals("SEND_MESSAGE_TO_WX")) {
                    taskType = "微信推送";
                } else if (taskType.equals("JOIN_TAG")) {
                    taskType = "加入自定义分组";
                }
            }
            List<Task> taskList = taskMapper.getTaskList(creator, startTime, endTime, taskType, start, pageSize);
            int count = taskList.size();
            int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("count", count);
            taskData.put("totalPage", totalPage);
            taskData.put("taskList", taskList);
            return taskData;
        }

        return null;
    }
}
