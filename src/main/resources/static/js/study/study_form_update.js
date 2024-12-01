let formItemCount = 0;
const studyFormTag = document.getElementById('studyForm');
const formItemsDiv = document.getElementById('formItems');
const form = JSON.parse(studyFormTag.dataset.form);
studyFormTag.remove();

const titleTag = document.getElementById('formTitle');
titleTag.value = form.title;

const descriptionTag = document.getElementById('formDescription');
descriptionTag.value = form.description;

form.questions = form.questions.map(question => {
    question.itemId = formItemCount;
    const newFormItem = createFormItemDiv(formItemCount);
    formItemsDiv.appendChild(newFormItem);

    const obj = toStudyFormItem(question);
    const contentsDiv = document.getElementById(`contents_${formItemCount}`);
    const titleTag = document.getElementById(`title_${formItemCount}`);
    const selectTag = document.getElementById(`select_${formItemCount}`);

    selectTag.value = question.type;
    titleTag.value = question.title;

    if(question.type === 'text') {
        obj.render(contentsDiv);
    } else {
        question.items.forEach(item => { obj.render(contentsDiv, item.content); });
    }

    formItemCount++;

    return obj;
});

const studyForm = form;

function addForm() {
    const newFormItem = createFormItemDiv(formItemCount);
    formItemsDiv.appendChild(newFormItem);

    const obj = createStudyFormItem(formItemCount);
    studyForm.questions.push(obj);

    const contentsDiv = document.getElementById(`contents_${formItemCount}`);
    obj.render(contentsDiv);

    formItemCount++;
}

function removeForm(itemId) {
    document.getElementById(`${itemId}`).remove();
    studyForm.questions = studyForm.questions.filter(question => question.id != itemId);
}

function createFormItemDiv(formItemCount) {
    const newFormItem = document.createElement('div');
    newFormItem.classList.add('form-item', 'bg-gray-50', 'p-4', 'rounded-md', 'shadow-sm');
    newFormItem.id = `${formItemCount}`;

    newFormItem.innerHTML = 
           `
            <div id="error_${formItemCount}" class="text-sm text-red-500 mb-2"></div>

                <div class="mb-4">
                    <label for="title_${formItemCount}" class="block text-gray-700 font-semibold mb-1">질문 입력</label>
                    <input id="title_${formItemCount}" type="text" 
                        class="block w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                        placeholder="질문을 입력하세요" 
                        onblur="onBlurTitleInput(${formItemCount})">
                </div>

                <div class="mb-4">
                    <label for="select_${formItemCount}" class="block text-gray-700 font-semibold mb-1">응답 유형</label>
                    <select id="select_${formItemCount}" 
                            class="block w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500" 
                            onchange="onChangeInputType(${formItemCount})">
                        <option value="text">텍스트</option>
                        <option value="radio">단일 선택</option>
                        <option value="checkbox">다중 선택</option>
                    </select>
                </div>

                <div id="contents_${formItemCount}" class="mb-4"></div>

                <div class="flex justify-end items-center space-x-3 pt-4">
                    <button type="button" 
                            class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-green-500"
                            onclick="onAddFormItem(${formItemCount})">추가</button>
                    <button type="button" 
                            class="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-red-400"
                            onclick="removeForm(${formItemCount})">폼 삭제</button>
                </div>
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

        render(contentsDiv, value) {
            let tag;
            if (this.type === 'text') {
                if (contentsDiv.children.length > 0) {
                    alert('텍스트 타입은 추가로 필드를 생성할 수 없습니다.');
                    return;
                }
                tag = createTextInputField(this.itemSeq, value);
            } else {
                tag = createRadioCheckInput(this.type, this.itemSeq, value);
            }
            contentsDiv.appendChild(tag);
        },
        clearContents(contentsDiv) {
            contentsDiv.innerHTML = '';
            this.items.length = 0;
        },
        setTitle(title) {
            this.title = title;
        },
        setType(type) {
            this.type = type;
        },
        removeContent(target) {
            const selectedIdx = getSelectedIndex(target);
            this.items = this.items.filter((_, index) => index !== selectedIdx);

            const contentsDiv = document.getElementById(`contents_${this.itemSeq}`);
            const delChild = contentsDiv.children[selectedIdx];
            contentsDiv.removeChild(delChild);
            this.field_count--;

            updateContentIds(contentsDiv, this);

        }
    };
}

function getSelectedIndex(target) {
    const parts = target.id.split('_');
    return parseInt(parts[2]);
}

function getStudyFormItem(itemId) {
    return studyForm.questions[itemId];
}

function updateContentIds(contentsDiv, studyFormItem) {
    Array.from(contentsDiv.children).forEach((child, index) => {
        Array.from(child.children).forEach((cchild) => {
            if (cchild.type === 'text') {
                cchild.id = `label_${studyFormItem.itemSeq}_${index}`;
            } else if (cchild.type === 'submit') {
                cchild.id = `del_${studyFormItem.itemSeq}_${index}`;
            } else {
                cchild.id = `${studyFormItem.type}_${studyFormItem.itemSeq}_${index}`;
            }
        });
    });
}

function createTextInputField(itemId, value) {
    let tag = document.createElement('input');
    tag.id = `text_${itemId}_0`
    tag.type = 'text';
    tag.className = 'w-full p-2 border rounded bg-gray-100 text-gray-500 cursor-not-allowed';
    tag.disabled = true;

    if (value)
        tag.value = value;

    return tag;
}

function createRadioCheckInput(inputType, itemId, value) {
    const contentsDiv = document.getElementById(`contents_${itemId}`);
    const inputContainer = createInputContainer();
    let length = 0;

    if(contentsDiv.children)
        length = contentsDiv.children.length;

    const input     = createInputField(inputType, itemId, length);
    const inputText = createLabelInput(itemId, length, value);
    const delbtn    = createDeleteButton(itemId, length);

    inputContainer.appendChild(input);
    inputContainer.appendChild(inputText);
    inputContainer.appendChild(delbtn);

    return inputContainer;
}

function createInputContainer() {
    const inputContainer = document.createElement('div');
    inputContainer.className = 'flex items-center space-x-4 p-4 bg-gray-100 rounded-lg';
    return inputContainer;
}

function createInputField(inputType, itemId, length) {
    const input = document.createElement('input');
    input.type = inputType;
    input.id = `${inputType}_${itemId}_${length}`;
    input.name = `${inputType}_${itemId}_${length}`;
    input.className = 'w-4 h-4 text-blue-600 focus:ring-blue-500';
    input.disabled = true;
    return input;
}

function createLabelInput(itemId, length, value) {
    const inputText = document.createElement('input');
    inputText.id = `label_${itemId}_${length}`;
    inputText.type = 'text';

    if (value)
        inputText.value = value;

    inputText.className = 'p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent';
    inputText.onblur = (e) => onUpdateLabelText(inputText, itemId, e.target);
    return inputText;
}

function createDeleteButton(itemId, length) {
    const delbtn = document.createElement('button');
    delbtn.id = `del_${itemId}_${length}`;
    delbtn.textContent = 'X';
    delbtn.className = 'bg-rose-400 p-2 rounded hover:bg-rose-500 focus:outline-none focus:ring-2 focus:ring-rose-500';
    delbtn.onclick = (e) => {
        e.preventDefault();
        onRemoveFormItem(e.target, itemId);
    };
    return delbtn;
}

function onAddFormItem(itemId) {
    document.getElementById(`error_${itemId}`).textContent = '';

    const contentsDiv = document.getElementById(`contents_${itemId}`);
    const studyFormItem = getStudyFormItem(itemId);

    studyFormItem.field_count++;
    studyFormItem.render(contentsDiv);
}

function onBlurTitleInput(itemId) {
    document.getElementById(`error_${itemId}`).textContent = '';

    const title = document.getElementById(`title_${itemId}`).value;
    if (title !== '') {
        const studyFormItem = getStudyFormItem(itemId);
        studyFormItem.setTitle(title);
    }
}

function onChangeInputType(itemId) {
    document.getElementById(`error_${itemId}`).textContent = '';

    const selectInputType = document.getElementById(`select_${itemId}`).value;
    const contentsDiv = document.getElementById(`contents_${itemId}`);
    const studyFormItem = getStudyFormItem(itemId);

    if (studyFormItem.type !== selectInputType) {
        if(studyFormItem.type !== 'text' && selectInputType !== 'type') {
            contentsDiv.innerHTML = '';
            studyFormItem.setType(selectInputType);
            studyFormItem.items.forEach(item => { studyFormItem.render(contentsDiv, item.content); });
        } else {
            studyFormItem.clearContents(contentsDiv);
            studyFormItem.setType(selectInputType);
            studyFormItem.render(contentsDiv);
            studyFormItem.field_count = 0;
        }    
    }
}
function onUpdateLabelText(inputElement, itemId, target) {

    document.getElementById(`error_${itemId}`).textContent = '';

    let text = inputElement.value.trim();
    const parts = target.id.split('_');
    const studyFormItem = getStudyFormItem(itemId);

    if(studyFormItem.items[parts[2]]) 
        studyFormItem.items[parts[2]].content = text;
    else
        studyFormItem.items[parts[2]] = {content:text};
}

function onRemoveFormItem(target, itemId) {
    const studyFormItem = getStudyFormItem(itemId);
    studyFormItem.removeContent(target);
}

function handleFormErrors(errorData) {
    const errorFields = {
        error_title: 'error_title',
        error_description: 'error_description',
        error_form_title: 'error_form_title',
        error_form_type: 'error_form_type',
        error_form_contents: 'error_form_contents',
        error_form_question: 'error_form_question'
    };

    Object.keys(errorFields).forEach(error => {
        if (error === errorFields.error_title) {
            document.getElementById(errorFields[error]).textContent = errorData[error];
            return;
        }

        if (error === errorFields.error_description) {
            document.getElementById(errorFields[error]).textContent = errorData[error];
            return;
        }

        if (error === errorFields.error_form_question) {
            document.getElementById(errorFields[error]).textContent = errorData[error];
            return;
        }

        if (errorData[error]) {
            document.getElementById(`error_${errorData['itemSeq']}`).textContent = errorData[error];
        }
    });
}

function isBlank(str) {
    if(!str || str.trim().length ===0) 
        return true;

    return false;
}

function onFormSubmit(e) {
    e.preventDefault();

    if(!confirm("수정 하시겠습니까?")) {
        return;
    }


    const title = document.getElementById("formTitle").value;
    const description = document.getElementById("formDescription").value;
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const errors = [];

    if (studyForm.questions.length < 1) {
        alert('폼을 생성해주세요.');
        return;
    }

    if (isBlank(title)) {
        alert(`전체 폼 제목이 입력되지 않았습니다.`);
        return;
    }

    if (isBlank(description)) {
        alert(`소개를 입력해주세요.`);
        return;
    }

    const formItems = studyForm.questions.map(question => {
        if (question.title.trim().length < 1) {
            alert.push(`제목이 입력되지 않았습니다.`);
            return;
        }

        if (isBlank(question.type)) {
            errors.push(`타입을 선택해 주세요.`);
            return;
        }

        if (question.type !== 'text') {
            const inputFieldLength = question.items.length;
            if (question.items.length === 0 || inputFieldLength !== question.field_count + 1) {
                errors.push(`빈 필드가 존재합니다. 확인해주세요.`);
                return;
            }

            question.items.forEach(item => {
                if (isBlank(item.content)) {
                    errors.push(`빈 필드가 존재합니다. 확인해주세요.`);
                    return;
                }
            })
        }

        return {
            id: question.id,
            itemSeq: question.itemSeq,
            title: question.title,
            items: question.items,
            type: question.type,
        };
    })

    if (errors.length > 0) {
        alert(errors[0]);
        return;
    }

    const data = {
        id : studyForm.id,
        studyId: studyForm.studyId,
        title: studyForm.title,
        description: studyForm.description,
        questions: formItems,
    };

    fetch("/study/form/edit", {
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
                window.location.href = `/study/${data.studyId}`;
            }
        })
        .catch(error => console.error("Error:", error));
}