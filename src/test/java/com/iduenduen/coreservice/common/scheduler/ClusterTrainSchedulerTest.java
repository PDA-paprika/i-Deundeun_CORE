package com.iduenduen.coreservice.common.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.iduenduen.coreservice.domain.parent.service.ParentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClusterTrainSchedulerTest {

    @Mock
    private ParentService parentService;

    @InjectMocks
    private ClusterTrainScheduler scheduler;

    @Test
    void scheduleTrain_호출시_train_1회_실행() {
        scheduler.scheduleTrain();

        verify(parentService, times(1)).train();
    }
}
