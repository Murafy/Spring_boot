// 전역 변수로 선언하여 모든 함수에서 접근 가능하게 합니다
let currentLanguage;
let editor; // Ace Editor 인스턴스

document.addEventListener('DOMContentLoaded', function() {
    // URL에서 언어 파라미터 추출
    const urlParams = new URLSearchParams(window.location.search);
    const language = urlParams.get('language');
    
    // 전역 변수에 현재 언어 저장
    currentLanguage = language;
    
    // 언어별 CSS 스타일 적용
    const styleLink = document.getElementById('language-style');
    if (language) {
        styleLink.href = `/assets/css/board/write/${language}.css`;
        
        // 언어별 헤더 타이틀 설정
        const titleElement = document.getElementById('title');
        const subtitleElement = document.getElementById('subtitle');
        
        if (titleElement && subtitleElement) {
            switch (language) {
                case 'java':
                    titleElement.innerHTML = '☕ Java 게시판 글쓰기';
                    subtitleElement.innerHTML = '자바 관련 지식과 코드를 공유해보세요!';
                    break;
                case 'javascr':
                    titleElement.innerHTML = '🚀 JavaScript 게시판 글쓰기';
                    subtitleElement.innerHTML = '자바스크립트 관련 팁과 트릭을 공유해보세요!';
                    break;
                case 'htmlcss':
                    titleElement.innerHTML = '🌐 HTML/CSS 게시판 글쓰기';
                    subtitleElement.innerHTML = '웹 디자인과 레이아웃 지식을 공유해보세요!';
                    break;
                case 'orasql':
                    titleElement.innerHTML = '📊 Oracle SQL 게시판 글쓰기';
                    subtitleElement.innerHTML = '데이터베이스 지식과 쿼리 팁을 공유해보세요!';
                    break;
                case 'springboot':
                    titleElement.innerHTML = '🍃 Spring Boot 게시판 글쓰기';
                    subtitleElement.innerHTML = '스프링 부트 프레임워크 지식을 공유해보세요!';
                    break;
            }
        }
    }

    // 썸네일 미리보기 기능
    const thumbnailUrlInput = document.getElementById('thumbnailUrl');
    const thumbnailPreview = document.getElementById('thumbnailPreview');
    const previewImage = document.getElementById('previewImage');
    const clearThumbnailBtn = document.getElementById('clearThumbnail');
    
    thumbnailUrlInput.addEventListener('input', function() {
        const url = this.value.trim();
        if (url) {
            previewImage.src = url;
            thumbnailPreview.style.display = 'block';
        } else {
            thumbnailPreview.style.display = 'none';
        }
    });
    
    clearThumbnailBtn.addEventListener('click', function() {
        thumbnailUrlInput.value = '';
        thumbnailPreview.style.display = 'none';
    });
    
    // Ace Editor 초기화
    try {
        // ace는 글로벌 객체로 정의되어 있어야 합니다
        if (typeof ace !== 'undefined') {
            // 기본 설정으로 에디터 초기화
            editor = ace.edit("editor");
            
            // 에디터 설정
            editor.setTheme("ace/theme/monokai");
            editor.session.setMode("ace/mode/markdown");
            
            // 추가 옵션 설정
            editor.setOptions({
                enableBasicAutocompletion: true,
                enableLiveAutocompletion: true,
                showPrintMargin: false,
                fontSize: "14px"
            });
            
            // 초기 내용 설정
            editor.setValue("# 제목\n\n내용을 입력하세요.\n\n코드 블록은 아래 '코드 블록 삽입' 버튼을 사용하여 추가할 수 있습니다.");
            
            // 커서를 맨 앞으로 이동
            editor.navigateFileStart();
            editor.focus();
            
            console.log("Ace Editor가 성공적으로 초기화되었습니다.");
        } else {
            console.error("ace 객체를 찾을 수 없습니다. Ace Editor 라이브러리가 로드되었는지 확인하세요.");
        }
    } catch (e) {
        console.error("Ace Editor 초기화 중 오류 발생:", e);
    }
    
    // 코드 블록 삽입 기능
    const addCodeBlockBtn = document.getElementById('addCodeBlock');
    const codeLanguageSelect = document.getElementById('codeLanguage');
    
    addCodeBlockBtn.addEventListener('click', function() {
        if (!editor) {
            console.error("에디터가 초기화되지 않았습니다.");
            return;
        }
        
        const language = codeLanguageSelect.value;
        const codeTemplate = `\n\`\`\`${language}\n// 여기에 코드를 입력하세요\n\`\`\`\n`;
        
        // 현재 커서 위치에 코드 삽입
        editor.insert(codeTemplate);
        editor.focus();
    });
    
    // 폼 제출 처리
	const postForm = document.getElementById('postForm');
	    
	    postForm.addEventListener('submit', function(e) {
	        e.preventDefault();
	        
	        if (!editor) {
	            alert("에디터가 로드되지 않았습니다. 페이지를 새로고침하거나 나중에 다시 시도해주세요.");
	            return;
	        }
	        
	        const title = document.getElementById('postTitle').value.trim();
	        const content = editor.getValue();
	        
	        // 수동으로 유효성 검사
	        if (!title) {
	            alert('제목을 입력해주세요.');
	            return;
	        }
	        
	        if (!content) {
	            alert('내용을 입력해주세요.');
	            return;
	        }
	        
	        // 폼 제출을 위해 hidden input에 에디터 내용 설정
	        document.getElementById('postContent').value = content;
	        
	        // 여기서 JWT 인증을 포함한 AJAX로 서버에 데이터 전송
	        const postData = {
	            title: title,
	            content: content,
	            lang: currentLanguage,
	            thumbnail: document.getElementById('thumbnailUrl').value.trim()
	        };
	        
	        console.log('서버로 전송할 데이터:', postData);
	        
	        fetch('/api/post/save', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json'
	            },
	            body: JSON.stringify(postData)
	        })
	        .then(response => {
	            if (!response.ok) {
	                throw new Error('서버 응답 오류: ' + response.status);
	            }
	            return response.json();
	        })
	        .then(data => {
	            if (data.success) {
	                alert('게시글이 등록되었습니다!');
	                window.location.href = '/main/board/' + currentLanguage;
	            } else {
	                alert('게시글 등록 실패: ' + data.message);
	            }
	        })
	        .catch(error => {
	            console.error('오류:', error);
	            alert('게시글 등록 중 오류가 발생했습니다: ' + error.message);
	        });
	    });
	});