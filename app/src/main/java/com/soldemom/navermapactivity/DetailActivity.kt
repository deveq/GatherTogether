package com.soldemom.navermapactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.soldemom.navermapactivity.testFrag.DetailAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.reflect.Member

class DetailActivity : AppCompatActivity() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var mapFragment: MapFragment
    lateinit var point: Point
    lateinit var latLng: LatLng

    var memberBelongs: Boolean = false
    lateinit var detailAdapter: DetailAdapter

    lateinit var leaderUser: User
    lateinit var memberList: List<User>
    lateinit var studyIdRef: DocumentReference
    lateinit var usersRef: CollectionReference
    lateinit var uid: String
    lateinit var studyId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        studyId = intent.getStringExtra("studyId")

        uid = auth.currentUser!!.uid
        usersRef = db.collection("users")
        studyIdRef = db.collection("markers").document(studyId)

        setUi()

        // Firestore의 document의 id를 studyId에 넣고 intent로 받았음.

        attend_btn.setOnClickListener {
            setAttendBtn()
        }

        val message = if (memberBelongs) "모임에서 나가시습니까?" else "참여하시겠습니까?"

        cancel_btn.setOnClickListener {
            finish()
        }


    }

    fun setUi() {
        studyIdRef.get().addOnSuccessListener {

            //해당 스터디의 point객체를 받음
            point = it.toObject(Point::class.java)!!

            //해당 모임에 가입되어있을 경우 가입/탈퇴 버튼 처리
            if (point.member!!.contains(uid)) {

                memberBelongs = true

                //자신이 리더일때 탈퇴버튼은 보이지 않도록, 승인버튼은 보이도록
                if (point.leader == uid) {
                    attend_btn.visibility = View.INVISIBLE
                    //가입승인버튼 보이기
                    detail_member_approve.visibility = View.VISIBLE
                    detail_member_approve.setOnClickListener {
                        val intent = Intent(this, MemberApproveActivity::class.java)
                        intent.putExtra("studyId", point.studyId)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    //멤버라면
                    attend_btn.text = "탈퇴하기"
                }
            } else {
                //가입되있지 않다면 정원초과, 가입대기 여부 확인
                if (point.currentCount!! >= point.maxCount!!) {
                    attend_btn.text = "정원초과"
                    attend_btn.isClickable = false
                } else if (point.waitingMember.containsKey(uid)) {
                    attend_btn.text = "가입대기중"
                    attend_btn.isClickable = false
                }
            }




            usersRef.document(point.leader!!).get()
                .addOnSuccessListener {
                    leaderUser = it.toObject(User::class.java)!!
                    detail_leader_name.text = leaderUser.name
                }
            usersRef.whereIn("uid", point.member!!).get()
                .addOnSuccessListener {
                    memberList = it.toObjects(User::class.java)

                    detailAdapter = DetailAdapter()
                    detail_participants_recycler_view.adapter = detailAdapter
                    detailAdapter.memberList = memberList
                    detailAdapter.notifyDataSetChanged()

                }

            detail_title.text = point.title
            detail_text.text = point.text
            detail_member_count.text = "${point.currentCount} / ${point.maxCount}"

        }
    }

    fun setAttendBtn() {

        val alertDialog = AlertDialog.Builder(this)
        //이미 가입이 되어있다면
        if (memberBelongs) {
            alertDialog.setMessage("모임에서 나가시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->

                    db.runBatch { batch ->
                        batch.update(
                            studyIdRef,
                            hashMapOf<String, Any>(
                                //marker의 member에서 삭제
                            "member" to FieldValue.arrayRemove(uid),
                                //인원 수 -1
                            "currentCount" to FieldValue.increment((-1))
                        ))

                        batch.update(
                            usersRef.document(uid),
                            hashMapOf<String,Any>(
                                // 가입된 스터디 배열에서 studyId 삭제
                                "studyList" to FieldValue.arrayRemove(studyId)
                            )
                        )
                    }

                        .addOnSuccessListener {
                            finish()
                        }
                }.create().show()
            //가입이 안되있다면
        } else {
            val et: EditText = EditText(this)
            et.hint = "간단히 소개를 적어주세요."
            alertDialog.setTitle("가입요청하기")
                .setView(et)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->

                    val attendText = et.text.toString()

                    studyIdRef
                        .update(
                            "waitingMember",
                            mapOf<String, String>(
                                uid to attendText
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(this, "신청되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()

                        }
                    //신청 다이얼로그 끝끝
                }.create().show()
        }
    }

}
