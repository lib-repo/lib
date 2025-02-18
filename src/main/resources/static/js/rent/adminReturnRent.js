document.addEventListener("DOMContentLoaded", function () {
    const returnButtons = document.querySelectorAll(".admin-rent-btn-return");

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

            axios.post(`/api/rent/admin/return/${rentId}`)
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
});
