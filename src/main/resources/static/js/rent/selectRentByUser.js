document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".extend-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            const rentId = this.getAttribute("data-rent-id");

            axios.post(`/api/rent/renew/${rentId}`)
                .then(response => {
                    alert("연장 신청이 완료되었습니다.");
                    location.reload();
                })
                .catch(error => {
                    alert("연장 신청 실패: " + (error.response?.data || error.message));
                });
        });
    });
});
