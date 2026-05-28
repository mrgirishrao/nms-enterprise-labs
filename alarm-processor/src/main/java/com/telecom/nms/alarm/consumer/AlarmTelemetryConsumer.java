package com.telecom.nms.alarm.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AlarmTelemetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlarmTelemetryConsumer.class);

    @KafkaListener(topics = "telecom-network-alarms", groupId = "nms-alarm-processing-group")
    public void consumeAlarmStream(ConsumerRecord<String, Map<String, Object>> record) {
        log.info("==================================================================");
        log.info("🚀 KAFKA STREAM INGESTED FROM PARTITION: {} AT OFFSET: {}", record.partition(), record.offset());
        log.info("🔑 ROUTING KEY [Equipment Identity]: {}", record.key());
        
        Map<String, Object> alarmData = record.value();
        log.info("📋 SEVERITY: {} | SPECIFIC PROBLEM: {}", alarmData.get("severity"), alarmData.get("problem"));
        log.info("==================================================================");
        
        // Next up in future labs: Forward down to ticket-service over Kafka streams!
    }
}