package ossindex;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushBroadcastMessageRequest;
import com.baidu.yun.channel.model.PushBroadcastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PushBroadcastMessage {

    private static Logger logger = LogManager.getLogger(PushBroadcastMessage.class.getName());

	public static void main(String[] args) {
        PushBroadcastMessage pushBroadcastMessage = new PushBroadcastMessage();
        pushBroadcastMessage.send("每日更新", "套图更新了,速来围观");
    }

    public void send(String title, String content) {
    /*
     * @brief	推送广播消息(消息类型为透传，由开发方应用自己来解析消息内容)
     * 			message_type = 0 (默认为0)
     */

        // 1. 设置developer平台的ApiKey/SecretKey
        String apiKey = "6NxlGErC78G5tGB2aWPblquO";
        String secretKey = "1AyvQY3WKjYKxGc1Rf2fubz4xAGbZ2UH";
        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        // 3. 若要了解交互细节，请注册YunLogHandler类
        channelClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

        try {

// 4. 创建请求类对象
            PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
            request.setDeviceType(3);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp

            request.setMessageType(1);
            request.setMessage("{\"title\":\"" + title + "\",\"description\":\"" + content + "\"}");

// 5. 调用pushMessage接口
            PushBroadcastMessageResponse response = channelClient.pushBroadcastMessage(request);

// 6. 认证推送成功
            logger.info("接收数量 : " + response.getSuccessAmount());

        } catch (ChannelClientException e) {
// 处理客户端错误异常
            logger.info(e.getMessage());
        } catch (ChannelServerException e) {
// 处理服务端错误异常
            logger.info(
                    String.format("request_id: %d, error_code: %d, error_message: %s",
                            e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
                    )
            );
        }
    }

}
