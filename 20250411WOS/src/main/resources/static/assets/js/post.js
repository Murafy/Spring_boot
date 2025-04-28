const page = new URLSearchParams(window.location.search).get('page') || 1;
const postListBody = document.getElementById('post-list-body');
const pagination = document.getElementById('pagination');

// 글쓰기 버튼 언어영역 추출 (전역변수 선언)
let currentLanguage;

document.addEventListener('DOMContentLoaded', function() {
    // CSS 동적 로딩 (페이지 로딩 완료 후 즉시 실행)
    const path = window.location.pathname;
    const language = path.split('/')[3]; // URL 경로에서 언어 추출, url의 / 을 기준으로 3번째 인덱스 추출
    const styleLink = document.getElementById('language-style');
	if (language) {
	    styleLink.onload = function() {
	        // CSS 로드 완료 시 'loading' 클래스 제거
	        document.body.classList.remove('loading');
	    };
	    styleLink.href = `/assets/css/board/${language}.css`;
	} else {
	    document.body.classList.remove('loading');
	}
	
    // 현재 언어 저장
    currentLanguage = language;

	// 게시글 목록 로드 (DOMContentLoaded 이벤트 리스너 안에서 호출)
	loadPosts(page, language);

	// 언어별 부제목 배열
	const javaSubtitles = [
	    "“ 맞아요. 자바는 프로그래밍 언어가 보여줄 수 있는 아주 좋은 사례입니다. 그러나 자바 어플리케이션은 절대 그렇게 만들지 말아야 하는 아주 좋은 사례입니다. ” <br> pixadel",
	    "“ 만약 여러분이 코딩을 할 수 있게 된다면, 앉은 자리에서 무언가를 만들어 낼 수 있고, 아무도 당신을 막을 수 없을 것이다. ” <br> Mark Zuckerberg(페이스북 CEO)",
	];

	const javaScriptSubtitles = [
	    "“ 자바스크립트의 가장 깊은 비밀은 매우 좋은 언어라는 것이다.” <br> Douglas Crockford",
	    "“ 당신은 모든 형태를 자바스크립트로 만들 수 있다. 난 그걸 매우 좋아한다.” <br> Brendan Eich",
	];

	const htmlcssSubtitles = [
	    "“ CSS는 매우 간단한 언어입니다. 단 하나의 규칙만 이해하면 됩니다.” <br> Chris Coyier",
	    "“ HTML은 웹의 근간이 되는 언어입니다. 하나의 아이디어에서 출발해 이제는 세계를 연결합니다.” <br> Tim Berners-Lee",
	];

	const springBootSubtitles = [
	    "“ Spring Boot는 생산성을 극대화하고 개발자 행복을 증진시키는 것을 목표로 합니다.” <br> Josh Long",
	    "“ 스프링을 사용하면 기술적인 복잡성보다 비즈니스 문제에 집중할 수 있습니다.” <br> Rod Johnson",
	];

	const oraclesqlSubtitles = [
	    "“ 데이터베이스는 정확함이 생명입니다. Oracle SQL은 바로 그 정확함을 제공합니다.” <br> Larry Ellison",
	    "“ 좋은 데이터 모델은 좋은 시스템의 기초입니다. SQL은 그 시작점입니다.” <br> Joe Celko",
	];

    // 랜덤 인덱스 선택
    const randomJavaIndex = Math.floor(Math.random() * javaSubtitles.length);
    const randomJavaScriptIndex = Math.floor(Math.random() * javaScriptSubtitles.length);
    const randomOracleIndex = Math.floor(Math.random() * oraclesqlSubtitles.length);
    const randomSpringIndex = Math.floor(Math.random() * springBootSubtitles.length);
    const randomhtmlcssIndex = Math.floor(Math.random() * htmlcssSubtitles.length);

    // 타이틀 역할인 <h1> 태그 요소 찾기 (id 기반으로 찾음.)
    const boardTitleElement = document.getElementById('title');
    const boardSubTtileElemet = document.getElementById('subtitle');

    // 언어 정보에 따라 <h1> 태그의 내용 변경
    if (boardTitleElement && boardSubTtileElemet) {
        switch (language) {
            case 'java':
                titleText = '☕ 자바 게시판';
                boardSubTtileElemet.innerHTML = javaSubtitles[randomJavaIndex];
                break;
            case 'javascr':
                titleText = '🚀 자바스크립트 게시판';
                boardSubTtileElemet.innerHTML = javaScriptSubtitles[randomJavaScriptIndex];
                break;
            case 'htmlcss':
                titleText = '🌐 HTML/CSS 게시판';
                boardSubTtileElemet.innerHTML = htmlcssSubtitles[randomhtmlcssIndex];
                break;
            case 'orasql':
                titleText = '📊 Oracle SQL 게시판';
                boardSubTtileElemet.innerHTML = oraclesqlSubtitles[randomOracleIndex];
                break;
            case 'springboot':
                titleText = '🍃 Spring Boot 게시판';
                boardSubTtileElemet.innerHTML = springBootSubtitles[randomSpringIndex];
                break;
        }
        boardTitleElement.innerHTML = titleText;
    }
});

// 글쓰기 버튼 클릭 시 실행되는 함수
function writePost() {
    // 세션 스토리지에서 사용자 정보 확인
    const userId = sessionStorage.getItem('userid');
    
    // userId가 있으면 로그인된 상태로 처리
    if (userId) {
        window.location.href = `/write?language=${currentLanguage}`;
    } else {
        // 서버에서 세션 확인 (추가 검증)
        fetchWithErrorHandling('/api/user/status')
            .then(data => {
                if (data.isLoggedIn) {
                    // 세션 스토리지에는 없지만 서버에는 세션이 있는 경우
                    window.location.href = `/write?language=${currentLanguage}`;
                } else {
                    if (confirm('로그인이 필요합니다. 로그인 하시겠습니까?')) {
                        window.location.href = `/main?showLoginModal=true&redirect=/main/board/${currentLanguage}`;
                    }
                }
            })
            .catch(error => {
                console.error('로그인 상태 확인 중 오류:', error);
                if (confirm('로그인 상태를 확인할 수 없습니다. 로그인 페이지로 이동하시겠습니까?')) {
                    window.location.href = '/main?showLoginModal=true';
                }
            });
    }
}

// 게시글 목록을 불러오는 함수, 페이지 번호와 언어 정보를 파라미터로 받음
function loadPosts(page, language) {
    // fetchWithErrorHandling 함수 사용
    fetchWithErrorHandling(`/api/board/${language}?page=${page}`)
        .then(data => {
            console.log('게시글 목록 데이터 확인 : {}', data);
            renderPosts(data.list, language);
            renderPagination(data);
        })
        .catch(error => {
            console.error('데이터를 불러오는 중 오류 발생:', error);
            // 오류 메시지 표시
            postListBody.innerHTML = `
                <div class="col-12 text-center py-5">
                    <div class="empty-state">
                        <i class="fas fa-exclamation-triangle fa-3x mb-3 text-danger"></i>
                        <h3>데이터를 불러올 수 없습니다</h3>
                        <p class="text-muted">${error.message || '서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.'}</p>
                        <button class="btn btn-primary mt-3" onclick="location.reload()">
                            <i class="fas fa-sync-alt"></i> 새로고침
                        </button>
                    </div>
                </div>`;
        });
}

// 글쓰기 함수
function writePost() {
    // JWT 인증 상태 확인
    if (jwtUtils.isLoggedIn()) {
        window.location.href = `/write?language=${currentLanguage}`;
    } else {
        if (confirm('로그인이 필요합니다. 로그인 하시겠습니까?')) {
            window.location.href = `/main?showLoginModal=true&redirect=/main/board/${currentLanguage}`;
        }
    }
}

// 언어별 색상 및 아이콘 정의
function getLanguageStyle(language) {
    const styles = {
        java: { color: '#FF8C00', icon: '☕', label: 'Java' },
        javascr: { color: '#3498db', icon: '🚀', label: 'JavaScript' },
        htmlcss: { color: '#0080ff', icon: '🌐', label: 'HTML/CSS' },
        orasql: { color: '#f62c2c', icon: '📊', label: 'Oracle SQL' },
        springboot: { color: '#2e8b57', icon: '🍃', label: 'Spring Boot' }
    };
    
    return styles[language] || { color: '#6c757d', icon: '📝', label: language };
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = Math.floor((now - date) / 1000); // 초 단위 차이
    
    // 1일 이내: "n분 전", "n시간 전" 표시
    if (diff < 60) return '방금 전';
    if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
    
    // 이외에는 년-월-일 형식으로 표시
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

// 게시글 내용 요약 함수 (HTML 태그, 코드 블록 제거)
function getSummary(content, maxLength = 100) {
    if (!content) return '간단한 게시글 설명이 들어갈 자리입니다.';
    
    // HTML 태그 및 코드 블록 제거
    const strippedContent = content
        .replace(/```[\s\S]*?```/g, '') // 코드 블록 제거
        .replace(/<[^>]*>/g, '')        // HTML 태그 제거
        .replace(/#+\s*[^\n]+/g, '')    // 마크다운 헤더 제거
        .trim();
    
    return strippedContent.length > maxLength 
        ? strippedContent.substring(0, maxLength) + '...' 
        : strippedContent;
}

// 게시글 목록을 구현하는 함수 
// 파라미터 활용을위해 language 를 인자로 받음
function renderPosts(posts, language) {
    postListBody.innerHTML = ''; // 컨테이너 초기화
    
    if (!posts || posts.length === 0) {
        // 게시글이 없는 경우
        postListBody.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="empty-state">
                    <i class="fas fa-inbox fa-3x mb-3 text-muted"></i>
                    <h3>아직 게시글이 없습니다</h3>
                    <p class="text-muted">첫 번째 게시글을 작성해보세요!</p>
                    <button class="btn btn-primary mt-3" onclick="writePost()">
                        <i class="fas fa-pencil-alt"></i> 글쓰기
                    </button>
                </div>
            </div>`;
        return;
    }
    
    // 언어 스타일 가져오기
    const langStyle = getLanguageStyle(language);
    
    posts.forEach(dto => {
        // 각 요소를 수동으로 생성하여 안전하게 구성
        const card = document.createElement('div');
        card.classList.add('col');
        
        // 카드 컨테이너 생성
        const cardContainer = document.createElement('div');
        cardContainer.classList.add('card', 'board-card', 'h-100');
        
        // 썸네일 이미지 생성
        const img = document.createElement('img');
        img.src = dto.thumbnail || '/thumbnail/noimage.jpg';
        img.alt = '썸네일 이미지';
        img.classList.add('card-img-top');
        
        // 카드 본문 생성
        const cardBody = document.createElement('div');
        cardBody.classList.add('card-body');
        
        // 언어 태그 추가
        const langTag = document.createElement('span');
        langTag.classList.add('language-tag');
        langTag.style.backgroundColor = langStyle.color;
        langTag.innerHTML = `${langStyle.icon} ${langStyle.label}`;
        
        // 제목 생성 (링크 포함)
        const title = document.createElement('h5');
        title.classList.add('card-title', 'mt-2');
        const titleLink = document.createElement('a');
        titleLink.href = `/read?language=${language}&idx=${dto.idx}&page=${page}`;
        titleLink.textContent = dto.title || '제목 없음';
        title.appendChild(titleLink);
        
        // 내용 생성
        const content = document.createElement('p');
        content.classList.add('card-text');
        content.textContent = getSummary(dto.content);
        
        // 작성자 정보 생성
        const infoFooter = document.createElement('div');
        infoFooter.classList.add('card-footer', 'bg-transparent', 'border-top', 'py-3');
        
        // 작성자 정보 - 메타 데이터
        const metaInfo = document.createElement('div');
        metaInfo.classList.add('card-meta', 'd-flex', 'justify-content-between', 'align-items-center');
        
        // 작성자
        const authorInfo = document.createElement('div');
        authorInfo.innerHTML = `<i class="fas fa-user"></i> ${dto.writer || '익명'}`;
        
        // 시간 및 조회수 정보
        const timeViews = document.createElement('div');
        const createDate = formatDate(dto.createat);
        timeViews.innerHTML = `
            <span><i class="far fa-clock"></i> ${createDate}</span>
            <span class="ms-2"><i class="fas fa-eye"></i> ${dto.readcount || 0}</span>
        `;
        
        metaInfo.appendChild(authorInfo);
        metaInfo.appendChild(timeViews);
        
        // 버튼 영역
        const buttonFooter = document.createElement('div');
        buttonFooter.classList.add('card-footer', 'bg-transparent', 'border-top', 'text-end', 'py-3');
        
        const detailButton = document.createElement('a');
        detailButton.href = `/read?language=${language}&idx=${dto.idx}&page=${page}`;
        detailButton.classList.add('btn', 'btn-sm', 'action-btn', 'primary-btn');
        detailButton.innerHTML = '<i class="fas fa-book-reader"></i> 자세히 보기';
        
        // 요소 조립
        buttonFooter.appendChild(detailButton);
        
        cardBody.appendChild(langTag);
        cardBody.appendChild(title);
        cardBody.appendChild(content);
        
        cardContainer.appendChild(img);
        cardContainer.appendChild(cardBody);
        cardContainer.appendChild(infoFooter);
        infoFooter.appendChild(metaInfo);
        cardContainer.appendChild(buttonFooter);
        
        card.appendChild(cardContainer);
        postListBody.appendChild(card);
    });
}

// 페이지네이션을 구현하는 함수, data를 인자로 받음 
function renderPagination(pageInfo) {
    console.log('페이지네이션 정보:', pageInfo);
    pagination.innerHTML = '';
    
    // 페이지네이션이 필요 없는 경우
    if (pageInfo.totalPages <= 1) return;

    // << 버튼 (첫 페이지)
    const firstPageItem = document.createElement('li');
    firstPageItem.className = 'page-item' + (pageInfo.currentPage === 1 ? ' disabled' : '');
    const firstPageLink = document.createElement('a');
    firstPageLink.className = 'page-link';
    firstPageLink.href = pageInfo.currentPage === 1 ? 'javascript:void(0)' : `?page=1`;
    firstPageLink.innerHTML = '<i class="fas fa-angle-double-left"></i>';
    firstPageLink.setAttribute('aria-label', '처음 페이지');
    firstPageItem.appendChild(firstPageLink);
    pagination.appendChild(firstPageItem);

    // < 버튼 (이전 페이지 그룹)
    if (pageInfo.startPage > 1) {
        const prevPageItem = document.createElement('li');
        prevPageItem.className = 'page-item';
        const prevPageLink = document.createElement('a');
        prevPageLink.className = 'page-link';
        prevPageLink.href = `?page=${pageInfo.startPage - 1}`;
        prevPageLink.innerHTML = '<i class="fas fa-angle-left"></i>';
        prevPageLink.setAttribute('aria-label', '이전 페이지 그룹');
        prevPageItem.appendChild(prevPageLink);
        pagination.appendChild(prevPageItem);
    }

    // 페이지 번호 버튼들
    for (let i = pageInfo.startPage; i <= pageInfo.endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = 'page-item' + (page == i ? ' active' : '');
        
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = page == i ? 'javascript:void(0)' : `?page=${i}`;
        pageLink.innerHTML = i;
        pageLink.setAttribute('aria-label', `${i} 페이지`);
        
        if (page == i) {
            pageLink.setAttribute('aria-current', 'page');
        }
        
        pageItem.appendChild(pageLink);
        pagination.appendChild(pageItem);
    }

    // > 버튼 (다음 페이지 그룹)
    if (pageInfo.endPage < pageInfo.totalPages) {
        const nextPageItem = document.createElement('li');
        nextPageItem.className = 'page-item';
        const nextPageLink = document.createElement('a');
        nextPageLink.className = 'page-link';
        nextPageLink.href = `?page=${pageInfo.endPage + 1}`;
        nextPageLink.innerHTML = '<i class="fas fa-angle-right"></i>';
        nextPageLink.setAttribute('aria-label', '다음 페이지 그룹');
        nextPageItem.appendChild(nextPageLink);
        pagination.appendChild(nextPageItem);
    }

    // >> 버튼 (마지막 페이지)
    const lastPageItem = document.createElement('li');
    lastPageItem.className = 'page-item' + (pageInfo.currentPage === pageInfo.totalPages ? ' disabled' : '');
    const lastPageLink = document.createElement('a');
    lastPageLink.className = 'page-link';
    lastPageLink.href = pageInfo.currentPage === pageInfo.totalPages ? 'javascript:void(0)' : `?page=${pageInfo.totalPages}`;
    lastPageLink.innerHTML = '<i class="fas fa-angle-double-right"></i>';
    lastPageLink.setAttribute('aria-label', '마지막 페이지');
    lastPageItem.appendChild(lastPageLink);
    pagination.appendChild(lastPageItem);
}