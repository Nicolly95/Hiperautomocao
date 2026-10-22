package br.com.nicolly.transparencia_bot.controller;

import br.com.nicolly.transparencia_bot.dto.ConsultaRequestDTO;
import br.com.nicolly.transparencia_bot.dto.ConsultaResponseDTO;
import br.com.nicolly.transparencia_bot.service.PlaywrightScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/consulta")
@Tag(name = "Consulta Robô Portal Transparência", description = "Endpoints para disparo do robô RPA e raspagem de dados")
public class ConsultaController {

  @Autowired
  private PlaywrightScraperService scraperService;

  @PostMapping
  @Operation(summary = "Executa a automação para buscar dados de Pessoa Física no Portal da Transparência")
  public ResponseEntity<ConsultaResponseDTO> executarConsulta(@Valid @RequestBody ConsultaRequestDTO request) {
    ConsultaResponseDTO response = scraperService.executaAutomocao(request);
    return ResponseEntity.ok(response);
  }
}