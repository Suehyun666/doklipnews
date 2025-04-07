document.addEventListener('DOMContentLoaded', function() {
    const likeButton = document.querySelector('.like-button');

    if (likeButton) {
        const articleId = likeButton.getAttribute('data-article-id');
        const likeCountElement = document.querySelector('.like-count');

        // 로컬 스토리지에서 좋아요 상태 가져오기
        const isLiked = localStorage.getItem(`liked_${articleId}`) === 'true';

        // 초기 상태 설정
        if (isLiked) {
            likeButton.classList.add('liked');
            likeButton.innerHTML = '<i class="fas fa-heart"></i> 좋아요 취소';
        }

        likeButton.addEventListener('click', function() {
            const isCurrentlyLiked = likeButton.classList.contains('liked');
            const url = isCurrentlyLiked
                ? `/articles/${articleId}/unlike`
                : `/articles/${articleId}/like`;

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    // 좋아요 수 업데이트
                    if (likeCountElement) {
                        likeCountElement.textContent = data.likeCount;
                    }

                    // 버튼 상태 업데이트
                    if (isCurrentlyLiked) {
                        likeButton.classList.remove('liked');
                        likeButton.innerHTML = '<i class="far fa-heart"></i> 좋아요';
                        localStorage.removeItem(`liked_${articleId}`);
                    } else {
                        likeButton.classList.add('liked');
                        likeButton.innerHTML = '<i class="fas fa-heart"></i> 좋아요 취소';
                        localStorage.setItem(`liked_${articleId}`, 'true');
                    }
                })
                .catch(error => {
                    console.error('좋아요 처리 중 오류가 발생했습니다:', error);
                });
        });
    }
});