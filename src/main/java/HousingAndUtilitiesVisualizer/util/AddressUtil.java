package HousingAndUtilitiesVisualizer.util;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;

@Component
public class AddressUtil {
    Logger logger = LogManager.getLogger(AddressUtil.class);
    private final String SEARCH_URL = "https://www.reformagkh.ru/search/houses?query=";
    private final String BASE_URL = "https://www.reformagkh.ru";

    private String formatAddressInput(String address) throws UnsupportedEncodingException {
        return java.net.URLEncoder.encode(address, "UTF-8");
    }

    private Document getDocumentDynamic(String url) {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptTimeout(10000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(10000);

        HtmlPage myPage = null;
        try {
            myPage = webClient.getPage(url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e, e.getStackTrace());
        }

        Document doc = Jsoup.parse(myPage.asXml());
        return doc;
    }

    private Document getDocument(String url) {
        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url), 30000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return doc;
    }

    public HashMap<String, String> findAllMatchingAddresses(String addressToSearch) {

        HashMap<String, String> result = new HashMap<>();

        String formattedAddress = null;
        try {
            formattedAddress = formatAddressInput(addressToSearch);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        if (formattedAddress == null) return null;

        Document addressesDocument = getDocument(SEARCH_URL + formattedAddress);


        Element searchedHousesTable = addressesDocument.select("table").first();
        Elements searchedHousesRows = searchedHousesTable.select("tr");
        for (Element houseRow : searchedHousesRows) {
            String addressStr = houseRow.select("td").select("a").text();
            String href = houseRow.select("td").select("a").attr("href");
            if (!addressStr.equals("") && !href.equals("")) result.put(addressStr, href);
        }
        return result;
    }

    private String getManagementCompanyUrl(String addressUrl) {
        Document houseDoc = getDocumentDynamic(BASE_URL + addressUrl);
        String managementCompanyUrl = houseDoc.select("body > section.p-5 > div.container.mt-5 > div > div:nth-child(2) > span.address > a").attr("href");
        return managementCompanyUrl;
    }


    public String getManagementCompanyInfo(String addressUrl) {
        StringBuilder result = new StringBuilder();
        Document mngmtCompanyDoc = getDocument(BASE_URL + getManagementCompanyUrl(addressUrl));
        Elements infoElements = mngmtCompanyDoc.selectXpath("/html/body/section[3]/div/div[2]/div[2]").first().children();

        for (Element infoElement: infoElements) {
            Element key = infoElement.getElementsByClass("col-4").first();
            Element value = infoElement.getElementsByClass("col-8").first();

            if (key != null && value != null) {
                result.append(key.text()).append(" ").append(value.text()).append("\n");
            }
        }
        return result.toString();
    }
}
