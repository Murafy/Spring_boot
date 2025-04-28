// 전역 오류 처리 유틸리티
const ErrorHandler = {
    // API 오류 처리 - 서버 응답 메시지 및 오류 코드 기반 처리로 개선
    handleApiError: function(error) {
        // 응답이 있는 경우 (서버에서 반환된 오류)
        if (error.response) {
            const data = error.response.data;
            
            // 서버에서 전달한 오류 메시지 우선 사용
            if (data && data.message) {
                // 오류 코드 기반 처리
                if (data.errorCode) {
                    // 인증 관련 오류 처리
                    if (data.errorCode === 'UNAUTHORIZED' || data.errorCode === 'INVALID_CREDENTIALS' || 
                        data.errorCode === 'TOKEN_EXPIRED' || data.errorCode === 'INVALID_TOKEN') {
                        this.handleUnauthorized(data);
                    }
                    // 권한 관련 오류 처리
                    else if (data.errorCode === 'ACCESS_DENIED') {
                        this.handleForbidden(data);
                    }
                    // 리소스 없음 처리
                    else if (data.errorCode === 'RESOURCE_NOT_FOUND' || data.errorCode === 'ENTITY_NOT_FOUND' || 
                             data.errorCode === 'POST_NOT_FOUND' || data.errorCode === 'USER_NOT_FOUND') {
                        this.handleNotFound(data);
                    }
                    // 서버 오류 처리
                    else if (data.errorCode === 'INTERNAL_SERVER_ERROR' || data.errorCode === 'DATABASE_ERROR') {
                        this.handleServerError(data);
                    }
                    // 입력값 오류 처리
                    else if (data.errorCode === 'INVALID_VALUE' || data.errorCode === 'INVALID_REQUEST' || 
                             data.errorCode === 'INVALID_LANGUAGE') {
                        this.handleBadRequest(data);
                    }
                    // 중복 오류 처리
                    else if (data.errorCode === 'DUPLICATE_USER' || data.errorCode === 'DUPLICATE_NICKNAME' || 
                             data.errorCode === 'DUPLICATE_ENTITY') {
                        this.handleBadRequest(data);
                    }
                    // 계정 잠금 처리
                    else if (data.errorCode === 'ACCOUNT_LOCKED') {
                        this.handleAccountLocked(data);
                    }
                    // 기타 오류 처리
                    else {
                        this.handleGenericError(data);
                    }
                } else {
                    // 오류 코드가 없는 경우에도 메시지 표시
                    this.handleGenericError(data);
                }
            } else {
                // 오류 메시지가 없는 경우 HTTP 상태 코드로 판단 (기존 로직 유지)
                const status = error.response.status;
                this.handleGenericError({ message: '요청 처리 중 오류가 발생했습니다.' });
            }
            
            return data.message || '요청 처리 중 오류가 발생했습니다.';
        } 
        // 요청 전송 중 오류 (네트워크 문제 등)
        else if (error.request) {
            this.handleNetworkError();
            return '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.';
        } 
        // 기타 오류
        else {
            console.error('처리되지 않은 오류:', error.message);
            return error.message || '알 수 없는 오류가 발생했습니다.';
        }
    },
    
    // 400 Bad Request 처리
    handleBadRequest: function(data) {
        console.error('잘못된 요청:', data);
        
        // 필드별 오류 메시지가 있는 경우
        if (data.errors && typeof data.errors === 'object') {
            // 각 필드의 오류 메시지 표시
            for (const field in data.errors) {
                const errorElement = document.getElementById(`${field}Error`);
                if (errorElement) {
                    errorElement.textContent = data.errors[field];
                    errorElement.style.display = 'block';
                }
            }
        } else {
            // 일반 오류 메시지 (서버에서 제공한 메시지 사용)
            alert(data.message || '입력 정보를 확인해주세요.');
        }
    },
    
    // 401 Unauthorized 처리
    handleUnauthorized: function(data) {
        console.error('인증 실패:', data);
        
        // 로그인 상태 초기화
        if (typeof jwtUtils !== 'undefined') {
            jwtUtils.removeUser();
        }
        
        // 로그인 세션 만료 메시지 (서버에서 제공한 메시지 사용)
        const message = data.message || '로그인이 필요하거나 세션이 만료되었습니다.';
        
        if (confirm(`${message}\n로그인 페이지로 이동하시겠습니까?`)) {
            // 현재 URL을 저장하여 로그인 후 리디렉션에 사용
            const currentPath = window.location.pathname + window.location.search;
            localStorage.setItem('redirectAfterLogin', currentPath);
            
            // 로그인 페이지로 이동
            window.location.href = '/main?showLoginModal=true';
        }
    },
    
    // 계정 잠금 처리 (추가)
    handleAccountLocked: function(data) {
        console.error('계정 잠금:', data);
        alert(data.message || '계정이 잠금 상태입니다. 관리자에게 문의하세요.');
    },
    
    // 403 Forbidden 처리
    handleForbidden: function(data) {
        console.error('접근 거부:', data);
        alert(data.message || '이 기능에 접근할 권한이 없습니다.');
    },
    
    // 404 Not Found 처리
    handleNotFound: function(data) {
        console.error('리소스 없음:', data);
        alert(data.message || '요청한 정보를 찾을 수 없습니다.');
        
        // 이전 페이지로 이동 또는 메인으로 리디렉션
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = '/main';
        }
    },
    
    // 500+ Server Error 처리
    handleServerError: function(data) {
        console.error('서버 오류:', data);
        alert(data.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    },
    
    // 네트워크 오류 처리
    handleNetworkError: function() {
        console.error('네트워크 오류');
        alert('서버에 연결할 수 없습니다. 인터넷 연결을 확인해주세요.');
    },
    
    // 기타 일반 오류 처리
    handleGenericError: function(data) {
        console.error('일반 오류:', data);
        alert(data.message || '오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    },
    
    // fetch API용 응답 처리 - ApiResponse 기반 처리로 개선
    handleFetchResponse: async function(response) {
        try {
            // 응답 데이터 파싱
            const data = await response.json();
            
            // 성공 응답인 경우 (success: true)
            if (response.ok && data.success) {
                return data.data || data;
            }
            
            // 오류 응답 처리 (ApiResponse 형식)
            const error = {
                response: {
                    status: response.status,
                    data: data
                }
            };
            
            // 오류 핸들러 사용
            this.handleApiError(error);
            
            // Promise 거부하여 호출측에서 catch 가능하도록
            return Promise.reject(data);
        } catch (e) {
            // JSON 파싱 실패한 경우 (빈 응답 등)
            if (!response.ok) {
                return Promise.reject({ 
                    message: '응답을 처리할 수 없습니다.',
                    status: response.status
                });
            }
            
            // 빈 응답인데 성공인 경우 빈 객체 반환
            if (response.ok) {
                return {};
            }
            
            return Promise.reject({ message: '응답을 처리할 수 없습니다.' });
        }
    }
};

// fetch 요청 래퍼 함수 - 모든 API 호출에 사용
async function fetchWithErrorHandling(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            // 기본적으로 쿠키 포함
            credentials: options.credentials || 'include',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });
        
        return await ErrorHandler.handleFetchResponse(response);
    } catch (error) {
        // 네트워크 오류 등 fetch 실패
        if (!error.response) {
            ErrorHandler.handleNetworkError();
        }
        throw error;
    }
}