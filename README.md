# StudyTogether
개인프로젝트. 다함께 스터디. 함께 공부하기 위한 스터디 앱

#### 개인프로젝트, Kotlin, Android Studio
#### 개발기간 : 2020.10.31 ~ 2020.11.29

#### Study Together를 만든 이유
안드로이드를 독학하고있어 스터디를 찾는데에 어려움을 겪던 중<br>
다양한 기능들을 사용하여 스터디앱을 만들어보는게 어떨까 생각하여 만들게 되었습니다.<br>

## 애플리케이션 설명
지도 및 구, 동 이름으로 스터디를 만들거나 찾아서 함께 공부하기 위한 애플리케이션.<br>
지도를 통해 주변의 스터디를 찾거나<br>
동 혹은 구의 이름을 입력하여 해당 지역의 스터디를 조회할 수 있으며<br>
스터디에 가입한 멤버들과의 채팅을 통해 서로 이야기를 할 수 있습니다.

#### 사용 라이브러리
1. Naver Map API
    - 지도로 스터디 찾기
2. Kakao Local API
    - 검색으로 스터디 찾기 
    - 주소 <-> LatLng
3. Firebase 
    - Firestore : 스터디 정보, 회원정보 
    - RealtimeDatabase : 채팅방
    - Storage : 스터디 이미지, 사용자 프로필 이미지
    - Authentication : 이메일로 가입, 구글로그인
4. Retrofit
    - Kakao Local API에 REST API로 데이터를 요청하고 받기 위해 사용
5. Glide
    - 스터디 이미지, 사용자의 프로필 이미지 표시
6. Dexter
    - 권한 요청

## 순서
- 0. 완성 화면
- 1. 사용한 개념 및 기능
- 2. 느낀점

### 완성화면
#### 가입 및 로그인
![1  joinNlogin](https://user-images.githubusercontent.com/66777885/100547554-ad677600-32aa-11eb-8978-45cdc8655855.gif)
<br>
Firebase Authentication의 email로그인과 Google로그인 2가지 방식으로 가입 & 로그인 할 수 있습니다.<br>
이메일 로그인시 이름을 별도로 입력해주어야 하고,<br>
Google 로그인 시 google계정에 설정한 이름으로 닉네임이 설정됩니다.<br>

#### 지도로 스터디 찾기
1. 스터디 찾기 및 가입신청<br>
![2  joinStudy](https://user-images.githubusercontent.com/66777885/100547590-f3bcd500-32aa-11eb-9046-33764cfbfbbe.gif)
<br>
가입하고자 하는 스터디 선택 후 가입신청을 하게되면 '가입대기중'상태가 되고,<br>
스터디의 리더가 가입승인을 하게 되면 멤버로써 가입이 완료됩니다.<br>
가입이 완료된 멤버라면 '가입'버튼이 '탈퇴'버튼으로 변경됩니다.<br>


2. 스터디 가입 승인하기<br>
![2_3 approve](https://user-images.githubusercontent.com/66777885/100547797-097eca00-32ac-11eb-8d0e-d8d8c6551e48.gif)
스터디의 리더가 가입신청목록에서 승인을 통해 가입신청한 사람을 승인할 수 있습니다.<br>
승인이 된다면 Firebase Firestore에서 Transaction을 통해 가입신청중 -> 가입완료 상태로 변경하고,<br>
해당 유저의 가입 스터디 목록에도 해당 스터디의 studyId를 추가합니다.<br>


3. 스터디 생성, 이미지 추가<br>
![2_2 createStudyNImage](https://user-images.githubusercontent.com/66777885/100547771-efdd8280-32ab-11eb-92e2-1aa13b2eee3b.gif)
스터디를 생성하고자 하는 위치를 LongClick 시 생성을 위한 다이얼로그가 나타납니다.<br>
이름 옆 이미지 부분을 클릭하면 이미지의 Uri를 얻기위해 ACTION_APPLICATION_DETAILS_SETTINGS의 action 키워드를 가진 intent를 실행하게 되고<br>
사진 선택 후 해당 uir를 통해 Firebase Storage에 스터디의 이미지를 업로드할 수 있습니다. <br>

4. 스터디의 채팅방(대화방)<br>
![2_4 chat](https://user-images.githubusercontent.com/66777885/100547898-9e81c300-32ac-11eb-9e89-93154553e65d.gif)
스터디에 가입한 후 멤버들과 채팅을 이용할 수 있습니다.<br>
Firebase RealtimeDatabase를 이용하였고, RecyclerView에 2개의 ViewHolder(ChatViewHolder와 DateViewHolder)를 이용하여<br>
혹시나 날짜가 변경되면 DateViewHolder의 view가 추가되어 출력되게끔, 날짜변경이 없다면 채팅이 출력되게 하였고,<br>
ChatViewAdapter에서는 채팅과 현재 사용자의 uid가 같은지 여부 확인 후,<br>
같다면 채팅view를 파란background + 오른쪽 정렬<br>
다르다면 회색background + 왼쪽정렬 + 이름 나타내기를 해주어 채팅을 구분할 수 있게 하였습니다.<br>

    
#### 동 이름 검색을 통해 스터디 찾기
동 이름 혹은 구 이름을 통해 검색한 뒤 해당 지역에 있는 스터디를 목록으로 보기.
![3  searchList](https://user-images.githubusercontent.com/66777885/100547995-2e277180-32ad-11eb-9c0b-c3fdd4252557.gif)
Kakao Local API를 이용하여 처음 화면에는 현재 위치의 위도, 경도(LatLng)를 통해 행정동 주소를 받고 그 주변의 스터디 리스트를 나타냅니다.<br>
그 후 검색을 통해 해당 지역의 스터디를 찾습니다.<br>
혹시 검색어를 통해 조회되는 지역이 2개 이상이라면 다이얼로그를 통해 선택 후 해당 지역의 스터디 목록을 받아옵니다.<br>


#### 개인정보
이름, 자기소개 수정 및 프로필 사진 추가
![4  userInfo](https://user-images.githubusercontent.com/66777885/100548065-adb54080-32ad-11eb-8818-213be51dcd8a.gif)
이름, 자기소개 부분 클릭 시 다이얼로그를 통해 이름과 자기소개를 수정할 수 있습니다.<br>
프로필 사진 클릭 시 Firebase Storage에 이미지를 업로드 할 수 있습니다.<br>
이름,자기소개,프로필사진은 스터디페이지에 가입된 멤버들의 리스트를 통해서도 볼 수 있습니다.<br>


### 기능 설명

### 마치며
