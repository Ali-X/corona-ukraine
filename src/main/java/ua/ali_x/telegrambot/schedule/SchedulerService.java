package ua.ali_x.telegrambot.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.service.ScheduleService;

import java.util.List;
import java.util.TimeZone;

@Component
public class SchedulerService {

    @Autowired
    private ScheduleService scheduleService;
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    public SchedulerService() {
        try {
            schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void initSchedulers() {
        initStatisticSchedulers();
        initMessageHistorySchedulers();
    }

    private void initMessageHistorySchedulers() {
        try {
            String jobName = "infoCollectorJob";
            JobDetail job = JobBuilder.newJob(InfoCollectorJob.class)
                    .withIdentity(jobName, "infoCollector")
                    .usingJobData("jobName", jobName)
                    .build();


            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("infoCollectorTrigger", "infoCollector")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 7,12,22 ? * * *").inTimeZone(TimeZone.getTimeZone("Europe/Kiev")))
                    .forJob(jobName, "infoCollector")
                    .build();


            scheduler.scheduleJob(job, trigger);

            System.out.println("Job " + jobName + " is scheduled");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void initStatisticSchedulers() {
        try {
            List<Schedule> allEnabledUser = scheduleService.getAllEnabledUser();

            allEnabledUser.forEach(schedule -> {
                if (schedule.getUserChat() != null) {
                    initStatisticJob(schedule.getUserChat().getChatId(), schedule.getCron());
                }
            });


            scheduler.start();
        } catch (SchedulerException ex) {
            ex.printStackTrace();
        }
    }

    public void initStatisticJob(long chatId, String cron) {
        try {
            String jobName = "statisticJob_" + chatId;
            JobDetail job = JobBuilder.newJob(StatisticJob.class)
                    .withIdentity(jobName, "statistic")
                    .usingJobData("chatId", chatId)
                    .usingJobData("jobName", jobName)
                    .build();


            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("statisticTrigger_" + chatId, "statistic")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZone.getTimeZone("Europe/Kiev")))
                    .forJob(jobName, "statistic")
                    .build();


            scheduler.scheduleJob(job, trigger);

            System.out.println("Job " + jobName + " is scheduled");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
