package org.tgbot.tgbot.configuration;


import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.tgbot.tgbot.bot.ExchangeRatesBot;

@Configuration
public class ExchangeRatesBotConfiguration {
  @Bean
  public TelegramBotsApi telegramBotsApi(ExchangeRatesBot exchangeRatesBot) {
    try {
      var api = new TelegramBotsApi(DefaultBotSession.class);
      api.registerBot(exchangeRatesBot);
      return api;
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient();
  }
}