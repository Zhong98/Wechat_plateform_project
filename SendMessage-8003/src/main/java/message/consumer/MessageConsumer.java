package message.consumer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import message.mapper.FanMapper;
import message.mapper.TaskMapper;
import message.util.SendMessageUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "sendMessage", consumerGroup = "message-consumer")
public class MessageConsumer implements RocketMQListener<String> {

    @Resource
    FanMapper fanMapper;

    @Resource
    TaskMapper taskMapper;

    @Value("${template-id}")
    String autoPushTemplateId;

    @Override
    public void onMessage(String s) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> taskInfo = gson.fromJson(s, type);
        String templateId = taskInfo.get("templateId");

        List<String> openidList;
        if (taskInfo.get("taskId") != null){
            int taskId = Integer.parseInt(taskInfo.get("taskId"));
            if (templateId == null) {
                openidList = fanMapper.selectNewSubscribers();
                templateId = autoPushTemplateId;
            } else {
                int tagId = Integer.parseInt(taskInfo.get("tagId"));
                openidList = fanMapper.getFanListByTagId(tagId);
            }
            if (openidList.size() > 1000) {
                openidList.subList(0, 1000).clear();
            }
            if (SendMessageUtil.sendMessageToWeChat(openidList, templateId)){
                taskMapper.updateTaskStatus(taskId);
            }
        }
    }
}
