// 좋아요 버튼
const likeButton = document.querySelector('.like-button');
const adminActions = document.getElementById('adminActions');

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

// 유튜브 링크 처리
processYoutubeLinks();

// 키보드 단축키 핸들러 추가 (Ctrl+Shift+?)
document.addEventListener('keydown', function(event) {
    if (event.ctrlKey && event.shiftKey && event.key === '?') {
        event.preventDefault(); // 기본 브라우저 동작 방지

        // admin-actions 요소의 display 상태를 토글
        if (adminActions.style.display === 'flex') {
            adminActions.style.display = 'none';
        } else {
            adminActions.style.display = 'flex';
        }
    }
});

// 유튜브 링크를 임베드로 변환하는 함수
function processYoutubeLinks() {
    const articleText = document.querySelector('.article-text');
    const articleImageContainer = document.querySelector('.article-image-container');
    if (!articleText) return;

    // 기사 본문에 포함된 유튜브 링크 검색
    const paragraphs = articleText.getElementsByTagName('p');
    for (let i = 0; i < paragraphs.length; i++) {
        const paragraph = paragraphs[i];
        const text = paragraph.textContent || '';

        if (text.includes('youtu.be/') || text.includes('youtube.com/watch')) {
            const videoId = extractYoutubeId(text);
            if (videoId) {
                const embedContainer = document.createElement('div');
                embedContainer.className = 'youtube-container';
                embedContainer.innerHTML = `<iframe src="https://www.youtube.com/embed/${videoId}" frameborder="0" allowfullscreen></iframe>`;

                // 대표 이미지가 있으면 그 위에 배치하고, 없으면 첫 번째 단락 위에 배치
                if (articleImageContainer) {
                    // 대표 이미지가 있으면 해당 이미지 컨테이너를 숨김
                    articleImageContainer.style.display = 'none';
                    articleImageContainer.parentNode.insertBefore(embedContainer, articleImageContainer);
                } else {
                    // 대표 이미지가 없으면 본문 맨 위에 삽입
                    articleText.insertBefore(embedContainer, articleText.firstChild);
                }

                // 첫 번째 유튜브 링크만 처리하고 종료 (여러 비디오가 있는 경우 한 개만 처리)
                break;
            }
        }
    }
}

// 유튜브 링크에서 비디오 ID 추출
function extractYoutubeId(text) {
    // youtu.be 형식
    let match = text.match(/youtu\.be\/([a-zA-Z0-9_-]{11})/);
    if (match && match[1]) return match[1];

    // youtube.com/watch 형식
    match = text.match(/youtube\.com\/watch\?v=([a-zA-Z0-9_-]{11})/);
    if (match && match[1]) return match[1];

    // youtube.com/embed 형식
    match = text.match(/youtube\.com\/embed\/([a-zA-Z0-9_-]{11})/);
    if (match && match[1]) return match[1];

    return null;
}

// 공유 기능
function shareArticle() {
    // 현재 페이지 URL
    const url = window.location.href;

    // 문서의 제목 (h1 태그에서 가져옴)
    const title = document.querySelector('.article-title').textContent;

    // 웹 공유 API 사용 (지원되는 브라우저의 경우)
    if (navigator.share) {
        navigator.share({
            title: title,
            url: url
        }).catch(console.error);
    } else {
        // 클립보드에 복사
        const tempInput = document.createElement('input');
        document.body.appendChild(tempInput);
        tempInput.value = url;
        tempInput.select();
        document.execCommand('copy');
        document.body.removeChild(tempInput);

        alert('기사 주소가 클립보드에 복사되었습니다.');
    }
}

// 댓글 기능 관련 자바스크립트
function showDeleteForm(commentId) {
    // 삭제 폼 표시
    const deleteForm = document.getElementById(`delete-form-${commentId}`);
    if (deleteForm) {
        deleteForm.style.display = 'block';
    }
}

function hideDeleteForm(commentId) {
    // 삭제 폼 숨기기
    const deleteForm = document.getElementById(`delete-form-${commentId}`);
    if (deleteForm) {
        deleteForm.style.display = 'none';
    }
}

function showReplyForm(commentId) {
    alert('대댓글 기능은 현재 개발 중입니다.');
}