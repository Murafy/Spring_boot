
document.getElementById('btnSave').addEventListener('click',sendPost)

// post로 요청을 보내기위해 메서드를 명시해줌 
function sendPost(){
	const url='/account'
	// input 요소들의 value 값만 쏙쏙 뽑아오기 
	const userid = document.getElementById('userid').value;
	const username = document.getElementById('username').value;
	const password = document.getElementById('password').value;
	const birth = document.getElementById('birth').value;
	const gender = document.getElementById('gender').value;
	const email = document.getElementById('email').value;
	
	// 저장할 데이터를 객체로 만들기 
	// 서버 dto 의 필드변수값과 같게한다 (데이터베이스 컬럼명)
	// value 값만 쏙쏙 뽑아 '변수'에 저장해놨음으로 백틱이나 따움표 없이 바로 작성
	const obj ={ 
		userid : userid,
		username : username,
		password : password,
		birth : birth,
		email : email,
		gender : gender
	}
	const options = {
		method:'post', // post 방식으로 요청 
		headers : {
			'Content-Type': 'application/json;charset=UTF-8'
		},
		body : JSON.stringify(obj) // 서버로 전송하기위해 직렬화 
	}
	fetch(url,options)
	.then(response =>{
		console.log("response : ",response)
		// 응답으로 받는 데이터중 페이지 상태코드도 같이 오기때문 가능
		if(response.status === 400){
			alert ('이미 있는 userid 또는 email 입니다.')
		}
		return response.json()
	})
	.then(data => {
		console.log("data : ",data)
		if(data === 1)
			alert('계정 등록 성공')
			clear() // 성공시 value 비우는 함수 호출
	})
	.catch(error => console.error("오류 : ", error))
}

// 회원가입 성공하면 input 태그의 value 비우기
function clear(){
	document.getElementById('userid').value=''
	document.getElementById('username').value=''
	document.getElementById('password').value=''
	document.getElementById('email').value=''
	document.getElementById('birth').value=''
}
