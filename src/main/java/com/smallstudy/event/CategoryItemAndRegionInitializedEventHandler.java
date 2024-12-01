package com.smallstudy.event;

import com.smallstudy.event.type.LocalCacheInitializedEvent;
import com.smallstudy.event.type.MemberInitializedEvent;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.service.CategoryItemService;
import com.smallstudy.service.InterestRegionService;
import com.smallstudy.service.StudyService;
import com.smallstudy.utils.MyLocalCache;
import com.smallstudy.utils.ParseService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;


@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryItemAndRegionInitializedEventHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final ParseService parseService;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    @EventListener(ApplicationReadyEvent.class)
    public void handler() throws IOException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("CategoryItemAndRegionInitializedEventHandler");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            log.info("CategoryItemAndRegionInitializedEventHandler call");

            parseService.parsingCategory();
            parseService.parsingRegion();
            transactionManager.commit(status);

            eventPublisher.publishEvent(new LocalCacheInitializedEvent());
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }



}
