package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.UserChat;

@Repository
@Component
public interface UserChatDao extends JpaRepository<UserChat, Long> {
    UserChat findFirstByChatId(long chatId);
}
