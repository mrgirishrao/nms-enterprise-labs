package com.telecom.nms.alarm.config;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);

    @Bean
    public CommonErrorHandler globalKafkaErrorHandler(
            // 🚀 CRITICAL FIX: Inject the JSON-safe template exclusively for DLQ shunting!
            @Qualifier("jsonRawKafkaTemplate") KafkaTemplate<String, Object> textTemplate) {
        
        DeadLetterPublishingRecoverer dlqRecoverer = new DeadLetterPublishingRecoverer(textTemplate,
            (consumerRecord, exception) -> {
                log.error("🚨 [TX-DLQ SHUNT] Exhausted retry attempts within transaction!");
                return new TopicPartition(consumerRecord.topic() + ".DLT", 0);
            });

        FixedBackOff backOffStrategy = new FixedBackOff(2000L, 3);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(dlqRecoverer, backOffStrategy);
        
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("⚠️ Transaction rollback initiated. Retrying stream chunk. Attempt #{}", deliveryAttempt);
        });

        return errorHandler;
    }
}