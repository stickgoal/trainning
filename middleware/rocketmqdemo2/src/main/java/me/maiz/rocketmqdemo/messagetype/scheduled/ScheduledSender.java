package me.maiz.rocketmqdemo.messagetype.scheduled;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 定时消息（RocketMQ 5.x）：通过 {@link Message#setDeliverTimeMs(long)} 指定投递时间戳（毫秒）。
 * <p>
 * 与 {@link me.maiz.rocketmqdemo.messagetype.delay.DelaySender} 中 {@code setDelayTimeLevel} 的固定档位延时不同，
 * 本案例可指定任意未来时间点投递。
 * <p>
 * 需要 RocketMQ 5.x Broker 支持定时消息（TimerMessage）。
 */
public class ScheduledSender {

    public static final String TOPIC = "TopicScheduled";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("scheduled_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        // 分别在 10s、20s、30s 后投递
        long[] delaySeconds = parseDelaySeconds(args);

        System.out.println("send at: " + LocalDateTime.now().format(FORMATTER));
        for (int i = 0; i < delaySeconds.length; i++) {
            long deliverTimeMs = System.currentTimeMillis() + delaySeconds[i] * 1000L;
            String body = "scheduled message #" + i + ", deliver after " + delaySeconds[i] + "s";
            Message msg = new Message(TOPIC, "TagScheduled", body.getBytes(StandardCharsets.UTF_8));
            msg.setDeliverTimeMs(deliverTimeMs);

            SendResult result = producer.send(msg);
            System.out.printf("sent msgId=%s, deliverAt=%s, body=%s%n",
                    result.getMsgId(),
                    formatTime(deliverTimeMs),
                    body);
        }

        producer.shutdown();
        System.out.println("ScheduledSender finished.");
    }

    private static long[] parseDelaySeconds(String[] args) {
        if (args.length == 0) {
            return new long[]{10, 20, 30};
        }
        long[] delays = new long[args.length];
        for (int i = 0; i < args.length; i++) {
            delays[i] = Long.parseLong(args[i]);
        }
        return delays;
    }

    private static String formatTime(long epochMs) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault()).format(FORMATTER);
    }
}
