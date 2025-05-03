package ru.panyukovnn.videoretellingbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetellingJob {

    @Scheduled(cron = "${retelling.scheduled-jobs.retelling.cron}", scheduler = "retellingScheduler")
    public void retell() {

    }
}
