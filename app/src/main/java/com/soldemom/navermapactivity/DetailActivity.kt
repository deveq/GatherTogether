package com.soldemom.navermapactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.soldemom.navermapactivity.testFrag.DetailAdapter
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var mapFragment: MapFragment
    lateinit var point: Point
    lateinit var latLng: LatLng
    lateinit var studyId: String
    var memberBelongs: Boolean = false
    lateinit var detailAdapter: DetailAdapter

    lateinit var leaderUser: User
    lateinit var memberList: List<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        cancel_btn
        attend_btn
        detail_text
        detail_title

        val uid = auth.currentUser!!.uid

        // Firestore의 document의 id를 studyId에 넣고 intent로 받았음.
        studyId = intent.getStringExtra("studyId")

        attend_btn.setOnClickListener {

            val _studyIdRef = db.collection("markers").document(studyId)
            val alertDialog = AlertDialog.Builder(this)
            //이미 가입이 되어있다면
            if (memberBelongs) {
                alertDialog.setMessage("모임에서 나가시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인") { _, _ ->
                        _studyIdRef.update(
                            "member",
                            FieldValue.arrayRemove(uid),
                            "currentCount",
                            FieldValue.increment((-1))
                        )
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
                        val attendRef = db.collection("attend")
                            .add(
                                hashMapOf(
                                    "uid" to uid,
                                    "message" to et.text.toString()
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(this, "신청되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }

            }

            val message = if (memberBelongs) "모임에서 나가시겠습니까?" else "참여하시겠습니까?"

            AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->


                    val studyIdRef = db.collection("markers")
                        .document(studyId)

                    if (memberBelongs) {
                        studyIdRef.update(
                            "member",
                            FieldValue.arrayRemove(uid),
                            "currentCount",
                            FieldValue.increment(-1)
                        )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "실패용...ㅠㅠ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                    } else {
                        studyIdRef.update(
                            "member",
                            FieldValue.arrayUnion(uid),
                            "currentCount",
                            FieldValue.increment(1)
                        )
                            //update를 통해 member라는 배열에 uid 추가.
                            //될지 모르겠지만... currentCount를 동시에 1 증가시키기.
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "성공해따 ㅠㅠ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                    }
                }.create().show()

        }

        cancel_btn.setOnClickListener {
            finish()
        }




        val markersRef = db.collection("markers")
        val usersRef = db.collection("users")

        markersRef.document(studyId).get().addOnSuccessListener {

            //해당 스터디의 point객체를 받음
            point = it.toObject(Point::class.java)!!

            //해당 모임에 가입되어있을 경우 가입/탈퇴 버튼 처리
            if (point.member!!.contains(uid)) {

                memberBelongs = true

                //자신이 리더일때는 탈퇴버튼이 안보이도록
                if (point.leader == uid) {
                    attend_btn.visibility = View.INVISIBLE
                } else {
                    //멤버라면
                    attend_btn.text = "탈퇴하기"
                }
            }

            usersRef.document(point.leader!!).get()
                .addOnSuccessListener {
                    leaderUser = it.toObject(User::class.java)!!
                    detail_leader_name.text = leaderUser.name
                }
            usersRef.whereIn("uid",point.member!!).get()
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

}
