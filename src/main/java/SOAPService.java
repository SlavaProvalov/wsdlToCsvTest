import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.oorsprong.websamples.ArrayOftCountryCodeAndName;
import org.oorsprong.websamples.CountryInfoServiceSoapType;
import org.oorsprong.websamples.TCountryCodeAndName;
import org.oorsprong.websamples.TCountryInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SOAPService {

    private static final String CSV_FILE = "./countries.csv";

    public static void main(String[] args) {
        toCSV();
    }

    private static CountryInfoServiceSoapType getCountryInfoServiceSoapType() {
        String soapServiceUrl = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL";

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(CountryInfoServiceSoapType.class);
        factoryBean.setAddress(soapServiceUrl);

        return (CountryInfoServiceSoapType) factoryBean.create();
    }

    private static void toCSV() {
        CountryInfoServiceSoapType serviceSoap = getCountryInfoServiceSoapType();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE))) {
            CSVPrinter printer = new CSVPrinter(writer,
                    CSVFormat.EXCEL.withHeader("ISOCode", "Name", "Capital", "Currency", "Phone Code"));

            ArrayOftCountryCodeAndName codeAndName = serviceSoap.listOfCountryNamesByCode();
            for (TCountryCodeAndName tCountryCodeAndName : codeAndName.getTCountryCodeAndName()) {
                String countryCode = tCountryCodeAndName.getSISOCode();
                String countryName = tCountryCodeAndName.getSName();
                TCountryInfo countryInfo = serviceSoap.fullCountryInfo(countryCode);
                String countryCapital = countryInfo.getSCapitalCity();
                String countryCurrencyCode = countryInfo.getSCurrencyISOCode();
                String phoneCode = countryInfo.getSPhoneCode();

                printer.printRecord(countryCode, countryName, countryCapital, countryCurrencyCode, phoneCode);

            }
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
