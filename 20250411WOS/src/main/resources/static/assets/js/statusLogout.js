// 네비게이션 바 업데이트 함수
function updateNav() {
    // 현재 인증 상태 확인 (fetch 사용)
    fetch('/api/user/status', {
        method: 'GET',
        credentials: 'include' // 쿠키를 포함하여 요청
    })
    .then(response => {
        if (!response.ok) {
            // 오류 응답인 경우 로그인되지 않은 상태로
            showDefaultNavItems();
            return Promise.reject('로그인 상태 확인 실패');
        }
        return response.json();
    })
    .then(data => {
        // 네비게이션 아이템 찾기
        const navItems = document.getElementById('navItems');
        if (!navItems) {
            console.error("navItems 요소를 찾을수 없음!");
            return;
        }
        
        navItems.innerHTML = ''; 
        
        if (data.isLoggedIn) {
            // 로그인된 상태 UI
            const nicknameItem = document.createElement('li');
            nicknameItem.classList.add('nav-item');
            nicknameItem.innerHTML = `<span class="nav-link">${data.nickname || data.id} 님 환영합니다!</span>`;
            navItems.appendChild(nicknameItem);
            
            // 마이페이지 링크 추가
            const myPageItem = document.createElement('li');
            myPageItem.classList.add('nav-item');
            const myPageLink = document.createElement('a');
            myPageLink.classList.add('nav-link');
            myPageLink.href = '#'; // URL 직접 접근 대신 클릭 이벤트 사용
            myPageLink.innerHTML = '<i class="fas fa-user-circle"></i> 마이페이지';
            
            // 클릭 이벤트 추가
            myPageLink.addEventListener('click', function(e) {
                e.preventDefault();
                
                // 쿠키에 JWT 토큰이 있는지 확인
                const hasToken = getCookie('accessToken') !== '';
                
                if (hasToken) {
                    // 토큰이 있으면 마이페이지로 이동
                    window.location.href = '/mypage';
                } else {
                    // 토큰이 없으면 로그인 모달 표시
                    showLoginModal();
                    localStorage.setItem('redirectAfterLogin', '/mypage');
                }
            });
            
            myPageItem.appendChild(myPageLink);
            navItems.appendChild(myPageItem);
            
            // 관리자 권한이 있는 경우 관리자 페이지 링크 추가
            if (data.role === 'ADMIN' || data.isAdmin) {
                const adminItem = document.createElement('li');
                adminItem.classList.add('nav-item');
                const adminLink = document.createElement('a');
                adminLink.classList.add('nav-link');
                adminLink.href = '#';
                adminLink.textContent = '관리자 페이지';
                
                adminLink.addEventListener('click', function(e) {
                    e.preventDefault();
                    
                    // 쿠키에 JWT 토큰이 있는지 확인
                    const hasToken = getCookie('accessToken') !== '';
                    
                    if (hasToken) {
                        // 토큰이 있으면 관리자 페이지로 이동
                        window.location.href = '/main/admin';
                    } else {
                        // 토큰이 없으면 로그인 모달 표시
                        showLoginModal();
                        localStorage.setItem('redirectAfterLogin', '/main/admin');
                    }
                });
                
                adminItem.appendChild(adminLink);
                navItems.appendChild(adminItem);
            }

            // 로그아웃 링크
            const logoutItem = document.createElement('li');
            logoutItem.classList.add('nav-item');
            const logoutLink = document.createElement('a');
            logoutLink.classList.add('nav-link');
            logoutLink.href = '#';
            logoutLink.textContent = '로그아웃';
            logoutLink.addEventListener('click', function (event) {
                event.preventDefault();
                logout();
            });
            logoutItem.appendChild(logoutLink);
            navItems.appendChild(logoutItem);
        } else {
            // 로그인되지 않은 상태 UI
            showDefaultNavItems();
        }
    })
    .catch(error => {
        console.error('인증 상태 확인 중 오류:', error);
        // 오류 발생 시 로그인되지 않은 상태로 처리
        showDefaultNavItems();
    });
}

// 기본 네비게이션 아이템 표시 (오류 발생 시)
function showDefaultNavItems() {
    const navItems = document.getElementById('navItems');
    if (!navItems) return;
    
    navItems.innerHTML = '';
    
    const loginItem = document.createElement('li');
    loginItem.classList.add('nav-item');
    loginItem.innerHTML = '<a class="nav-link" href="#" data-bs-toggle="modal" data-bs-target="#staticBackdrop">로그인</a>';
    navItems.appendChild(loginItem);

    const signupItem = document.createElement('li');
    signupItem.classList.add('nav-item');
    signupItem.innerHTML = '<a class="nav-link" href="/main/regist">회원가입</a>';
    navItems.appendChild(signupItem);
}

// 로그인 모달 표시 함수
function showLoginModal() {
    const loginModalElement = document.getElementById('staticBackdrop');
    if (loginModalElement) {
        const loginModal = new bootstrap.Modal(loginModalElement);
        loginModal.show();
    } else {
        // 모달이 없으면 로그인 페이지로 리다이렉트
        window.location.href = '/main?showLoginModal=true';
    }
}

// 로그아웃 함수
function logout() {
    fetch('/api/user/logout', {
        method: 'POST',
        credentials: 'include' // 쿠키 포함
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('로그아웃 처리 실패');
        }
        return response.json();
    })
    .then(data => {
        console.log('로그아웃 성공:', data);
        
        // 쿠키 삭제
        deleteCookie('accessToken');
        deleteCookie('refreshToken');
        
        // 로컬 스토리지에서 사용자 정보 삭제
        localStorage.removeItem('user');
        localStorage.removeItem('redirectAfterLogin');
        sessionStorage.removeItem('userid');
        
        alert('로그아웃 되었습니다.');
        
        // 메인 페이지로 리다이렉트
        window.location.href = '/main';
    })
    .catch(error => {
        console.error('로그아웃 실패:', error);
        
        // 쿠키 삭제
        deleteCookie('accessToken');
        deleteCookie('refreshToken');
        
        // 오류가 발생해도 로컬 데이터는 정리
        localStorage.removeItem('user');
        localStorage.removeItem('redirectAfterLogin');
        sessionStorage.removeItem('userid');
        
        alert('로그아웃 중 오류가 발생했지만, 로컬에서 로그아웃 처리되었습니다.');
        window.location.href = '/main';
    });
}

// 페이지 로드 시 네비게이션 바 업데이트
document.addEventListener('DOMContentLoaded', updateNav);