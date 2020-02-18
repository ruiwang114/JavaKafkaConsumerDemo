package io.netty.example.http.websocketx.kafkaproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Arrays;
import java.util.Properties;


/**
 *
 * Kafka Consumer类，实现Kafka消费者功能
 *
 *@author oldRi
 *Date 20200218
 */
@Slf4j
public class KafkaConsumerDemo {


    public static void main(String[] args) {
        try {
            InitConnect();
        }catch(Exception err){
            err.printStackTrace();

        }
    }

    /**
     * Kafka连接对象初始化
     *
     * @param
     * @return Producer<String, String> producer
     */
    public static void InitConnect(){

        //kafka连接配置
        Properties properties = new Properties();
        //kafka地址,格式：ip:port
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "1.1.1.1:9993");
        //消费者组名称，随便起，程序重启时建议修改新的组名称
        properties.put("group.id", "group-5");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        //ssl证书设置
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        //ssl证书绝对路径,一所提供
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/.../client.truststore.jks");
        //证书密码,一所提供
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,  "xxx");
        //消费开始offset，可选设置：earliest或latest，earliest从第一个可消费位置开始，latest从最新位置开始
        properties.put("auto.offset.reset", "latest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("ssl.endpoint.identification.algorithm", "");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("eventLog"));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                //输出一行，解析record中的value字段，即是所要消费的数据内容
                System.out.println(record);
                System.out.println();
            }
        }
    }

}
