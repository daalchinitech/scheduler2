package com.dc.server.scheduler2;

import com.dc.server.config.BrokerConfig;
import com.dc.server.messaging.MessagingErrorHandler;
import com.dc.server.scheduler2.config.AutowiringSpringBeanJobFactory;
import com.dc.server.scheduler2.messaging.ActiveMQMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.log4j.Log4j2;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Log4j2
@EnableScheduling
@EntityScan("com.dc.server")
@EnableJpaRepositories("com.dc.server")
@ConfigurationPropertiesScan("com.dc.server.config")
@SpringBootApplication(scanBasePackages = "com.dc.server")
public class SchedulerService2Application {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerService2Application.class, args);
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
		return new MappingJackson2HttpMessageConverter(objectMapper);
	}

	@Bean("springBeanJobFactory")
	public SpringBeanJobFactory springBeanJobFactory(
			@Autowired ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean("schedulerFactoryBean")
	public SchedulerFactoryBean schedulerFactoryBean(SpringBeanJobFactory springBeanJobFactory) {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setJobFactory(springBeanJobFactory);

		return schedulerFactory;
	}

	@Bean
	public ActiveMQConnectionFactory activemqConnectionFactory(
			@Value("${url.activemq-broker.tcp}") String address) {
		log.info("Connecting to activemq at [{}]", address);
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(address);
		factory.setClientIDPrefix(BrokerConfig.getServiceClientId("scheduler"));
		return factory;
	}

	@Bean("jmsTopicListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsTopicListenerContainerFactory(
			ActiveMQConnectionFactory activemqConnectionFactory,
			MessagingErrorHandler errorHandler) {
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(activemqConnectionFactory);
		containerFactory.setErrorHandler(errorHandler);
		containerFactory.setPubSubDomain(true);

		return containerFactory;
	}

	@Bean("jmsDurableCancelledTopicListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsDurableCancelledTopicListenerContainerFactory(
			ActiveMQConnectionFactory activemqConnectionFactory,
			MessagingErrorHandler errorHandler) {
		// need to have same clientId for reconnection
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(activemqConnectionFactory);
		containerFactory.setErrorHandler(errorHandler);
		containerFactory.setPubSubDomain(true);
		containerFactory.setSubscriptionDurable(true);
		containerFactory.setClientId(BrokerConfig.CLIENT_ID_SCHEDULER_ORDER_CANCELLED);
		containerFactory.setClientId(BrokerConfig.CLIENT_ID_SCHEDULER_ORDER_CANCELLED);

		return containerFactory;
	}

	@Bean("jmsDurablePrePlacedTopicListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsDurablePrePlacedTopicListenerContainerFactory(
			ActiveMQConnectionFactory activemqConnectionFactory,
			MessagingErrorHandler errorHandler) {
		// need to have same clientId for reconnection
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(activemqConnectionFactory);
		containerFactory.setErrorHandler(errorHandler);
		containerFactory.setPubSubDomain(true);
		containerFactory.setSubscriptionDurable(true);
		containerFactory.setClientId(BrokerConfig.CLIENT_ID_SCHEDULER_ORDER_PRE_PLACED);

		return containerFactory;
	}

	@Bean("jmsDurableMachineOutboundTopicListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsDurableMachineOutboundTopicListenerContainerFactory(
			ActiveMQConnectionFactory activemqConnectionFactory,
			MessagingErrorHandler errorHandler) {
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(activemqConnectionFactory);
		containerFactory.setErrorHandler(errorHandler);

		containerFactory.setPubSubDomain(true);
		containerFactory.setSubscriptionDurable(true);
		containerFactory.setClientId(BrokerConfig.CLIENT_ID_SCHEDULER_MACHINE_OUTBOUND);

		return containerFactory;
	}

	@Bean("jmsTopicTemplate")
	public JmsTemplate jmsTopicTemplate(
			ActiveMQConnectionFactory activemqConnectionFactory,
			ActiveMQMessageConverter messageConverter) {
		final JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(activemqConnectionFactory);
		jmsTemplate.setMessageConverter(messageConverter);
		jmsTemplate.setPubSubDomain(true);

		return jmsTemplate;
	}

	@Bean("jmsQueueTemplate")
	public JmsTemplate jmsQueueTemplate(
			ActiveMQConnectionFactory activemqConnectionFactory,
			ActiveMQMessageConverter messageConverter) {
		final JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(activemqConnectionFactory);
		jmsTemplate.setMessageConverter(messageConverter);
		jmsTemplate.setPubSubDomain(false);

		return jmsTemplate;
	}

	@Bean("redisTemplateStringJson")
	public RedisTemplate<String, Object> redisTemplateStringLong(RedisConnectionFactory connectionFactory) {
		var template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(RedisSerializer.string());
		template.setHashKeySerializer(RedisSerializer.string());
		template.setValueSerializer(RedisSerializer.json());
		template.setHashValueSerializer(RedisSerializer.json());

		return template;
	}
}
