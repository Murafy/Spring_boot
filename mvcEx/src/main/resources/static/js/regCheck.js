/**
 * 4_register.jsp 의 form 요소값을 검사
 */
// submit 버튼을 실행할 때 처리되는 이벤트 함수
function checkValues(){
//	alert("checkValues 함수 입니다.");
//	return false;    // false 값은 submit 동작 하지 않음.

	const frm = document.forms[0]  // form 은 배열로 접근
	let isSubmit=true
	
	document.getElementById('useridMsg').innerHTML = ''
	document.getElementById('usernameMsg').innerHTML = ''
	document.getElementById('passwordMsg').innerHTML = ''
	document.getElementById('birthMsg').innerHTML = ''
	document.getElementById('genderMsg').innerHTML = ''
	document.getElementById('emailMsg').innerHTML = ''
	
	if(frm.userid.value === ''){
		document.getElementById('useridMsg').innerHTML='필수입력입니다.'
		frm.username.focus()
		isSubmit= false
	}
	
	if(frm.username.value === ''){
		document.getElementById('usernameMsg').innerHTML='필수입력입니다.'
		frm.birth.focus()
		isSubmit= false
	}
	
	if(frm.password.value === ''){
		document.getElementById('passwordMsg').innerHTML='필수입력입니다.'
		frm.password.focus()
		isSubmit= false
	}
	
	if(frm.userid.value === ''){
		document.getElementById('birthMsg').innerHTML='필수입력입니다.'
		frm.userid.focus()
		isSubmit= false
	}
	
	if(frm.username.value === ''){
		document.getElementById('genderMsg').innerHTML='필수입력입니다.'
		frm.username.focus()
		isSubmit= false
	} 
	if(frm.username.value === ''){
		document.getElementById('emailMsg').innerHTML='필수입력입니다.'
		frm.username.focus()
		isSubmit= false
	} 
	return isSubmit
}