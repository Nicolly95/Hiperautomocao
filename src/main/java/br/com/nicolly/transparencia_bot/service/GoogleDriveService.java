package br.com.nicolly.transparencia_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class GoogleDriveService {

  @Value("${google.drive.folder-id:}")
  private String folderId;

  private Drive getDriveService() throws Exception {
    ClassPathResource resource = new ClassPathResource("credentials.json");
    if (!resource.exists()) {
      throw new RuntimeException("Arquivo credentials.json não encontrado na pasta resources!");
    }

    try (InputStream credentialsStream = resource.getInputStream()) {
      GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
          .createScoped(Collections.singleton(DriveScopes.DRIVE));

      return new Drive.Builder(
          GoogleNetHttpTransport.newTrustedTransport(),
          GsonFactory.getDefaultInstance(),
          new HttpCredentialsAdapter(credentials))
          .setApplicationName("Transparencia Bot")
          .build();
    }
  }

  public String salvarJsonNoDrive(Object dtoResponse, String idConsulta) {
    try {
      Drive driveService = getDriveService();

      String dataHoraFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      String nomeArquivo = idConsulta + "_" + dataHoraFormatada + ".json";

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      byte[] jsonBytes = objectMapper.writeValueAsBytes(dtoResponse);

      File metadataArquivo = new File();
      metadataArquivo.setName(nomeArquivo);
      metadataArquivo.setMimeType("application/json");

      if (folderId != null && !folderId.trim().isEmpty()) {
        metadataArquivo.setParents(Collections.singletonList(folderId.trim()));
      }

      InputStreamContent mediaContent = new InputStreamContent("application/json", new ByteArrayInputStream(jsonBytes));

      File arquivoCriado = driveService.files().create(metadataArquivo, mediaContent)
          .setSupportsAllDrives(true)
          .setFields("id, webViewLink")
          .execute();

      return arquivoCriado.getWebViewLink();

    } catch (Exception e) {
      System.out.println("Aviso do Drive (Cota da Service Account): " + e.getMessage());
      return "N/A_Drive_Indisponivel";
    }
  }
}