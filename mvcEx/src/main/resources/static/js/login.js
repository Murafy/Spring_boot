/**
 *  5_login.jsp 의 입력값 검사
 */
function checkValues(){
	let isSubmit = true
	const test = document.querySelectorAll('div.form-floating > input')
	const reverse = Array.from(test).reverse()
	reverse.forEach(element => {
		element.nextElementSibling.innerHTML=''
		if(element.value === ''){
			element.nextElementSibling.innerHTML= element.placeholder +' 필수 입력 입니다.'
			element.focus()
			isSubmit=false
		}
	})
		return isSubmit
}