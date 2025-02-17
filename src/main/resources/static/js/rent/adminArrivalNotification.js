document.addEventListener("DOMContentLoaded", function () {
    const returnButtons = document.querySelectorAll(".admin-rent-btn-return");
    const notifyButtons = document.querySelectorAll(".admin-rent-btn-notify");

    returnButtons.forEach(button => {
        button.addEventListener("click", function () {
            const rentId = this.closest("tr").getAttribute("data-rent-id");
            console.log(rentId);

            if (!rentId) {
                alert("대여 정보가 없습니다.");
                return;
            }

            if (!confirm("해당 도서를 반납 처리하시겠습니까?")) {
                return;
            }

            axios.post(`/api/rent/return/${rentId}`)
                .then(response => {
                    alert("반납이 완료되었습니다.");
                    location.reload();
                })
                .catch(error => {
                    console.error("반납 처리 오류:", error);
                    alert(error.response?.data || "반납 처리 중 오류 발생");
                });
        });
    });

    notifyButtons.forEach(button => {
        button.addEventListener("click", function () {
            const rentId = this.closest("tr").getAttribute("data-rent-id");
            console.log(rentId);

            if (!rentId) {
                alert("대여 정보가 없습니다.");
                return;
            }

            if (!confirm("도착 통보를 이메일로 전송하시겠습니까?")) {
                return;
            }

            axios.post(`/api/rent/${rentId}/arrival`)
                .then(response => {
                    alert("도착 통보 이메일이 전송되었습니다.");
                })
                .catch(error => {
                    console.error("도착 통보 오류:", error);
                    alert(error.response?.data || "도착 통보 처리 중 오류 발생");
                });
        });
    });
});
