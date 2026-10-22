package br.com.nicolly.transparencia_bot.dto;

public class BeneficioDTO {

  private String tipoBeneficio;
  private String periodo;
  private String valor;
  private String detalheAdicional;

  public BeneficioDTO() {
  }

  public BeneficioDTO(String tipoBeneficio, String periodo, String valor, String detalheAdicional) {
    this.tipoBeneficio = tipoBeneficio;
    this.periodo = periodo;
    this.valor = valor;
    this.detalheAdicional = detalheAdicional;
  }

  public static BeneficioDTOBuilder builder() {
    return new BeneficioDTOBuilder();
  }

  public static class BeneficioDTOBuilder {
    private String tipoBeneficio;
    private String periodo;
    private String valor;
    private String detalheAdicional;

    public BeneficioDTOBuilder tipoBeneficio(String tipoBeneficio) {
      this.tipoBeneficio = tipoBeneficio;
      return this;
    }

    public BeneficioDTOBuilder periodo(String periodo) {
      this.periodo = periodo;
      return this;
    }

    public BeneficioDTOBuilder valor(String valor) {
      this.valor = valor;
      return this;
    }

    public BeneficioDTOBuilder detalheAdicional(String detalheAdicional) {
      this.detalheAdicional = detalheAdicional;
      return this;
    }

    public BeneficioDTO build() {
      return new BeneficioDTO(tipoBeneficio, periodo, valor, detalheAdicional);
    }
  }

  public String getTipoBeneficio() { return tipoBeneficio; }
  public void setTipoBeneficio(String tipoBeneficio) { this.tipoBeneficio = tipoBeneficio; }

  public String getPeriodo() { return periodo; }
  public void setPeriodo(String periodo) { this.periodo = periodo; }

  public String getValor() { return valor; }
  public void setValor(String valor) { this.valor = valor; }

  public String getDetalheAdicional() { return detalheAdicional; }
  public void setDetalheAdicional(String detalheAdicional) { this.detalheAdicional = detalheAdicional; }
}