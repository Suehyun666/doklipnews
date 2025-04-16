const form = document.getElementById('signupForm');
const errorMessage = document.getElementById('errorMessage');

form.addEventListener('submit', function(e) {
    e.preventDefault();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        errorMessage.style.display = 'block';
        errorMessage.textContent = '비밀번호가 일치하지 않습니다.';
    } else {
        alert('회원가입 성공!');
        window.location.href = 'login';
    }
});