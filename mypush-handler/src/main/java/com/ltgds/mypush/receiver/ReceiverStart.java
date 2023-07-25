package com.ltgds.mypush.receiver;

import com.ltgds.mypush.constans.MessageQueuePipeline;
import com.ltgds.mypush.utils.GroupIdMappingUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author Li Guoteng
 * @data 2023/7/23
 * @description  启动消费者
 */
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
@Slf4j
public class ReceiverStart {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ConsumerFactory consumerFactory;

    /**
     * receiver的消费方法常量
     */
    private static final String RECEIVER_METHOD_NAME = "Receiver.consumer";

    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroups();

    /**
     * 下标(用于迭代groupIds位置)
     */
    private static Integer index = 0;

    /**
     * 由于使用@KafkaListener注解从Kafka拉取消息
     * 目的：多个group消费同一个topic,不可能给每个group都定义一个消费方法
     *
     * 解决方法：消费者逻辑一样,但是创建多个独立的消费者,通过切面而不用手动的创建,然后手动的指定groupId
     */

    /**
     * 为每个渠道每个不同消息类型,创建一个Receiver对象
     * 初始化消费者,有多少个groupId就初始化多少个消费者
     */
    @PostConstruct  //在项目启动时执行此方法(Spring容器启动时执行) 如果需要在生成对象时完成初始化操作,且这些操作又依赖注入,可使用@PostConstruct,会在依赖注入完成后被自动调用
    public void init() {
        for (int i = 0; i < groupIds.size(); i++) {
            context.getBean(Receiver.class);
        }
    }

    /**
     * 给每个Receiver对象的consumer方法 拿到对应@KafkaListener注解 赋值相应的groupId
     *
     * 动态传入groupId 进而创建多个消费者了
     *
     * KafkaListenerAnnotationBeanPostProcessor,处理Kafka监听的注解
     *  - 扫描被@KafkaListener注解标记的类,并创建必要的消息监听器来消费Kafka主题中的消息
     * @return
     */
    @Bean
    public static KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer groupIdEnhancer() {
        return (attrs, element) -> {
            if (element instanceof Method) {
                String name = ((Method) element).getDeclaringClass().getSimpleName() + "." + ((Method) element).getName();
                if (RECEIVER_METHOD_NAME.equals(name)) {
                    attrs.put("groupId", groupIds.get(index++));
                }
            }
            return attrs;
        };
    }

    /**
     * 针对tag消息过滤
     * 发送时,将tag写进Kafka的头部,在消费前将非自身tag的消息过滤掉
     *
     * 将tag写进header里面
     * @param tagIdKey
     * @param tagIdValue
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory filterContainerFactory(@Value("${austin.business.tagId.key}") String tagIdKey,
                                                                          @Value("${austin.business.tagId.value}") String tagIdValue) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.setAckDiscarded(true);

        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Optional.ofNullable(consumerRecord.value()).isPresent()) {
                for (Header header : consumerRecord.headers()) {
                    if (header.key().equals(tagIdKey) && new String(header.value()).equals(new String(tagIdValue.getBytes(StandardCharsets.UTF_8)))) {
                        return false;
                    }
                }
            }
            //返回true将被丢弃
            return true;
        });
        return factory;
    }

}
