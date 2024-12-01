package com.smallstudy.service;

import com.smallstudy.domain.study_entity.*;
import com.smallstudy.domain.study_form_entity.QuestionType;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.domain.study_form_entity.StudyFormQuestion;
import com.smallstudy.domain.study_form_entity.StudyFormQuestionItem;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionItemDTO;
import com.smallstudy.repo.study_repo.StudyFormRepository;
import com.smallstudy.repo.study_repo.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.smallstudy.domain.study_form_entity.QuestionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyFormService {

    private final StudyRepository studyRepository;
    private final StudyFormRepository studyFormRepository;

    @Transactional(readOnly = true)
    public Study findStudyByFormId(Long formId) {
        Optional<Study> findStudy = studyRepository.findStudyWithFormAndMemberByFormId(formId);
        Study study = findStudy.orElseThrow(EntityNotFoundException::new);

        study.getStudyForm().getStudyFormQuestions()
                .forEach(question -> question.getStudyFormQuestionItems()
                        .forEach(StudyFormQuestionItem::getId));

        return study;
    }

    @Transactional(readOnly = true)
    public StudyForm findStudyFormById(Long formId) {
        Optional<StudyForm> findForm = studyFormRepository.findById(formId);
        return findForm.orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Long save(StudyFormDTO form) {

        Optional<Study> findStudy = studyRepository.findById(form.getStudyId());
        Study study = findStudy.orElseThrow(EntityNotFoundException::new);

        StudyForm studyForm = new StudyForm(form.getTitle(), form.getDescription());

        List<StudyFormQuestion> studyFormQuestions = toStudyFormQuestionList(form);

        studyFormQuestions.forEach(studyForm::addStudyFormQuestion);

        studyFormRepository.save(studyForm);
        study.setStudyForm(studyForm);

        return study.getId();
    }

    @Transactional
    public Long editForm(StudyFormDTO form) {

        Long formId = form.getId();
        Optional<StudyForm> findForm = studyFormRepository.findStudyFormById(formId);
        StudyForm studyForm = findForm.orElseThrow(EntityNotFoundException::new);

        checkForm(form, studyForm);

        List<StudyFormQuestion> studyFormQuestions = studyForm.getStudyFormQuestions();
        Map<Long, StudyFormQuestion> questionMap   = getStudyFormQuestionMap(studyForm);
        Set<Long> newQuestionIds                   = questionDTOsToLongSet(form.questions);

        removeIfQuestionNonExist(questionMap, newQuestionIds, studyFormQuestions);

        List<StudyFormQuestionDTO> questionDTOs = form.questions;
        questionDTOs.forEach(questionDTO -> {
            if(Objects.nonNull(questionDTO.id)) {
                StudyFormQuestion studyFormQuestion = questionMap.get(questionDTO.id);

                checkQuestion(questionDTO, studyFormQuestion);
                
                List<StudyFormQuestionItem> questionItems         = studyFormQuestion.getStudyFormQuestionItems();
                Map<Long, StudyFormQuestionItem> questionItemsMap = getStudyFormQuestionItemMap(questionItems);
                Set<Long> itemDTOIds                              = ItemDTOsToLongSet(questionDTO.items);

                removeIfItemsNonExist(questionItemsMap, itemDTOIds, questionItems);

                List<StudyFormQuestionItemDTO> itemDTOs = questionDTO.items;
                itemDTOs.forEach(item -> {
                    if(Objects.nonNull(item.id)) {
                        StudyFormQuestionItem studyFormQuestionItem = questionItemsMap.get(item.id);
                        checkItem(item, studyFormQuestionItem);
                    } else {
                        StudyFormQuestionItem studyFormQuestionItem = new StudyFormQuestionItem(item.content);
                        studyFormQuestion.addQuestionItem(studyFormQuestionItem);
                    }
                });

            } else {
                StudyFormQuestion newQuestion = new StudyFormQuestion(questionDTO.title, toQuestionType(questionDTO.type));

                questionDTO.items.stream()
                        .map(item -> new StudyFormQuestionItem(item.content))
                        .forEach(newQuestion::addQuestionItem);
                studyForm.addStudyFormQuestion(newQuestion);
            }
        });

        return form.getStudyId();
    }

    @Transactional
    public void deleteForm(Long studyId) {
        Optional<Study> findStudy = studyRepository.findById(studyId);
        Study study = findStudy.orElseThrow(EntityNotFoundException::new);
        study.setStudyForm(null);
    }

    public StudyFormDTO toStudyFormDTO(StudyForm form, Long studyId) {
        List<StudyFormQuestionDTO> questionDTOList = toQuestionDTOList(form.getStudyFormQuestions());
        return new StudyFormDTO(form.getId(), form.getTitle(), form.getDescription(), questionDTOList, studyId);
    }

    private  void checkForm(StudyFormDTO form, StudyForm studyForm) {
        if(!form.getTitle().equals(studyForm.getTitle()) || !form.getDescription().equals(studyForm.getDescription())) {
            studyForm.editForm(form.getTitle(), form.getDescription());
        }
    }

    private void checkItem(StudyFormQuestionItemDTO item, StudyFormQuestionItem studyFormQuestionItem) {
        if(!studyFormQuestionItem.getItem().equals(item.content)) {
            studyFormQuestionItem.editItem(item.content);
        }
    }

    private void removeIfQuestionNonExist(Map<Long, StudyFormQuestion> questionMap, Set<Long> questionDTOs, List<StudyFormQuestion> studyFormQuestions) {
        questionMap.entrySet().removeIf(entry -> {
            if(!questionDTOs.contains(entry.getKey())) {
                studyFormQuestions.remove(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private  Set<Long> questionDTOsToLongSet(List<StudyFormQuestionDTO> questions) {
        return questions.stream()
                .filter(question -> Objects.nonNull(question.id))
                .map(StudyFormQuestionDTO::getId)
                .collect(Collectors.toSet());
    }

    private void removeIfItemsNonExist(Map<Long, StudyFormQuestionItem> questionItemsMap, Set<Long> itemDTOIds, List<StudyFormQuestionItem> questionItems) {
        questionItemsMap.entrySet().removeIf(entry -> {
            if(!itemDTOIds.contains(entry.getKey())) {
                questionItems.remove(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private Set<Long> ItemDTOsToLongSet(List<StudyFormQuestionItemDTO> items) {
        return items.stream()
                .filter(item -> Objects.nonNull(item.id))
                .map(StudyFormQuestionItemDTO::getId)
                .collect(Collectors.toSet());
    }

    private void checkQuestion(StudyFormQuestionDTO question, StudyFormQuestion studyFormQuestion) {
        if(!studyFormQuestion.getQuestion().equals(question.title)) {
            studyFormQuestion.editQuestion(question.title);
        }

        String type = studyFormQuestion.getType().toString().toLowerCase();
        if(!type.equals(question.type)) {
            studyFormQuestion.editType(toQuestionType(question.type));
        }
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

    private List<StudyFormQuestion> toStudyFormQuestionList(StudyFormDTO form) {
        return form.getQuestions().stream()
                .map(question -> {
                    StudyFormQuestion studyFormQuestion = new StudyFormQuestion(question.getTitle(), toQuestionType(question.getType()));

                    List<StudyFormQuestionItem> items = question.getItems().stream().map(item ->
                            new StudyFormQuestionItem(item.getContent())
                    ).toList();

                    items.forEach(studyFormQuestion::addQuestionItem);
                    return studyFormQuestion;
                }).toList();
    }


    private  List<StudyFormQuestionDTO> toQuestionDTOList(List<StudyFormQuestion> studyFormQuestions) {
        return studyFormQuestions.stream()
                .map(question -> new StudyFormQuestionDTO(
                        question.getId(),
                        question.getQuestion(),
                        question.getStudyFormQuestionItems().stream()
                                .map(item -> new StudyFormQuestionItemDTO(item.getId(), item.getItem()))
                                .toList(),
                        question.getType().toString().toLowerCase())
                ).toList();
    }


    public QuestionType toQuestionType(String type) {
        return switch (type.toLowerCase()) {
            case "text" -> TEXT;
            case "radio" -> RADIO;
            case "checkbox" -> CHECKBOX;
            default -> throw new IllegalArgumentException("Invalid question type: " + type);
        };
    }

}
