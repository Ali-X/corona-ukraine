package ua.ali_x.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ua.ali_x.telegrambot.model.Statistic;

import java.util.Date;

@Repository
@Component
public interface StatisticDao extends JpaRepository<Statistic, Long> {

    Statistic findFirstByOrderByDateDesc();

}
