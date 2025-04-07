document.addEventListener('DOMContentLoaded', function() {
    // 검색 기능
    const searchIcon = document.getElementById('search-icon');
    const searchForm = document.querySelector('.search-form');
    const searchInput = document.querySelector('.search-input');
    const searchClose = document.getElementById('searchClose');
    const searchOverlay = document.getElementById('searchOverlay');

    // 검색 아이콘 클릭 이벤트
    if (searchIcon && searchForm) {
        searchIcon.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            searchForm.style.display = 'block';
            searchOverlay.style.display = 'block';
            searchInput.focus();
            document.body.style.overflow = 'hidden';
        });
    }

    // 관리자 페이지 단축키
    document.addEventListener('keydown', function (event) {
        if (event.ctrlKey && event.shiftKey && event.key === '?') {
            window.location.href = '/articles/admin-secret-xzy123/write';
        }
    });

    // 닫기 버튼 이벤트
    searchClose.addEventListener('click', function () {
        searchForm.style.display = 'none';
        searchOverlay.style.display = 'none';
        document.body.style.overflow = '';
    });

    // 오버레이 클릭 시 닫기
    searchOverlay.addEventListener('click', function () {
        searchForm.style.display = 'none';
        searchOverlay.style.display = 'none';
        document.body.style.overflow = '';
    });

    // ESC 키 누를 시 닫기
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && searchForm.style.display !== 'none') {
            searchForm.style.display = 'none';
            searchOverlay.style.display = 'none';
            document.body.style.overflow = '';
        }
    });
});