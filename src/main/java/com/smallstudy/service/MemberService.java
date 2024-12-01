package com.smallstudy.service;


import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.dto.FileDTO;
import com.smallstudy.dto.profile_dto.ProfileDTO;
import com.smallstudy.error.EmailSendException;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.utils.EmailService;
import com.smallstudy.utils.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberService {


    private final FileService multipartFileService;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    public boolean duplicatedEmail(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);

        if(findMember.isEmpty())
            return false;

        Member member = findMember.get();
        return member.isEmailValid();
    }

    public boolean duplicatedNickname(String nickname) {
        return (memberRepository.countByNickname(nickname) > 0);
    }


    @Transactional
    public void sendEmailTokenAndTemporarySave(String email) {

        String token = UUID.randomUUID().toString().substring(0, 8);

        Optional<Member> findMember = memberRepository.findByEmail(email);
        Member member = findMember.orElseGet(() -> new Member(email, ""));

        member.setEmailToken(token);
        member.setEmailTokenReceivedAt(LocalDateTime.now());
        memberRepository.save(member);
        emailService.sendMail(email, token);
//        CompletableFuture<Void> future = emailService.sendMail(member.getId(), email, token);
//        future.handle((result, ex) -> {
//            if(Objects.nonNull(ex)) {
//                if (ex.getCause() instanceof EmailSendException emailEx) {
//                    emailService.handleEmailSendFailure(emailEx); // 예외 처리 로직
//                } else {
//                    log.error("Unexpected exception: " + ex.getMessage());
//                }
//            } else {
//                log.info("Email sent successfully");
//            }
//            return null;
//        });
    }



    @Transactional
    public boolean emailValidAndSignup(Member unverifiedMember) {

        Optional<Member> findMember = memberRepository.findByEmail(unverifiedMember.getEmail());
        if(findMember.isEmpty())
            return true;

        Member member = findMember.get();
        String token = member.getEmailToken();

        if(Objects.isNull(token))
            return true;

        if(token.equals(unverifiedMember.getEmailToken())) {
            member.copyWithoutId(unverifiedMember);
            member.setEmailValid();
            return false;
        }

        return true;
    }


    public boolean canResendEmailToken(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if(findMember.isEmpty()) return true;

        Member member = findMember.get();
        LocalDateTime tenPlus = member.getEmailTokenReceivedAt().plusMinutes(10);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(tenPlus);
    }

    @Transactional
    public Member updateMemberProfile(ProfileDTO dto) throws IOException {

        Optional<Member> findMember = memberRepository.findByEmail(dto.username);
        findMember.orElseThrow(() -> new UsernameNotFoundException("해당 하는 이메일이 존재하지 않습니다."));

        Member member = findMember.get();
        member.profileUpdate(dto.nickname, dto.message);

        MultipartFile multipartFile = dto.profileImage;
        if(!multipartFile.isEmpty()) {
            FileDTO fileDTO = multipartFileService.saveMultiPartFile(multipartFile);
            member.profileUpdateImage(fileDTO.originalName, fileDTO.uuid, fileDTO.path);
        }
        return member;
    }
}
