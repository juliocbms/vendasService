package com.microservico.vendas.Config.RabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ {

    @Value("${broker.exchange.vendas.name}")
    private String exchangeVendas;


    @Value("${broker.queue.vendas.confirmacao.name}")
    private String queueVendasConfirmacao;


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange vendasExchange() {
        return new TopicExchange(exchangeVendas);
    }

    @Bean
    public Queue vendasConfirmacaoQueue() {
        return new Queue(queueVendasConfirmacao, true);
    }

    @Bean
    public Binding bindingVendasConfirmacao() {
        return BindingBuilder.bind(vendasConfirmacaoQueue())
                .to(vendasExchange())
                .with("pedido.aprovado");
    }
}
