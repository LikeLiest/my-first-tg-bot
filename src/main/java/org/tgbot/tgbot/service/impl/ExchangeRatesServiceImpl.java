package org.tgbot.tgbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tgbot.tgbot.client.CbrClient;
import org.tgbot.tgbot.exception.ServiceException;
import org.tgbot.tgbot.service.ExchangeRatesService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;


@Service
@RequiredArgsConstructor
public class ExchangeRatesServiceImpl implements ExchangeRatesService {
  private final CbrClient cbrClient;
  
  private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
  private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
  
  @Override
  public String getUSDExchangeRate() throws SecurityException {
    var xml = cbrClient.getCurrencyRatesXML();
    return extractCurrencyValueFromXML(xml, USD_XPATH);
  }
  
  @Override
  public String getEURExchangeRate() throws SecurityException {
    var xml = cbrClient.getCurrencyRatesXML();
    return extractCurrencyValueFromXML(xml, EUR_XPATH);
  }
  
  private static String extractCurrencyValueFromXML(String xml, String xpathExpression) {
    var source = new InputSource(new StringReader(xml));
    try {
      var xpath = XPathFactory.newInstance().newXPath();
      var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
      return xpath.evaluate(xpathExpression, document);
    } catch (XPathExpressionException e) {
      throw new ServiceException("Не удалось распарсить XML", e);
    }
  }
}
