package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//用main上的类，也就是正式部署那个main
public class kafkaTests {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","wait");
        kafkaProducer.sendMessage("test","你好");
        try{
            Thread.sleep(1000*10);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}


@Component
class KafkaConsumer {

   //消费者是被动的，不需要注入KafkaTemplate
    @KafkaListener(topics = {"test"})

    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
