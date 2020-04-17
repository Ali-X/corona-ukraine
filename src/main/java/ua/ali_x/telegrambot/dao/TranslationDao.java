package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.Translation;

@Repository
@Component
public interface TranslationDao extends JpaRepository<Translation, Long> {
    Translation findFirstByRus(String rusName);
}
