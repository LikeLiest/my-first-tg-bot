package org.tgbot.tgbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tgbot.tgbot.exception.ServiceException;
import org.tgbot.tgbot.service.ExchangeRatesService;

import java.time.LocalDate;

@Slf4j
@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
  private static final String CHAT_BOT_NAME = "JSpringChatBot";
  
  private static final String START = "/start";
  private static final String USD = "/usd";
  private static final String EUR = "/eur";
  private static final String HELP = "/help";
  
  private final ExchangeRatesService ratesService;
  
  public ExchangeRatesBot(@Value("${bot.token}") String botToken,
                          ExchangeRatesService ratesService) {
    super(botToken);
    this.ratesService = ratesService;
  }
  
  @Override
  public void onUpdateReceived(Update update) {
    if (!update.hasMessage() || !update.getMessage().hasText()) {
      return;
    }
    
    var message = update.getMessage().getText();
    var chatId = update.getMessage().getChatId();
    var username = update.getMessage().getChat().getUserName();
    
    switch (message) {
      case START -> startCommand(chatId, username);
      case USD -> usdCommand(chatId);
      case EUR -> eurCommand(chatId);
    }
  }
  
  
  @Override
  public String getBotUsername() {
    return CHAT_BOT_NAME;
  }
  
  private void startCommand(Long chatId, String userName) {
    String text = """
       Добро пожаловать в бот, %s!
      
       Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
      
       Для этого воспользуйтесь командами:
       /usd - курс доллара
       /eur - курс евро
      
       Дополнительные команды:
       /help - получение справки
      """;
    String formattedText = String.format(text, userName);
    sendMessage(chatId, formattedText);
  }
  
  private void usdCommand(Long chatId) {
    String formattedText;
    
    try {
      var usd = ratesService.getUSDExchangeRate();
      var text = "Курс доллара на %s соcтавляет %s рублей";
      formattedText = String.format(text, LocalDate.now(), usd);
    } catch (ServiceException e) {
      log.error("Ошибка получения курса доллара", e);
      formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже";
    }
    
    sendMessage(chatId, formattedText);
  }
  
  private void eurCommand(Long chatId) {
    String formattedText;
    
    try {
      var eur = ratesService.getEURExchangeRate();
      var text = "Курс евро на %s соcтавляет %s рублей";
      formattedText = String.format(text, LocalDate.now(), eur);
    } catch (ServiceException e) {
      log.error("Ошибка получения курса евро", e);
      formattedText = "Не удалось получить текущий курс евро. Попробуйте позже";
    }
    
    sendMessage(chatId, formattedText);
  }
  
  private void sendMessage(Long chatId, String text) {
    var chatIdStr = String.valueOf(chatId);
    var sendMessage = new SendMessage(chatIdStr, text);
    
    try {
      execute(sendMessage);
    } catch (TelegramApiException e) {
      throw new ServiceException("Ошибка при отправке сообщения", e);
    }
  }
}
