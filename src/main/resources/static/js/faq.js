// 현재 날짜 표시
document.addEventListener('DOMContentLoaded', function() {
    const dateElement = document.getElementById('current-date');
    const now = new Date();

    const days = ['일', '월', '화', '수', '목', '금', '토'];
    const dayOfWeek = days[now.getDay()];

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    dateElement.textContent = `${year}년 ${month}월 ${day}일 ${dayOfWeek}요일`;

    // 폼 제출 처리
    const contactForm = document.getElementById('contactForm');
    const successMessage = document.getElementById('successMessage');

    contactForm.addEventListener('submit', function(e) {
        e.preventDefault(); // 실제 폼 제출 방지

        // 폼 검증
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const subject = document.getElementById('subject').value;
        const message = document.getElementById('message').value;
        const privacy = document.getElementById('privacy').checked;

        if (!name || !email || !subject || !message || !privacy) {
            alert('필수 항목을 모두 입력해주세요.');
            return;
        }

        // 여기서 실제로는 서버에 데이터를 전송합니다.
        // 데모 목적으로 성공 메시지만 표시
        successMessage.style.display = 'block';
        contactForm.reset();

        // 5초 후에 성공 메시지 숨기기
        setTimeout(function() {
            successMessage.style.display = 'none';
        }, 5000);
    });
});