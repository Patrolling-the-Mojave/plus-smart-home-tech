package ru.yandex.practicum.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.aggregator.starter.AggregationStarter;

@SpringBootApplication
@ConfigurationProperties
public class Aggregator {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Aggregator.class, args);
        AggregationStarter starter = applicationContext.getBean(AggregationStarter.class);
        starter.start();
    }
}
