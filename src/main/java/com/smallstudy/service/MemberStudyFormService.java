package com.smallstudy.service;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_study_form_entity.MemberStudyForm;
import com.smallstudy.domain.member_study_form_entity.MemberStudyFormQuestionTextAnswer;
import com.smallstudy.domain.member_study_form_entity.MemberStudyFormSelectedQuestionItem;
import com.smallstudy.domain.study_entity.*;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.domain.study_form_entity.StudyFormQuestion;
import com.smallstudy.domain.study_form_entity.StudyFormQuestionItem;
import com.smallstudy.dto.member_study_form_dto.MemberAnswerDTO;
import com.smallstudy.dto.member_study_form_dto.MemberStudyFormDTO;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.repo.member_study_form_repo.MemberStudyFormRepository;
import com.smallstudy.repo.study_repo.*;
import com.smallstudy.utils.JsonConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.smallstudy.domain.study_form_entity.QuestionType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberStudyFormService {

    private final StudyFormService studyFormService;
    private final MemberRepository memberRepository;
    private final StudyFormRepository studyFormRepository;
    private final MemberStudyFormRepository memberStudyFormRepository;

    @Transactional
    public void save(MemberStudyFormDTO memberStudyDTO, Long memberId) {

        Optional<Member> findMember = memberRepository.findById(memberId);
        Member member = findMember.orElseThrow(() -> new EntityNotFoundException("Member not found for ID: " + memberId));

        StudyForm studyForm = studyFormRepository.findById(memberStudyDTO.getFormId())
                .orElseThrow(() -> new EntityNotFoundException("StudyForm not found for ID: " + memberStudyDTO.getFormId()));

        Map<Long, StudyFormQuestion> questionMap = getStudyFormQuestionMap(studyForm);
        MemberStudyForm memberStudyForm = new MemberStudyForm(member, studyForm);

        List<MemberAnswerDTO> answers = memberStudyDTO.getAnswers();

        answers.forEach(answer -> {
            StudyFormQuestion question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                throw new EntityNotFoundException("Question not found for ID: " + answer.getQuestionId());
            }

            if (question.getType().equals(TEXT)) {
                MemberStudyFormQuestionTextAnswer textAnswer = new MemberStudyFormQuestionTextAnswer(
                        memberStudyForm, question, answer.getText());
                memberStudyForm.addMemberStudyFormQuestionTextAnswer(textAnswer);
            } else {
                Map<Long, StudyFormQuestionItem> itemMap = getStudyFormQuestionItemMap(question.getStudyFormQuestionItems());

                answer.getAnswerItemIds().forEach(itemId -> {
                    StudyFormQuestionItem questionItem = itemMap.get(itemId);
                    if (questionItem == null) {
                        throw new EntityNotFoundException("Question item not found for ID: " + itemId);
                    }
                    MemberStudyFormSelectedQuestionItem selectedItem =
                            new MemberStudyFormSelectedQuestionItem(memberStudyForm, question, questionItem);
                    memberStudyForm.addMemberStudyFormSelectQuestionItem(selectedItem);
                });
            }
        });

        memberStudyFormRepository.save(memberStudyForm);
    }

    @Transactional
    public String[] getJsonDataStudyFormAndAnswer(Long memberId, Long formId) {

        Optional<MemberStudyForm> findMemberStudyForm = memberStudyFormRepository.findByMemberIdAndStudyFormId(memberId, formId);
        MemberStudyForm memberStudyForm = findMemberStudyForm.orElseThrow(EntityNotFoundException::new);
        StudyForm studyForm = memberStudyForm.getStudyForm();
        Study study = studyForm.getStudy();

        StudyFormDTO dto = studyFormService.toStudyFormDTO(studyForm, study.getId());
        String studyFormData = JsonConverter.getJsonData(dto);

        MemberStudyFormDTO memberStudyDTO = new MemberStudyFormDTO(study.getId(), studyForm.getId(), memberId);

        for (StudyFormQuestion question : studyForm.getStudyFormQuestions()) {
            Long questionId = question.getId();
            if (question.getType().equals(TEXT)) {
                MemberStudyFormQuestionTextAnswer textAnswer = getQuestionTextAnswer(memberStudyForm, questionId);
                memberStudyDTO.addMemberAnswerDTO(new MemberAnswerDTO(questionId, null, textAnswer.getAnswer()));
            } else {
                List<Long> selectedAnswerIds = getSelectedAnswerIds(memberStudyForm, questionId);
                memberStudyDTO.addMemberAnswerDTO(new MemberAnswerDTO(questionId, selectedAnswerIds, null));
            }
        }

        String memberStudyFormData = JsonConverter.getJsonData(memberStudyDTO);

        return new String[]{studyFormData, memberStudyFormData};
    }

    private List<Long> getSelectedAnswerIds(MemberStudyForm memberStudyForm, Long questionId) {
        return memberStudyForm.getMemberStudyFormSelectedQuestionItems().stream()
                .filter(answer -> answer.getStudyFormQuestion().getId().equals(questionId))
                .map(answer -> answer.getStudyFormQuestionItem().getId())
                .toList();
    }
    private MemberStudyFormQuestionTextAnswer getQuestionTextAnswer(MemberStudyForm memberStudyForm, Long questionId) {
        return memberStudyForm.getMemberStudyFormQuestionTextAnswers()
                .stream()
                .filter(answer -> answer.getStudyFormQuestion().getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Text answer not found for question ID: " + questionId));
    }
    private Map<Long, StudyFormQuestion> getStudyFormQuestionMap(StudyForm studyForm) {
        return studyForm.getStudyFormQuestions().stream()
                .collect(Collectors.toMap(
                        StudyFormQuestion::getId,
                        question -> question
                ));
    }
    private Map<Long, StudyFormQuestionItem> getStudyFormQuestionItemMap(List<StudyFormQuestionItem> questionItems) {
        return questionItems
                .stream()
                .collect(Collectors.toMap(
                        StudyFormQuestionItem::getId,
                        item -> item
                ));
    }



}
