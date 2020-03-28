package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.MessageTemplate;

@Repository
@Component
public interface MessageTemplateDao extends JpaRepository<MessageTemplate, Long> {

    MessageTemplate findFirstByCode(String code);
}
