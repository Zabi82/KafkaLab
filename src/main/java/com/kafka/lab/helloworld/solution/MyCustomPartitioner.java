package com.kafka.lab.helloworld.solution;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

public class MyCustomPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions =
                cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if ((keyBytes == null) || (!(key instanceof String)))
            throw new InvalidRecordException("String key expected");
        String keyStr = (String) key;
        //check if last char of key is a number and is odd then partition 0 else partition 1
        char lastChar = keyStr.charAt(keyStr.length() - 1);
        return (Character.isDigit(lastChar) && (Character.getNumericValue(lastChar)) % 2 == 1) ? 0 : 1;

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

}