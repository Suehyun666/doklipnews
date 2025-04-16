const form = document.getElementById('loginForm');
const errorMessage = document.getElementById('errorMessage');

form.addEventListener('submit', function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // 로그인 유효성 검사 (예시: 사용자명 = 'user', 비밀번호 = 'password')
    if (username === 'user' && password === 'password') {
        alert('로그인 성공!');
        return "article";
        // 로그인 성공 후 처리 로직 (예: 다른 페이지로 리디렉션)
    } else {
        errorMessage.style.display = 'block';
        window.location.href = '/main';
    }
});
const forgotPasswordLink = document.querySelector('.forgot-password');
forgotPasswordLink.addEventListener('click', function(e) {
    e.preventDefault();
    alert('어쩌라구요 기억해내세요.');
});