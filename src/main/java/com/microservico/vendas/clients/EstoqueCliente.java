package com.microservico.vendas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "estoque-Service", url = "${estoque.service.url}")
public interface EstoqueCliente {


    @GetMapping("/products/{id}/available")
    ResponseEntity<Boolean> validarDisponibilidade(
            @PathVariable("id") Long id,

            @RequestParam("quantidade") int quantidade);

}
