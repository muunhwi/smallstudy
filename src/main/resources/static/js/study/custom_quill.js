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

let selectedBadges = [];

const form = document.getElementById('quill_form');
form.addEventListener('submit', function (event) {
  event.preventDefault();

  const quillData = quill.getContents().ops;
  const title = document.getElementById('title').value.trim();
  const region = document.getElementById('regions').value;
  const groupSize = document.getElementById('groupSize').value;
  const selectedDate = document.getElementById('date').value;
  const isQuillEmpty = quillData.length === 1 && quillData[0].insert.trim() === "";
  const csrfToken = document.querySelector('input[name="_csrf"]').value;

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

  const today = new Date().toISOString().split('T')[0];
  if (selectedDate < today) {
    alert("마감 일자는 현재 날짜보다 이전일 수 없습니다.");
    return;
  }

  const formData = new FormData();
  formData.append('title', title);
  formData.append('contents', JSON.stringify(quillData));
  formData.append('region', region);
  formData.append('groupSize', groupSize);
  formData.append('endDate', selectedDate);

  selectedBadges.forEach((category) => {
    formData.append('categories', category);
  });

  fetch('/study', {
    method: 'POST',
    body: formData,
    headers: {
      'X-CSRF-TOKEN': csrfToken,
    }
  })
    .then(response => response.ok ? response.json() : response.json().then(errorData => handleFormErrors(errorData)))
    .then(data => {
      if (data && data.studyId) {
        window.location.href = `/study/${data.studyId}`;
      }
    })
    .catch(error => console.error('Error:', error));
});

function handleFormErrors(errorData) {
  const errorFields = {
    error_title: 'error_title',
    error_region: 'error_region',
    error_categories: 'error_categories',
    error_contents: 'error_contents',
    error_endDate: 'error_endDate',
    error_groupSize: 'error_groupSize',
  };

  Object.keys(errorFields).forEach(error => {
    if (errorData[error]) {
      document.getElementById(errorFields[error]).textContent = errorData[error];
    }
  });
}

['title', 'regions', 'categories', 'date', 'groupSize'].forEach(id => {
  document.getElementById(id).addEventListener('change', () => {

    if( document.getElementById(`error_${id}`))
    document.getElementById(`error_${id}`).textContent = "";

  });
});

document.getElementById('categories').addEventListener('change', function () {
  const selectedValue = this.value;

  const selectedText = this.options[this.selectedIndex].text;
  const badgeContainer = document.getElementById('selected');

  if (!selectedBadges.includes(selectedValue)) {
    selectedBadges.push(selectedValue);
    createBadge(selectedValue, selectedText, badgeContainer);
  }
});

function createBadge(value, text, container) {
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
  container.appendChild(span);

  span.querySelector('button').addEventListener('click', () => {
    span.remove();
    selectedBadges = selectedBadges.filter(badge => badge !== value);
    document.getElementById('categories').value = "";
  });
}

quill.on('selection-change', function (range) {
  if (range && range.length >= 0) {
    document.getElementById('error_contents').textContent = "";
  }
});