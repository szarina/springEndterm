package kz.kbtu.quartz.controller;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/pause")
    public String pauseJob() {
        try {
            scheduler.pauseJob(JobKey.jobKey("Second_Job_Detail"));
            return "Job paused successfully";
        } catch (SchedulerException e) {
            return "Error pausing job: " + e.getMessage();
        }
    }

    @PostMapping("/resume")
    public String resumeJob() {
        try {
            scheduler.resumeJob(JobKey.jobKey("Second_Job_Detail"));
            return "Job resumed successfully";
        } catch (SchedulerException e) {
            return "Error resuming job: " + e.getMessage();
        }
    }
}