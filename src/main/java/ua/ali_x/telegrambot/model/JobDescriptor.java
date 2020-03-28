package ua.ali_x.telegrambot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JobDescriptor {

    private String messageId;
    private String cron;

}
