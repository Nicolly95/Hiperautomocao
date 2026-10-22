package br.com.nicolly.transparencia_bot.dto;

import jakarta.validation.constraints.NotBlank;

public class ConsultaRequestDTO {

  @NotBlank(message = "O termo de busca é obrigatório")
  private String termoBusca;

  private Boolean filtroBeneficiarioSocial;

  public ConsultaRequestDTO() {
  }

  public ConsultaRequestDTO(String termoBusca, Boolean filtroBeneficiarioSocial) {

    this.termoBusca = termoBusca;
    this.filtroBeneficiarioSocial = filtroBeneficiarioSocial;
  }

  public String getTermoBusca() {
    return termoBusca;
  }

  public void setTermoBusca(String termoBusca) {
    this.termoBusca = termoBusca;
  }

  public Boolean getFiltroBeneficiarioSocial() {
    return filtroBeneficiarioSocial;
  }

  public void setFiltroBeneficiarioSocial(Boolean filtroBeneficiarioSocial) {
    this.filtroBeneficiarioSocial = filtroBeneficiarioSocial;
  }
}