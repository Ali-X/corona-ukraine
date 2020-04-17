package ua.ali_x.telegrambot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.ali_x.telegrambot.dao.TranslationDao;
import ua.ali_x.telegrambot.model.Translation;
@Service
@Transactional
@Component
public class TranslationServiceImpl implements TranslationService {
    @Autowired
    private TranslationDao translationDao;

    @Override
    public String findUkrByRus(String rusName) {
        Translation obj = translationDao.findFirstByRus(rusName);
        return obj != null ? obj.getUkr() : rusName;
    }
}
