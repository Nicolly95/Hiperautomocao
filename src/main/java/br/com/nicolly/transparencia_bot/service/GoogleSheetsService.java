package br.com.nicolly.transparencia_bot.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

  @Value("${google.sheets.spreadsheet-id}")
  private String spreadsheetId;

  private Sheets getSheetsService() throws Exception {
    ClassPathResource resource = new ClassPathResource("credentials.json");
    if (!resource.exists()) {
      throw new RuntimeException("Arquivo credentials.json não encontrado na pasta resources!");
    }

    try (InputStream credentialsStream = resource.getInputStream()) {
      GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
          .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

      return new Sheets.Builder(
          GoogleNetHttpTransport.newTrustedTransport(),
          GsonFactory.getDefaultInstance(),
          new HttpCredentialsAdapter(credentials))
          .setApplicationName("Transparencia Bot")
          .build();
    }
  }

  public void registrarConsultaPágina(String idConsulta, String nome, String cpf, String dataHora, String linkDrive) {
    if (spreadsheetId == null || spreadsheetId.trim().isEmpty()) {
      System.out.println("ID da planilha não configurado no application.properties.");
      return;
    }

    try {
      Sheets sheetsService = getSheetsService();

      List<Object> linha = Arrays.asList(
          idConsulta,
          nome != null ? nome : "",
          cpf != null ? cpf : "",
          dataHora != null ? dataHora : "",
          linkDrive != null ? linkDrive : ""
      );

      ValueRange body = new ValueRange()
          .setValues(Collections.singletonList(linha));

      sheetsService.spreadsheets().values()
          .append(spreadsheetId, "Sheet1!A1", body)
          .setValueInputOption("USER_ENTERED")
          .execute();

      System.out.println("Registro gravado na planilha Google Sheets com sucesso!");

    } catch (Exception e) {
      System.err.println("ERRO REAL DO SHEETS: ");
      e.printStackTrace();
    }
  }
}