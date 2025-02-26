document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('book-search-input');

    // Enter 키를 눌렀을 때
    searchInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            const keyword = searchInput.value.trim();

            // 검색어가 비어있지 않으면 서버로 검색 요청
            if (keyword !== '') {
                // 페이지 리로드 없이 URL에 파라미터를 추가하여 검색
                window.location.href = `/books/search?keyword=${encodeURIComponent(keyword)}`;
            }
        }
    });
});