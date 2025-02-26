import { API } from './api.js';

let isbn = '';
let currentBookId = null;
let initialRender = true;
let currentPage = 1;
let pageSize = 10;

// ISBN으로 도서 검색 (open api를 호출)
async function handleSearchBookByIsbn() {
    isbn = document.getElementById('isbn-input').value;
    console.log(isbn);

    const response = await axios.get(API.BOOKS.url + `/search/${isbn}`);
    const bookData = response.data;

    if (bookData) {
        document.getElementById('title').value = bookData.title;
        document.getElementById('author').value = bookData.author;
        document.getElementById('publisher').value = bookData.publisher;
        document.getElementById('publicationYear').value = bookData.publicationYear;
        document.getElementById('description').value = bookData.description;
        document.getElementById('imageUrl').value = bookData.imageUrl;
    }
}

// 도서 등록
async function handleRegisterBook(event) {
    event.preventDefault();

    const bookData = {
        isbn: document.getElementById('isbn-input').value,
        subjectId: document.getElementById('subject').value,
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        publisher: document.getElementById('publisher').value,
        publicationYear: document.getElementById('publicationYear').value,
        description: document.getElementById('description').value,
        imageUrl: document.getElementById('imageUrl').value
    };

    try {
        if (currentBookId) {
            await axios.put(API.BOOKS.url + `/${currentBookId}`, bookData);
            alert('책 수정이 완료되었습니다.');
        } else {
            await axios.post(API.BOOKS.url, bookData);
            alert('책 등록이 완료되었습니다.');
        }

        // 모달 닫기
        const modal = bootstrap.Modal.getInstance(document.getElementById('book-register-modal'));
        modal.hide();

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
        const response = await axios.get(API.BOOKS.url + `/${bookId}`);
        const bookData = response.data;

        document.getElementById('isbn-input').value = bookData.isbn;
        document.getElementById('title').value = bookData.title;
        document.getElementById('author').value = bookData.author;
        document.getElementById('publisher').value = bookData.publisher;
        document.getElementById('publicationYear').value = bookData.publicationYear;
        document.getElementById('description').value = bookData.description;
        document.getElementById('imageUrl').value = bookData.imageUrl;

        currentBookId = bookId;

        // 모달 헤더 수정
        document.querySelector('#book-register-modal h5').innerText = "도서 수정";
        document.getElementById('registerButton').innerText = "수정";

        // 모달 열기
        const modal = new bootstrap.Modal(document.getElementById('book-register-modal'));
        modal.show();

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
            await axios.delete(API.BOOKS.url + `/${bookId}`);
            alert('책이 삭제되었습니다.');

            updateBookList();
        } catch (error) {
            console.error(error);
            alert('책 삭제에 실패했습니다.');
        }
    }
}

function changePage(page) {
    if (page < 1 || page > totalPages) return;
    currentPage = page;
    updateBookList();
}

function createPagination(totalPages) {
    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = '';

    // 이전 페이지 버튼
    const prevItem = document.createElement('li');
    prevItem.classList.add('page-item');
    if (currentPage === 1) {
        prevItem.classList.add('disabled'); // 1페이지면 비활성화
    }
    const prevLink = document.createElement('a');
    prevLink.classList.add('page-link');
    prevLink.href = "javascript:void(0)";
    prevLink.innerText = '이전';
    prevLink.addEventListener('click', function() {
        changePage(currentPage - 1);
    });
    prevItem.appendChild(prevLink);
    pagination.appendChild(prevItem);

    // 페이지 번호 생성
    for (let i = 1; i <= totalPages; i++) {
        const pageItem = document.createElement('li');
        pageItem.classList.add('page-item');
        if (currentPage === i) {
            pageItem.classList.add('active'); // 현재 페이지는 active 상태
        }
        const pageLink = document.createElement('a');
        pageLink.classList.add('page-link');
        pageLink.href = "javascript:void(0)";
        pageLink.innerText = i;
        pageLink.addEventListener('click', function() {
            changePage(i);
        });
        pageItem.appendChild(pageLink);
        pagination.appendChild(pageItem);
    }

    // 다음 페이지 버튼
    const nextItem = document.createElement('li');
    nextItem.classList.add('page-item');
    if (currentPage === totalPages) {
        nextItem.classList.add('disabled'); // 마지막 페이지면 비활성화
    }
    const nextLink = document.createElement('a');
    nextLink.classList.add('page-link');
    nextLink.href = "javascript:void(0)";
    nextLink.innerText = '다음';
    nextLink.addEventListener('click', function() {
        changePage(currentPage + 1);
    });
    nextItem.appendChild(nextLink);
    pagination.appendChild(nextItem);
}

// 도서 리스트가 변경되면 업데이트
async function updateBookList() {
    try {
        const response = await axios.get(API.BOOKS.url + `?page=${currentPage - 1}&size=${pageSize}`);
        const books = response.data.content;
        totalBooks = response.data.totalElements;
        totalPages = response.data.totalPages;
        console.log("totalEle" + totalBooks + "/   totalPage" + totalPages);

        const bookList = document.getElementById('book-list');
        bookList.innerHTML = '';

        books.forEach(book => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                        <td>${book.isbn}</td>
                        <td><img src="${book.imageUrl}" alt="Book Image"></td>
                        <td>${book.title}</td>
                        <td>${book.author}</td>
                        <td>${book.publisher}</td>
                        <td>${book.publicationYear}</td>
                        <td>
                            <a class="edit-btn" data-book-id="${book.bookId}"><i class="bi bi-pencil"></i></a>
                            <a class="delete-btn" data-book-id="${book.bookId}"><i class="bi bi-trash"></i></a>
                        </td>
                    `;
            bookList.appendChild(tr);
        });
        addEventListenersToButtons();
        createPagination(totalPages);
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

function resetModalForm() {
    document.getElementById('isbn-input').value = '';
    document.getElementById('subject').value = '';
    document.getElementById('title').value = '';
    document.getElementById('author').value = '';
    document.getElementById('publisher').value = '';
    document.getElementById('publicationYear').value = '';
    document.getElementById('description').value = '';
    document.getElementById('imageUrl').value = '';

    currentBookId = null;
    document.querySelector('#book-register-modal h5').innerText = "도서 등록";
    document.getElementById('registerButton').innerText = "등록";
}

document.getElementById('book-register-modal').addEventListener('hidden.bs.modal', function () {
    resetModalForm();
});

document.addEventListener('DOMContentLoaded', function () {
    const modalElems = document.querySelectorAll('.modal');
    modalElems.forEach(elem => {
        new bootstrap.Modal(elem, {
            backdrop: 'static',
            keyboard: false
        });
    });

    if (initialRender) {
        initialRender = false;
    } else {
        updateBookList();
    }

    createPagination(totalPages);
    addEventListenersToButtons();

    document.getElementById('searchButton').addEventListener('click', handleSearchBookByIsbn);
    document.getElementById('registerButton').addEventListener('click', handleRegisterBook);
});