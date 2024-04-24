package kz.kbtu.quartz.jobs;
import kz.kbtu.quartz.service.SampleJobService;
import kz.kbtu.quartz.service.SecondJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class SecondJob implements Job {

    Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private SecondJobService jobService;

    public void execute(JobExecutionContext context) throws JobExecutionException {

        logger.info("Second_job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        jobService.executeSecondJob();;

        logger.info("Next second_job scheduled @ {}", context.getNextFireTime());
    }
}