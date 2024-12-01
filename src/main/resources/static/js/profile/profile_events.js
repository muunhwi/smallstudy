    // 이미지 변경 이벤트
document.getElementById('profileImage').addEventListener('change', function (event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    document.getElementById('profileImagePreview').src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
     });


function confirmAndSubmit(event) {
     let result = confirm("프로필 변경 사항을 적용하시겠습니까?");
     if (result)
         return true;
     else {
         event.preventDefault();
         return false;
     }
}