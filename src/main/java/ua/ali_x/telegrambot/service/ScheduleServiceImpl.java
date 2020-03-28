package ua.ali_x.telegrambot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.ali_x.telegrambot.dao.ScheduleDao;
import ua.ali_x.telegrambot.model.Schedule;

import java.util.List;

@Service
@Transactional
@Component
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;


    @Override
    public List<Schedule> getAllEnabledUser() {
        return scheduleDao.findAllByEnabledTrue();
    }
}
