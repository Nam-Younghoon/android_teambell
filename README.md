### UPDATE HISTORY
09.22 메인페이지, 드로어 네비게이션

10.08 
  1) 하단 네비게이션 메뉴 추가(기록, 통계, 홈, 그룹, 설정)
  2) 통계 일,주,월,년 별 프래그 추가 및 ViewPager 식으로 변경
  3) 라이딩 종료 시 기록 출력 추가
  4) 설정창 뷰 
  
10.13
  1) 개인 라이딩 기록 수치 안정화
  2) 이동 시 PolyLine 그리기 
  
10.16
  1) 그룹 목록 추가
  2) 그룹 등록 추가
  3) 그룹 가입 추가
  4) 내가 가입한 그룹 리스트 추가
  5) 그룹 라이딩 시, 같은 그룹 회원들중 온라인인 사람을 지도에 위치 마커 추가
  
10.19
     MQTT 통신을 통해 그룹원들과 신호를 주고 받을 수 있다. 
     
10.26
     구글 로그인 추가
     카카오 로그인 
     
### 0. Splash
<img src="https://user-images.githubusercontent.com/58909988/97797799-5c407400-1c63-11eb-8afa-60dc9e8cdc58.jpg" width="50%" height="50%">

- 어플 실행 시 발생할 화면입니다.
     
### 1. Main 
<img src="https://user-images.githubusercontent.com/58909988/97797796-5b0f4700-1c63-11eb-8e79-b6b806576cbd.jpg" width="50%" height="50%">

- 어플의 메인 화면 입니다. 개인 라이딩/그룹 라이딩을 선택하여 라이딩을 할 수 있습니다.

### 2. Record
<img src="https://user-images.githubusercontent.com/58909988/97797807-5f3b6480-1c63-11eb-91d8-256e2249251f.jpg" width="50%" height="50%">

### 2-2. Record
<img src="https://user-images.githubusercontent.com/58909988/97797789-577bc000-1c63-11eb-8fcd-026a1266ab9b.jpg" width="50%" height="50%">

- 기록 Fragment입니다. 라이딩 종료 후, 기록을 하면 이 곳에 표시되어 지난 라이딩들의 기록을 확인할 수 있습니다.
- 내 위치가 잡히지 않은 채로 종료 시, 위치를 찾을 수 없었다는 표시를 하고 기록이 남아있지 않습니다.
- 제대로 기록이 되었을 시, 출발점과 종료지점의 위치를 주소로 알려주고 간략한 정보를 표시해줍니다.

### 3. Record_Detail_Success
<img src="https://user-images.githubusercontent.com/58909988/97797791-59458380-1c63-11eb-91c2-989c33cf5de4.jpg" width="50%" height="50%">

- 시작점과 종료점을 지도에 알려주고, 내 이동경로를 PolyLine으로 확인할 수 있습니다.
- 주행 시간, 평균 속도, 총 주행거리 등을 확인할 수 있습니다.

### 4. Record_Detail_Non-record
<img src="https://user-images.githubusercontent.com/58909988/97797794-5a76b080-1c63-11eb-9b8e-4192f7c81ba6.jpg" width="50%" height="50%">

- 위치를 잡지않고 그냥 종료 시에, 기록된 내용이 없으므로 사용자에게 해당 사실을 알린 후 종료시킵니다.

### 5. Statistic
<img src="https://user-images.githubusercontent.com/58909988/97797801-5d71a100-1c63-11eb-9e02-1c68cc577da9.jpg" width="50%" height="50%">

- 내 라이딩 기록의 통계입니다. 일/주/월/년 단위로 라이딩 간단 내역을 확인할 수 있습니다.

### 6. Group-List
<img src="https://user-images.githubusercontent.com/58909988/97797806-5f3b6480-1c63-11eb-9a13-174b1b303ec7.jpg" width="50%" height="50%">

- 개설된 총 그룹리스트들 입니다. 원하는 방에 가입을 할 수 있고, 검색을 통해 방을 찾을 수 있습니다.

### 7. Group-in
<img src="https://user-images.githubusercontent.com/58909988/97797797-5ba7dd80-1c63-11eb-85b4-55215b388940.jpg" width="50%" height="50%">

- 방을 클릭 시 입장하는 화면입니다. 방마다 비밀번호가 있기에, 비밀번호를 입력해야 하며 해당 그룹의 간단한 소개를 볼 수 있습니다.

### 8. My-Group
<img src="https://user-images.githubusercontent.com/58909988/97797802-5e0a3780-1c63-11eb-997e-5e6b38f435b3.jpg" width="50%" height="50%">

- 메인 페이지의 그룹라이딩 클릭 시 보여지는 내가 가입된 그룹 리스트 페이지 입니다. 이 중 오늘 내가 참여할 그룹 방을 클릭하여 서로 실시간 위치와, 라이딩 시그널을 보낼 수 있습니다.

### 9. Personal-Riding
<img src="https://user-images.githubusercontent.com/58909988/97797803-5e0a3780-1c63-11eb-8639-a34a9b43ded2.jpg" width="50%" height="50%">

- 개인 라이딩 시에는 진행 시간, 평균 속도, 현재 속도, 누적 이동 거리를 알려줍니다. 이동 시에 내 첫 출발점을 Marker로 알려주고 그 위치에서 부터 이동할 때마다 PolyLine으로 그려줍니다.

### 10. Find_My_Location
<img src="https://user-images.githubusercontent.com/58909988/97797805-5ea2ce00-1c63-11eb-9117-63ddaadb8119.jpg" width="50%" height="50%">

- 첫 위치를 잡고 있는 모습입니다. 첫 위치를 잡고 시작해야 제대로 된 기록을 할 수 있습니다.

### 11. Riding
<img src="https://user-images.githubusercontent.com/58909988/97797795-5a76b080-1c63-11eb-9a3c-e3c6b4eaf00d.jpg" width="50%" height="50%">

- 라이딩 도중의 모습입니다.

### 12. After-Riding
<img src="https://user-images.githubusercontent.com/58909988/97797790-58aced00-1c63-11eb-938c-4c2ae72d48ca.jpg" width="50%" height="50%">

- 라이딩 종료 시, 보여주는 기록창입니다. 사용자는 내용을 기록할 지, 그냥 일회성으로 보고 지울 지 선택권이 있습니다.

### 13. Real-Time_Group_Location
<img src="https://user-images.githubusercontent.com/58909988/97797800-5cd90a80-1c63-11eb-9cdd-86f4fad32b03.jpg" width="50%" height="50%">

- 그룹 라이딩에서의 실시간 위치 추적 입니다. 사진과 같이 라이딩을 같이하고 있는 그룹들의 위치가 실시간으로 표시됩니다. 
- MQTT통신을 통해 내 자전거의 벨을 누를 시에, 참여하고 있는 모든 그룹원들에게 알림을 보낼 수 있는 기능이 있습니다.

### 14. Setting
<img src="https://user-images.githubusercontent.com/58909988/97797798-5c407400-1c63-11eb-8b17-b2b024209ed2.jpg" width="50%" height="50%">

- 설정창입니다. 
