let formItemCount = 0;
const studyFormTag = document.getElementById('studyForm');
const answersTag = document.getElementById('answers');
const pageTag = document.getElementById('pageNumber');

const formItemsDiv = document.getElementById('formItems');
const form = JSON.parse(studyFormTag.dataset.form);
const memberAnswers = JSON.parse(answersTag.dataset.answers);
const pageNumber = pageTag.dataset.page;


studyFormTag.remove();
answersTag.remove();
pageTag.remove();

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

    if(question.type === 'text') {

        const text = memberAnswers.answers
                                .filter(answer => answer.questionId === question.id)
                                .map(answer => answer.text);

        obj.render(contentsDiv, text);
    } else {

        const ids = memberAnswers.answers
                                .filter(answer => answer.questionId === question.id)
                                .map(answer => answer.answerItemIds)
                                .flat();


        question.items.forEach(item => { obj.render(contentsDiv, item, ids); });
    }

    formItemCount++;

    return obj;
});

const studyForm = form;

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
    obj.field_count = question.items.length -1;
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

        render(contentsDiv, item, ids) {
            let tag;
            if (this.type === 'text') {
                if (contentsDiv.children.length > 0) {
                    alert('텍스트 타입은 추가로 필드를 생성할 수 없습니다.');
                    return;
                }
                tag = createTextInputField(this.itemSeq, item);
            } else {
                tag = createRadioCheckInput(this.type, this.itemSeq, item, ids);
            }
            contentsDiv.appendChild(tag);
        },
    };
}

function createRadioCheckInput(inputType, itemSeq, item, ids) {
    const contentsDiv = document.getElementById(`contents_${itemSeq}`);
    const inputContainer = createInputContainer();
    let length = 0;

    if(contentsDiv.children)
        length = contentsDiv.children.length;

    const input     = createInputField(inputType, itemSeq, item, length, ids);
    const inputText = createLabelInput(itemSeq, length, item.content);

    inputContainer.appendChild(input);
    inputContainer.appendChild(inputText);

    return inputContainer;
}

function createInputContainer() {
    const inputContainer = document.createElement('div');
    inputContainer.className = 'flex items-center space-x-4 p-4 bg-gray-100 rounded-lg';
    return inputContainer;
}


function createTextInputField(itemId, text) {
    let tag = document.createElement('input');
    tag.id = `text_${itemId}_0`
    tag.type = 'text';
    tag.className = 'w-full p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent';
    tag.value=text;
    tag.readOnly = true;
    return tag;
}


function createInputField(inputType, itemSeq, item, length, ids) {
    const input = document.createElement('input');
    input.type = inputType;
    input.id = `${inputType}_${itemSeq}_${length}`;
    input.name = `${inputType}_${itemSeq}`;
    input.className = 'w-4 h-4 text-blue-600 focus:ring-blue-500';

    if(ids.some(id => id == item.id)) {
        input.checked = true;
    }

    input.disabled = true;
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

