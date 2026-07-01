package joney.board.common.outboxmessagerelay;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
// 트랜잭션이 끝나면 카프카에 대한 이벤트 전송을 비동기로 처리하기로 함
@Configuration
@ComponentScan("joney.board.common.outboxmessagerelay")
@EnableScheduling // 주기적으로 polling하기 위함.
public class MessageRelayConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //프로듀스 애플리케이션들이 이벤트를 전송할 수 있도록 하기 위한 카프카템플릿
    @Bean
    public KafkaTemplate<String, String> messageRelayKafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // hot-article쪽에 yml을 보면 key랑 value를 String으로 역직렬화 되어있음.
        // 그래서 동일하게 String으로 맞춰줌.
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); //데이터 유실 방지를 위해 all로 설정
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    //트랜젝션이 끝날때마다 이벤트를 비동기로 전송하기 위한 스레드풀 설정
    @Bean
    public Executor messageRelayPublishEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20); // 설정들 - 서버 스펙에 따라 변경 가능
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mr-pub-event-");
        return executor;
    }

    //아직 이벤트 전송이 완료되지 않은 것들을 위한 스레드풀
    @Bean
    public Executor messageRelayPublishPendingEventExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
