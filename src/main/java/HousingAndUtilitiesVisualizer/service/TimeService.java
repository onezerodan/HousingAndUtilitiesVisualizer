package HousingAndUtilitiesVisualizer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TimeService {

    Logger log = LogManager.getLogger(TimeService.class);

    public Date getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd-MM-yyyy");
        Date date = null;
        try {
            date = formatter.parse(formatter.format(new Date()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return date;
    }

    public Date parseDateFromStr(String dateStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter.parse(dateStr);
        return date;
    }

    public String dateToStr(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy ");
        return dateFormat.format(date);
    }
}
