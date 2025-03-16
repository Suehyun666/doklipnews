document.addEventListener('DOMContentLoaded', function() {
    // 헤드라인 뉴스 로딩
    loadHeadlineNews();

    // 카테고리별 뉴스 로딩
    loadCategoryNews();

    // 인기 뉴스 불러오기
    loadTrendingNews();

    // 오피니언 기사 불러오기
    loadOpinionArticles();

    // 이벤트 리스너 설정
    setupEventListeners();
});

// 헤드라인 뉴스 로딩
function loadHeadlineNews() {
    fetch('/api/articles/featured?size=1')
        .then(response => response.json())
        .then(data => {
            if (data.content && data.content.length > 0) {
                const headline = data.content[0];
                const headlineSection = document.querySelector('.headline-news');

                headlineSection.innerHTML = `
                    <h2 class="headline-title">${headline.title}</h2>
                    <img src="${headline.imageUrl || '/api/placeholder/600/400'}" alt="${headline.title}" class="headline-image">
                    <p class="headline-summary">${headline.content.substring(0, 300)}${headline.content.length > 300 ? '...' : ''}</p>
                `;

                headlineSection.addEventListener('click', () => {
                    window.location.href = `/articles/${headline.id}`;
                });
            }
        })
        .catch(error => console.error('Error loading headline news:', error));
}

// 카테고리별 뉴스 로딩
function loadCategoryNews() {
    fetch('/api/articles?size=4')
        .then(response => response.json())
        .then(data => {
            const newsGrid = document.querySelector('.news-grid');
            newsGrid.innerHTML = '';

            if (data.content && data.content.length > 0) {
                data.content.forEach(article => {
                    const newsCard = document.createElement('div');
                    newsCard.className = 'news-card';

                    newsCard.innerHTML = `
                        <h3 class="news-title">${article.title}</h3>
                        <img src="${article.imageUrl || '/api/placeholder/300/200'}" alt="${article.title}" class="news-image">
                        <p class="news-summary">${article.content.substring(0, 100)}${article.content.length > 100 ? '...' : ''}</p>
                    `;

                    newsCard.addEventListener('click', () => {
                        window.location.href = `/articles/${article.id}`;
                    });

                    newsGrid.appendChild(newsCard);
                });
            }
        })
        .catch(error => console.error('Error loading category news:', error));
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // 로그인, 회원가입 모달 기능
    setupModalListeners();

    // 검색 폼 제출
    const searchForm = document.querySelector('.search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchTerm = document.querySelector('.search-input').value;

            if (searchTerm.trim()) {
                window.location.href = `/search.html?q=${encodeURIComponent(searchTerm)}`;
            }
        });
    }

    // 뉴스레터 구독 폼 제출
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.querySelector('.newsletter-input').value;

            if (email.trim()) {
                alert(`${email} 주소로 뉴스레터 구독 신청이 완료되었습니다.`);
                this.reset();
                // TODO: 실제 구독 API 호출
            }
        });
    }
}

// 모달 이벤트 리스너 설정
function setupModalListeners() {
    // 로그인 링크
    const loginLink = document.getElementById('login-link');
    if (loginLink) {
        loginLink.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('login-modal').style.display = 'flex';
        });
    }

    // 회원가입 링크
    const signupLink = document.getElementById('signup-link');
    if (signupLink) {
        signupLink.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('signup-modal').style.display = 'flex';
        });
    }

    // 모달 닫기 버튼
    document.querySelectorAll('.modal-close').forEach(close => {
        close.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        document.querySelectorAll('.modal').forEach(modal => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    });

    // 로그인 폼 제출
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;

            if (email.trim() && password.trim()) {
                // TODO: 실제 로그인 API 호출
                alert('로그인 시도 중...');
                document.getElementById('login-modal').style.display = 'none';
                this.reset();
            }
        });
    }

    // 회원가입 폼 제출
    const signupForm = document.getElementById('signup-form');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const name = document.getElementById('signup-name').value;
            const email = document.getElementById('signup-email').value;
            const password = document.getElementById('signup-password').value;
            const passwordConfirm = document.getElementById('signup-password-confirm').value;

            if (password !== passwordConfirm) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }

            if (name.trim() && email.trim() && password.trim()) {
                // TODO: 실제 회원가입 API 호출
                alert('회원가입 처리 중...');
                document.getElementById('signup-modal').style.display = 'none';
                this.reset();
            }
        });
    }
}