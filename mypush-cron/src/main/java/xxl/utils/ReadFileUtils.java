package xxl.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.*;
import com.google.common.base.Throwables;
import csv.CountFileRowHandler;
import lombok.extern.slf4j.Slf4j;
import vo.CrowdInfoVo;

import java.io.FileReader;
import java.util.*;

/**
 * @author Li Guoteng
 * @data 2023/8/4
 * @description 读取人群文件
 */
@Slf4j
public class ReadFileUtils {

    /**
     * csv文件 存储接收者的列表
     */
    public static final String RECEIVER_KEY = "userId";

    /**
     * 读取csv文件,每读取一行都会调用 csvRowHandler对应的方法
     * @param path
     * @param csvRowHandler 作为处理每一行的逻辑
     */
    public static void getCsvRow(String path, CsvRowHandler csvRowHandler) {
        try {
            //首行为标题,获取reader
            CsvReader reader = CsvUtil.getReader(new FileReader(path), new CsvReadConfig().setContainsHeader(true));

            reader.read(csvRowHandler); //按照每行读取

        } catch (Exception e) {
            log.error("ReadFileUtils#getCsvRow fail!{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 读取csv文件,获取文件里的行数
     * @param path
     * @param countFileRowHandler
     * @return
     */
    public static long countCsvRow(String path, CountFileRowHandler countFileRowHandler) {

        try {
            //将首行作为标题,获取reader
            CsvReader reader = CsvUtil.getReader(new FileReader(path), new CsvReadConfig().setContainsHeader(true));

            reader.read(countFileRowHandler);
        } catch (Exception e) {
            log.error("ReadFileUtils#getCsvRow fail!{}", Throwables.getStackTraceAsString(e));
        }
        return countFileRowHandler.getRowSize(); //获取csv文件里的行数
    }

    /**
     * 从文件的每一行数据获取params数据
     * [{key,value}, {key,value}]
     * @param fieldMap
     * @return
     */
    public static HashMap<String, String> getParamFormLine(Map<String, String> fieldMap) {
        HashMap<String, String> params = MapUtil.newHashMap();

        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            if (!ReadFileUtils.RECEIVER_KEY.equals(entry.getKey())) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        return params;
    }

    /**
     * 一次性读取csv文件所有内容
     * 1.获取第一行信息(id, paramsKey1, paramsKey2),第一列默认为接收者id
     * 2.将文件信息塞进对象内
     * 3.将对象返回
     *
     * @param path
     * @return
     */
    @Deprecated
    public static List<CrowdInfoVo> getCsvRowList(String path) {
        List<CrowdInfoVo> result = new ArrayList<>();

        try {
            CsvData data = CsvUtil.getReader().read(FileUtil.file(path));
            if (Objects.isNull(data) || Objects.isNull(data.getRow(0)) || Objects.isNull(data.getRow(1))) {
                log.error("read csv file empty!, path:{}", path);
            }

            //第一行默认为头信息,所以从第二行开始遍历,第一列默认为接收者id(不处理)
            CsvRow headerInfo = data.getRow(0); //获取第一行
            for (int i = 1; i < data.getRowCount(); i++) {
                CsvRow row = data.getRow(i);
                Map<String, String> param = MapUtil.newHashMap();
                //
                for (int j = 1; j < headerInfo.size(); j++) {
                    param.put(headerInfo.get(j), row.get(j));
                }

                result.add(CrowdInfoVo.builder()
                                .receiver(CollUtil.getFirst(row.iterator()))
                                .params(param)
                        .build());
            }
        } catch (Exception e) {
            log.error("ReadFileUtils#getCsvRowList fail!{}", Throwables.getStackTraceAsString(e));
        }

        return result;
    }
}
