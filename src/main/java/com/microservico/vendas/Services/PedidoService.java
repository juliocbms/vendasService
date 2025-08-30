package com.microservico.vendas.Services;

import com.microservico.vendas.Config.Security.Service.AuthenticationService;
import com.microservico.vendas.Entities.ItemPedido;
import com.microservico.vendas.Entities.Pedido;
import com.microservico.vendas.Entities.Status;
import com.microservico.vendas.Repository.PedidoRepository;
import com.microservico.vendas.Services.exceptions.DatabaseException;
import com.microservico.vendas.Services.exceptions.EstoqueInsuficienteException;
import com.microservico.vendas.Services.exceptions.ResourceNotFoundException;
import com.microservico.vendas.clients.EstoqueCliente;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EstoqueCliente estoqueCliente;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${broker.exchange.vendas.name}")
    private String exchangeVendas;


    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        BigDecimal valorTotalDoPedido = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItensList()) {
            try {
                System.out.println("Validando estoque para o produto ID: " + item.getProdutoId() + ", Quantidade: " + item.getQuantidade());
                estoqueCliente.validarDisponibilidade(
                        item.getProdutoId(),
                        item.getQuantidade()
                );

                System.out.println("Buscando preço para o produto ID: " + item.getProdutoId());
                ResponseEntity<BigDecimal> responsePreco = estoqueCliente.getPriceById(item.getProdutoId());
                BigDecimal precoUnitario = responsePreco.getBody();

                BigDecimal subtotalItem = precoUnitario.multiply(BigDecimal.valueOf(item.getQuantidade()));
                item.setSubtotal(subtotalItem);
                valorTotalDoPedido = valorTotalDoPedido.add(subtotalItem);
                item.setPedido(pedido);

            } catch (FeignException e) {
                if (e.status() == 404) {
                    throw new ResourceNotFoundException("O produto com ID " + item.getProdutoId() + " não foi encontrado.");
                } else if (e.status() == 400) {
                    throw new EstoqueInsuficienteException("Estoque insuficiente para o produto com ID " + item.getProdutoId());
                } else {
                    throw new DatabaseException("Ocorreu um erro na comunicação com o serviço de estoque para o produto ID " + item.getProdutoId());
                }
            }
        }
        pedido.setValorTotal(valorTotalDoPedido);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(Status.PENDENTE);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        rabbitTemplate.convertAndSend(exchangeVendas, "pedido.criado", pedidoSalvo);
        return pedidoSalvo;
    }

    public List<Pedido> listarPedidos(){
       return pedidoRepository.findAll();
    }

    public List<Pedido> findPedidosDoUsuarioLogado() {
        UUID usuarioId = authenticationService.getAuthenticatedUserId();
        return pedidoRepository.findByClientId(usuarioId);
    }

    public Optional<Pedido> findById(Long id){
        return pedidoRepository.findById(id);
    }

    public void atualizarStatusPedido(Long id, Status status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        pedido.setId(id);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
    }
}
