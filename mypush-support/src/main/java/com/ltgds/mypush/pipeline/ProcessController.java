package com.ltgds.mypush.pipeline;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.exception.ProcessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 流程控制器
 */
@Slf4j
@Data
public class ProcessController {

    /**
     * 模板映射
     */
    private Map<String, ProcessTemplate> templateConfig = null; //将责任链code和业务执行模板进行映射

    /**
     * 执行责任链
     *
     * @param context
     * @return 返回上下文内容
     */
    public ProcessContext process(ProcessContext context) {
        /**
         * 前置检查
         *
         * 1.流程上下文是否为空
         * 2.业务代码是否为空
         * 3.流程模板是否为空
         * 4.业务处理器列表(List<BusinessProcess>)是否为空
         */
        try {
            preCheck(context);
        } catch (ProcessException e) {
            return e.getProcessContext();
        }

        /**
         * 遍历流程节点
         */
        List<BusinessProcess> processList = templateConfig.get(context.getCode()).getProcessList(); //获取业务执行器列表
        //遍历业务执行器列表,执行 具体的业务执行器的process的逻辑
        for (BusinessProcess businessProcess : processList) {
            businessProcess.process(context);
            if (context.getNeedBreak()) {
                break;
            }
        }
        return context;
    }

    /**
     * 前置检查，出问题则抛出异常
     *
     * @param context 执行上下文
     */
    private void preCheck(ProcessContext context) {
        //上下文 为空,返回失败消息
        if (context == null) {
            context = new ProcessContext();
            context.setResponse(BasicResultVO.fail(RespStatusEnum.CONTEXT_IS_NULL)); //设置响应编码
            throw new ProcessException(context); //抛出响应编码
        }

        //业务代码 为空
        String businessCode = context.getCode();
        if (StrUtil.isBlank(businessCode)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.BUSINESS_CODE_IS_NULL));
            throw new ProcessException(context);
        }

        //执行模板 为空
        ProcessTemplate processTemplate = templateConfig.get(businessCode); //责任链code不为空,获取对应的执行模板
        if (processTemplate == null) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_TEMPLATE_IS_NULL));
            throw new ProcessException(context);
        }

        //执行模板列表
        List<BusinessProcess> processList = processTemplate.getProcessList(); //执行模板不为空,获取BusinessProcess列表
        if (CollUtil.isEmpty(processList)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_LIST_IS_NULL));
            throw new ProcessException(context);
        }
    }

}
