// 이메일 인증 관련 변수
	let verificationCode = ""; // 서버에서 생성된 인증 코드를 저장
	let isEmailVerified = false; // 이메일 인증 완료 여부
	let timerInterval; // 타이머 인터벌 참조
	let timeLeft = 300; // 남은 시간 (초 단위, 5분)

	// 이메일 인증 관련 변수 저장 
	function sendVerificationCode() {
	    const email = document.getElementById('email').value.trim();
	    const emailError = document.getElementById('emailError');
	    const verificationSection = document.getElementById('verificationSection');
	    const sendVerificationBtn = document.getElementById('sendVerificationBtn');
	    
	    // 이메일 유효성 검사
	    if (!email) {
	        showError('emailError', '이메일을 입력해주세요.');
	        return;
	    }
	    
	    if (!isValidEmail(email)) {
	        showError('emailError', '유효한 이메일 주소를 입력해주세요.');
	        return;
	    }
	    
	    // 버튼 비활성화 및 로딩 상태 표시
	    sendVerificationBtn.disabled = true;
	    sendVerificationBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 발송 중...';
	    
	    // 실제 서버 API 호출
	    fetch('/api/email/send-verification', {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json'
	        },
	        body: JSON.stringify({ email: email })
	    })
	    .then(response => {
	        if (!response.ok) {
	            return response.json().then(data => {
	                throw new Error(data.message || '인증번호 발송 중 오류가 발생했습니다.');
	            });
	        }
	        return response.json();
	    })
	    .then(data => {
	        // 인증번호 입력 영역 표시
	        verificationSection.style.display = 'block';
	        
	        // 버튼 상태 복원 및 텍스트 변경
	        sendVerificationBtn.disabled = false;
	        sendVerificationBtn.innerHTML = '<i class="fas fa-envelope"></i> 재발송';
	        
	        // 이메일 에러 메시지 제거
	        emailError.style.display = 'none';
	        
	        // 타이머 시작
	        startTimer();
	        
	        alert(data.message || '이메일로 인증번호가 발송되었습니다. 이메일을 확인해주세요.');
	    })
	    .catch(error => {
	        // 오류 처리
	        showError('emailError', error.message);
	        
	        // 버튼 상태 복원
	        sendVerificationBtn.disabled = false;
	        sendVerificationBtn.innerHTML = '<i class="fas fa-envelope"></i> 인증번호 발송';
	    });
	}

	// 랜덤 인증번호 생성 함수 (6자리)
	function generateRandomCode() {
	    return Math.floor(100000 + Math.random() * 900000).toString();
	}

	// 타이머 시작 함수 
	function startTimer() {
	    // 기존 타이머가 있으면 초기화
	    if (timerInterval) {
	        clearInterval(timerInterval);
	    }
	    
	    // 시간 초기화 (5분)
	    timeLeft = 300;
	    updateTimerDisplay();
	    
	    // 1초마다 타이머 업데이트
	    timerInterval = setInterval(() => {
	        timeLeft--;
	        updateTimerDisplay();
	        
	        // 시간이 다 되면 타이머 중지 및 만료 처리
	        if (timeLeft <= 0) {
	            clearInterval(timerInterval);
	            expireVerification();
	        }
	    }, 1000);
	    
	    // 재발송 링크 이벤트 리스너
	    document.getElementById('resendLink').onclick = sendVerificationCode;
	}

	// 타이머 표시 업데이트
	function updateTimerDisplay() {
	    const minutes = Math.floor(timeLeft / 60);
	    const seconds = timeLeft % 60;
	    const timerDisplay = document.getElementById('verificationTimer');
	    
	    timerDisplay.textContent = `남은 시간: ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
	}

	// 인증 만료 처리 
	function expireVerification() {
	    const verificationError = document.getElementById('verificationError');
	    verificationError.textContent = '인증시간이 만료되었습니다. 인증번호를 재발송해주세요.';
	    verificationError.style.display = 'block';
	    
	    // 인증번호 무효화
	    verificationCode = "";
	}

	function verifyCode() {
	    const email = document.getElementById('email').value.trim();
	    const inputCode = document.getElementById('verificationCode').value.trim();
	    const verificationError = document.getElementById('verificationError');
	    const verificationSuccess = document.getElementById('verificationSuccess');
	    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
	    
	    // 인증번호 입력 확인
	    if (!inputCode) {
	        verificationError.textContent = '인증번호를 입력해주세요.';
	        verificationError.style.display = 'block';
	        verificationSuccess.style.display = 'none';
	        return;
	    }
	    
	    // 서버에 인증 요청
	    fetch('/api/email/verify', {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json'
	        },
	        body: JSON.stringify({ 
	            email: email,
	            code: inputCode 
	        })
	    })
	    .then(response => {
	        if (!response.ok) {
	            return response.json().then(data => {
	                throw new Error(data.message || '인증 실패');
	            });
	        }
	        return response.json();
	    })
	    .then(data => {
	        // 인증 성공
	        clearInterval(timerInterval); // 타이머 중지
	        
	        // 인증 완료 상태 설정
	        isEmailVerified = true;
	        
	        // 성공 메시지 표시 및 오류 메시지 숨김
	        verificationSuccess.style.display = 'block';
	        verificationError.style.display = 'none';
	        
	        // 인증 버튼 비활성화 및 스타일 변경
	        verifyCodeBtn.disabled = true;
	        verifyCodeBtn.innerHTML = '<i class="fas fa-check-circle"></i> 인증 완료';
	        verifyCodeBtn.classList.remove('btn-success');
	        verifyCodeBtn.classList.add('btn-secondary');
	        
	        // 인증번호 입력 필드 비활성화
	        document.getElementById('verificationCode').disabled = true;
	        
	        // 이메일 필드 비활성화 (인증 완료 후 변경 방지)
	        document.getElementById('email').disabled = true;
	        document.getElementById('sendVerificationBtn').disabled = true;
	    })
	    .catch(error => {
	        // 인증 실패
	        verificationError.textContent = error.message;
	        verificationError.style.display = 'block';
	        verificationSuccess.style.display = 'none';
	    });
	}

	// 오류 메시지 표시 함수 
	function showError(elementId, message) {
	    const errorElement = document.getElementById(elementId);
	    errorElement.textContent = message;
	    errorElement.style.display = 'block';
	}

	// 이메일 유효성 검사 함수 
	function isValidEmail(email) {
	    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	    return emailRegex.test(email);
	}
	// 주소 가져오기
	function getFullAddress() {
	    const roadAddress = document.getElementById('sample4_roadAddress').value;
	    const detailAddress = document.getElementById('sample4_detailAddress').value;
	    const extraAddress = document.getElementById('sample4_extraAddress').value;
	    
	    // 기본 주소
	    let fullAddress = roadAddress;
	    
	    // 상세주소가 있으면 추가
	    if (detailAddress) {
	        fullAddress += ' ' + detailAddress;
	    }
	    
	    // 참고항목이 있으면 추가
	    if (extraAddress) {
	        fullAddress += ' ' + extraAddress;
	    }
	    
	    return fullAddress;
	}
	// DOM이 로드된 후 실행되는 코드
	document.addEventListener('DOMContentLoaded', function() {
	    const form = document.getElementById('signupForm');
	    const userId = document.getElementById('userId');
	    const password = document.getElementById('password');
	    const confirmPassword = document.getElementById('confirmPassword');
	    const nickname = document.getElementById('nickname');
	    const email = document.getElementById('email');
	    const agreeTerms = document.getElementById('agreeTerms');
	    
	    // 비밀번호 표시/숨김 토글
	    document.getElementById('togglePassword').addEventListener('click', function() {
	        togglePasswordVisibility('password', this);
	    });
	    
	    document.getElementById('toggleConfirmPassword').addEventListener('click', function() {
	        togglePasswordVisibility('confirmPassword', this);
	    });
	    
	    // 비밀번호 확인 일치 검사
	    confirmPassword.addEventListener('input', function() {
	        if (password.value === confirmPassword.value && confirmPassword.value !== '') {
	            document.getElementById('passwordMatchFeedback').style.display = 'block';
	            document.getElementById('confirmPasswordError').style.display = 'none';
	        } else {
	            document.getElementById('passwordMatchFeedback').style.display = 'none';
	            if (confirmPassword.value !== '') {
	                document.getElementById('confirmPasswordError').textContent = '비밀번호가 일치하지 않습니다.';
	                document.getElementById('confirmPasswordError').style.display = 'block';
	            }
	        }
	    });
	    
	    // 폼 제출
	    form.addEventListener('submit', function(e) {
	        e.preventDefault();
	        
	        // 오류 메시지 초기화
	        hideAllErrors();
	        
	        // 폼 유효성 검사
	        let isValid = true;
	        
	        // 아이디 검사
	        if (!userId.value.trim()) {
	            showError('userIdError', '아이디를 입력해주세요.');
	            isValid = false;
	        } else if (!/^[a-zA-Z0-9]{4,20}$/.test(userId.value)) {
	            showError('userIdError', '아이디는 영문, 숫자 조합으로 4-20자리여야 합니다.');
	            isValid = false;
	        }
	        
	        // 비밀번호 검사
	        if (!password.value) {
	            showError('passwordError', '비밀번호를 입력해주세요.');
	            isValid = false;
	        } else if (password.value.length < 8) {
	            showError('passwordError', '비밀번호는 8자 이상이어야 합니다.');
	            isValid = false;
	        } else {
	            // 비밀번호 복잡성 검사
	            let complexity = 0;
	            if (/[a-zA-Z]/.test(password.value)) complexity++;
	            if (/[0-9]/.test(password.value)) complexity++;
	            if (/[^a-zA-Z0-9]/.test(password.value)) complexity++;
	            
	            if (complexity < 2) {
	                showError('passwordError', '비밀번호는 영문, 숫자, 특수문자 중 2가지 이상 조합해야 합니다.');
	                isValid = false;
	            }
	        }
	        
	        // 비밀번호 확인 검사
	        if (!confirmPassword.value) {
	            showError('confirmPasswordError', '비밀번호 확인을 입력해주세요.');
	            isValid = false;
	        } else if (password.value !== confirmPassword.value) {
	            showError('confirmPasswordError', '비밀번호가 일치하지 않습니다.');
	            isValid = false;
	        }
	        
	        // 닉네임 검사
	        if (!nickname.value.trim()) {
	            showError('nicknameError', '닉네임을 입력해주세요.');
	            isValid = false;
	        } else if (nickname.value.length < 2 || nickname.value.length > 10) {
	            showError('nicknameError', '닉네임은 2-10자 사이여야 합니다.');
	            isValid = false;
	        }
	        
	        // 이메일 검사 (선택사항이므로 입력 시에만 검사)
	        if (email.value && !isValidEmail(email.value)) {
	            showError('emailError', '유효한 이메일 주소를 입력해주세요.');
	            isValid = false;
	        }
	        
	        // 약관 동의 검사
	        if (!agreeTerms.checked) {
	            showError('agreeTermsError', '이용약관 및 개인정보처리방침에 동의해주세요.');
	            isValid = false;
	        }
	        
	        // 이메일 인증 확인
	        if (email.value.trim() && !isEmailVerified) {
	            showError('emailError', '이메일 인증이 완료되지 않았습니다.');
	            isValid = false;
	        }
	        
	        
	        
	        // 유효성 검사 통과 시 폼 제출
	        if (isValid) {
	            // 서버에 회원가입 요청
	            fetch('/api/regist/register', {
	                method: 'POST',
	                headers: {
	                    'Content-Type': 'application/json'
	                },
	                body: JSON.stringify({
	                    id: userId.value,
	                    password: password.value,
	                    nickname: nickname.value,
	                    email: email.value || null,
	                    address: getFullAddress() // 주소
	                })
	            })
	            .then(response => {
	                if (!response.ok) {
	                    return response.json().then(data => {
	                        throw new Error(data.message || '회원가입에 실패했습니다.');
	                    });
	                }
	                return response.json();
	            })
	            .then(data => {
	                // 회원가입 성공
	                alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
	                window.location.href = '/main?showLoginModal=true';
	            })
	            .catch(error => {
	                // 서버 오류 또는 중복 아이디/닉네임 등의 오류 처리
	                if (error.message.includes('아이디')) {
	                    showError('userIdError', error.message);
	                } else if (error.message.includes('닉네임')) {
	                    showError('nicknameError', error.message);
	                } else if (error.message.includes('이메일')) {
	                    showError('emailError', error.message);
	                } else {
	                    alert(error.message);
	                }
	            });
	        }
	    });
	    
	    // 비밀번호 표시/숨김 토글 함수
	    function togglePasswordVisibility(inputId, icon) {
	        const input = document.getElementById(inputId);
	        if (input.type === 'password') {
	            input.type = 'text';
	            icon.classList.replace('fa-eye-slash', 'fa-eye');
	        } else {
	            input.type = 'password';
	            icon.classList.replace('fa-eye', 'fa-eye-slash');
	        }
	    }
	    
	    // 모든 오류 메시지 숨김 함수
	    function hideAllErrors() {
	        const errorElements = document.querySelectorAll('.error-message');
	        errorElements.forEach(element => {
	            element.style.display = 'none';
	        });
	        
	        document.getElementById('passwordMatchFeedback').style.display = 'none';
	    }
	});