package com.spnotes.kafka.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.HashMap;
import java.util.Map;

public class CountryPartitioner implements Partitioner {
  private static Map<String, Integer> countryToPartitionMap;

  // This method will gets called at the start, you should use it to do one time startup activity
  public void configure(Map<String, ?> configs) {
    System.out.println("Inside CountryPartitioner.configure " + configs);
    countryToPartitionMap = new HashMap<String, Integer>();
    for (Map.Entry<String, ?> entry : configs.entrySet()) {
      if (entry.getKey().startsWith("partitions.")) {
        String keyName = entry.getKey();
        String value = (String) entry.getValue();
        System.out.println(keyName.substring(11));
        int partitionId = Integer.parseInt(keyName.substring(11));
        countryToPartitionMap.put(value, partitionId);
      }
    }
  }

  //This method will get called once for each message
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
                       Cluster cluster) {
    String countryName = ((String) value).split(":")[0];
    if (countryToPartitionMap.containsKey(countryName)) {
      // If the country is mapped to particular partition return it
      return countryToPartitionMap.get(countryName);
    } else {
      // If no, country is mapped to particular partition distribute between remaining partitions
      int noOfPartitions = cluster.topics().size();
      System.out.println(String.format("cluster.topics(): [%s]", cluster.topics()));
      System.out.println(String.format("noOfPartitions: [%s]", noOfPartitions));
      System.out.println(String.format("value: [%s]", value));
      System.out.println(String.format("value.hashCode(): [%d]", value.hashCode()));
      System.out.println(String.format("value.hashCode() percent noOfPartitions: [%d]", value.hashCode() % noOfPartitions));
      System.out.println(String.format("countryToPartitionMap.size(): [%d]", countryToPartitionMap.size()));

      Integer result = value.hashCode() % noOfPartitions + countryToPartitionMap.size();
      System.out.println(String.format("result: [%d]", result));
      return result;
    }
  }

  // This method will get called at the end and gives your partitioner class chance to cleanup
  public void close() {
  }
}