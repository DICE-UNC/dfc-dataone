/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.irods.jargon.dataone.events.EventsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Master flow core
 * 
 * @author mcc
 *
 */

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
@ImportResource({ "/event-dao-hibernate-spring.cfg.xml", "/event-service-beans.xml" })
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * 
	 */
	public Application() {
	}

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = new SpringApplication(Application.class).run(args);
		System.out.println("Hit Enter to terminate");
		System.in.read();
		ctx.close();
	}

	@Bean
	public MessageChannel amqpInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer,
			@Qualifier("amqpInputChannel") MessageChannel channel) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
		adapter.setOutputChannel(channel);
		return adapter;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames("irods.dataone.logger");
		container.setConcurrentConsumers(2);
		// ...
		return container;
	}

	@Bean
	@ServiceActivator(inputChannel = "amqpInputChannel")
	public MessageHandler handler(AccessLogDAO accessLogDAO) {
		return new MessageHandler() {

			@Override
			@Transactional(rollbackFor = { MessagingException.class }, propagation = Propagation.REQUIRED)

			public void handleMessage(Message<?> message) throws MessagingException {
				EventConverterUtil util = new EventConverterUtil();
				LoggingEvent event;
				try {
					event = util.loggingEventFromPayload((byte[]) message.getPayload());
					log.info("event:{}", event);
					AccessLog accessLog = new AccessLog();
					accessLog.setIrodsPath(event.getPath());
					accessLog.setDateAdded(util.dateFromTimestamp(event.getTimestamp()));
					accessLog.setEvent(EventsEnum.READ);
					accessLog.setNodeIdentifier("foo");
					accessLog.setSubject(event.getAuthor().getName());
					log.info("access log to write:{}", accessLog);
					accessLogDAO.save(accessLog);
					log.info("saved!");
				} catch (EventLoggingException e) {
					log.error("error converting message", e);
					throw new MessagingException("error converting message", e);
				}
				log.info("msg:{}", event);
			}

		};
	}

}
