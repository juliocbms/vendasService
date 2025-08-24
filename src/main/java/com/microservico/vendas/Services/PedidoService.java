package com.microservico.vendas.Services;

import com.microservico.vendas.Entities.ItemPedido;
import com.microservico.vendas.Entities.Pedido;
import com.microservico.vendas.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido salvarPedido(Pedido pedido){

        if (pedido.getItensList() != null){
            for (ItemPedido item : pedido.getItensList()){
                item.setPedido(pedido);
            }
        }
        return  pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos(){
       return pedidoRepository.findAll();
    }
}
