package com.smallstudy.event;

import com.smallstudy.event.type.LocalCacheInitializedEvent;
import com.smallstudy.event.type.MemberInitializedEvent;
import com.smallstudy.service.CategoryItemService;
import com.smallstudy.service.InterestRegionService;
import com.smallstudy.utils.MyLocalCache;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Component
@RequiredArgsConstructor
@Slf4j
public class LocalCacheInitializedEventHandler {

    private final InterestRegionService interestRegionService;
    private final CategoryItemService categoryItemService;
    private final ApplicationEventPublisher eventPublisher;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    @EventListener(LocalCacheInitializedEvent.class)
    //@EventListener(ApplicationReadyEvent.class)
    public void handler() {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("LocalCacheInitializedEventHandler");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            log.info("LocalCacheInitializedEventHandler call");
            MyLocalCache.categoryItems = categoryItemService.getCategoryItemDTOs();
            MyLocalCache.regions = interestRegionService.getRegionDTOs();
            transactionManager.commit(status);


          eventPublisher.publishEvent(new MemberInitializedEvent());
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }

    }



}
