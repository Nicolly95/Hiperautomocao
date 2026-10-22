package br.com.nicolly.transparencia_bot.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class PlaywrightConfig {

  @Bean(destroyMethod = "close")
  public Playwright playwright() {
    return Playwright.create();
  }

  @Bean(destroyMethod = "close")
  public Browser browser(Playwright playwright) {
    return playwright.chromium().launch(
      new BrowserType.LaunchOptions()
          .setHeadless(false)
          .setArgs(Arrays.asList(
              "--disable-blink-features=AutomationControlled",
              "--start-maximized"
          ))
    );
  }

}
