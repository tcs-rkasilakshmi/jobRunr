package com.jobrunr.order.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.filters.ElectStateFilter;
import org.jobrunr.jobs.states.FailedState;
import org.jobrunr.jobs.states.JobState;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.exceptions.JobNotFoundException;
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static java.time.Instant.now;
import static org.jobrunr.jobs.states.StateName.FAILED_STATES;

@Component
@Slf4j
public class SchedulerConfig {

    @Autowired
    public ApplicationContext context;

    public static final int numberOfRetries = 10;

    @Bean
    public JobScheduler getJobScheduler(){
        return JobRunr.configure()
                .useJobActivator(context::getBean)
                .useStorageProvider(new PostgresStorageProvider(getDataSource()))
                .withJobFilter(getRetryFilter())
                .useBackgroundJobServer(10)
                .useDashboard(8001)
                .initialize()
                .getJobScheduler();
    }

    private ElectStateFilter getRetryFilter() {
        return (Job job, JobState newState) -> {
            FeignException exception = (FeignException)((FailedState) newState).getException();
            if (!String.valueOf(exception.status()).startsWith("4") || isNotFailed(newState) || isJobNotFoundException(newState) || isProblematicExceptionAndMustNotRetry(newState) || maxAmountOfRetriesReached(job))
                return;

            job.scheduleAt(now().plusSeconds(3), String.format("Retry %d of %d", getFailureCount(job), getMaxNumberOfRetries(job)));
        };
    }

    private DataSource getDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("preston");
        return new HikariDataSource(config);
    }

    private static boolean isJobNotFoundException(JobState newState) {
        if (newState instanceof FailedState) {
            return ((FailedState) newState).getException() instanceof JobNotFoundException;
        }
        return false;
    }

    private static boolean isProblematicExceptionAndMustNotRetry(JobState newState) {
        if (newState instanceof FailedState) {
            return ((FailedState) newState).mustNotRetry();
        }
        return false;
    }

    private static boolean isNotFailed(JobState newState) {
        return !(newState instanceof FailedState);
    }

    private int getMaxNumberOfRetries(Job job) {
        if (job.getAmountOfRetries() != null) return job.getAmountOfRetries();
        return numberOfRetries;
    }

    private long getFailureCount(Job job) {
        return job.getJobStates().stream().filter(FAILED_STATES).count();
    }

    private boolean maxAmountOfRetriesReached(Job job) {
        return getFailureCount(job) > getMaxNumberOfRetries(job);
    }
}
