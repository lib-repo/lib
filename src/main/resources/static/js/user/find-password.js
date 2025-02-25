document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("find-password-form");
    const sendCodeBtn = document.getElementById("send-code");
    const verificationCodeInput = document.getElementById("verificationCode");
    const newPasswordInput = document.getElementById("newPassword");
    const resetPasswordBtn = document.getElementById("reset-password-btn");

    sendCodeBtn.addEventListener("click", function () {
        const emailInput = document.getElementById("email");
        const domainSelect = document.getElementById("email-domain");
        const userId = document.getElementById("userId").value;

        if (!domainSelect.value) {
            alert("이메일 도메인을 선택해주세요.");
            return;
        }

        const fullEmail = emailInput.value + "@" + domainSelect.value;

        fetch("/find-password/send-code", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                userId: userId,
                email: fullEmail
            })
        })
        .then(response => response.text())
        .then(html => {
            alert("인증번호가 이메일로 전송되었습니다.");
            verificationCodeInput.classList.remove("hidden");
            newPasswordInput.classList.remove("hidden");
            resetPasswordBtn.classList.remove("hidden");
        })
        .catch(error => {
            alert("에러 발생: " + error);
            console.error("Error:", error);
        });
    });

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        const userId = document.getElementById("userId").value;
        const emailInput = document.getElementById("email");
        const domainSelect = document.getElementById("email-domain");
        const verificationCode = verificationCodeInput.value;
        const newPassword = newPasswordInput.value;
        const fullEmail = emailInput.value + "@" + domainSelect.value;

        fetch("/find-password/reset", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                userId: userId,
                email: fullEmail,
                code: verificationCode,
                newPassword: newPassword
            })
        })
        .then(response => {
            if(response.ok){
                alert("비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.");
                window.location.href = "/user/login";
            } else {
                response.text().then(text => {
                    alert("비밀번호 변경 실패: " + text);
                });
            }
        })
        .catch(error => {
            alert("에러 발생: " + error);
            console.error("Error:", error);
        });
    });
});
