document.addEventListener("DOMContentLoaded", function () {

    window.requestLoan = function(button) {
        const libraryId = button.getAttribute("data-library-id");

        if (!libraryId) {
            alert("도서관 정보가 없습니다. 다시 시도해주세요.");
            console.error("도서관 ID가 없습니다.");
            return;
        }
        const bookId = document.body.getAttribute("data-book-id");

        if (!bookId) {
            alert("도서 정보가 없습니다. 다시 시도해주세요.");
            console.error("bookId가 없습니다.");
            return;
        }

        button.disabled = true;

        console.log("전송되는 libraryId: ", libraryId);
        console.log("전송되는 bookId: ", bookId);

        axios.post(`/api/rent/${bookId}`, {
            userId: 1,
            libraryId: libraryId
        })
            .then(response => {
                alert("대출 신청이 완료되었습니다!");
                location.reload();
            })
            .catch(error => {
                const errorMessage = error.response?.data?.message || JSON.stringify(error.response?.data) || "알 수 없는 오류";
                alert("대출 신청에 실패했습니다: " + errorMessage);
            })
            .finally(() => {

                button.disabled = false;
                button.textContent = "신청하기";
            });
    }

});
