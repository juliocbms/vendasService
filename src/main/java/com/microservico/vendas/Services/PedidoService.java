package com.microservico.vendas.Services;

import com.microservico.vendas.Entities.ItemPedido;
import com.microservico.vendas.Entities.Pedido;
import com.microservico.vendas.Repository.PedidoRepository;
import com.microservico.vendas.Services.exceptions.DatabaseException;
import com.microservico.vendas.Services.exceptions.EstoqueInsuficienteException;
import com.microservico.vendas.Services.exceptions.ResourceNotFoundException;
import com.microservico.vendas.clients.EstoqueCliente;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EstoqueCliente estoqueCliente;

    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        BigDecimal valorTotalDoPedido = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItensList()) {
            try {
                System.out.println("Buscando preço para o produto ID: " + item.getProdutoId());
                ResponseEntity<BigDecimal> responsePreco = estoqueCliente.getPriceById(item.getProdutoId());
                BigDecimal precoUnitario = responsePreco.getBody();
                BigDecimal subtotalItem = precoUnitario.multiply(BigDecimal.valueOf(item.getQuantidade()));
                item.setSubtotal(subtotalItem);
                valorTotalDoPedido = valorTotalDoPedido.add(subtotalItem);

            } catch (FeignException e) {
                if (e.status() == 404) {
                    throw new ResourceNotFoundException("O produto com ID " + item.getProdutoId() + " não foi encontrado para cálculo de preço.");
                } else {
                    throw new DatabaseException("Erro ao buscar preço para o produto ID " + item.getProdutoId());
                }
            }
        }
        pedido.setValorTotal(valorTotalDoPedido);
        for (ItemPedido item : pedido.getItensList()) {
            try {
                System.out.println("Validando estoque para o produto ID: " + item.getProdutoId() + ", Quantidade: " + item.getQuantidade());
                estoqueCliente.validarDisponibilidade(
                        item.getProdutoId(),
                        item.getQuantidade()
                );
            } catch (FeignException e) {
                if (e.status() == 404) {
                    throw new EstoqueInsuficienteException("Estoque insuficiente para o produto com ID " + item.getProdutoId());
                } else {
                    throw new DatabaseException("Erro ao validar estoque para o produto ID " + item.getProdutoId());
                }
            }
        }

        for (ItemPedido item : pedido.getItensList()) {
            item.setPedido(pedido);
        }

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos(){
       return pedidoRepository.findAll();
    }
}
