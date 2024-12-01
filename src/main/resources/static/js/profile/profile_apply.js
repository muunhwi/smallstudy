function onRejectFormSubmit(e) {

    if (!confirm("취소 하시겠습니까?")) {
        return;
    }
}

function onFormSubmit(e) {

    if (!confirm("승인 하시겠습니까?")) {
        return;
    }

}