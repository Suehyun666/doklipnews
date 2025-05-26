// common-script.js - 공통 JavaScript 기능

document.addEventListener('DOMContentLoaded', function() {
    initMobileMenu();
    initSmoothScrolling();
    initBackToTop();
    initImageLazyLoading();
    initNewsletterForm();
});

// 모바일 메뉴 토글
function initMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            mobileMenu.classList.toggle('active');

            // 아이콘 변경
            const icon = mobileMenuBtn.querySelector('i');
            if (mobileMenu.classList.contains('active')) {
                icon.className = 'fas fa-times';
            } else {
                icon.className = 'fas fa-bars';
            }
        });

        // 메뉴 외부 클릭시 닫기
        document.addEventListener('click', function(e) {
            if (!mobileMenuBtn.contains(e.target) && !mobileMenu.contains(e.target)) {
                mobileMenu.classList.remove('active');
                const icon = mobileMenuBtn.querySelector('i');
                icon.className = 'fas fa-bars';
            }
        });
    }
}

// 부드러운 스크롤링
function initSmoothScrolling() {
    const links = document.querySelectorAll('a[href^="#"]');

    links.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');

            if (href === '#') return;

            e.preventDefault();

            const target = document.querySelector(href);
            if (target) {
                const headerHeight = document.querySelector('.header').offsetHeight;
                const targetPosition = target.offsetTop - headerHeight - 20;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// 맨 위로 버튼
function initBackToTop() {
    // 맨 위로 버튼 생성
    const backToTopBtn = document.createElement('button');
    backToTopBtn.innerHTML = '<i class="fas fa-chevron-up"></i>';
    backToTopBtn.className = 'back-to-top';
    backToTopBtn.setAttribute('aria-label', '맨 위로');
    document.body.appendChild(backToTopBtn);

    // 스크롤 이벤트 처리
    let isVisible = false;
    window.addEventListener('scroll', function() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        if (scrollTop > 300 && !isVisible) {
            backToTopBtn.classList.add('visible');
            isVisible = true;
        } else if (scrollTop <= 300 && isVisible) {
            backToTopBtn.classList.remove('visible');
            isVisible = false;
        }
    });

    // 클릭 이벤트
    backToTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
}

// 이미지 지연 로딩
function initImageLazyLoading() {
    if ('IntersectionObserver' in window) {
        const images = document.querySelectorAll('img[data-src]');

        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    }
}

// 뉴스레터 폼 처리
function initNewsletterForm() {
    const forms = document.querySelectorAll('.newsletter-form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const email = form.querySelector('.newsletter-input').value;
            const button = form.querySelector('.newsletter-button');

            if (!isValidEmail(email)) {
                showMessage('올바른 이메일 주소를 입력해주세요.', 'error');
                return;
            }

            // 버튼 상태 변경
            const originalText = button.textContent;
            button.textContent = '구독 중...';
            button.disabled = true;

            // 실제 구독 처리 (여기서는 시뮬레이션)
            setTimeout(() => {
                showMessage('뉴스레터 구독이 완료되었습니다!', 'success');
                form.querySelector('.newsletter-input').value = '';
                button.textContent = originalText;
                button.disabled = false;
            }, 2000);
        });
    });
}

// 이메일 유효성 검사
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 메시지 표시
function showMessage(message, type = 'info') {
    // 기존 메시지 제거
    const existingMessage = document.querySelector('.message-toast');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 새 메시지 생성
    const messageEl = document.createElement('div');
    messageEl.className = `message-toast message-${type}`;
    messageEl.textContent = message;

    // 메시지 스타일
    messageEl.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#22c55e' : type === 'error' ? '#ef4444' : '#3b82f6'};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 500;
        z-index: 1000;
        transform: translateX(100%);
        transition: transform 0.3s ease;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    `;

    document.body.appendChild(messageEl);

    // 애니메이션
    setTimeout(() => {
        messageEl.style.transform = 'translateX(0)';
    }, 100);

    // 자동 제거
    setTimeout(() => {
        messageEl.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (messageEl.parentNode) {
                messageEl.remove();
            }
        }, 300);
    }, 3000);
}

// 검색 기능
function initSearch() {
    const searchBtn = document.querySelector('.search-btn');
    const searchOverlay = createSearchOverlay();

    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            searchOverlay.style.display = 'flex';
            searchOverlay.querySelector('.search-input').focus();
        });
    }
}

function createSearchOverlay() {
    const overlay = document.createElement('div');
    overlay.className = 'search-overlay';
    overlay.innerHTML = `
        <div class="search-content">
            <div class="search-box">
                <input type="text" class="search-input" placeholder="검색어를 입력하세요...">
                <button class="search-close">&times;</button>
            </div>
            <div class="search-suggestions">
                <div class="search-category">
                    <h4>인기 검색어</h4>
                    <div class="suggestion-tags">
                        <span class="suggestion-tag">NewJeans</span>
                        <span class="suggestion-tag">DJMAX</span>
                        <span class="suggestion-tag">리뷰</span>
                        <span class="suggestion-tag">리듬게임</span>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 스타일
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        display: none;
        justify-content: center;
        align-items: flex-start;
        padding-top: 10vh;
        z-index: 1000;
    `;

    overlay.querySelector('.search-content').style.cssText = `
        background: white;
        border-radius: 12px;
        padding: 32px;
        width: 90%;
        max-width: 600px;
        max-height: 80vh;
        overflow-y: auto;
    `;

    overlay.querySelector('.search-box').style.cssText = `
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 32px;
    `;

    overlay.querySelector('.search-input').style.cssText = `
        flex: 1;
        padding: 16px 20px;
        border: 2px solid #e5e5e5;
        border-radius: 8px;
        font-size: 18px;
        outline: none;
    `;

    overlay.querySelector('.search-close').style.cssText = `
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
        padding: 8px;
        color: #666;
    `;

    // 이벤트 리스너
    overlay.querySelector('.search-close').addEventListener('click', () => {
        overlay.style.display = 'none';
    });

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            overlay.style.display = 'none';
        }
    });

    // ESC 키로 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && overlay.style.display === 'flex') {
            overlay.style.display = 'none';
        }
    });

    document.body.appendChild(overlay);
    return overlay;
}

// 카드 호버 효과
function initCardEffects() {
    const cards = document.querySelectorAll('.article-card, .album-card');

    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-4px)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
}

// 스크롤 애니메이션
function initScrollAnimations() {
    if ('IntersectionObserver' in window) {
        const animatedElements = document.querySelectorAll('.animate-on-scroll');

        const animationObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animated');
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });

        animatedElements.forEach(el => {
            animationObserver.observe(el);
        });
    }
}

// 다크모드 토글 (향후 확장용)
function initDarkMode() {
    const darkModeToggle = document.querySelector('.dark-mode-toggle');

    if (darkModeToggle) {
        const isDarkMode = localStorage.getItem('darkMode') === 'true';

        if (isDarkMode) {
            document.body.classList.add('dark-mode');
        }

        darkModeToggle.addEventListener('click', function() {
            document.body.classList.toggle('dark-mode');
            localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
        });
    }
}

// 페이지 로딩 상태
function showLoading() {
    const loader = document.createElement('div');
    loader.className = 'page-loader';
    loader.innerHTML = '<div class="loader-spinner"></div>';
    loader.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.9);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    `;

    const spinner = loader.querySelector('.loader-spinner');
    spinner.style.cssText = `
        width: 40px;
        height: 40px;
        border: 3px solid #f3f3f3;
        border-top: 3px solid #000;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    `;

    document.body.appendChild(loader);

    return {
        hide: () => {
            loader.style.opacity = '0';
            setTimeout(() => {
                if (loader.parentNode) {
                    loader.remove();
                }
            }, 300);
        }
    };
}

// CSS 애니메이션 추가
const style = document.createElement('style');
style.textContent = `
    .back-to-top {
        position: fixed;
        bottom: 30px;
        right: 30px;
        width: 50px;
        height: 50px;
        background: #000;
        color: white;
        border: none;
        border-radius: 50%;
        cursor: pointer;
        font-size: 18px;
        opacity: 0;
        visibility: hidden;
        transform: translateY(20px);
        transition: all 0.3s ease;
        z-index: 1000;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .back-to-top.visible {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
    }
    
    .back-to-top:hover {
        background: #333;
        transform: translateY(-2px);
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    .animate-on-scroll {
        opacity: 0;
        transform: translateY(30px);
        transition: all 0.6s ease;
    }
    
    .animate-on-scroll.animated {
        opacity: 1;
        transform: translateY(0);
    }
    
    @media (max-width: 768px) {
        .back-to-top {
            bottom: 20px;
            right: 20px;
            width: 45px;
            height: 45px;
            font-size: 16px;
        }
    }
`;

document.head.appendChild(style);

// 초기화 함수들 실행
document.addEventListener('DOMContentLoaded', function() {
    initSearch();
    initCardEffects();
    initScrollAnimations();
    initDarkMode();
});