package function;

import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.common.domain.AnchorInfo;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description 处理
 */
public class AustinFlatMapFunction implements FlatMapFunction<String, AnchorInfo> {
    @Override
    public void flatMap(String value, Collector<AnchorInfo> collector) throws Exception {
        AnchorInfo anchorInfo = JSON.parseObject(value, AnchorInfo.class);
        collector.collect(anchorInfo);
    }
}
