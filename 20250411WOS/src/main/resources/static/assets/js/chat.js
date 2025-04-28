// 전역 변수
let stompClient = null;
let username = '';
let userCount = 0;
let unreadCount = 0;

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    // 로그인 상태 확인
    checkLoginStatus().then(initialize);
    
    // 메시지 폼 제출 이벤트 리스너
    const messageForm = document.getElementById('messageForm');
    if (messageForm) {
        messageForm.addEventListener('submit', sendMessage);
    }
});

// 로그인 상태 확인 (JWT 기반으로 변경)
function checkLoginStatus() {
    // jwtUtils 객체 사용 (common.js에 정의됨)
    if (typeof jwtUtils !== 'undefined' && jwtUtils.isLoggedIn()) {
        // 로컬 스토리지에서 사용자 정보 가져오기
        const user = jwtUtils.getUser();
        if (user) {
            username = user.nickname || user.id || '익명';
            return Promise.resolve(true);
        }
    }
    
    // 서버에서 직접 확인 (fallback)
    return fetch('/api/user/status')
        .then(response => response.json())
        .then(data => {
            if (data.isLoggedIn) {
                username = data.nickname || data.userId || '익명';
                return true;
            } else {
                // 로그인 안된 경우 익명 이름 생성
                username = '손님_' + generateRandomId();
                return false;
            }
        })
        .catch(error => {
            console.error('로그인 상태 확인 실패:', error);
            username = '손님_' + generateRandomId();
            return false;
        });
}

// 랜덤 ID 생성
function generateRandomId() {
    return Math.floor(1000 + Math.random() * 9000);
}

// 초기화 함수
function initialize(isLoggedIn) {
    // 로그인 여부에 따라 채팅창 표시/숨김
    if (isLoggedIn) {
        // WebSocket 연결
        connectWebSocket();
    } else {
        // 채팅창 표시 여부 확인 및 숨김 처리
        const chatContainer = document.querySelector('.chat-card');
        if (chatContainer) {
            chatContainer.style.display = 'none';
        }
        
        // 안내 메시지 표시
        const container = document.querySelector('.container.mt-4');
        if (container) {
            const loginMessage = document.createElement('div');
            loginMessage.className = 'alert alert-info text-center';
            loginMessage.innerHTML = '<i class="fas fa-info-circle"></i> 채팅 기능을 이용하려면 <a href="/main?showLoginModal=true&redirect=/chat">로그인</a>이 필요합니다.';
            container.prepend(loginMessage);
        }
    }
}

// WebSocket 연결 (JWT 토큰 포함)
function connectWebSocket() {
    const socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);
    
    // 디버깅 메시지 비활성화
    stompClient.debug = null;
    
    // JWT 토큰을 포함한 헤더 설정
    const headers = {};
    
    // JWT 토큰이 있으면 헤더에 추가
    if (typeof jwtUtils !== 'undefined') {
        const token = jwtUtils.getToken();
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }
    }
    
    stompClient.connect(headers, onConnected, onError);
}

// 연결 성공 시 콜백
function onConnected() {
    // 공개 채널 구독
    stompClient.subscribe('/topic/public', onMessageReceived);
    
    // 사용자 참여 이벤트 전송
    stompClient.send('/app/chat.join', {}, JSON.stringify({
        sender: username,
        type: 'JOIN'
    }));
    
    console.log('WebSocket 연결 성공');
}

// 연결 오류 시 콜백
function onError(error) {
    console.error('WebSocket 연결 오류:', error);
    
    // 인증 오류인 경우 로그인 페이지로 리다이렉트
    if (error.includes('Unauthorized') || error.includes('401')) {
        alert('로그인이 필요하거나 세션이 만료되었습니다. 다시 로그인해주세요.');
        if (typeof jwtUtils !== 'undefined') {
            jwtUtils.removeToken();
            jwtUtils.removeUser();
        }
        window.location.href = '/main?showLoginModal=true&redirect=/chat';
        return;
    }
    
    // 5초 후 재연결 시도
    setTimeout(connectWebSocket, 5000);
}

// 메시지 전송 함수
function sendMessage(event) {
    event.preventDefault();
    
    const messageInput = document.getElementById('message');
    const messageContent = messageInput.value.trim();
    
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    
    // 입력 필드로 포커스 이동
    messageInput.focus();
}

// 메시지 수신 처리
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const chatMessages = document.getElementById('chatMessages');
    
    if (!chatMessages) return;
    
    // 메시지 종류에 따라 처리
    switch(message.type) {
        case 'JOIN':
            addSystemMessage(message.content);
            userCount++;
            updateUserCount();
            break;
            
        case 'LEAVE':
            addSystemMessage(message.content);
            userCount = Math.max(0, userCount - 1);
            updateUserCount();
            break;
            
        case 'CHAT':
            addChatMessage(message);
            break;
    }
    
    // 스크롤을 최신 메시지로 이동
    chatMessages.scrollTop = chatMessages.scrollHeight;
    
    // 현재 페이지가 채팅 페이지가 아니면 알림 카운트 증가
    if (!document.hasFocus() || !window.location.pathname.includes('/chat')) {
        if (message.type === 'CHAT' && message.sender !== username) {
            unreadCount++;
            updateChatBadge();
        }
    }
}

// 시스템 메시지 추가
function addSystemMessage(content) {
    const chatMessages = document.getElementById('chatMessages');
    if (!chatMessages) return;
    
    const messageElement = document.createElement('div');
    messageElement.classList.add('system-message');
    messageElement.textContent = content;
    
    chatMessages.appendChild(messageElement);
}

// 채팅 메시지 추가
function addChatMessage(message) {
    const chatMessages = document.getElementById('chatMessages');
    if (!chatMessages) return;
    
    const timestamp = message.timestamp ? new Date(message.timestamp) : new Date();
    const timeString = timestamp.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    
    const messageElement = document.createElement('div');
    
    // 내 메시지인지 다른 사람의 메시지인지 확인
    if (message.sender === username) {
        messageElement.classList.add('message', 'my-message');
        
        messageElement.innerHTML = `
            <div class="message-content">${escapeHtml(message.content)}</div>
            <span class="message-time">${timeString}</span>
        `;
    } else {
        messageElement.classList.add('message', 'other-message');
        
        messageElement.innerHTML = `
            <span class="message-sender">${escapeHtml(message.sender)}</span>
            <div class="message-content">${escapeHtml(message.content)}</div>
            <span class="message-time">${timeString}</span>
        `;
    }
    
    chatMessages.appendChild(messageElement);
}

// 참가자 수 업데이트
function updateUserCount() {
    const userCountElement = document.getElementById('userCount');
    if (userCountElement) {
        userCountElement.textContent = userCount;
    }
}

// 채팅 배지 업데이트
function updateChatBadge() {
    // 미니 채팅 컴포넌트에 배지 추가
    const chatBadge = document.querySelector('.chat-badge');
    
    if (unreadCount > 0) {
        if (chatBadge) {
            chatBadge.textContent = unreadCount;
            chatBadge.style.display = 'flex';
        } else if (document.getElementById('chatToggleBtn')) {
            const badge = document.createElement('span');
            badge.classList.add('chat-badge');
            badge.textContent = unreadCount;
            document.getElementById('chatToggleBtn').appendChild(badge);
        }
    } else {
        if (chatBadge) {
            chatBadge.style.display = 'none';
        }
    }
}

// HTML 이스케이프 함수
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 채팅창 토글 함수
function toggleChat() {
    const chatContainer = document.querySelector('.mini-chat-container');
    if (!chatContainer) return;
    
    // 로그인 상태 확인
    if (typeof jwtUtils !== 'undefined' && !jwtUtils.isLoggedIn()) {
        // 로그인이 필요한 경우
        if (confirm('채팅 기능을 이용하려면 로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?')) {
            window.location.href = '/main?showLoginModal=true&redirect=' + window.location.pathname;
        }
        return;
    }
    
    if (chatContainer.style.display === 'none' || !chatContainer.style.display) {
        chatContainer.style.display = 'flex';
        // 읽음 처리
        unreadCount = 0;
        updateChatBadge();
    } else {
        chatContainer.style.display = 'none';
    }
}

// 페이지 나갈 때 처리
window.addEventListener('beforeunload', function() {
    if (stompClient) {
        // 사용자 퇴장 이벤트 전송
        stompClient.send('/app/chat.leave', {}, JSON.stringify({
            sender: username,
            type: 'LEAVE'
        }));
        
        // 연결 종료
        stompClient.disconnect();
    }
});