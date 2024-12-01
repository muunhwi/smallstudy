let formItemCount = 0;
const studyFormTag = document.getElementById('studyForm');
const formItemsDiv = document.getElementById('formItems');
const form = JSON.parse(studyFormTag.dataset.form);
studyFormTag.remove();

const titleTag = document.getElementById('formTitle');
titleTag.value = form.title;
titleTag.readOnly = true;

const descriptionTag = document.getElementById('formDescription');
descriptionTag.value = form.description;
descriptionTag.readOnly = true;


form.questions = form.questions.map(question => {
    question.itemId = formItemCount;
    const newFormItem = createFormItemDiv(formItemCount);
    formItemsDiv.appendChild(newFormItem);

    const obj = toStudyFormItem(question);
    const contentsDiv = document.getElementById(`contents_${formItemCount}`);
    const titleTag = document.getElementById(`title_${formItemCount}`);

    titleTag.value = question.title;
    titleTag.readOnly = true;

    if (question.type === 'text') {
        obj.render(contentsDiv);
    } else {
        question.items.forEach(item => { obj.render(contentsDiv, item.content); });
    }

    formItemCount++;

    return obj;
});

studyForm = form;

function getSelectedIndex(target) {
    const parts = target.id.split('_');
    return parseInt(parts[2]);
}

function getStudyFormItem(itemId) {
    return studyForm.questions[itemId];
}

function createFormItemDiv(formItemCount) {
    const newFormItem = document.createElement('div');
    newFormItem.classList.add('form-item', 'bg-gray-50', 'p-4', 'rounded-md', 'shadow-sm');
    newFormItem.id = `${formItemCount}`;

    newFormItem.innerHTML =
        `   <div id="error_${formItemCount}" class="text-sm text-red-500 mb-2"></div> 
                <input id="title_${formItemCount}" type="text" class="block w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500">
                <div id="contents_${formItemCount}"></div>
            `
        ;
    return newFormItem;
}

function toStudyFormItem(question) {
    const obj = createStudyFormItem(formItemCount);
    obj.id = question.id;
    obj.title = question.title;
    obj.field_count = question.items.length - 1;
    obj.items = question.items;
    obj.type = question.type;

    return obj;
}

function createStudyFormItem(formItemCount) {
    return {
        itemSeq: formItemCount,
        title: '',
        field_count: 0,
        items: [],
        type: 'text',

        render(contentsDiv, value) {
            let tag;
            if (this.type === 'text') {
                if (contentsDiv.children.length > 0) {
                    alert('텍스트 타입은 추가로 필드를 생성할 수 없습니다.');
                    return;
                }
                tag = createTextInputField(this.itemSeq);
            } else {
                tag = createRadioCheckInput(this.type, this.itemSeq, value);
            }
            contentsDiv.appendChild(tag);
        },
    };
}

function createRadioCheckInput(inputType, itemId, value) {
    const contentsDiv = document.getElementById(`contents_${itemId}`);
    const inputContainer = createInputContainer();
    let length = 0;

    if (contentsDiv.children)
        length = contentsDiv.children.length;

    const input = createInputField(inputType, itemId, length);
    const inputText = createLabelInput(itemId, length, value);

    inputContainer.appendChild(input);
    inputContainer.appendChild(inputText);

    return inputContainer;
}

function createInputContainer() {
    const inputContainer = document.createElement('div');
    inputContainer.className = 'flex items-center space-x-4 p-4 bg-gray-100 rounded-lg';
    return inputContainer;
}


function createTextInputField(itemId) {
    let tag = document.createElement('input');
    tag.id = `text_${itemId}_0`
    tag.type = 'text';
    tag.placeholder = '답변'
    tag.className = 'w-full p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent';
    tag.onblur = (e) => onUpdateLabelText(tag, itemId, e.target);

    return tag;
}

function createInputField(inputType, itemId, length) {
    const input = document.createElement('input');
    input.type = inputType;
    input.id = `${inputType}_${itemId}_${length}`;
    input.name = `${inputType}_${itemId}`;
    input.className = 'w-4 h-4 text-blue-600 focus:ring-blue-500';


    return input;
}

function createLabelInput(itemId, length, value) {
    const inputText = document.createElement('input');
    inputText.id = `label_${itemId}_${length}`;
    inputText.type = 'text';
    inputText.readOnly = true;

    if (value)
        inputText.value = value;

    inputText.className = 'p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent';
    return inputText;
}

function onUpdateLabelText(inputElement, itemId, target) {

    document.getElementById(`error_${itemId}`).textContent = '';

    let text = inputElement.value.trim();
    const parts = target.id.split('_');
    const studyFormItem = getStudyFormItem(itemId);

    if (studyFormItem.items[parts[2]])
        studyFormItem.items[parts[2]].content = text;
    else
        studyFormItem.items[parts[2]] = { content: text };
}

function handleFormErrors(errorData) {
    const errorFields = {
        error_id: 'error_id',
        error_answer: 'error_answer',
    };

    Object.keys(errorFields).forEach(error => {
        if (errorData[error]) {
            alert(errorData[error]);
        }
    });
}


function onFormSubmit(e) {
    e.preventDefault();

    if (!confirm("신청 하시겠습니까?")) {
        return;
    }

    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const error = [];

    const data = {
        studyId: studyForm.studyId,
        formId: studyForm.id,
        questionCount : studyForm.questions.length,
        answers: [],
    }


    studyForm.questions.forEach(question => {
        const answer = {
            questionId: question.id,
            answerItemIds: [],
            text : '',
        }

        if (question.type === 'text') {

            if(question.items.length === 0)
            {
                error.push('모든 답변을 작성해주세요.');
                return;
            }
            answer.text = question.items[0].content;
        }
        else if (question.type === 'radio') {
            const radioGroup = document.getElementsByName(`radio_${question.itemSeq}`)
            radioGroup.forEach(radio => {
                if (radio.checked) {
                    const parts = radio.id.split('_');
                    answer.answerItemIds.push(question.items[parts[2]].id);

                }
            })
        } else {
            const checkboxs = document.getElementsByName(`checkbox_${question.itemSeq}`);

            checkboxs.forEach(checkbox => {
                if (checkbox.checked) {
                    const parts = checkbox.id.split('_');
                    answer.answerItemIds.push(question.items[parts[2]].id);
                }
            })
        }

        if(question.type !== 'text') {
            if(answer.answerItemIds.length === 0) {
                error.push('선택되지 않은 라디오, 체크박스가 존재합니다. 확인해주세요');
                return;
            }
        }

        data.answers.push(answer);
    })

    if(error.length > 0) {
        alert(error[0]);
        return;
    }

    fetch("/study/apply", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-CSRF-TOKEN': csrfToken,
        },
        body: JSON.stringify(data)
    })
        .then(response => response.ok ? response.json() : response.json().then(errorData => handleFormErrors(errorData)))
        .then(data => {
            if (data && data.studyId) {
                alert("신청이 완료 되었습니다.");
                window.location.href = `/study/${data.studyId}`;
            }
        })
        .catch(error => console.error("Error:", error));
}