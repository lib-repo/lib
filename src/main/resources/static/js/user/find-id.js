document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("find-id-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        const emailInput = document.getElementById("email");
        const domainSelect = document.getElementById("email-domain");

        if (!domainSelect.value) {
            alert("이메일 도메인을 선택해주세요.");
            return;
        }

        // 이메일 필드에 전체 이메일 주소 설정 후 제출
        const fullEmail = emailInput.value + "@" + domainSelect.value;
        const emailHiddenInput = document.createElement("input");
        emailHiddenInput.type = "hidden";
        emailHiddenInput.name = "email";
        emailHiddenInput.value = fullEmail;
        form.appendChild(emailHiddenInput);

        form.submit();
    });
});