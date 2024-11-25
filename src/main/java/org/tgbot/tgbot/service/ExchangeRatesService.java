package org.tgbot.tgbot.service;

public interface ExchangeRatesService {
  String getUSDExchangeRate() throws  SecurityException;
  String getEURExchangeRate() throws  SecurityException;
}
