package kz.kbtu.quartz.jobs;

import kz.kbtu.quartz.service.SampleJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class SampleJob implements Job {



    @Autowired
    private SampleJobService jobService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
