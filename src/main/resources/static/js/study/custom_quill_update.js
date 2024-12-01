const quill = new Quill('#editor', {
  modules: {
    toolbar: [
      [{ header: [1, 2, false] }],
      ['bold', 'italic', 'underline'],
      ['image'],
    ],
  },
  theme: 'snow',
});

const form = document.getElementById('quill_form');
const studyId = document.getElementById('studyId').dataset.studyId;
const contents = document.getElementById('contents').dataset.contents;
const studySelectedRegion = document.getElementById('studySelectedRegion').dataset.studySelectedRegion;
const studySelectedCategories = JSON.parse(document.getElementById('studySelectedCategories').dataset.studySelectedCategories);
const groupSize = document.getElementById('groupSizeVar').dataset.groupSizeVar;

document.getElementById('contents').remove();
document.getElementById('studyId').remove();
document.getElementById('studySelectedRegion').remove();
document.getElementById('studySelectedCategories').remove();
document.getElementById('groupSizeVar').remove();

quill.setContents(JSON.parse(contents));

document.getElementById('regions').value = studySelectedRegion;
document.getElementById('groupSize').value = groupSize;

let selectedBadges = [];
const badgeContainer = document.getElementById('selected');
const select = document.getElementById('categories');

function createBadge(value, text) {
  const span = document.createElement('span');
  span.className = 'inline-flex items-center px-2 py-1 me-2 mb-2 text-sm font-medium text-yellow-800 bg-yellow-100 rounded dark:bg-yellow-900 dark:text-yellow-300';
  span.id = `badge-${value}`;
  span.innerHTML = `
    ${text}
    <button type="button" class="inline-flex items-center p-1 ms-2 text-sm text-yellow-400 bg-transparent rounded-sm hover:bg-yellow-200 hover:text-yellow-900 dark:hover:bg-yellow-800 dark:hover:text-yellow-300" aria-label="Remove">
      <svg class="w-2 h-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
      </svg>
      <span class="sr-only">Remove badge</span>
    </button>
  `;
  badgeContainer.appendChild(span);

  span.querySelector('button').addEventListener('click',  () => {
    span.remove();
    selectedBadges = selectedBadges.filter(badge => badge != value);
    select.value = "";
  });
}

studySelectedCategories.forEach(categoryId => {
  const option = [...select.options].find(option => option.value == categoryId);
  if (option) {
    createBadge(categoryId, option.text);
    selectedBadges.push(String(categoryId));
  }
});

select.addEventListener('change', function () {
  const selectedValue = this.value;

  const selectedText = this.options[this.selectedIndex].text;
  if (!selectedBadges.includes(selectedValue)) {
    selectedBadges.push(selectedValue);
    createBadge(selectedValue, selectedText);
  }
});

form.addEventListener('submit', function (event) {
  event.preventDefault();

  const quillData = quill.getContents().ops;
  const title = document.getElementById('title').value.trim();
  const region = document.getElementById('regions').value;
  const groupSize = document.getElementById('groupSize').value;
  const selectedDate = document.getElementById('date').value;
  const isQuillEmpty = quillData.length === 1 && quillData[0].insert.trim() === "";

  const csrfToken = document.querySelector('input[name="_csrf"]').value;
  const today = new Date().toISOString().split('T')[0];

  if (!title) {
    alert('타이틀을 입력해주세요.');
    return;
  }

  if (isQuillEmpty) {
    alert('내용을 입력해주세요.');
    return;
  }

  if (!region) {
    alert("관심 지역을 선택해주세요");
    return;
  }

  if (!groupSize) {
    alert("인원수를 선택해주세요");
    return;
  }

  if (selectedBadges.length < 1) {
    alert("관심 분야를 선택해주세요");
    return;
  }

  if (selectedDate < today) {
    alert("마감 일자는 현재 날짜보다 이전일 수 없습니다.");
    return;
  }

  // Prepare form data
  const formData = new FormData();
  formData.append('title', title);
  formData.append('contents', JSON.stringify(quillData));
  formData.append('region', region);
  formData.append('groupSize', groupSize);
  formData.append('endDate', selectedDate);
  selectedBadges.forEach(category => formData.append('categories', category));


  // Submit form via fetch
  fetch(`/study/${studyId}/update`, {
    method: 'POST',
    body: formData,
    headers: {
      'X-CSRF-TOKEN': csrfToken,
    }
  })
    .then(response => response.ok ? response.json() : response.json().then(handleErrors))
    .then(data => {
      if (data && data.studyId) window.location.href = `/study/${data.studyId}`;
    })
    .catch(error => console.error('Error:', error));
});

function handleErrors(errorData) {
  const errorFields = ['error_title', 'error_region', 'error_categories', 'error_contents', 'error_endDate', 'error_groupSize'];
  errorFields.forEach(error => {
    if (errorData[error]) {
      document.getElementById(error).textContent = errorData[error];
    }
  });
}

['title', 'regions', 'categories', 'date', 'groupSize'].forEach(id => {
  document.getElementById(id).addEventListener('change', () => {
    if(document.getElementById(`error_${id}`))
      document.getElementById(`error_${id}`).textContent = "";
  });
});

quill.on('selection-change', function (range) {
  if (range && range.length >= 0) {
    document.getElementById('error_contents').textContent = "";
  }
});