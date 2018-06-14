package cn.xj.common.tool;

public class IdWorker {

    //时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
    private final static long twepoch = 1516489628123L;
    //对应时间2018-01-21 7:7:8.123// 机器标识位数
    private final static long workerIdBits = 5L;
    //数据中心标识位数
    private final static long datacenterIdBits = 5L;
    //机器ID最大值
    private final static long maxWorkerId = ~(-1L << workerIdBits);
    //二进制5位最大值：11111=十进制31
    //数据中心ID最大值
    private final static long maxDatacenterId = ~(-1L << datacenterIdBits);
    //二进制5位最大值：11111
    //毫秒内自增位：我创建的是11位，连上奇偶性位数12位
    private final static long sequenceBits = 11L;
    //数字的奇偶性
    private final static long odevityBits = 1;
    //添加自己的业务是数据具有奇偶性，在使用该id生成器是发现如果不是高并发的生成id，生成的每个//毫秒内自增向左移1位
    private final static long sequenceShift = odevityBits;
    //机器ID偏左移12位
    private final static long workerIdShift = odevityBits + sequenceBits;
    //数据中心ID左移17位
    private final static long datacenterIdShift = odevityBits + sequenceBits + workerIdBits;
    //时间毫秒左移22位
    private final static long timestampLeftShift = odevityBits + sequenceBits + workerIdBits + datacenterIdBits;
    //序列号最大值11位：11111111111=2047
    private final static long sequenceMask = ~(-1L << sequenceBits);
    //2047/* 上次生产id时间戳 */
    private static long lastTimestamp = -1L;
    //0，并发控制
    private long sequence = 0L;
    //    private long odevity;
    private final long workerId;
    //数据标识id部分
    private final long datacenterId;

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence << sequenceShift | timestamp & 1;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
