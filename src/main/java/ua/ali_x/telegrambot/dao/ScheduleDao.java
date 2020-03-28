package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.Schedule;

import java.util.List;

@Repository
@Component
public interface ScheduleDao extends JpaRepository<Schedule, Long> {

    Schedule findByName(String name);

    Schedule findByChatId(Long chatId);

    List<Schedule> findAllByEnabledTrue();

}
