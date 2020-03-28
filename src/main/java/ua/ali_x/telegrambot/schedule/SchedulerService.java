package ua.ali_x.telegrambot.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.ScheduleService;
import ua.ali_x.telegrambot.service.StatisticService;

@Component
public class SchedulerService {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private StatisticService statisticService;

    @Value("${token}")
    private String token;

    public void initSchedulers() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            initStatisticJob(scheduler);

            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void initStatisticJob(Scheduler scheduler) {

        String statistics = statisticService.getStatistics();

        scheduleService.getAllEnabledUser().forEach(schedule -> {
            long chatId = schedule.getChatId();

            try {
                JobDetail job = JobBuilder.newJob(StatisticJob.class)
                        .withIdentity("statisticJob_" + chatId, "statistic")
                        .usingJobData("chatId", chatId)
                        .usingJobData("message", statistics)
                        .usingJobData("token", token)
                        .build();


                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("statisticTrigger_" + chatId, "statistic")
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ?"))
                        .forJob("statisticJob_" + chatId, "statistic")
                        .build();


                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
    }
}
