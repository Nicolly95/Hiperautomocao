package br.com.nicolly.transparencia_bot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ConsultaResponseDTO {

  private String idConsulta;
  private String localidade;
  private LocalDateTime dataHoraConsulta;
  private String status;
  private String mensagem;
  private String nomePessoa;
  private String cpfOuNis;
  private List<BeneficioDTO> beneficios;

  @JsonProperty("evidenciaBase64")
  private String evidenciaBase64;

  public ConsultaResponseDTO() {
  }

  public ConsultaResponseDTO(String idConsulta, String localidade, LocalDateTime dataHoraConsulta,
                             String status, String mensagem, String nomePessoa, String cpfOuNis,
                             List<BeneficioDTO> beneficios, String evidenciaBase64) {
    this.idConsulta = idConsulta;
    this.localidade = localidade;
    this.dataHoraConsulta = dataHoraConsulta;
    this.status = status;
    this.mensagem = mensagem;
    this.nomePessoa = nomePessoa;
    this.cpfOuNis = cpfOuNis;
    this.beneficios = beneficios;
    this.evidenciaBase64 = evidenciaBase64;
  }

  public static ConsultaResponseDTOBuilder builder() {
    return new ConsultaResponseDTOBuilder();
  }

  public static class ConsultaResponseDTOBuilder {
    private String idConsulta;
    private String localidade;
    private LocalDateTime dataHoraConsulta;
    private String status;
    private String mensagem;
    private String nomePessoa;
    private String cpfOuNis;
    private List<BeneficioDTO> beneficios;
    private String evidenciaBase64;

    public ConsultaResponseDTOBuilder idConsulta(String idConsulta) {
      this.idConsulta = idConsulta;
      return this;
    }

    public ConsultaResponseDTOBuilder localidade(String localidade) {
      this.localidade = localidade;
      return this;
    }

    public ConsultaResponseDTOBuilder dataHoraConsulta(LocalDateTime dataHoraConsulta) {
      this.dataHoraConsulta = dataHoraConsulta;
      return this;
    }

    public ConsultaResponseDTOBuilder status(String status) {
      this.status = status;
      return this;
    }

    public ConsultaResponseDTOBuilder mensagem(String mensagem) {
      this.mensagem = mensagem;
      return this;
    }

    public ConsultaResponseDTOBuilder nomePessoa(String nomePessoa) {
      this.nomePessoa = nomePessoa;
      return this;
    }

    public ConsultaResponseDTOBuilder cpfOuNis(String cpfOuNis) {
      this.cpfOuNis = cpfOuNis;
      return this;
    }

    public ConsultaResponseDTOBuilder beneficios(List<BeneficioDTO> beneficios) {
      this.beneficios = beneficios;
      return this;
    }

    public ConsultaResponseDTOBuilder evidenciaBase64(String evidenciaBase64) {
      this.evidenciaBase64 = evidenciaBase64;
      return this;
    }

    public ConsultaResponseDTO build() {
      return new ConsultaResponseDTO(idConsulta, localidade, dataHoraConsulta, status,
          mensagem, nomePessoa, cpfOuNis, beneficios, evidenciaBase64);
    }
  }

  public String getIdConsulta() { return idConsulta; }
  public void setIdConsulta(String idConsulta) { this.idConsulta = idConsulta; }

  public String getLocalidade() { return localidade; }
  public void setLocalidade(String localidade) { this.localidade = localidade; }

  public LocalDateTime getDataHoraConsulta() { return dataHoraConsulta; }
  public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) { this.dataHoraConsulta = dataHoraConsulta; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public String getMensagem() { return mensagem; }
  public void setMensagem(String mensagem) { this.mensagem = mensagem; }

  public String getNomePessoa() { return nomePessoa; }
  public void setNomePessoa(String nomePessoa) { this.nomePessoa = nomePessoa; }

  public String getCpfOuNis() { return cpfOuNis; }
  public void setCpfOuNis(String cpfOuNis) { this.cpfOuNis = cpfOuNis; }

  public List<BeneficioDTO> getBeneficios() { return beneficios; }
  public void setBeneficios(List<BeneficioDTO> beneficios) { this.beneficios = beneficios; }

  public String getEvidenciaBase64() { return evidenciaBase64; }
  public void setEvidenciaBase64(String evidenciaBase64) { this.evidenciaBase64 = evidenciaBase64; }
}
