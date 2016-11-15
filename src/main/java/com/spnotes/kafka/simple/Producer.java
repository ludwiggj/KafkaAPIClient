package com.spnotes.kafka.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.annotation.InterfaceStability;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by sunilpatil on 12/28/15.
 */
public class Producer {

  public static void main(String[] argv) throws Exception {
    if (argv.length < 4) {
      System.err.printf("Usage: %s <noOfDistinctKeys> <noOfMessages> <messageDelayInMillis> <topicName>[1..n]\n", Producer.class.getSimpleName());
      System.exit(-1);
    }
    Integer noOfDistinctKeys = Integer.parseInt(argv[0]);
    Integer noOfMessages = Integer.parseInt(argv[1]);
    Integer messageDelayInMillis = Integer.parseInt(argv[2]);

    String[] topicNames = Arrays.copyOfRange(argv, 3, argv.length);


    //Configure the Producer
    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);

    for (int i = 0; i <= noOfMessages; i++) {
      System.out.println();

      // Small sleep to spread messages out
      Thread.sleep(messageDelayInMillis);

      //TODO: Make sure to use the ProducerRecord constructor that does not take partition Id
      String key = String.format("key-" + i % noOfDistinctKeys);
      LocalDateTime currentTime = LocalDateTime.now();
      String value = String.format("Message [%s] [%d]", currentTime, i);

      for (String topicName : topicNames) {
        ProducerRecord<String, String> rec =
            new ProducerRecord<String, String>(topicName, key, value);
        producer.send(rec);

        System.out.println(String.format("Sent record %s", rec));
      }
    }
    producer.close();
  }
}