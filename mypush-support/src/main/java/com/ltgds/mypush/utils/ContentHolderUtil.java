package com.ltgds.mypush.utils;

import com.ltgds.mypush.constans.MessageQueuePipeline;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.PropertyPlaceholderHelper;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 内容占位符  替换
 * <p>
 * 替换格式： {$var}
 */
public class ContentHolderUtil {

    /**
     * 占位符前缀
     */
    public static final String PLACE_HOLDER_PREFIX = "{$";

    /**
     * 占位符后缀
     */
    public static final String PLACE_HOLDER_SUFFIX = "}";

    public static final StandardEvaluationContext EVALUATION_CONTEXT;

    public static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper(
            PLACE_HOLDER_PREFIX, PLACE_HOLDER_SUFFIX
    );

    static {
        EVALUATION_CONTEXT = new StandardEvaluationContext();
        EVALUATION_CONTEXT.addPropertyAccessor(new MapAccessor());
    }

    private static class CustomPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final String template;
        private final Map<String, String> paraMap;

        public CustomPlaceholderResolver(String template, Map<String, String> paraMap) {
            super();
            this.template = template;
            this.paraMap = paraMap;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            String value = paraMap.get(placeholderName);
            if (value == null) {
                String errorStr = MessageFormat.format("template:{0} require param:{1},but not exist! paramMap:{2}",
                        template, placeholderName, paraMap.toString());
                throw new IllegalArgumentException(errorStr);
            }
            return value;
        }
    }

    public static String replacePlaceHolder(final String template, final Map<String, String> paraMap) {
        String replacedPushContent = PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(template,
                new CustomPlaceholderResolver(template, paraMap));
        return replacedPushContent;
    }
}
