package com.telecom.nms.alarm.config;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public CommonErrorHandler globalKafkaErrorHandler(KafkaTemplate<String, Object> template) {
        
        // 1. The Recoverer remains our safety valve for shunting exhausted records
        DeadLetterPublishingRecoverer dlqRecoverer = new DeadLetterPublishingRecoverer(template,
            (consumerRecord, exception) -> {
                log.error("🚨 [TX-DLQ SHUNT] Exhausted retry attempts within transaction!");
                return new TopicPartition(consumerRecord.topic() + ".DLT", 0);
            });

        // 2. 3 Retries, spaced 2 seconds apart
        FixedBackOff backOffStrategy = new FixedBackOff(2000L, 3);

        // 3. 🚀 THE TRANSACTIONAL UPGRADE: Use DefaultErrorHandler
        // When a TransactionManager is active, DefaultErrorHandler automatically detects it
        // and clears any uncommitted message batches from the transaction context on rollback!
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(dlqRecoverer, backOffStrategy);
        
        // Let's add a log interceptor to clearly see the rollbacks happening in the console
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("⚠️ Transaction rollback initiated. Retrying stream chunk. Attempt #{}", deliveryAttempt);
        });

        return errorHandler;
    }
}