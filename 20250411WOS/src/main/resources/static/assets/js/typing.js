const typingTextElement = document.getElementById('typing-text');
console.log("typingTextElement:", typingTextElement);
const textArray = ["Java", "JavaScript", "HTML/CSS", "SpringBoot", "OracleSQL", "React"]; // 애니메이션 텍스트 배열
const textColorClasses = ["java-color", "javascript-color", "html-css-color", "springboot-color", "oraclesql-color", "react-color"]; // 텍스트 색상 클래스 배열 // ✨ 추가됨
let textIndex = 0; // 텍스트 배열 인덱스
let charIndex = 0; // 현재 글자 인덱스
const typingDelay = 50; // 타이핑 속도 (ms)
const deletingDelay = 100; // 삭제 속도 (ms)
const newTextDelay = 2000; // 다음 텍스트 시작 전 대기 시간 (ms)

function type() {
    if (charIndex < textArray[textIndex].length) {
        const charSpan = document.createElement('span'); // <span> 요소 생성 
        charSpan.classList.add(textColorClasses[textIndex]); // <span> 요소에 색상 클래스 추가 
        charSpan.textContent = textArray[textIndex].charAt(charIndex); // <span> 요소에 글자 추가
        typingTextElement.appendChild(charSpan); // <span> 요소를 typingTextElement에 추가 
        charIndex++;
        setTimeout(type, typingDelay); // typingDelay 시간 후 type 함수 다시 실행 (재귀 호출)
    } else {
        // 타이핑 완료 후 삭제 시작
        setTimeout(deleteText, newTextDelay); // newTextDelay 시간 후 deleteText 함수 실행
    }
}

function deleteText() {
    if (charIndex > 0) {
        typingTextElement.removeChild(typingTextElement.lastChild); // 마지막 <span> 요소 삭제 
        charIndex--;
        setTimeout(deleteText, deletingDelay); // deletingDelay 시간 후 deleteText 함수 다시 실행 (재귀 호출)
    } else {
        // 삭제 완료 후 다음 텍스트로 이동
        textIndex++;
        if (textIndex >= textArray.length) {
            textIndex = 0; // 텍스트 배열 순환
        }
        setTimeout(type, 500); // 0.5초 대기 후 type 함수 실행 (다음 텍스트 타이핑 시작)
    }
}

// 애니메이션 시작
document.addEventListener('DOMContentLoaded', () => { // HTML 문서 로드 완료 후 실행
    setTimeout(type, 500); // 0.5초 후 첫 번째 텍스트 타이핑 시작
});