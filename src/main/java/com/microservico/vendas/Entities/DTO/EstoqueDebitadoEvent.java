package com.microservico.vendas.Entities.DTO;

import java.util.UUID;

public class EstoqueDebitadoEvent {
    private Long pedidoId;
    private UUID clientId;

    public EstoqueDebitadoEvent() {
    }

    public EstoqueDebitadoEvent(Long pedidoId, UUID clientId) {
        this.pedidoId = pedidoId;
        this.clientId = clientId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
