package ua.ali_x.telegrambot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.ali_x.telegrambot.service.UpdateService;

@RestController
public class MainController {

    private final String DEFAULT_MESSAGE = "Привіт! Ми оновилися. Клікай /start";
    @Autowired
    private UpdateService updateService;

    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam(value = "to", defaultValue = "0") String to,
                              @RequestParam(value = "msg", defaultValue = DEFAULT_MESSAGE) String msg) {

        return updateService.sendUpdateMessage(Long.parseLong(to), msg) ? "Success!" : "Fault!";
    }
}