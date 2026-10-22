package br.com.nicolly.transparencia_bot.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

  @GetMapping
  @Operation(summary = "Recupera dados para o Front End")
  public String checkHealth() {
    return "API funcionando!";
  }
}