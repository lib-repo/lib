let isbn = '';
let currentBookId = null;
let initialRender = true;

// ISBN으로 도서 검색 (open api를 호출)
async function handleSearchBookByIsbn() {
    isbn = document.getElementById('isbn-input').value;
    console.log(isbn);

    const response =  await axios.get(`/api/books/search/${isbn}`);
    const bookData = response.data;

    if (bookData) {
        document.getElementById('title').value = bookData.title;
        document.getElementById('author').value = bookData.author;
        document.getElementById('publisher').value = bookData.publisher;
        document.getElementById('publicationYear').value = bookData.publicationYear;
        document.getElementById('description').value = bookData.description;
        document.getElementById('imageUrl').value = bookData.imageUrl;

        document.getElementById('title').focus();
        document.getElementById('author').focus();
        document.getElementById('publisher').focus();
        document.getElementById('publicationYear').focus();
        document.getElementById('description').focus();
        document.getElementById('imageUrl').focus();
    }
}

// 도서 등록
async function handleRegisterBook(event) {
    event.preventDefault();

    const bookData = {
        isbn: document.getElementById('isbn-input').value,
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        publisher: document.getElementById('publisher').value,
        publicationYear: document.getElementById('publicationYear').value,
        description: document.getElementById('description').value,
        imageUrl: document.getElementById('imageUrl').value
    };

    try {
        if (currentBookId) {
            await axios.put(`/api/books/${currentBookId}`, bookData);
            alert('책 수정이 완료되었습니다.');
        } else {
            await axios.post(`/api/books`, bookData);
            alert('책 등록이 완료되었습니다.');
        }

        var elem = document.getElementById('book-register-modal');
        var instance = M.Modal.getInstance(elem);

        instance.close();

        updateBookList();
    } catch (error) {
        console.error(error);
        alert('실패했습니다.');
    }
}

// 도서 수정
async function handleEditBook(event) {
    const bookId = event.target.closest('a').getAttribute('data-book-id');
    console.log(`수정할 책 ID: ${bookId}`);

    try {
        const response = await axios.get(`/api/books/${bookId}`);
        const bookData = response.data;

        document.getElementById('isbn-input').value = bookData.isbn;
        document.getElementById('title').value = bookData.title;
        document.getElementById('author').value = bookData.author;
        document.getElementById('publisher').value = bookData.publisher;
        document.getElementById('publicationYear').value = bookData.publicationYear;
        document.getElementById('description').value = bookData.description;
        document.getElementById('imageUrl').value = bookData.imageUrl;

        currentBookId = bookId;

        document.querySelector('#book-register-modal h5').innerText = "도서 수정";
        document.getElementById('registerButton').innerText = "수정";

        var elem = document.getElementById('book-register-modal');
        var instance = M.Modal.getInstance(elem);
        instance.open();

        document.getElementById('isbn-input').focus();
        document.getElementById('title').focus();
        document.getElementById('author').focus();
        document.getElementById('publisher').focus();
        document.getElementById('publicationYear').focus();
        document.getElementById('description').focus();
        document.getElementById('imageUrl').focus();

    } catch (error) {
        console.error(error);
        alert('책 정보를 불러오는 데 실패했습니다.');
    }
}

// 도서 삭제
async function handleDeleteBook(event) {
    const bookId = event.target.closest('a').getAttribute('data-book-id');
    console.log(`삭제할 책 ID: ${bookId}`);

    if (confirm(`삭제하시겠습니까?`)) {
        try {
            await axios.delete(`/api/books/${bookId}`);
            alert('책이 삭제되었습니다.');

            updateBookList();
        } catch (error) {
            console.error(error);
            alert('책 삭제에 실패했습니다.');
        }
    }
}

// 도서 리스트가 변경되면 업데이트
async function updateBookList() {
    try {
        const response = await axios.get(`/api/books`);
        const books = response.data;

        const bookList = document.getElementById('book-list');
        bookList.innerHTML = '';

        books.forEach(book => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                        <td>${book.isbn}</td>
                        <td><img src="${book.imageUrl}" alt="Book Image" style="width: 50px; height: auto;"></td>
                        <td>${book.title}</td>
                        <td>${book.author}</td>
                        <td>${book.publisher}</td>
                        <td>${book.publicationYear}</td>
                        <td>
                            <a class="btn-flat edit-btn" data-book-id="${book.bookId}"><i class="material-icons">edit</i></a>
                            <a class="btn-flat delete-btn" data-book-id="${book.bookId}"><i class="material-icons">delete</i></a>
                        </td>
                    `;
            bookList.appendChild(tr);
        });
        addEventListenersToButtons();
    } catch (error) {
        console.error(error);
    }
}

// 수정과 삭제 버튼
function addEventListenersToButtons() {
    const editButtons = document.querySelectorAll('.edit-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');

    editButtons.forEach(button => {
        button.addEventListener('click', handleEditBook);
    });

    deleteButtons.forEach(button => {
        button.addEventListener('click', handleDeleteBook);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    var elems = document.querySelectorAll('.modal');
    var instances = M.Modal.init(elems, {
        onCloseEnd: function () {
            document.getElementById('isbn-input').value = '';
            document.getElementById('title').value = '';
            document.getElementById('author').value = '';
            document.getElementById('publisher').value = '';
            document.getElementById('publicationYear').value = '';
            document.getElementById('description').value = '';
            document.getElementById('imageUrl').value = '';

            M.updateTextFields();

            document.querySelector('#book-register-modal h5').innerText = "도서 등록";
            document.getElementById('registerButton').innerText = "등록";
            currentBookId = null;
        }
    });

    if (initialRender) {
        initialRender = false;
    } else {
        updateBookList();
    }

    addEventListenersToButtons();
    document.getElementById('searchButton').addEventListener('click', handleSearchBookByIsbn);
    document.getElementById('registerButton').addEventListener('click', handleRegisterBook);
});