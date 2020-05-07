package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.MessageHistory;

@Repository
@Component
public interface MessageHistoryDao extends JpaRepository<MessageHistory, Long> {

    MessageHistory findFirstByTypeOrderByDateDesc(String type);

}
