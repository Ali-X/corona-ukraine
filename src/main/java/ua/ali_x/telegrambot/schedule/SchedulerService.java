package ua.ali_x.telegrambot.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.service.ScheduleService;

import java.util.List;

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
        try {
            List<Schedule> allEnabledUser = scheduleService.getAllEnabledUser();

            allEnabledUser.forEach(schedule -> {
                initStatisticJob(schedule.getUserChat().getChatId(), schedule.getCron());
            });


            scheduler.start();
        } catch (SchedulerException ex) {
            ex.printStackTrace();
        }
    }

    private void initStatisticJob(long chatId, String cron) {
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
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .forJob(jobName, "statistic")
                    .build();


            scheduler.scheduleJob(job, trigger);

            System.out.println("Job " + jobName + " is scheduled");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
