package com.soldemom.navermapactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.navermapactivity.recyclerview.ApproveAdapter
import kotlinx.android.synthetic.main.activity_member_approve.*

class MemberApproveActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth
    lateinit var point: Point
    lateinit var approveAdapter: ApproveAdapter
    lateinit var approveUserList: MutableList<ApproveUser>
    lateinit var studyId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_approve)

        approve_title
        approve_recycler_view
        approve_return_main_btn

        studyId = intent.getStringExtra("studyId")

        db.collection("markers").document(studyId)
            .get().addOnSuccessListener {
                point = it.toObject(Point::class.java)!!
                approve_title.text = point.title
                val list = point.waitingMember.keys.toList()
                if (list.isNotEmpty()) {
                    db.collection("users").whereIn("uid", list)
                        .get()
                        .addOnSuccessListener {
                            approveUserList = mutableListOf<ApproveUser>()

                            //파이어베이스가 join이 안된대요 ㅠㅠ
                            for (i in it) {
                                //그래서.. approveUser의 uid를 waitingMember의 key값으로 넣어서
                                //introduce를 받아옴.
                                val user = i.toObject(ApproveUser::class.java)
                                user.introduce = point.waitingMember.get(user.uid)!!
                                approveUserList.add(user)
                            }

                            approveAdapter = ApproveAdapter(::approveLambda, ::denyLambda)
                            approveAdapter.list = approveUserList
                            approve_recycler_view.adapter = approveAdapter
                        }
                } else {
                    approve_recycler_view.visibility = View.GONE
                    approve_empty_list.visibility = View.VISIBLE

                }

            }

        approve_return_main_btn.setOnClickListener {
            val intent = Intent(this, TestActivity3::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun approveLambda(approveUser: ApproveUser) {
        val uid = approveUser.uid
        val markerRef = db.collection("markers").document(studyId)
        val usersRef = db.collection("users").document(uid)
        Log.d("트랜잭션","외않되?")
        db.runBatch { batch ->
            Log.d("트랜잭션","외않되?")

            batch.update(
                markerRef, hashMapOf<String, Any>(
                    //map의 element 지울라면 점찍고 들어가기
                    "waitingMember.$uid" to FieldValue.delete(),
                    "currentCount" to FieldValue.increment(1),
                    "member" to FieldValue.arrayUnion(uid)
                )
            )

            batch.update(usersRef, "studyList", FieldValue.arrayUnion(studyId))

        }.addOnSuccessListener {
            Toast.makeText(this, "가입 승인 완료", Toast.LENGTH_LONG).show()

            approveUserList.remove(approveUser) // 여기에 대기 목록에서 지우기.
            if (approveUserList.isNotEmpty()) {
                approveAdapter.list = approveUserList
                approveAdapter.notifyDataSetChanged()
            } else {
                approve_recycler_view.visibility = View.GONE
                approve_empty_list.visibility = View.VISIBLE

            }
        }.addOnFailureListener {
            Log.d("실패", it.message)
        }
    }

    fun denyLambda(approveUser: ApproveUser) {
        val uid = approveUser.uid
        val markerRef = db.collection("markers").document(studyId)

        // waitingMember에서 삭제만
        markerRef.update(
            hashMapOf<String, Any>(
                "waitingMember.$uid" to FieldValue.delete()
            ))
            .addOnSuccessListener {
                Toast.makeText(this, "거부되었습니다.", Toast.LENGTH_SHORT).show()
                approveUserList.remove(approveUser) // 대기유저 목록에서 지우기.
                if (approveUserList.isNotEmpty()) {
                    approveAdapter.list = approveUserList
                    approveAdapter.notifyDataSetChanged()
                } else {
                    approve_recycler_view.visibility = View.GONE
                    approve_empty_list.visibility = View.VISIBLE

                }

            }.addOnFailureListener {
                Log.d("실패", it.message)
            }

    }
}