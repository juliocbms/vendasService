package com.microservico.vendas.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID clientId;
    private LocalDateTime dataPedido;
    private Status status;
    private Double valorTotal;
    private List<Itens> itensList;
}
