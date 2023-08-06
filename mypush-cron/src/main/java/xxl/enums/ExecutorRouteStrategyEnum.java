package xxl.enums;

/**
 * @author Li Guoteng
 * @data 2023/8/3
 * @description 路由策略
 */
public enum ExecutorRouteStrategyEnum {

    /**
     * first
     */
    FIRST,

    /**
     * last
     */
    LAST,

    /**
     * round
     */
    ROUND,

    /**
     * random
     */
    RANDOM,

    /**
     * consistent_hash
     */
    CONSISTENT_HASH,

    /**
     * least_frequently_used
     */
    LEAST_FREQUENTLY_USED,

    /**
     * least_recently_used
     */
    LEAST_RECENTLY_USED,

    /**
     * failover
     */
    FAILOVER,

    /**
     * busyover
     */
    BUSYOVER,

    /**
     * sharding_broadcast
     */
    SHARDING_BROADCAST;

    ExecutorRouteStrategyEnum() {
    }
}
