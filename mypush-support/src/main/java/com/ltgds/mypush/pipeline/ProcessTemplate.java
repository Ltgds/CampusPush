package com.ltgds.mypush.pipeline;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 业务执行模板（将责任链的逻辑串联起来）
 *
 * 将具体的实现类串联起来
 */
public class ProcessTemplate {

    private List<BusinessProcess> processList;

    public List<BusinessProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<BusinessProcess> processList) {
        this.processList = processList;
    }

}
