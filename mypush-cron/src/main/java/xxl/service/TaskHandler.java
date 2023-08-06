package xxl.service;

/**
 * @author Li Guoteng
 * @data 2023/8/4
 * @description 具体处理定时任务逻辑的Handler
 */
public interface TaskHandler {

    /**
     * 处理的具体逻辑
     * @param messageTemplateId
     */
    void handle(Long messageTemplateId);
}
