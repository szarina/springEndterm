package kz.kbtu.quartz.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;

import jakarta.annotation.PostConstruct;
import kz.kbtu.quartz.config.AutoWiringSpringBeanJobFactory;
import kz.kbtu.quartz.jobs.SampleJob;
import kz.kbtu.quartz.jobs.SecondJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

@Configuration
@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
public class QrtzScheduler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("Hello world from Quartz...");
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(@Qualifier("jobDetail") JobDetail job,
                               @Qualifier("secondJobDetail") JobDetail secondJobDetail,
                               @Qualifier("trigger") Trigger trigger,
                               @Qualifier("secondJobTrigger")Trigger secondJobTrigger,
                              SchedulerFactoryBean factory) throws SchedulerException {
        logger.debug("Getting a handle to the Scheduler");
        Scheduler scheduler = factory.getScheduler();
        

        scheduler.scheduleJob(job, trigger);
        scheduler.scheduleJob(secondJobDetail,secondJobTrigger);

        logger.debug("Starting Scheduler threads");
        scheduler.start();
        return scheduler;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(springBeanJobFactory());
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    @Qualifier("jobDetail")
    public JobDetail jobDetail() {

        return newJob().ofType(SampleJob.class).storeDurably().
                withIdentity(JobKey.jobKey("Qrtz_Job_Detail")).
                withDescription("Invoke Sample Job service...").
                build();
    }

    @Bean
    @Qualifier("trigger")
    public Trigger trigger(@Qualifier("jobDetail") JobDetail job) {

        int frequencyInSec = 60;
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);

        return newTrigger().forJob(job).withIdentity(TriggerKey.
                triggerKey("Qrtz_Trigger")).
                withDescription("Sample trigger").
                withSchedule(simpleSchedule().withIntervalInSeconds(frequencyInSec).
                        repeatForever()).build();
    }

    @Bean
    @Qualifier("secondJobDetail")
    public JobDetail secondJobDetail() {
        return newJob().ofType(SecondJob.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Second_Job_Detail"))
                .withDescription("Invoke Second Job service...")
                .build();
    }

    @Bean
    @Qualifier("secondJobTrigger")
    public Trigger secondJobTrigger(@Qualifier("secondJobDetail") JobDetail secondJobDetail) {
        int frequencyInSec = 120;
        logger.info("Configuring trigger for Second Job to fire every {} seconds", frequencyInSec);
        return newTrigger().forJob(secondJobDetail)
                .withIdentity(TriggerKey.triggerKey("Second_Job_Trigger"))
                .withDescription("Trigger for Second Job")
                // After 3 mins
                .startAt(DateBuilder.futureDate(3, DateBuilder.IntervalUnit.MINUTE))
                .withSchedule(simpleSchedule().withIntervalInSeconds(frequencyInSec)
                        .repeatForever())
                .build();

    }
}