package com.microservico.vendas.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID clientId;
    private LocalDateTime dataPedido;
    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itensList;

    public Pedido() {
    }

    public Pedido(Long id, UUID clientId, LocalDateTime dataPedido, Status status, BigDecimal valorTotal, List<ItemPedido> itensList, String descricao) {
        this.id = id;
        this.clientId = clientId;
        this.dataPedido = dataPedido;
        this.status = status;
        this.valorTotal = valorTotal;
        this.itensList = itensList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemPedido> getItensList() {
        return itensList;
    }

    public void setItensList(List<ItemPedido> itensList) {
        this.itensList = itensList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
