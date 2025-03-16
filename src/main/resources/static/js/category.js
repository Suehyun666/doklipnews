// 전역 변수
let currentCategory = '';
let currentSubcategory = 'all';
let currentPage = 1;
let totalPages = 1;

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    // URL에서 카테고리와 서브카테고리 파라미터 읽기
    const urlParams = new URLSearchParams(window.location.search);
    currentCategory = urlParams.get('category') || 'politics'; // 기본값: 정치
    currentSubcategory = urlParams.get('subcategory') || 'all'; // 기본값: 전체
    currentPage = parseInt(urlParams.get('page') || '1'); // 기본값: 1페이지

    // 카테고리와 서브카테고리 설정
    setCategoryAndSubcategory(currentCategory, currentSubcategory);

    // 기사 목록 불러오기
    loadArticles(currentCategory, currentSubcategory, currentPage);

    // 인기 뉴스 불러오기
    loadTrendingNews();

    // 오피니언 기사 불러오기
    loadOpinionArticles();

    // 네비게이션 이벤트 리스너 설정
    setupNavigationListeners();

    // 폼 제출 이벤트 리스너 설정
    setupFormListeners();
});

// 카테고리와 서브카테고리 설정 함수
function setCategoryAndSubcategory(category, subcategory) {
    // 여기에 카테고리 데이터가 있다고 가정
    const categoryInfo = categoryData[category];
    if (!categoryInfo) return;

    // 카테고리 제목과 설명 설정
    document.getElementById('category-title').textContent = categoryInfo.title;
    document.getElementById('category-description').textContent = categoryInfo.description;

    // 네비게이션 항목 활성화
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item.dataset.category === category) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    // 서브카테고리 네비게이션 설정
    const subcategoryNav = document.getElementById('subcategory-nav');
    subcategoryNav.innerHTML = '';

    Object.entries(categoryInfo.subcategories).forEach(([key, value]) => {
        const subItem = document.createElement('div');
        subItem.className = `subcategory-item${key === subcategory ? ' active' : ''}`;
        subItem.textContent = value;
        subItem.dataset.subcategory = key;
        subItem.onclick = () => changeSubcategory(key);
        subcategoryNav.appendChild(subItem);
    });
}

function changeSubcategory(subcategory) {
    // URL 변경하여 서버로 요청
    window.location.href = `/articles/category?category=${currentCategory}&subcategory=${subcategory}&page=0`;
}

// 기사 목록 불러오기 함수
function loadArticles(category, subcategory, page) {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('article-list').innerHTML = '';

    // API 엔드포인트 구성
    let url = `/api/articles?page=${page-1}&size=10`;
    if (category && category !== 'all') {
        url += `&category=${category}`;
    }
    if (subcategory && subcategory !== 'all') {
        url += `&subcategory=${subcategory}`;
    }

    // API 호출
    fetch(url)
        .then(response => response.json())
        .then(data => {
            document.getElementById('loading').style.display = 'none';

            // 기사 목록 렌더링
            const articles = data.content;
            const articleList = document.getElementById('article-list');

            if (articles.length === 0) {
                articleList.innerHTML = '<p class="no-article">해당 카테고리에 기사가 없습니다.</p>';
                return;
            }

            articles.forEach(article => {
                const articleCard = document.createElement('div');
                articleCard.className = 'article-card';
                articleCard.onclick = () => window.location.href = `/articles/${article.id}`;

                articleCard.innerHTML = `
          <div class="article-image-container">
            <img src="${article.imageUrl || '/images/default.jpg'}" alt="${article.title}" class="article-image">
          </div>
          <div class="article-content">
            <h3 class="article-title">${article.title}</h3>
            <p class="article-summary">${article.content.substring(0,
                    200)}${article.content.length > 200 ? '...' : ''}</p>
            <div class="article-meta">
              <div class="article-author">${article.author}</div>
              <div class="article-date">${formatDate(new Date(article.createdAt))}</div>
            </div>
          </div>
        `;

                articleList.appendChild(articleCard);
            });

            // 페이지네이션 업데이트
            updatePagination(data.totalPages, page);
        })
        .catch(error => {
            console.error('Error fetching articles:', error);
            document.getElementById('loading').style.display = 'none';
            document.getElementById('article-list').innerHTML =
                '<p class="error">기사를 불러오는 데 문제가 발생했습니다.</p>';
        });
}

// 날짜 포맷 함수
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 페이지네이션 업데이트 함수
function updatePagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    // 최대 5개 페이지 버튼 표시
    const maxPages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxPages / 2));
    let endPage = Math.min(totalPages, startPage + maxPages - 1);

    if (endPage - startPage < maxPages - 1) {
        startPage = Math.max(1, endPage - maxPages + 1);
    }

    // 이전 페이지 버튼
    if (currentPage > 1) {
        addPageButton(pagination, currentPage - 1, '&laquo;');
    }

    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
        addPageButton(pagination, i, i.toString(), i === currentPage);
    }

    // 다음 페이지 버튼
    if (currentPage < totalPages) {
        addPageButton(pagination, currentPage + 1, '&raquo;');
    }
}

// 페이지 버튼 추가 함수
function addPageButton(pagination, page, text, isActive = false) {
    const button = document.createElement('div');
    button.className = `page-item${isActive ? ' active' : ''}`;
    button.innerHTML = text;
    button.onclick = () => changePage(page);
    pagination.appendChild(button);
}

// 페이지 변경 함수
function changePage(page) {
    currentPage = page;
    loadArticles(currentCategory, currentSubcategory, currentPage);

    // URL 업데이트
    const url = new URL(window.location);
    url.searchParams.set('page', page);
    window.history.pushState({}, '', url);

    // 페이지 상단으로 스크롤
    window.scrollTo(0, 0);
}

// 네비게이션 이벤트 리스너 설정
function setupNavigationListeners() {
    // 카테고리 클릭 이벤트
    document.querySelectorAll('.nav-item').forEach(item => {
        if (!item.dataset.category) return;

        item.addEventListener('click', function() {
            window.location.href = `/category.html?category=${this.dataset.category}`;
        });
    });

    // 드롭다운 항목 클릭 이벤트
    document.querySelectorAll('.dropdown-item').forEach(item => {
        if (!item.dataset.subcategory) return;

        item.addEventListener('click', function(e) {
            e.stopPropagation(); // 상위 이벤트 전파 중지
            const category = this.parentElement.parentElement.dataset.category;
            window.location.href = `/category.html?category=${category}&subcategory=${this.dataset.subcategory}`;
        });
    });
}
function setupFormListeners() {
    // 검색 폼
    const searchForm = document.querySelector('.search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchTerm = document.querySelector('.search-input').value;
            if (searchTerm.trim()) {
                window.location.href = `/articles/search?q=${encodeURIComponent(searchTerm)}`;
            }
        });
    }

    // 뉴스레터 구독 폼
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