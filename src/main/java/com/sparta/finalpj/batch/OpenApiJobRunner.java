package com.sparta.finalpj.batch;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * 스케줄러 설정
 */
@Component
public class OpenApiJobRunner extends JobRunner {

    @Autowired
    private Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) {
        JobDetail jobDetail = buildJobDetail(OpenApiSchJob.class, "openApiJob", "batch", new HashMap());
        Trigger trigger = buildJobTrigger("0 0 15 * * ?");

        try{
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
