/*


*/
document.getElementById('btnGetList').addEventListener('click',getList)
document.getElementById('btnGetOne').addEventListener('click',getOne)
// 유저 아이디 중복검사 (만들어야함)
document.getElementById('userid').addEventListener('keyup',userid)

// 메서드 지정을 안해주면 자동으로 Get 방식으로 요청보낸다
function getOne(){
	const userid = document.getElementById('search').value
	const url = `/account/${userid}` // 자바스크립트 뺵틱
	fetch(url) // url 기반으로 request  
		.then(response => {
				console.log("response : ",response)
				return response.json() // 응답의 body를 역직렬화 다음 then에게 전달
		})
		.then(data => { // function ooo (data)
			console.log("data : ",data)
			dataPrint(data)
		})
		.catch(error => console.error("에러 : ", error))
		}

// 특정 유저 개인정보 표시
function dataPrint(data){
//	document.getElementById('userid').value=data.userid
	const result= `
	<input id="userid" value="${data.userid}">
	<input id="username" value="${data.username}">
	<input id="password" type="password" value="${data.password}">
	<input id="birth" type="date" value="${data.birth}">
	<input id="gender" value="${data.gender}">
	<input id="email" type="email" value="${data.email}">
	`		
	document.getElementById('result').innerHTML=result
	}
function getList(){
	const url = '/account'
	fetch(url)
		.then(response =>{
			console.log("response : ",response)
			return response.json() //응답의 문자열을 처리를 위해 객체화
		})
		.then(data =>{
			console.log("data : ",data)
			// document.getElementById('result').innerHTML = JSON.stringify(data) 깡으로 문자열로 찍힘
			rowsPrint(data)
		})
		.catch(error=>console.error("에러 : ",error))
}

function rowsPrint(list){
	// 원래 있던 내용 지워주기 (새로 넣어주기 위해)
	document.getElementById('result').innerHTML='' 
	
	list.forEach((item)=>{
		const box = document.createElement("div")
		// 위에서 만든 div 태그의 class 속성값 할당
		// index에서 새로운 스타일을 주기위해 작성 
		box.className = 'row' 
		const result= `
		<input value="${item.userid}" readonly>
		<input value="${item.username}"readonly>
		<input type="password" value="${item.password}"readonly>
		<input type="date" value="${item.birth}"readonly>
		<input value="${item.gender}"readonly>
		<input type="email" value="${item.email}"readonly>
		`	
		box.innerHTML = result // 
	document.getElementById('result').appendChild(box) // result 요소의 자식요소 box 추가  
	})
}