package com.ltgds.mypush.service.api.impl.config;

import com.ltgds.mypush.pipeline.ProcessController;
import com.ltgds.mypush.pipeline.ProcessTemplate;
import com.ltgds.mypush.service.api.enmus.BusinessCode;
import com.ltgds.mypush.service.api.impl.action.AfterParamCheckAction;
import com.ltgds.mypush.service.api.impl.action.AssembleAction;
import com.ltgds.mypush.service.api.impl.action.PreParamCheckAction;
import com.ltgds.mypush.service.api.impl.action.SendMqAction;
import org.hibernate.annotations.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/6/4
 * @description api层的pipeline配置类
 */
@Configuration
public class PipelineConfig {

    @Autowired
    private PreParamCheckAction preParamCheckAction;
    @Autowired
    private AssembleAction assembleAction;
    @Autowired
    private AfterParamCheckAction afterParamCheckAction;
    @Autowired
    private SendMqAction sendMqAction;

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     * @return 组装业务执行器（通过责任链模式）,生成业务执行模板
     */
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate(); //生成业务执行器 列表
        //将具体的业务执行器 加入到 业务执行器列表中
        processTemplate.setProcessList(Arrays.asList(preParamCheckAction, assembleAction,
                afterParamCheckAction, sendMqAction));
        return processTemplate;
    }

    /**
     * pip流程控制器
     * 后续扩展则加BusinessCode和ProcessTemplate
     * @return
     */
    @Bean
    public ProcessController processController() {
        ProcessController processController = new ProcessController(); //生成流程控制器
        Map<String, ProcessTemplate> templateConfig = new HashMap<>(4);  //模板映射大小为4
        //将责任链code和业务执行模板进行映射
        //普通发送(common_send) -> 具体的业务执行列表(processTemplate)
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        //将templateConfig填充到processController里的templateConfig中
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
}
