// 채팅 컴포넌트 접근 제어를 위한 스크립트
document.addEventListener('DOMContentLoaded', function() {
    // 채팅 관련 UI 요소 선택
    const chatToggleBtn = document.getElementById('chatToggleBtn');
    const chatContainer = document.querySelector('.mini-chat-container');
    
    // 요소가 없으면 실행 중지
    if (!chatToggleBtn && !chatContainer) {
        return;
    }
    
    // 인증 상태 확인 함수 - axios 대신 fetch 사용
    function checkAuth() {
        return fetch('/api/auth/check', {
            credentials: 'include' // 쿠키 포함
        })
        .then(response => {
            if (!response.ok) {
                // 401, 403 등의 오류가 발생한 경우
                return { isAuthenticated: false };
            }
            return response.json();
        })
        .catch(error => {
            console.error('인증 상태 확인 실패:', error);
            return { isAuthenticated: false };
        });
    }
    
    // 인증 상태 확인
    checkAuth()
    .then(result => {
        if (result.isAuthenticated) {
            // 인증된 사용자: 채팅 UI 표시
            if (chatToggleBtn) chatToggleBtn.style.display = 'block';
            
            // 접근 권한이 있으므로 채팅 컴포넌트 로드
            loadChatComponent();
        } else {
            // 인증되지 않은 사용자: 채팅 UI 숨김
            if (chatToggleBtn) chatToggleBtn.style.display = 'none';
            if (chatContainer) chatContainer.style.display = 'none';
        }
    })
    .catch(error => {
        console.error('인증 상태 확인 실패:', error);
        // 오류 발생 시 안전하게 채팅 UI 숨김
        if (chatToggleBtn) chatToggleBtn.style.display = 'none';
        if (chatContainer) chatContainer.style.display = 'none';
    });
});

// 채팅 컴포넌트 로드 함수
function loadChatComponent() {
    // 이미 로드되었는지 확인
    if (document.querySelector('.mini-chat-container')) {
        return;
    }
    
    // chat-component.html 로드
    fetch('/assets/components/chat-component.html')
    .then(response => response.text())
    .then(html => {
        // DOM에 컴포넌트 추가
        const container = document.createElement('div');
        container.innerHTML = html;
        document.body.appendChild(container);
        
        // 토글 버튼 이벤트 핸들러 설정
        const toggleBtn = document.getElementById('chatToggleBtn');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', window.toggleChat || toggleChat);
        }
        
        // 메시지 폼 핸들러 설정
        const messageForm = document.getElementById('miniMessageForm');
        if (messageForm) {
            messageForm.addEventListener('submit', window.sendMiniMessage || sendMiniMessage);
        }
        
        // 채팅 컴포넌트 초기화
        if (typeof initializeMiniChat === 'function') {
            initializeMiniChat();
        }
        
        // 드래그 기능 활성화
        if (typeof enableDragForMiniChat === 'function') {
            enableDragForMiniChat();
        }
    })
    .catch(error => {
        console.error('채팅 컴포넌트 로드 실패:', error);
    });
}

// 토글 함수가 정의되지 않은 경우를 위한 폴백 함수
function toggleChat() {
    const chatContainer = document.querySelector('.mini-chat-container');
    if (!chatContainer) return;
    
    // 로그인 상태 확인 - checkAuth 함수 재사용
    checkAuth()
    .then(result => {
        if (result.isAuthenticated) {
            // 토글 동작
            if (chatContainer.style.display === 'none' || !chatContainer.style.display) {
                chatContainer.style.display = 'flex';
            } else {
                chatContainer.style.display = 'none';
            }
        } else {
            // 로그인이 필요한 경우
            if (confirm('채팅 기능을 이용하려면 로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?')) {
                // 로그인 후 돌아올 URL을 sessionStorage에 저장 (localStorage 대신)
                sessionStorage.setItem('redirectAfterLogin', window.location.pathname);
                window.location.href = '/main?showLoginModal=true';
            }
        }
    })
    .catch(error => {
        console.error('인증 상태 확인 실패:', error);
        // 오류 발생 시 안내
        alert('채팅 기능을 사용할 수 없습니다. 다시 시도해주세요.');
    });
}

// 메시지 전송 함수가 정의되지 않은 경우를 위한 폴백 함수
function sendMiniMessage(event) {
    event.preventDefault();
    alert('채팅 기능을 초기화하는 중입니다. 잠시 후 다시 시도해주세요.');
}

// 인증 상태 확인 함수 - 외부에서도 접근 가능하도록 전역 함수로 정의
function checkAuth() {
    return fetch('/api/auth/check', {
        credentials: 'include' // 쿠키 포함
    })
    .then(response => {
        if (!response.ok) {
            // 401, 403 등의 오류가 발생한 경우
            return { isAuthenticated: false };
        }
        return response.json();
    })
    .catch(error => {
        console.error('인증 상태 확인 실패:', error);
        return { isAuthenticated: false };
    });
}