package com.ltgds.mypush.web.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.domain.ChannelAccount;
import com.ltgds.mypush.web.amis.CommonAmisVo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description
 */
@Slf4j
public class Convert4Amis {

    /**
     * 标识忽略
     */
    public static final int IGNORE_TG = 0;
    /**
     * 标识已读取到'$'字符
     */
    public static final int START_TG = 1;
    /**
     * 标识已读取到'{'字符
     */
    public static final int READ_TG = 2;

    /**
     * 需要打散的字段(将json字符串打散为一个一个字段返回）
     * (主要是用于回显数据)
     */
    private static final List<String> FLAT_FIELD_NAME = Arrays.asList("msgContent");

    /**
     * 需要格式化为jsonArray返回的字段
     * (前端是一个JSONArray传递进来)
     */
    private static final List<String> PARSE_JSON_ARRAY = Arrays.asList("feedCards", "btns", "articles");

    /**
     * (前端是一个JSONObject传递进来，返回一个JSONArray回去)
     */
    private static final List<String> PARSE_JSON_OBJ_TO_ARRAY = Arrays.asList("officialAccountParam", "miniProgramParam");

    /**
     * 钉钉工作消息OA实际的映射
     */
    private static final List<String> DING_DING_OA_FIELD = Arrays.asList("dingDingOaHead", "dingDingOaBody");
    /**
     * 钉钉OA字段名实际的映射
     */
    private static final Map<String, String> DING_DING_OA_NAME_MAPPING = new HashMap<>();

    static {
        DING_DING_OA_NAME_MAPPING.put("bgcolor", "dingDingOaHeadBgColor");
        DING_DING_OA_NAME_MAPPING.put("text", "dingDingOaHeadTitle");
        DING_DING_OA_NAME_MAPPING.put("title", "dingDingOaTitle");
        DING_DING_OA_NAME_MAPPING.put("image", "media_id");
        DING_DING_OA_NAME_MAPPING.put("author", "dingDingOaAuthor");
        DING_DING_OA_NAME_MAPPING.put("content", "dingDingOaContent");
    }

    /**
     * 将List对象转换成Map(无嵌套)
     *
     * @param param
     * @return
     */
    public static <T> List<Map<String, Object>> flatListMap(List<T> param) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T t : param) {
            Map<String, Object> map = flatSingleMap(t);
            result.add(map);
        }
        return result;
    }

    /**
     * 将单个对象转换成Map(无嵌套)
     * <p>
     * 主要兼容amis的回显(前端不用amis可忽略)
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> flatSingleMap(Object obj) {
        Map<String, Object> result = MapUtil.newHashMap(32);
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            if (FLAT_FIELD_NAME.contains(field.getName())) {
                String fieldValue = (String) ReflectUtil.getFieldValue(obj, field);
                JSONObject jsonObject = JSONObject.parseObject(fieldValue);
                for (String key : jsonObject.keySet()) {
                    /**
                     * 钉钉OA消息回显
                     */
                    if (DING_DING_OA_FIELD.contains(key)) {
                        JSONObject object = jsonObject.getJSONObject(key);
                        for (String objKey : object.keySet()) {
                            result.put(DING_DING_OA_NAME_MAPPING.get(objKey), object.getString(objKey));
                        }
                    } else if (PARSE_JSON_ARRAY.contains(key)) {
                        /**
                         * 部分字段是直接传入数组，把数组直接返回(用于回显)
                         */
                        result.put(key, JSON.parseArray(jsonObject.getString(key)));
                    } else if (PARSE_JSON_OBJ_TO_ARRAY.contains(key)) {
                        /**
                         * 部分字段是直接传入Obj，把数组直接返回(用于回显)
                         */
                        String value = "[" + jsonObject.getString(key) + "]";
                        result.put(key, JSON.parseArray(value));
                    } else {
                        result.put(key, jsonObject.getString(key));
                    }
                }
            }
            result.put(field.getName(), ReflectUtil.getFieldValue(obj, field));
        }
        return result;
    }



    /**
     * 【这个方法不用看】，纯粹为了适配amis前端
     * <p>
     * 获取占位符的参数
     *
     * @param msgContent
     * @return
     */
    public static CommonAmisVo getTestContent(String msgContent) {
        Set<String> placeholderList = getPlaceholderList(msgContent);
        if (CollUtil.isEmpty(placeholderList)) {
            return null;
        }

        // placeholderList!=null  说明有占位符
        CommonAmisVo testParam = CommonAmisVo.builder()
                .type("input-table")
                .name("testParam")
                .addable(true)
                .editable(true)
                .needConfirm(false)
                .build();
        List<CommonAmisVo.ColumnsDTO> columnsDtoS = new ArrayList<>();
        for (String param : placeholderList) {
            CommonAmisVo.ColumnsDTO dto = CommonAmisVo.ColumnsDTO.builder().name(param).label(param).type("input-text").required(true).quickEdit(true).build();
            columnsDtoS.add(dto);
        }
        testParam.setColumns(columnsDtoS);
        return testParam;
    }

    /**
     * 获取占位符的参数
     *
     * @param content
     * @return
     */
    public static Set<String> getPlaceholderList(String content) {
        char[] textChars = content.toCharArray();
        StringBuilder textSofar = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        // 存储占位符 位置信息集合
        List<String> placeholderList = new ArrayList<>();
        // 当前标识
        int modeTg = IGNORE_TG;
        for (int m = 0; m < textChars.length; m++) {
            char c = textChars[m];
            textSofar.append(c);
            switch (c) {
                case '{': {
                    modeTg = START_TG;
                    sb.append(c);
                }
                break;
                case '$': {
                    if (modeTg == START_TG) {
                        sb.append(c);
                        modeTg = READ_TG;
                    } else {
                        if (modeTg == READ_TG) {
                            sb = new StringBuilder();
                            modeTg = IGNORE_TG;
                        }
                    }
                }
                break;
                case '}': {
                    if (modeTg == READ_TG) {
                        modeTg = IGNORE_TG;
                        sb.append(c);
                        String str = sb.toString();
                        if (StrUtil.isNotEmpty(str)) {
                            placeholderList.add(str);
                            textSofar = new StringBuilder();
                        }
                        sb = new StringBuilder();
                    } else if (modeTg == START_TG) {
                        modeTg = IGNORE_TG;
                        sb = new StringBuilder();
                    }
                    break;
                }
                default: {
                    if (modeTg == READ_TG) {
                        sb.append(c);
                    } else if (modeTg == START_TG) {
                        modeTg = IGNORE_TG;
                        sb = new StringBuilder();
                    }
                }
            }
        }
        Set<String> result = placeholderList.stream().map(s -> s.replaceAll("\\{", "").replaceAll("\\$", "").replaceAll("\\}", "")).collect(Collectors.toSet());
        return result;
    }



    /**
     * 【这个方法不用看】，纯粹为了适配amis前端
     * <p>
     * 得到渠道账号信息，返回给前端做展示
     *
     * @return
     */
    public static List<CommonAmisVo> getChannelAccountVo(List<ChannelAccount> channelAccounts, Integer channelType) {
        List<CommonAmisVo> result = new ArrayList<>();
        if (ChannelType.SMS.getCode().equals(channelType)) {
            CommonAmisVo commonAmisVo = CommonAmisVo.builder().label("AUTO").value("0").build();
            result.add(commonAmisVo);
        }
        for (ChannelAccount channelAccount : channelAccounts) {
            CommonAmisVo commonAmisVo = CommonAmisVo.builder().label(channelAccount.getName()).value(String.valueOf(channelAccount.getId())).build();
            result.add(commonAmisVo);
        }
        return result;
    }

}
