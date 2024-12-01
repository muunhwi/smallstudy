
const contentDiv = document.getElementById('contents');
const contents = contentDiv.dataset.contents;
contentDiv.remove();

const alarm = document.getElementById('alarm');
const isEnd = alarm.dataset.end;
alarm.remove();



const quill = new Quill('#editor', {
  modules: {
    toolbar: false  
  },
  theme: 'snow', 
  readOnly: true,
});

 quill.setContents(JSON.parse(contents));

console.log(isEnd);

 if(isEnd === 'yes') {
  alert("신청 마감 되었습니다.");
 } else if(isEnd === 'no') {
  alert("신청이 완료되었습니다.");
 }

function confirmApply() {
  return confirm("신청 하시겠습니까?");
}

 function confirmDelete() {
  return confirm("정말로 삭제하시겠습니까?");
}