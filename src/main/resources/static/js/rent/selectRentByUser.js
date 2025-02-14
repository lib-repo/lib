document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".extend-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            const rentId = this.getAttribute("data-rent-id");

            fetch(`/api/rent/renew/${rentId}`, {
                method: "POST"
            })
                .then(response => {
                    if (response.ok) {
                        alert("연장 신청이 완료되었습니다.");
                        location.reload();
                    } else {
                        return response.text().then(text => { throw new Error(text); });
                    }
                })
                .catch(error => {
                    alert("연장 신청 실패: " + error.message);
                });
        });
    });
});
