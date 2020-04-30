package ua.ali_x.telegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ua.ali_x.telegrambot.bot.CoronaUkraineBot;
import ua.ali_x.telegrambot.schedule.SchedulerService;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@EnableScheduling
public class TelegrambotApplication implements CommandLineRunner {

    static {
        ApiContextInitializer.init();
    }

    @Autowired
    private CoronaUkraineBot bot;

    @Autowired
    private SchedulerService schedulerService;

    public static void main(String[] args) {
        SpringApplication.run(TelegrambotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(bot);

        schedulerService.initSchedulers();
    }
}