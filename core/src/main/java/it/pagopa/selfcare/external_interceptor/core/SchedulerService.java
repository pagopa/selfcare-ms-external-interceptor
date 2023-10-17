package it.pagopa.selfcare.external_interceptor.core;

import java.util.Optional;

public interface SchedulerService {
    void startScheduler(Optional<Integer> size);
}
