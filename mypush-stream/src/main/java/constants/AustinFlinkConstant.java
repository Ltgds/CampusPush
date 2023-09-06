package constants;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description Flink常量信息
 */
public class AustinFlinkConstant {

    /**
     * kafka配置信息
     *
     */
    public static final String GROUP_ID = "austinLogGroup";
    public static final String TOPIC_NAME = "austinTraceLog";
    public static final String BROKER = "austin-kafka:9092";

    /**
     * redis配置
     */
    public static final String REDIS_IP = "austin-redis";
    public static final String REDIS_PORT = "6379";
    public static final String REDIS_PASSWORD = "austin";

    /**
     * Flink流程常量
     */
    public static final String SOURCE_NAME = "austin_kafka_source";
    public static final String FUNCTION_NAME = "austin_transfer";
    public static final String SINK_NAME = "austin_sink";
    public static final String JOB_NAME = "AustinBootStrap";
}

