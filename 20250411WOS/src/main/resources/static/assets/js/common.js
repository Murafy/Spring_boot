// Base64 인코딩
function b64Encode(str) {
    return window.btoa(unescape(encodeURIComponent(str)));
}

// Base64 디코딩
function b64Decode(str) {
    try {
        return decodeURIComponent(escape(window.atob(str)));
    } catch (e) {
        return ""; // 디코딩 실패 시 빈 문자열 반환
    }
}

// 쿠키 설정
function setCookie(cookieName, cookieValue, days) {
    const date = new Date();
    date.setDate(date.getDate() + days);
    const expires = days ? "; expires=" + date.toUTCString() : "";
    document.cookie = cookieName + "=" + cookieValue + expires + "; path=/"; // path=/ 추가
}

// 쿠키 가져오기
function getCookie(cookieName) {
    const name = cookieName + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');
    for (let i = 0; i < cookieArray.length; i++) {
        let cookie = cookieArray[i];
        while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1);
        }
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return "";
}

// 쿠키 삭제 (만료일을 0초로 설정해서 즉시삭제한다.)
function deleteCookie(name) {
    document.cookie = name + '=; Path=/; Max-Age=0';
}

// JWT 토큰 관련 유틸리티 함수들
const jwtUtils = {
  // 사용자 정보 세션 저장
  setUser: function(user) {
    // 세션스토리지는 브라우저 탭/창 닫힐 때 삭제됨
    sessionStorage.setItem('user', JSON.stringify(user));
  },
  
  // 저장된 사용자 정보 가져오기
  getUser: function() {
    const userStr = sessionStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },
  
  // 사용자 정보 삭제
  removeUser: function() {
    sessionStorage.removeItem('user');
  },
  
  // 로그인 상태 확인 - 쿠키 기반
  isLoggedIn: function() {
    return getCookie('accessToken') !== '';
  },
  
  // 로그아웃 처리
  logout: function() {
    this.removeUser();
    // 서버에서 쿠키를 삭제하는 로직은 백엔드에서 처리함
    // 클라이언트에서는 세션 정보만 정리
  },
  
  // 토큰 갱신 요청
  refreshToken: async function() {
    try {
      const response = await fetch('/api/user/refresh-token', {
        method: 'GET',
        credentials: 'include' // 쿠키 포함
      });
      
      if (!response.ok) {
        throw new Error('토큰 갱신 실패');
      }
      
      const data = await response.json();
      // 쿠키는 서버에서 설정됨, 로컬 저장소에 저장하지 않음
      return data.accessToken;
    } catch (error) {
      console.error('토큰 갱신 중 오류:', error);
      // 토큰 갱신 실패 시 로그아웃 처리
      this.removeUser();
      return null;
    }
  }
};