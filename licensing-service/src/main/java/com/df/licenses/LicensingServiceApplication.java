package com.df.licenses;

import com.df.licenses.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableBinding(Sink.class)
public class LicensingServiceApplication {
  private final ServiceConfig serviceConfig;

  @Autowired
  public LicensingServiceApplication(ServiceConfig serviceConfig) {
    this.serviceConfig = serviceConfig;
  }

  @LoadBalanced
  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Sampler defaultSampler() {
    return new AlwaysSampler();
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
    jedisConnFactory.setHostName(serviceConfig.getRedisServer());
    jedisConnFactory.setPort(serviceConfig.getRedisPort());
    return jedisConnFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
  }

  public static void main(String[] args) {
    SpringApplication.run(LicensingServiceApplication.class, args);
  }
}
