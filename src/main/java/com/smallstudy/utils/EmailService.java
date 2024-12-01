package com.smallstudy.utils;


import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.error.BadRequestException;
import com.smallstudy.error.EmailSendException;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;

    @Async
    public CompletableFuture<Void> sendMail(Long memberId, String email, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("스몰 스터디 이메일 확인 메일");
            mimeMessageHelper.setText(String.format("토큰 코드 : [%s] 복사하여 입력해주세요!", token));
            mailSender.send(mimeMessage);
            log.info("Succeeded to send Email");
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.info("Failed to send Email");
            return CompletableFuture.failedFuture(new EmailSendException(memberId));
        }
    }

    @Transactional
    public void sendMail(String email, String token){
        log.info("email {} -> token : {}", email, token);
    }


    @Transactional
    public void handleEmailSendFailure(EmailSendException emailEx) {
        Optional<Member> findMember = memberRepository.findById(emailEx.getMemberId());
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        if(Objects.nonNull(member.getEmailToken())) {
           memberRepository.deleteById(member.getId()); // 임시 저장 삭제
        }

        log.info("handleEmailSendFailure call");
    }


}
