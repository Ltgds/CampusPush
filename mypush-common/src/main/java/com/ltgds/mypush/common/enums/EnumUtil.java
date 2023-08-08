package com.ltgds.mypush.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/8/7
 * @description 枚举工具类
 * 获取枚举的描述、获取枚举的code、获取枚举的code列表
 */
public class EnumUtil {

    private EnumUtil() {

    }

    /**
     * 通过code获取枚举的描述
     * @param code
     * @param enumClass
     * @return
     * @param <T>
     */
    public static <T extends PowerfulEnum> String getDescriptionByCode(Integer code, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(e.getCode(), code)) //通过code获取对应的枚举类
                .findFirst().map(PowerfulEnum::getDescription).orElse(""); //获取描述
    }

    /**
     * 通过code获取枚举类
     * @param code
     * @param enumClass
     * @return
     * @param <T>
     */
    public static <T extends PowerfulEnum> T getEnumByCode(Integer code, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().orElse(null);
    }

    /**
     *
     * @param enumClass
     * @return
     * @param <T>
     */
    public static <T extends PowerfulEnum> List<Integer> getCodeList(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(PowerfulEnum::getCode)
                .collect(Collectors.toList());
    }
}
