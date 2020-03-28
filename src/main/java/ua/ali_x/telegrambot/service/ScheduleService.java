package ua.ali_x.telegrambot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.ali_x.telegrambot.model.Schedule;

import java.util.List;

public interface ScheduleService {

    List<Schedule> getAllEnabledUser();

}
