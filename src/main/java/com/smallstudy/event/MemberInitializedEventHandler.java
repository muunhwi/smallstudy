package com.smallstudy.event;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.event.type.MemberInitializedEvent;
import com.smallstudy.event.type.StudyInitializedEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Component
@RequiredArgsConstructor
@Slf4j
public class MemberInitializedEventHandler {

    @PersistenceContext
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener(MemberInitializedEvent.class)
    public void handler() {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("MemberInitializedEventHandler");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            log.info("MemberInitializedEvent call");
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            String email = "test@google.com";
            String password = "dwc02207!";
            String nickname ="admin";
            Member member = new Member(email, nickname, encoder.encode(password));
            member.setEmailValid();

            String email2 = "test2@google.com";
            String nickname2 ="admin2";
            Member member2 = new Member(email2, nickname2, encoder.encode(password));
            member2.setEmailValid();

            String email3 = "test3@google.com";
            String nickname3 ="admin3";
            Member member3 = new Member(email3, nickname3, encoder.encode(password));
            member3.setEmailValid();

            entityManager.persist(member);
            entityManager.persist(member2);
            entityManager.persist(member3);

            transactionManager.commit(status);

            eventPublisher.publishEvent(new StudyInitializedEvent());
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }


    }



}
