// 수정 시작
function makeEditable(button, subjectId) {
    var td = button.parentElement.previousElementSibling;
    var originalValue = td.innerText.trim();


    if (!td.querySelector('input')) {
        var input = document.createElement('input');
        input.type = 'text';
        input.value = originalValue;
        input.classList.add('editable-input');

        // 원래 크기 유지
        td.style.width = td.offsetWidth + 'px';
        td.style.height = td.offsetHeight + 'px';

        // input 스타일 조정
        input.style.width = '100%';
        input.style.height = '100%';
        input.style.boxSizing = 'border-box';
        input.style.border = 'none';
        input.style.outline = 'none';
        input.style.fontSize = 'inherit';
        input.style.fontFamily = 'inherit';
        input.style.textAlign = 'inherit';

        td.innerText = '';
        td.appendChild(input);
        input.focus();

        input.addEventListener('keydown', function (event) {
            if (event.key === 'Enter' && !event.repeat) {
                event.preventDefault();
                saveEditable(input, td, button, subjectId, originalValue);
                input.blur();
            } else if (event.key === 'Escape') {
                td.innerText = originalValue; // ESC 키를 누르면 원래 값으로 복구
            }
        });
    }
}

function saveEditable(input, td, button, subjectId, originalValue) {
    var updatedValue = input.value.trim();

    if (updatedValue === originalValue) {
        td.innerText = originalValue;
        return;
    }

    if (updatedValue === "") {
        alert('주제 값으로 공백이 올 수 없습니다.');
        td.innerText = originalValue;
        return;
    }

    td.innerText = updatedValue;
    button.onclick = function () {
        makeEditable(button, subjectId);
    };

    var data = { name: updatedValue, id: subjectId };

    axios.put('/api/subject/update', data)
        .then(function (response) {
            console.log('Success:', response.data);
        })
        .catch(function (error) {
            console.error('Error:', error);
        });
}
// 수정 끝

// 추가 시작
function addSubject() {
    let tbody = document.querySelector("tbody");
    let newRow = document.createElement("tr");

    newRow.innerHTML = `
        <td class="subject_detail">
            <input type="text" class="new-subject" placeholder="주제 입력 후 엔터">
        </td>
        <td>
            <button class="edit" disabled>
                <img src="/icon/modify.png" class="modify">
            </button>
            <img src="/icon/delete.png" class="delete">
        </td>
    `;

    tbody.appendChild(newRow);

    let input = newRow.querySelector(".new-subject");
    input.focus();

    input.addEventListener("keydown", function (event) {
        if (event.key === "Enter") {
            let subjectName = input.value.trim();
            if (subjectName) {
                axios.post('/api/subject', { name: subjectName })
                    .then(function (response) {
                        console.log("추가 성공:", response.data);
                        location.reload();
                    })
                    .catch(function (error) {
                        console.error("오류 발생:", error);
                    });
            }
        } else if (event.key === "Escape") {
            newRow.remove(); // Esc 키를 누르면 입력 취소
        }
    });

}
//추가 끝


//단건 조회
function getSubject(subjectName) {
    axios.get('/api/subject/' + subjectName)
        .then(response => {
            const subject = response.data;
            alert(`ID: ${subject.id}\n이름: ${subject.name}`);
        })
        .catch(error => {
            console.error("Error fetching subject:", error);
            alert("책 주제를 가져오는 데 실패했습니다.");
        });
}


//삭제
function deleteSubject(subjectId) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        axios.delete('/api/subject/' + subjectId)
            .then(function (response) {
                alert("삭제 완료되었습니다.");
                location.reload();
            })
            .catch(function (error) {
                alert("삭제 중 오류 발생: " + error.response.data);
                console.error('오류 발생:', error);
            });
    }
}