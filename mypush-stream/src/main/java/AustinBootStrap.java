import com.ltgds.mypush.common.domain.AnchorInfo;
import constants.AustinFlinkConstant;
import function.AustinFlatMapFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.kafka.core.KafkaAdmin;
import sink.AustinSink;
import utils.MessageQueueUtils;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description flink启动类
 */
@Slf4j
public class AustinBootStrap {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /**
         * 1.获取KafkaConsumer
         */
        KafkaSource<String> kafkaConsumer = MessageQueueUtils.getKafkaConsumer(AustinFlinkConstant.TOPIC_NAME,
                AustinFlinkConstant.GROUP_ID, AustinFlinkConstant.BROKER);

        DataStreamSource<String> kafkaSource = env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks(),
                AustinFlinkConstant.SOURCE_NAME);

        /**
         * 2.数据转换处理
         */
        SingleOutputStreamOperator<AnchorInfo> dataStream = kafkaSource.flatMap(new AustinFlatMapFunction())
                .name(AustinFlinkConstant.FUNCTION_NAME);

        /**
         * 3.将实时数据多维度写入Redis,离线数据写入hive
         */
        dataStream.addSink(new AustinSink())
                .name(AustinFlinkConstant.SINK_NAME);

        env.execute(AustinFlinkConstant.JOB_NAME);
    }
}
