document.addEventListener('DOMContentLoaded', function() {
    const searchIcon = document.querySelector('.search-icon-css');
    const searchForm = document.querySelector('.search-form');
    const searchInput = document.querySelector('.search-input');
    const searchClose = document.getElementById('searchClose');
    const searchOverlay = document.getElementById('searchOverlay');

    if (searchIcon && searchForm) {
        // 검색 아이콘 클릭 이벤트
        searchIcon.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            openSearchForm();
        });

        // 터치 이벤트 처리 (모바일용)
        searchIcon.addEventListener('touchend', function(e) {
            e.preventDefault();
            e.stopPropagation();
            openSearchForm();
        });

        // 닫기 버튼 이벤트
        if (searchClose) {
            searchClose.addEventListener('click', closeSearchForm);
        }

        // 오버레이 클릭 시 닫기
        if (searchOverlay) {
            searchOverlay.addEventListener('click', closeSearchForm);
        }

        // 검색 폼 열기 함수
        function openSearchForm() {
            searchForm.style.display = 'block';
            searchOverlay.style.display = 'block';

            // 입력 필드에 포커스
            if (searchInput) {
                setTimeout(function() {
                    searchInput.focus();
                }, 100);
            }

            // 스크롤 방지 (선택적)
            document.body.style.overflow = 'hidden';
        }

        // 검색 폼 닫기 함수
        function closeSearchForm() {
            searchForm.style.display = 'none';
            searchOverlay.style.display = 'none';

            // 스크롤 복원 (선택적)
            document.body.style.overflow = '';
        }

        // ESC 키를 눌러 검색 폼 닫기
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && searchForm.style.display === 'block') {
                closeSearchForm();
            }
        });
    }
});