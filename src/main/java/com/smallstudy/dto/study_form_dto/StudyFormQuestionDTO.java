package com.smallstudy.dto.study_form_dto;



import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudyFormQuestionDTO {

   public Long id;
   public Long itemSeq;

   public String title;
   public List<StudyFormQuestionItemDTO> items;
   public String type;

   public StudyFormQuestionDTO(Long id, String title, List<StudyFormQuestionItemDTO> items, String type) {
      this.id = id;
      this.title = title;
      this.items = items;
      this.type = type;
   }

   public StudyFormQuestionDTO(String title, List<StudyFormQuestionItemDTO> items, String type) {
      this.title = title;
      this.items = items;
      this.type = type;
   }

   public void addStudyFormItem(StudyFormQuestionItemDTO item) {
      items.add(item);
   }

}
