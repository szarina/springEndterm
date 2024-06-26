package kz.kbtu.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SecondJobService {
    public static final long EXECUTION_TIME = 5000L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AtomicInteger count = new AtomicInteger();

    public void executeSecondJob() {


        try {
            Thread.sleep(EXECUTION_TIME);
        } catch (InterruptedException e) {
            logger.error("Error while executing second_ job", e);
        } finally {
            count.incrementAndGet();
            logger.info("Sample job has finished...");
        }
    }



    public int getNumberOfInvocations() {
        return count.get();
    }
}
