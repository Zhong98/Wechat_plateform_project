package message.util;

import java.util.List;

public class SendMessageUtil {

    /**
     * 模拟推送微信消息的方法，该方法一次只能推1000个粉丝openid
     *
     * @param openidList,粉丝openid列表
     * @return
     */
    public static Boolean sendMessageToWeChat(List<String> openidList, String templateId) {
        int a = (int) (Math.random() * 10);

        if (a > 2) {
            return true;
        }
        return false;
    }
}
