package ua.ali_x.telegrambot.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageHistoryDao;
import ua.ali_x.telegrambot.model.MessageHistory;
import ua.ali_x.telegrambot.service.course.CourseService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;
import ua.ali_x.telegrambot.utils.DateUtils;

@Component
public class InfoCollectorJob implements Job {

    public static InfoCollectorJob instance;

    @Autowired
    private MessageHistoryDao messageHistoryDao;

    @Autowired
    @Qualifier("exchangeArchiveCourseNBU")
    private CourseService courseServiceNBU;

    @Autowired
    @Qualifier("exchangeCoursePB")
    private CourseService courseServicePB;

    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService statisticServiceUA;

    @Autowired
    private DateUtils dateUtils;

    public InfoCollectorJob() {
        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        extractStatisticUA();
        extractCourseNBU();
        extractCoursePB();

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }

    private void extractStatisticUA() {
        String statistics = instance.statisticServiceUA.getStatistics();
        MessageHistory history = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic");

        if (history == null || !StringUtils.equals(history.getMessage(), statistics)) {
            MessageHistory newHistory = new MessageHistory();
            newHistory.setDate(instance.dateUtils.getNow());
            newHistory.setMessage(statistics);
            newHistory.setType("statistic");
            instance.messageHistoryDao.save(newHistory);
        }
    }

    private void extractCourseNBU() {
        String course = instance.courseServiceNBU.getCourse();
        MessageHistory history = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("courseNBU");

        if (history == null || !StringUtils.equals(history.getMessage(), course)) {
            MessageHistory newHistory = new MessageHistory();
            newHistory.setDate(instance.dateUtils.getNow());
            newHistory.setMessage(course);
            newHistory.setType("courseNBU");
            instance.messageHistoryDao.save(newHistory);
        }
    }

    private void extractCoursePB() {
        String course = instance.courseServicePB.getCourse();
        MessageHistory history = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("coursePB");

        if (history == null || !StringUtils.equals(history.getMessage(), course)) {
            MessageHistory newHistory = new MessageHistory();
            newHistory.setDate(instance.dateUtils.getNow());
            newHistory.setMessage(course);
            newHistory.setType("coursePB");
            instance.messageHistoryDao.save(newHistory);
        }
    }
}
