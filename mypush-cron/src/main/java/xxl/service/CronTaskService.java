package xxl.service;

import com.ltgds.mypush.common.vo.BasicResultVO;
import xxl.entity.XxlJobGroup;
import xxl.entity.XxlJobInfo;

/**
 * @author Li Guoteng
 * @data 2023/8/3
 * @description 定时任务服务
 */
public interface CronTaskService {

    /**
     * 新增/修改 定时任务
     * @param xxlJobInfo
     * @return
     */
    BasicResultVO saveCronTask(XxlJobInfo xxlJobInfo);

    /**
     * 删除定时任务
     * @param taskId
     * @return
     */
    BasicResultVO deleteCronTask(Integer taskId);

    /**
     * 启动定时任务
     *
     * @param taskId
     * @return
     */
    BasicResultVO startCronTask(Integer taskId);

    /**
     * 暂停定时任务
     * @param taskId
     * @return
     */
    BasicResultVO stopCronTask(Integer taskId);

    /**
     * 得到执行器id
     * @param appName
     * @param title
     * @return
     */
    BasicResultVO getGroupId(String appName, String title);

    /**
     * 创建执行器
     * @param xxlJobGroup
     * @return
     */
    BasicResultVO createGroup(XxlJobGroup xxlJobGroup);
}
