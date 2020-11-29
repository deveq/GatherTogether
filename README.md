# StudyTogether
개인프로젝트. 다함께 스터디. 함께 공부하기 위한 스터디 앱

#### 개인프로젝트, Kotlin, Android Studio
#### 개발기간 : 2020.10.31 ~ 2020.11.30

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

Firebase Authentication의 email로그인과 Google로그인 2가지 방식으로 가입 & 로그인 할 수 있습니다.<br>
이메일 로그인시 이름을 별도로 입력해주어야 하고,<br>
Google 로그인 시 google계정에 설정한 이름으로 닉네임이 설정됩니다.<br>

#### 지도로 스터디 찾기
1. 스터디 찾기 및 가입신청<br>
![2  joinStudy](https://user-images.githubusercontent.com/66777885/100547590-f3bcd500-32aa-11eb-9046-33764cfbfbbe.gif)

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
1. 채팅방
<pre><code>
    // DetailChatAdapter.kt
    
    
    override fun getItemViewType(position: Int): Int {
        // Chat객체의 type에 Chat.CHAT_TYPE 혹은 Chat.DATE_TYPE 2가지 값이 들어갈 수 있고
        // 해당 TYPE을 반환해주는 getItemViewType메서드를 override합니다.
        return chatList[position].type
    }
    
    // getItemViewType에서 반환된 viewType을 이용해서 ChatList에 있는 값이 채팅인지 날짜인지 구분합니다
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        val inflater = LayoutInflater.from(parent.context)

        // viewType에 따라서 반환해주는 ViewHolder를 다르게 해줍니다.
        return when (viewType) {
            Chat.CHAT_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_list_item, parent, false)
                DetailChatViewHolder(view)
            }
            Chat.DATE_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_date_list_item, parent, false)
                DetailChatDateViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }
</code></pre>

2. NaverMap API
MainMapFragment가 OnMapReadyCallback 인터페이스를 구현할 수 있도록한 후 onMapReady 메서드를 오버라이드합니다.
mapView가 로딩이 완료된 이후 getMapAsync를 통해 naverMap객체를 사용할 수 있는 onMapReady가 호출될 수 있게 한 후
LongClick에 대한 이벤트처리, Firebase Firestore에 저장된 Marker에 대한 정보(Point객체)를 받아 Marker로 표시할 수 있게 합니다.

3. Kakao Local API
REST API를 이용하여 GET방식으로 동이름 <-> 위,경도 정보를 받을 수 있는 API입니다.
(Naver Geolocation을 사용하려 하였으나 무료건수가 1000건이었기 때문에 무료인 Kakao Local API를 사용하였습니다.)
Retrofit을 이용하여 통신하였고, repsonse로 받는 정보 중 시(혹은 도) 이름인 depth1과 구 이름인 depth2를 이용해 주소를 받아왔습니다.
해당 API는 스터디 생성시, 리스트검색시 사용되었습니다.
<pre><code>
interface RetrofitService{

    //동 이름으로 DocAddr객체를 받아옵니다.
    //리스트로 검색 시 동 이름만 검색하여 주소를 받아온 후
    //그 주소와 동일한 Marker에 대한 정보를 Firestore를 통해 whereIn문으로 얻어올 수 있습니다.
    @GET("/v2/local/search/address.json")
    fun getByAdd(
        @Header("Authorization") appKey: String,
        @Query("query") address: String
    ) : Call<DocAddr>

    //위도,경도 정보를 통해 JsonObject를 콜백으로 받습니다.
    //리스트검색의 첫 화면에는 사용자의 위치정보를 통해 해당 지역의 정보를 얻은 다음
    //그 지역 내에 존재하는 스터디의 목록을 나타낼 수 있도록 하였습니다.
    //또 스터디 생성시 표시되는 지역명 또한 아래의 메서드를 통해 얻습니다.
    @GET("/v2/local/geo/coord2regioncode.json")
    fun getByGeo(
        @Header("Authorization") appKey: String,
        @Query("x") x: Double,
        @Query("y") y: Double
    ) : Call<JsonObject>
}
</code></pre>

### 마치며
작은 규모의 프로젝트만 만들다가 처음으로 1달이라는 시간이 소요되는 프로젝트를 만들어보았습니다.<br>
기존에는 책,인강 등 제공되는 틀에 제가 원하는 기능을 추가하여 만들었지만<br>
이번에는 Kakao Oven을 통해 프로토타입을 간단히 만들어보고 원하는 기능이 있다면 그것에 대한 자료를 구글링하여 만들어보고<br>
처음부터 끝까지 모든 코드에 노력을 담아서 만들었습니다.<br>
문제에 직면했을 때 해결되지 않는 부분은 1주일가까이 붙잡혀있었던적도 있었지만<br>
그러한 시행착오가 있었기 때문에 다양한 라이브러리를 시도해보고 Firebase와 REST API를 사용하는 방법에 대해 많이 알게되었습니다.<br>
다만 아쉬운점은 코로나로 인해 친구들과의 약속도 잡지 않고 평일과 주말 가리지 않고 매일매일 12시간 이상씩 이 프로젝트 개발만 진행하였고<br>
1달이라는 시간이 지나 완성되었지만<br>
개인프로젝트이다보니.. 완성되었다는 기쁨을 함께 나눌 사람이 없어 많이 아쉬웠습니다...<br>
하지만 개인프로젝트를 하다보니 처음부터 끝까지 모든 부분을 제가 다 작성하다보니<br>
다양한 기능들을 모두 접해볼 수 있는 아주 좋은 기회가 되었다고 생각합니다.<br>
다음에 어떤 프로젝트를 진행할지 아직 정하지는 않았지만<br>
이번 프로젝트를 통해 조금은 성장할 수 있는 기회가 되었다고 생각합니다.<br>
<br>
읽어주셔서 감사합니다.<br>


<br>
...<br>
..<br>
.<br>
얼른 코로나 끝나고 마스크 벗고 한강에 따릉이 타면서 놀고싶습니다..ㅠㅠ<br>
다들 건강하시고.. 코로나를 이겨냅시다...<br>
