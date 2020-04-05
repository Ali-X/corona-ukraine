package ua.ali_x.telegrambot.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class QuarantineService {

    @Value("${finalQuarantineDate}")
    String finalQuarantineDate;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getDaysLeftMessage() {
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yyyy");

            Date endDate = myFormat.parse(finalQuarantineDate);
            Date startDay = new Date();

            long diffMs = endDate.getTime() - startDay.getTime();
            long diffDays = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);

            if (diffDays > 0) {
                return String.format(messageTemplateDao.findFirstByCode("quarantine_left").getMessage(), diffDays);
            } else {
                return messageTemplateDao.findFirstByCode("quaratine_finished").getMessage();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }
}
