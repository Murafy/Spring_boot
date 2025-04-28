// loginModal, loginInProgress 변수가 아직 선언되지 않은 경우에만 전역 스코프에 선언함
// (코드 중복 실행 방지 목적)
// 이 변수들은 여러 함수에서 공유되어야 하는 상태(모달 참조, 로그인 진행 상태)를 저장함
if (typeof loginModal === 'undefined') {
  var loginModal; // 모달제어를 위한 모달 참조변수
}
if (typeof loginInProgress === 'undefined') {
  var loginInProgress = false; // 로그인 여부를 추적하는 참조변수
}

// main 페이지 로딩 완료 후 실행:
// 1. 로그인 모달 객체 생성 및 준비.
// 2. 이전에 '아이디 기억하기'로 저장된 쿠키가 있으면 아이디 필드 채우기.
// 3. 모달이 닫힐 때 로그인 폼을 초기화하는 이벤트 리스너
// 4. 비밀번호 필드에서 Enter 키로 로그인하는 이벤트 리스너 
document.addEventListener('DOMContentLoaded', function() {
    // 로그인 모달 객체 생성 및 준비.
    const loginModalElement = document.getElementById('staticBackdrop');
    if (loginModalElement) {
        loginModal = new bootstrap.Modal(loginModalElement);
        
        // 모달이 닫힐 때 로그인 상태와 버튼 초기화
        loginModalElement.addEventListener('hidden.bs.modal', function () {
            resetLoginForm();
        });
        
        // 로그인 후 리디렉션 처리
        loginModalElement.addEventListener('shown.bs.modal', function () {
            // 로컬 스토리지에서 리디렉트 URL 확인
            const redirectUrl = localStorage.getItem('redirectAfterLogin');
            if (redirectUrl) {
                // hidden input에 저장하여 로그인 성공 후 사용
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.id = 'redirectAfterLogin';
                redirectInput.value = redirectUrl;
                loginModalElement.querySelector('form').appendChild(redirectInput);
            }
        });
    }
    
    // 아이디 기억하기
    const savedId = getCookie('rememberMeId');
    if (savedId) {
        const usernameInput = document.getElementById('username');
        if (usernameInput) {
            usernameInput.value = b64Decode(savedId);
            
            // 체크박스 체크
            const rememberCheckbox = document.getElementById('flexCheckDefault');
            if (rememberCheckbox) {
                rememberCheckbox.checked = true;
            }
        }
    }
    
    // 패스워드 입력 필드에서 엔터키 입력시 로그인
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                login();
            }
        });
    }
    
    // 로그인 폼에 submit 이벤트 리스너 추가
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault(); // 기본 폼 제출 방지
            login();
        });
    }
    
    // URL 파라미터에서 showLoginModal=true인 경우 모달 표시
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('showLoginModal') === 'true' && loginModal) {
        loginModal.show();
    }
});

// 모달 닫기 버튼 클릭 시 실행될 함수
function handleModalClose() {
    // 리다이렉트 파라미터가 있는지 확인
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('redirect')) {
        // 메인 페이지로 이동 (파라미터 제거)
        window.location.href = '/main';
    }
}

// 로그인 폼 초기화 함수 추가
function resetLoginForm() {
    // 로그인 진행 상태 초기화
    loginInProgress = false;
    
    // 오류 메시지 초기화
    const idMsg = document.getElementById('idMsg');
    const passwordMsg = document.getElementById('passwordMsg');
    
    if (idMsg) idMsg.textContent = '';
    if (passwordMsg) passwordMsg.textContent = '';
    
    // 로그인 버튼 상태 초기화
    const loginButton = document.querySelector('button[onclick="login()"]');
    if (loginButton) {
        loginButton.disabled = false;
        loginButton.innerHTML = '로그인';
    }
    
    // ID 기억하기가 체크되어 있지 않으면 입력 필드 초기화 (선택 사항)
    const rememberMeCheckbox = document.getElementById('flexCheckDefault');
    if (rememberMeCheckbox && !rememberMeCheckbox.checked) {
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        
        if (usernameInput) usernameInput.value = '';
        if (passwordInput) passwordInput.value = '';
    }
}

function login() {
    // 이미 로그인 진행 중이면 중복 요청 방지
    if (loginInProgress) {
        return;
    }
    
    loginInProgress = true;
    
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const idMsg = document.getElementById("idMsg");
    const passwordMsg = document.getElementById("passwordMsg");
    const rememberMeCheckbox = document.getElementById('flexCheckDefault');

    // 입력값 정리
    const id = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    // 오류 메시지 초기화
    if (idMsg) idMsg.textContent = '';
    if (passwordMsg) passwordMsg.textContent = '';

    // 기본 유효성 검사
    if (!id) {
        if (idMsg) idMsg.textContent = '아이디를 입력하세요.';
        if (usernameInput) usernameInput.focus();
        loginInProgress = false;
        return;
    }
    
    if (!password) {
        if (passwordMsg) passwordMsg.textContent = '비밀번호를 입력하세요.';
        if (passwordInput) passwordInput.focus();
        loginInProgress = false;
        return;
    }

    // 아이디 기억하기 처리
    if (rememberMeCheckbox && rememberMeCheckbox.checked) {
        setCookie('rememberMeId', b64Encode(id), 7);
    } else {
        deleteCookie('rememberMeId');
    }
    
    // 로그인 버튼 비활성화 및 로딩 표시
    const loginButton = document.querySelector('button[onclick="login()"]');
    let originalText = '';
    if (loginButton) {
        originalText = loginButton.innerHTML;
        loginButton.disabled = true;
        loginButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 로그인 중...';
    }
    
    // 명시적으로 POST 메서드로 로그인 요청
    fetch('/api/user/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: id, password: password }),
        credentials: 'include' // 쿠키를 포함하여 요청
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || '로그인 실패');
            });
        }
        return response.json();
    })
    .then(data => {
        // 로그인 성공 처리
        if (data.user) {
            // accessToken이 있으면 명시적으로 쿠키에 저장 (백업용)
            if (data.accessToken) {
                // 토큰 만료 시간을 계산 (일반적으로 서버에서 설정한 값)
                const expirationDays = 1; // 1일 (필요에 따라 조정)
                setCookie('accessToken', data.accessToken, expirationDays);
                
                // JWT 토큰을 localStorage에 저장하지 않음 (보안상 쿠키만 사용)
            }
            
            // 사용자 기본 정보만 localStorage에 저장 (토큰 제외)
            const userInfo = {
                id: data.user.id,
                nickname: data.user.nickname,
                role: data.user.role
            };
            localStorage.setItem('user', JSON.stringify(userInfo));
            
            // 성공 메시지 표시
            alert(`${data.user.nickname}님, 환영합니다!`);
            
            // 리디렉트 URL 확인
            const redirectInput = document.getElementById('redirectAfterLogin');
            const urlParams = new URLSearchParams(window.location.search);
            
            let redirectUrl = '/main';
            if (redirectInput && redirectInput.value) {
                redirectUrl = redirectInput.value;
                localStorage.removeItem('redirectAfterLogin');
            } else if (urlParams.get('redirect')) {
                redirectUrl = urlParams.get('redirect');
            }
            
            // 네비게이션 바 업데이트 (가능한 경우)
            if (typeof updateNav === 'function') {
                updateNav();
            }
            
            // 페이지 이동
            window.location.href = redirectUrl;
        } else {
            window.location.reload(); // 기본 리디렉션
        }
        
        loginInProgress = false;
    })
    .catch(error => {
        // 원래 상태로 복원
        if (loginButton) {
            loginButton.disabled = false;
            loginButton.innerHTML = originalText;
        }
        
        // 로그인 실패 처리
        console.error('로그인 오류:', error);
        
        // 오류 메시지 표시
        let errorMessage = '로그인에 실패했습니다.';
        if (error.message) {
            errorMessage = error.message;
        }
        
        if (passwordMsg) {
            passwordMsg.textContent = errorMessage;
        }
        
        if (passwordInput) {
            passwordInput.value = '';
        }
        
        // 계정 잠금 메시지가 포함된 경우 버튼 비활성화
        if (errorMessage.includes('잠금') || errorMessage.includes('초과')) {
            if (loginButton) {
                loginButton.disabled = true;
            }
            
            // 30분 후 자동 활성화 (1000ms * 60 * 30 = 30분)
            setTimeout(() => {
                if (loginButton) {
                    loginButton.disabled = false;
                }
                
                if (idMsg) {
                    idMsg.textContent = '로그인 시도 횟수 제한이 초기화되었습니다.';
                }
            }, 1000 * 60 * 30);
        }
        
        loginInProgress = false;
    });
}

// 계정 상태 확인 함수 (서버에 계정 잠금 상태 확인)
function checkAccountStatus(id) {
    return fetch(`/api/user/account-status?id=${encodeURIComponent(id)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('계정 상태 확인 실패');
        }
        return response.json();
    })
    .then(data => {
        return data.isLocked;
    })
    .catch(error => {
        console.error('계정 상태 확인 오류:', error);
        return false; // 오류 발생 시 잠금 상태 아님으로 처리
    });
}