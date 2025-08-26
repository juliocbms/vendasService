package com.microservico.vendas.Consumer;


import com.microservico.vendas.Entities.DTO.EstoqueDebitadoEvent;
import com.microservico.vendas.Entities.Status;
import com.microservico.vendas.Services.PedidoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacaoEstoqueConsumer {

    @Autowired
    private PedidoService pedidoService;

    @RabbitListener(queues = "${broker.queue.vendas.confirmacao.name}")
    public void listenerConfirmacaoEstoqueQueue(@Payload EstoqueDebitadoEvent event) {
        System.out.println("Recebida confirmação de estoque para o pedido ID: " + event.getPedidoId());
        pedidoService.atualizarStatusPedido(event.getPedidoId(), Status.CONCLUIDO);
    }
}
