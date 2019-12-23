package io.netty.example.http.websocketx.kafkaproducer;

import io.netty.example.http.websocketx.base.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;


/**
 *
 * Kafka Producer类，实现Kafka生产者功能
 *
 *@author oldRi
 *Date 20191217
 */
@Slf4j
public class KafkaClient {


    public static void main(String[] args) {
        try {
            Producer<String, String> producer=InitConnect();
            kafkaSend(producer,"test");
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
    public static Producer<String, String>  InitConnect(){

        Properties props = new Properties();
//        props.put("type","async");
//        props.put("bootstrap.servers", "localhost:9092");
        props.put("bootstrap.servers", Global.bootStrapServers);
        props.put("acks", "0");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("batch.size", "16384");//约160M
        props.put("linger.ms", 1);
        props.put("buffer.memory", "33554432");//约32M
        props.put("compression.type", "lz4");
//        props.put("client.id", "producer"+random());

        Producer<String, String> producer = new KafkaProducer<String,String>(props);
        log.info("kafka连接对象初始化成功");
        return producer;
    }

    /**
     * kafka向固定topic发送数据
     *
     * @param producer
     * @param k01Msg
     */
    public static void kafkaSend(Producer<String, String> producer,String k01Msg){
        try {
//            ProducerRecord<String, String> record = new ProducerRecord<String, String>("test1205", k01Msg);
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(Global.topic, k01Msg);
//            producer.send(record);
//            System.out.println("消息发送成功:" + msg);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (null != e){
                        log.info("send error" + e.getMessage());
                    }else {
//                        log.info(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));

                    }
                }
            });
//            producer.close();
        }catch(Exception err){
            err.printStackTrace();
        }
//        }
//        producer.close();
    }

}
