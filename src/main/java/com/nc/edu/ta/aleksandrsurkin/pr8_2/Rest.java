package com.nc.edu.ta.aleksandrsurkin.pr8_2;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Rest {

    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        System.out.println(headerValue(xmlPath));
        System.out.println("==============================XML Parser===============================");
        xmlParser(xmlPath);
        System.out.println("==============================Json Parser==============================");
        jsonParser(jsonPath);
    }

    public static XPathFactory xPathFactory;
    public static Document doc;
    public static DocumentContext documentContext;
    public static String xmlPath = "http://www.mocky.io/v2/5bebe91f3300008500fbc0e3";
    public static String jsonPath = "http://www.mocky.io/v2/5bed52fd3300004c00a2959d";

    /**
     * This method is create get-resonse from URL
     * @param urlGet takes String value of URL-address
     * @return String information of gotten URL address
     */
    public static String httpClient(String urlGet) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(urlGet);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is return String value of header
     * @param pathOfUrl input String URL-adress
     * @return value of header
     * @throws IOException
     */
    public static String headerValue(String pathOfUrl) throws IOException {
        URL obj = new URL(pathOfUrl);
        URLConnection connection = obj.openConnection();
        String valueOfheader = connection.getHeaderField("X-Ta-Course-Example-Header");
        return "Значение заголовка по запрашиваемому ключу: " + valueOfheader;
    }

    /**
     * This method is create XML Xpath parser and execute another methods with parser
     * @param pathXmlFile input String URL of XML file
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws SAXException
     */
    public static void xmlParser(String pathXmlFile) throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {
        //Парсер XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(pathXmlFile);
        xPathFactory = XPathFactory.newInstance();
        xmlAllFood();
        xmlAllCalloriesFood();
        xmlPrice();
    }

    /**
     * This method is create a parser for Json file
     * @param pathJsonFile input String Json file
     */
    public static void jsonParser(String pathJsonFile) {
        //парсер
        String json = httpClient(pathJsonFile);
        documentContext = JsonPath.parse(json);
        jsonAllNames();
        jsonPathCall();
        jsonPathPrice700();
        jsonPathMaxNumberArray();
    }

    /**
     * This method is print a list of all food names
     */
    public static void jsonAllNames() {
        //список всех блюд
        JsonPath jsonPathName = JsonPath.compile("$.breakfast_menu.food[*].name");
        Object jsonListName = documentContext.read(jsonPathName);
        System.out.println("Список всех блюд: ");
        System.out.println(jsonListName.toString());
        System.out.println();
    }

    /**
     * This method is return food names which have maximum calories
     */
    public static void jsonPathCall() {
        //названия блюд с макс кол-вом калорий
        JsonPath jsonPathCall = JsonPath.compile("$..food[?(@.calories==$..calories.max())].name");
        Object jsonListCall = documentContext.read(jsonPathCall);
        System.out.println("Названия блюд с максимальным количеством калорий: ");
        System.out.println(jsonListCall);
        System.out.println();
    }

    /**
     * This method is print food prices which have calories less than 700
     */
    public static void jsonPathPrice700() {
        //цены блюд с кол-вом калорий меньше 700
        JsonPath jsonPathPrice = JsonPath.compile("$..food[?(@.calories<700)].price");
        Object jsonListPrice = documentContext.read(jsonPathPrice);
        System.out.println("Цены блюд, количество калорий которых меньше 700: ");
        System.out.println(jsonListPrice);
        System.out.println();
    }

    /**
     * This method is print max number of array
     */
    public static void jsonPathMaxNumberArray() {
        //макс число из массива
        JsonPath jsonPathMaxNumb = JsonPath.compile("$..numbers[*].max()");
        Object jsonListMaxNumb = documentContext.read(jsonPathMaxNumb);
        System.out.println("Максимальное число из массива: ");
        System.out.println(jsonListMaxNumb);
    }

    /**
     * This method is print a list of all food names
     * @throws XPathExpressionException
     */
    public static void xmlAllFood() throws XPathExpressionException {
        //Названия всех блюд
        XPath xpathName = xPathFactory.newXPath();
        XPathExpression xPathExpressionName = xpathName.compile("//name/text()");
        Object resultName = xPathExpressionName.evaluate(doc, XPathConstants.NODESET);
        NodeList nodesName = (NodeList) resultName;
        System.out.println("Список всех блюд: ");
        for (int i = 0; i < nodesName.getLength(); i++) {
            System.out.println(nodesName.item(i).getNodeValue());
        }
    }

    /**
     * This method is return food names which have maximum calories
     * @throws XPathExpressionException
     */
    public static void xmlAllCalloriesFood() throws XPathExpressionException {
        //Блюдо с наибольшим количеством калорий
        XPath xpathCallories = xPathFactory.newXPath();
        XPathExpression xPathExpressionCall = xpathCallories.compile("descendant::food[not(descendant::calories < //calories)]/name/text()");
        Object resultCall = xPathExpressionCall.evaluate(doc, XPathConstants.NODESET);
        NodeList nodesCall = (NodeList) resultCall;
        System.out.println();
        System.out.println("Имя блюда с наибольшим количеством калорий: ");
        for (int i = 0; i < nodesCall.getLength(); i++) {
            System.out.println(nodesCall.item(i).getNodeValue());
        }
    }

    /**
     * This method is print food prices which have calories less than 700
     * @throws XPathExpressionException
     */
    public static void xmlPrice() throws XPathExpressionException {
        //Вывести цену для блюд, калорийность которых меньше 700
        XPath xpathPrice = xPathFactory.newXPath();
        XPathExpression xPathExpressionPrice = xpathPrice.compile("descendant::food[descendant::calories < '700']/price/text()");
        Object resultPrice = xPathExpressionPrice.evaluate(doc, XPathConstants.NODESET);
        NodeList nodesPrice = (NodeList) resultPrice;
        System.out.println();
        System.out.println("Цены блюд, калорийность которых меньше 700: ");
        for (int i = 0; i < nodesPrice.getLength(); i++) {
            System.out.println(nodesPrice.item(i).getNodeValue());
        }
    }
}
