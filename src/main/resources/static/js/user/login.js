document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("login-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const userId = document.getElementById("userId").value;
        const password = document.getElementById("password").value;

        fetch("/user/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userId, password })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("로그인 성공!");
                window.location.href = "/";
            } else {
                alert("로그인 실패: " + data.message);
            }
        })
        .catch(error => console.error("Error:", error));
    });
});