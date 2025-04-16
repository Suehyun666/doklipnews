$(document).ready(function() {
    // Summernote 에디터 초기화
    $('#summernote').summernote({
        height: 500,                 // 에디터 높이
        minHeight: 300,              // 최소 높이
        maxHeight: 800,              // 최대 높이
        focus: true,                 // 에디터 로딩후 포커스 설정
        lang: 'ko-KR',               // 한글 설정
        toolbar: [
            ['style', ['style']],
            ['font', ['bold', 'underline', 'clear']],
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture', 'video']],
            ['view', ['fullscreen', 'codeview', 'help']]
        ],
        callbacks: {
            onImageUpload: function(files) {
                // 이미지 업로드 처리
                for (let i = 0; i < files.length; i++) {
                    uploadSummernoteImageFile(files[i]);
                }
            }
        }
    });

    // 이미지 미리보기
    $('#imageFile').change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#imagePreview').html('<img src="' + e.target.result + '" class="preview-image" />');
            };
            reader.readAsDataURL(file);
        } else {
            $('#imagePreview').html('');
        }
    });

    // 서머노트 이미지 업로드 함수
    function uploadSummernoteImageFile(file) {
        const data = new FormData();
        data.append("file", file);
        $.ajax({
            data: data,
            type: "POST",
            url: "/api/upload/image",
            contentType: false,
            processData: false,
            success: function(data) {
                $('#summernote').summernote('insertImage', data.url);
            },
            error: function(xhr, status, error) {
                console.error("이미지 업로드 실패:", error);
                alert("이미지 업로드에 실패했습니다.");
            }
        });
    }

    // 삭제 버튼 클릭 이벤트
    $('#deleteBtn').click(function() {
        if (confirm('정말로 이 기사를 삭제하시겠습니까?')) {
            $('#deleteForm').submit();
        }
    });

    // 폼 제출 전 유효성 검사
    $('#articleForm').submit(function(e) {
        if ($('#title').val().trim() === '') {
            alert('제목을 입력해주세요.');
            e.preventDefault();
            return false;
        }

        if ($('#category').val() === '') {
            alert('카테고리를 선택해주세요.');
            e.preventDefault();
            return false;
        }

        if ($('#author').val().trim() === '') {
            alert('작성자를 입력해주세요.');
            e.preventDefault();
            return false;
        }

        const content = $('#summernote').summernote('code');
        if (content.trim() === '' || content === '<p><br></p>') {
            alert('내용을 입력해주세요.');
            e.preventDefault();
            return false;
        }

        return true;
    });
});