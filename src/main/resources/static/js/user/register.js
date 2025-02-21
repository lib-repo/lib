/*document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("register-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const userId = document.getElementById("userId").value;
        const password = document.getElementById("password").value;
        const email = document.getElementById("email").value;
        const userName = document.getElementById("userName").value;
        const phone = document.getElementById("phone").value;

        fetch("/user/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userId, password, email, userName, phone })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("회원가입 성공! 로그인 페이지로 이동합니다.");
                window.location.href = "/user/login";
            } else {
                alert("회원가입 실패: " + data.message);
            }
        })
        .catch(error => console.error("Error:", error));
    });
});*/