package ua.ali_x.telegrambot.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

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
        try {
            JobDetail job = JobBuilder.newJob(StatisticJob.class)
                    .withIdentity("statisticJob", "statistic")
                    .build();


            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("statisticTrigger", "statistic")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ?"))
                    .forJob("statisticJob", "statistic")
                    .build();


            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
