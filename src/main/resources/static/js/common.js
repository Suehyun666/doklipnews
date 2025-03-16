// 날짜 포맷 함수
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 모달 설정 함수
document.addEventListener('DOMContentLoaded', function() {
    // 모달 닫기 버튼 이벤트
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
                // 로그인 POST 요청 보내기
                fetch('/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email, password }),
                })
                    .then(response => {
                        if (response.ok) {
                            window.location.reload();
                        } else {
                            alert('로그인에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('로그인 처리 중 오류가 발생했습니다.');
                    });

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
                // 회원가입 POST 요청 보내기
                fetch('/signup', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ name, email, password }),
                })
                    .then(response => {
                        if (response.ok) {
                            alert('회원가입이 완료되었습니다. 로그인해주세요.');
                            document.getElementById('signup-modal').style.display = 'none';
                            document.getElementById('login-modal').style.display = 'flex';
                        } else {
                            alert('회원가입에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('회원가입 처리 중 오류가 발생했습니다.');
                    });

                document.getElementById('signup-modal').style.display = 'none';
                this.reset();
            }
        });
    }
});

// 인기 뉴스 불러오기
function loadTrendingNews() {
    fetch('/api/articles/trending')
        .then(response => response.json())
        .then(articles => {
            const trendingList = document.getElementById('trending-list');
            trendingList.innerHTML = '';

            articles.forEach((article, index) => {
                const li = document.createElement('li');
                li.innerHTML = `<span class="number">${index + 1}</span> ${article.title}`;
                li.onclick = () => window.location.href = `/articles/${article.id}`;
                trendingList.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error fetching trending news:', error);
        });
}

// 오피니언 기사 불러오기
function loadOpinionArticles() {
    fetch('/api/articles?category=opinion&size=4')
        .then(response => response.json())
        .then(data => {
            const opinionList = document.getElementById('opinion-list');
            opinionList.innerHTML = '';

            data.content.forEach(article => {
                const li = document.createElement('li');
                li.textContent = article.title;
                li.onclick = () => window.location.href = `/articles/${article.id}`;
                opinionList.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error fetching opinion articles:', error);
        });
}