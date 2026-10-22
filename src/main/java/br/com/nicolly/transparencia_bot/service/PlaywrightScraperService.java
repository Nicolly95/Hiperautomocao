package br.com.nicolly.transparencia_bot.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import br.com.nicolly.transparencia_bot.dto.BeneficioDTO;
import br.com.nicolly.transparencia_bot.dto.ConsultaRequestDTO;
import br.com.nicolly.transparencia_bot.dto.ConsultaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlaywrightScraperService {

  @Autowired
  private Playwright playwright;

  @Autowired
  private Browser browser;

  @Autowired
  private GoogleDriveService googleDriveService;

  @Autowired
  private GoogleSheetsService googleSheetsService;

  public synchronized ConsultaResponseDTO executaAutomocao(ConsultaRequestDTO request) {
    String idConsulta = UUID.randomUUID().toString();
    boolean isCpfOuNis = request.getTermoBusca() != null && request.getTermoBusca().matches(".*\\d+.*");

    Browser.NewContextOptions options = new Browser.NewContextOptions()
        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
        .setViewportSize(null);

    try (BrowserContext context = browser.newContext(options);
         Page page = context.newPage()) {

      page.setDefaultTimeout(60000);
      page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

      page.navigate("https://portaldatransparencia.gov.br/pessoa-fisica/busca/lista");
      page.waitForLoadState(LoadState.DOMCONTENTLOADED);
      fechaBannerCookies(page);

      Locator inputBusca = page.locator("input#termo, #termo").first();
      inputBusca.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
      inputBusca.click();
      inputBusca.clear();
      inputBusca.fill(request.getTermoBusca());

      Locator btnEnviar = page.locator("#btn-busca-lista, .card-busca button, button#btnEnviar").first();
      if (btnEnviar.isVisible()) {
        btnEnviar.click();
      } else {
        inputBusca.press("Enter");
      }

      try {
        page.locator("#resultados, .lista-resultados, div.card-resultado")
            .getByText(request.getTermoBusca(), new Locator.GetByTextOptions().setExact(false))
            .first()
            .waitFor(new Locator.WaitForOptions().setTimeout(12000));
      } catch (Exception e) {
        page.waitForTimeout(2000);
      }
      fechaBannerCookies(page);

      boolean semResultados = page.locator("text=Foram encontrados 0 resultados, text=Nenhum resultado encontrado").isVisible()
          || page.locator("#resultados a, .lista-resultados a, div.card-resultado a").count() == 0;

      if (semResultados) {
        String mensagemErro = isCpfOuNis
            ? "Não foi possível retornar os dados no tempo de resposta solicitado"
            : "Foram encontrados 0 resultados para o termo " + request.getTermoBusca();

        byte[] screenshotBytesErro = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        String base64EvidenciaErro = Base64.getEncoder().encodeToString(screenshotBytesErro);

        ConsultaResponseDTO respostaErro = ConsultaResponseDTO.builder()
            .idConsulta(idConsulta)
            .dataHoraConsulta(LocalDateTime.now())
            .status("ERRO")
            .mensagem(mensagemErro)
            .nomePessoa(null)
            .cpfOuNis(request.getTermoBusca())
            .beneficios(Collections.emptyList())
            .evidenciaBase64(base64EvidenciaErro)
            .build();

        try {
          String linkDriveErro = googleDriveService.salvarJsonNoDrive(respostaErro, idConsulta);
          googleSheetsService.registrarConsultaPágina(
              idConsulta,
              request.getTermoBusca(),
              request.getTermoBusca(),
              respostaErro.getDataHoraConsulta().toString(),
              linkDriveErro != null && !linkDriveErro.isEmpty() ? linkDriveErro : "N/A"
          );
        } catch (Exception e) {
          System.out.println("Aviso: Falha ao integrar com o Google no cenário de erro: " + e.getMessage());
        }

        return respostaErro;
      }

      Locator resultadoEspecifico = page.locator("#resultados a, .lista-resultados a, div.card-resultado a")
          .filter(new Locator.FilterOptions().setHasText(request.getTermoBusca()))
          .first();

      if (resultadoEspecifico.isVisible()) {
        resultadoEspecifico.click();
      } else {
        page.locator("#resultados a, .lista-resultados a").first().click();
      }

      page.waitForLoadState(LoadState.DOMCONTENTLOADED);
      page.waitForTimeout(2000);
      fechaBannerCookies(page);

      String nomeColetado = extraiTextoSeguro(page, "h1.titulo-pagina, .box-detalhe h2, div.conteudo h1");
      if (nomeColetado.isEmpty() || nomeColetado.equalsIgnoreCase("Pessoa Física")) {
        nomeColetado = request.getTermoBusca();
      }
      String cpfColetado = extraiTextoSeguro(page, "xpath=//*[contains(text(),'***.')]");
      String localidadeColetada = extraiTextoSeguro(page, "xpath=//*[contains(text(),' - ')]");

      List<BeneficioDTO> listaBeneficios = detalharEExtrairBeneficios(page);

      String base64Evidencia = "";
      try {
        page.waitForTimeout(1000);
        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        base64Evidencia = Base64.getEncoder().encodeToString(screenshotBytes);
      } catch (Exception e) {
        System.out.println("Erro ao tirar screenshot: " + e.getMessage());
      }

      System.out.println("Tamanho do Base64 gerado: " + base64Evidencia.length());

      ConsultaResponseDTO responseDTO = ConsultaResponseDTO.builder()
          .idConsulta(idConsulta)
          .dataHoraConsulta(LocalDateTime.now())
          .status("SUCESSO")
          .mensagem("Consulta realizada com sucesso")
          .nomePessoa(nomeColetado)
          .cpfOuNis(cpfColetado)
          .localidade(localidadeColetada)
          .beneficios(listaBeneficios)
          .evidenciaBase64(base64Evidencia)
          .build();

      String linkDrive = "";
      try {
        linkDrive = googleDriveService.salvarJsonNoDrive(responseDTO, idConsulta);
      } catch (Exception e) {
        System.out.println("Drive ignorado por cota/permissão: " + e.getMessage());
      }

      try {
        googleSheetsService.registrarConsultaPágina(
            responseDTO.getIdConsulta(),
            responseDTO.getNomePessoa(),
            responseDTO.getCpfOuNis(),
            responseDTO.getDataHoraConsulta().toString(),
            linkDrive != null && !linkDrive.isEmpty() ? linkDrive : "N/A"
        );
      } catch (Exception e) {
        System.out.println("Sheets ignorado por erro de ID/permissão: " + e.getMessage());
      }

      return responseDTO;

    } catch (PlaywrightException e) {
      String mensagemErro = isCpfOuNis
          ? "Não foi possível retornar os dados no tempo de resposta solicitado"
          : "Erro ao executar a automação no Portal da Transparência: " + e.getMessage();

      return ConsultaResponseDTO.builder()
          .idConsulta(idConsulta)
          .dataHoraConsulta(LocalDateTime.now())
          .status("ERRO")
          .mensagem(mensagemErro)
          .build();
    }
  }

  private List<BeneficioDTO> detalharEExtrairBeneficios(Page page) {
    List<BeneficioDTO> listaBeneficios = new ArrayList<>();

    try {
      try {
        Locator accordion = page.locator("*:has-text('RECEBIMENTOS DE RECURSOS')").last();
        if (accordion.isVisible()) {
          accordion.click(new Locator.ClickOptions().setForce(true));
          page.waitForTimeout(1000);
        }
      } catch (Exception ignored) {}

      Locator btnDetalhar = page.locator("a:has-text('Detalhar'), button:has-text('Detalhar')").first();

      if (btnDetalhar.isVisible()) {
        btnDetalhar.click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2500);

        fechaBannerCookies(page);

        String nomeBeneficio = "Benefício Social";
        try {
          Locator h1 = page.locator("h1, h2, .titulo-pagina").first();
          if (h1.isVisible()) {
            nomeBeneficio = h1.innerText().trim().replaceAll("\\s+", " ");
          }
        } catch (Exception ignored) {}

        Locator linhasTabela = page.locator("table tbody tr");
        int totalLinhas = linhasTabela.count();

        for (int i = 0; i < totalLinhas; i++) {
          Locator linha = linhasTabela.nth(i);
          Locator colunas = linha.locator("td");
          int qtdColunas = colunas.count();

          if (qtdColunas >= 2) {
            String periodo = colunas.nth(0).innerText().trim();
            String valor = "";

            for (int j = qtdColunas - 1; j >= 0; j--) {
              String texto = colunas.nth(j).innerText().trim();
              if (texto.matches(".*\\d+,\\d{2}.*")) {
                valor = texto;
                break;
              }
            }

            if (!periodo.isEmpty() && !valor.isEmpty()) {
              listaBeneficios.add(BeneficioDTO.builder()
                  .tipoBeneficio(nomeBeneficio)
                  .periodo(periodo)
                  .valor(valor)
                  .build());
            }
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Erro ao extrair detalhes dos benefícios: " + e.getMessage());
    }

    return listaBeneficios;
  }

  private void fechaBannerCookies(Page page) {
    try {
      Locator btnAceitar = page.locator("button:has-text('Aceitar'), button:has-text('Concordar'), .lgpd-banner button").first();
      if (btnAceitar.isVisible()) {
        btnAceitar.click();
      }
    } catch (Exception ignored) {}
  }

  private String extraiTextoSeguro(Page page, String selector) {
    try {
      Locator loc = page.locator(selector).first();
      return loc.isVisible() ? loc.innerText().trim() : "";
    } catch (Exception e) {
      return "";
    }
  }
}