document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("find-password-form");
    const sendCodeBtn = document.getElementById("send-code");
    const verificationCodeInput = document.getElementById("verificationCode");
    const newPasswordInput = document.getElementById("newPassword");
    const resetPasswordBtn = document.getElementById("reset-password-btn");

    sendCodeBtn.addEventListener("click", function () {
        const emailInput = document.getElementById("email");
        const domainSelect = document.getElementById("email-domain");

        if (!domainSelect.value) {
            alert("이메일 도메인을 선택해주세요.");
            return;
        }

        const fullEmail = emailInput.value + "@" + domainSelect.value;

        fetch("/user/send-verification-code", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email: fullEmail })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("인증번호가 이메일로 전송되었습니다.");
                verificationCodeInput.classList.remove("hidden");
                newPasswordInput.classList.remove("hidden");
                resetPasswordBtn.classList.remove("hidden");
            } else {
                alert("이메일 전송 실패: " + data.message);
            }
        })
        .catch(error => console.error("Error:", error));
    });

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        const userId = document.getElementById("userId").value;
        const verificationCode = verificationCodeInput.value;
        const newPassword = newPasswordInput.value;

        fetch("/user/reset-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                userId: userId,
                code: verificationCode,
                newPassword: newPassword
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.");
                window.location.href = "/user/login";
            } else {
                alert("비밀번호 변경 실패: " + data.message);
            }
        })
        .catch(error => console.error("Error:", error));
    });
});