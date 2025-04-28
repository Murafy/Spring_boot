// 게시글 내용 처리 함수 (코드 블록 감지 및 처리)
function processContent(content) {
    if (!content) return '';
    
    // 마크다운 코드 블록 패턴 (Ace Editor에서 저장된 형식)
    const codeBlockRegex = /```([a-z]*)\s*([\s\S]*?)\s*```/g;
    
    // HTML과 마크다운이 혼합된 경우를 위한 추가 정규식
    const htmlCodeBlockRegex = /<p>\s*```([a-z]*)\s*([\s\S]*?)\s*```\s*<\/p>/gi;
    
    // 먼저 HTML 내부의 코드 블록 처리
    let processedContent = content.replace(htmlCodeBlockRegex, function(match, language, code) {
        // HTML 엔티티 및 태그 제거
        code = decodeHtmlEntities(code.replace(/<[^>]*>/g, ''));
        
        // 원본 코드 형식 보존
        code = code.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
        
        const lang = language || 'plaintext';
        
        return `<pre class="line-numbers"><code class="language-${lang}">${escapeHtml(code.trim())}</code></pre>
        <div class="code-header">${lang}</div>`;
    });
    
    // 순수 마크다운 코드 블록 처리
    processedContent = processedContent.replace(codeBlockRegex, function(match, language, code) {
        // 코드 정리
        code = code.trim();
        
        // 원본 코드 형식 보존
        code = code.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
        
        const lang = language || 'plaintext';
        
        return `<pre class="line-numbers"><code class="language-${lang}">${escapeHtml(code)}</code></pre>
        <div class="code-header">${lang}</div>`;
    });
    
    return processedContent;
}

// HTML 이스케이프 처리 함수
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// HTML 엔티티 디코딩 함수
function decodeHtmlEntities(text) {
    const textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    return textarea.value;
}

// 좋아요 상태 확인
function checkLikeStatus(postId) {
    fetchWithErrorHandling(`/api/post/like/status?idx=${postId}`)
        .then(data => {
            const isLiked = data.liked;
            
            // 좋아요 카운트 API 호출
            return fetchWithErrorHandling(`/api/post/likes?idx=${postId}`)
                .then(countData => {
                    updateLikeUI(isLiked, countData.count || 0);
                });
        })
        .catch(error => {
            console.error('좋아요 정보 로드 오류:', error);
            // 오류 시 기본값으로 설정
            updateLikeUI(false, 0);
        });
}

// 좋아요 UI 업데이트
function updateLikeUI(isLiked, count) {
    const likeButton = document.getElementById('likeButton');
    const likeCountElement = document.getElementById('likeCount');
    
    if (likeButton) {
        if (isLiked) {
            likeButton.classList.add('liked');
        } else {
            likeButton.classList.remove('liked');
        }
    }
    
    if (likeCountElement) {
        likeCountElement.textContent = `${count}명이 좋아합니다`;
    }
}

// 좋아요 토글
function toggleLike() {
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('idx');
    
    if (!postId) return;
    
    const likeButton = document.getElementById('likeButton');
    const isCurrentlyLiked = likeButton.classList.contains('liked');
    
    fetchWithErrorHandling(`/api/post/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            postIdx: postId,
            liked: !isCurrentlyLiked
        })
    })
    .then(data => {
        if (data) {
            updateLikeUI(data.liked, data.count || 0);
            
            if (data.liked) {
                createHeartAnimation();
            }
        }
    })
    .catch(error => {
        console.error('좋아요 업데이트 오류:', error);
    });
}

// 하트 애니메이션 생성
function createHeartAnimation() {
    const container = document.getElementById('heartContainer');
    if (!container) return;
    
    const likeButton = document.getElementById('likeButton');
    if (!likeButton) return;
    
    const buttonRect = likeButton.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();
    
    // 여러 개의 하트 생성 (5-8개)
    const heartsCount = 5 + Math.floor(Math.random() * 4);
    
    for (let i = 0; i < heartsCount; i++) {
        const heart = document.createElement('div');
        heart.className = 'heart';
        heart.innerHTML = '<i class="fas fa-heart"></i>';
        
        // 하트 위치 설정 (버튼 중앙으로부터)
        const offset = 30; // 최대 오프셋 (픽셀)
        const leftPos = (buttonRect.left - containerRect.left) + buttonRect.width/2 - 5;
        const randomX = leftPos + (Math.random() * offset * 2 - offset);
        
        heart.style.left = `${randomX}px`;
        heart.style.bottom = '5px';
        
        // 애니메이션 지연 및 속도 랜덤화
        heart.style.animationDuration = `${1.5 + Math.random()}s`;
        heart.style.animationDelay = `${Math.random() * 0.5}s`;
        
        // 하트 크기 랜덤화
        const scale = 0.8 + Math.random() * 0.6;
        heart.style.fontSize = `${scale}rem`;
        
        // 컨테이너에 추가
        container.appendChild(heart);
        
        // 애니메이션 완료 후 제거
        setTimeout(() => {
            heart.remove();
        }, 3000);
    }
} 

// 댓글 추가
function addComment() {
    const commentInput = document.getElementById('commentInput');
    const comment = commentInput.value.trim();
    
    if (!comment) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }
    
    // 예시 댓글 처리 (실제 구현 시 서버로 전송해야 함)
    const commentList = document.getElementById('commentList');
    const commentItem = document.createElement('li');
    commentItem.className = 'comment-item';
    
    commentItem.innerHTML = `
        <div class="comment-author">사용자</div>
        <div class="comment-text">${comment}</div>
        <div class="comment-date">${new Date().toLocaleDateString('ko-KR')}</div>
    `;
    
    commentList.appendChild(commentItem);
    commentInput.value = '';
    
    // 애니메이션 효과 
    commentItem.style.opacity = '0';
    commentItem.style.transform = 'translateY(20px)';
    commentItem.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    
    setTimeout(() => {
        commentItem.style.opacity = '1';
        commentItem.style.transform = 'translateY(0)';
    }, 10);
}

// 게시글 공유
function sharePost() {
    const url = window.location.href;
    
    if (navigator.share) {
        navigator.share({
            title: document.getElementById('postTitle').textContent,
            url: url
        })
        .catch(error => console.error('공유 오류:', error));
    } else {
        navigator.clipboard.writeText(url)
            .then(() => {
                alert('게시글 주소가 클립보드에 복사되었습니다.');
            })
            .catch(err => {
                console.error('클립보드 복사 실패:', err);
                prompt('이 게시글의 주소를 복사하세요:', url);
            });
    }
}

// 메인 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
    // URL에서 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const idx = urlParams.get('idx');
    const language = urlParams.get('language');
    
    console.log('게시글 파라미터:', { idx, language });

    if (idx) {
        // API로 게시글 데이터 가져오기 (fetchWithErrorHandling 사용)
        fetchWithErrorHandling(`/api/read?language=${language}&idx=${idx}`)
            .then(data => {
                console.log('데이터 수신:', Object.keys(data));
                
                // 게시글 제목
                document.getElementById('postTitle').innerHTML = data.title || '제목 없음';
                
                // 게시글 메타 정보
                document.getElementById('postWriter').innerHTML = data.writer || '작성자 없음';
                const date = data.createat ? new Date(data.createat) : new Date();
                document.getElementById('postDate').innerHTML = date.toLocaleDateString('ko-KR');
                document.getElementById('postReadcount').innerHTML = data.readcount || 0;
                
                // 게시글 내용 (코드 하이라이팅 처리)
                const processedContent = processContent(data.content || '내용이 없습니다.');
                document.getElementById('postBody').innerHTML = processedContent;
                
                // 썸네일 처리
                const postThumbnailElement = document.getElementById('postThumbnail');
                if (postThumbnailElement) {
                    if (data.thumbnail) {
                        postThumbnailElement.src = data.thumbnail;
                        postThumbnailElement.style.display = 'block';
                    } else {
                        postThumbnailElement.style.display = 'none';
                    }
                }
                
                // Prism.js로 코드 하이라이팅 적용
                if (typeof Prism !== 'undefined') {
                    Prism.highlightAll();
                    console.log('코드 하이라이팅 적용 완료');
                }
                
                // 로딩 인디케이터 숨기고 컨텐츠 표시
                document.getElementById('loadingSpinner').style.display = 'none';
                document.getElementById('postContent').style.display = 'block';
                
                console.log('게시글 렌더링 완료');
            })
            .catch(error => {
                console.error('게시글 로드 오류:', error);
                
                // 오류 메시지 표시
                document.getElementById('postTitle').innerHTML = '게시글을 불러올 수 없습니다.';
                document.getElementById('postBody').innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle"></i>
                        ${error.message || '오류가 발생했습니다. 잠시 후 다시 시도해주세요.'}
                    </div>
                    <div class="text-center mt-4">
                        <button class="btn btn-primary" onclick="location.reload()">
                            <i class="fas fa-sync-alt"></i> 새로고침
                        </button>
                        <a href="javascript:history.back()" class="btn btn-secondary ms-2">
                            <i class="fas fa-arrow-left"></i> 이전 페이지로
                        </a>
                    </div>
                `;
                
                // 오류 발생해도 로딩 인디케이터 숨기고 컨텐츠 표시
                document.getElementById('loadingSpinner').style.display = 'none';
                document.getElementById('postContent').style.display = 'block';
            });
            
        // 좋아요 상태 확인
        checkLikeStatus(idx);
    } else {
        document.getElementById('postTitle').innerHTML = '잘못된 접근입니다.';
        document.getElementById('postBody').innerHTML = `
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                게시글 ID가 제공되지 않았습니다.
            </div>
            <div class="text-center mt-4">
                <a href="javascript:history.back()" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> 이전 페이지로
                </a>
            </div>
        `;
        
        // 잘못된 접근이어도 로딩 인디케이터 숨기고 컨텐츠 표시
        document.getElementById('loadingSpinner').style.display = 'none';
        document.getElementById('postContent').style.display = 'block';
    }
});

// 코드 렌더링 후 명시적으로 하이라이팅 재호출
setTimeout(function() {
    if (typeof Prism !== 'undefined') {
        Prism.highlightAll();
    }
}, 500); // 모든 DOM 렌더링이 완료될 시간을 확보하기 위해 지연 실행