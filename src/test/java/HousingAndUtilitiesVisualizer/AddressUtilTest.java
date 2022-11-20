package HousingAndUtilitiesVisualizer;
import HousingAndUtilitiesVisualizer.util.AddressUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class AddressUtilTest {
    @Autowired
    AddressUtil addressValidator;

    @Test
    void validatorTest() {
        System.out.println(addressValidator.findAllMatchingAddresses("шаумяна, 87"));
    }

    @Test
    void getManagementCompaniesTest() {
        Map<String, String> addresses = addressValidator.findAllMatchingAddresses("шаумяна, 102А");
        for (Map.Entry<String, String> stringStringEntry : addresses.entrySet()) {
            System.out.println(stringStringEntry.getKey() + " : " + stringStringEntry.getValue());
        }


    }

    @Test
    void getManagementCompanyTest() {
        System.out.println(
                addressValidator
                        .getManagementCompanyInfo("https://www.reformagkh.ru/myhouse/profile/view/8925068"));
    }
}
