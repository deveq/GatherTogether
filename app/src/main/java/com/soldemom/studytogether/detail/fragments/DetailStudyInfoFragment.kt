package com.soldemom.studytogether.detail.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.soldemom.studytogether.MemberApproveActivity
import com.soldemom.studytogether.main.dto.Point
import com.soldemom.studytogether.R
import com.soldemom.studytogether.detail.DetailViewModel
import com.soldemom.studytogether.detail.recycler.DetailMemberListAdapter
import com.soldemom.studytogether.main.dto.User
import kotlinx.android.synthetic.main.fragment_study_info.*
import kotlinx.android.synthetic.main.fragment_study_info.view.*

class DetailStudyInfoFragment(val studyId: String) : Fragment() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var point: Point

    var memberBelongs: Boolean = false
    lateinit var detailAdapter: DetailMemberListAdapter

    lateinit var leaderUser: User
    lateinit var memberList: List<User>
    lateinit var studyIdRef: DocumentReference
    lateinit var usersRef: CollectionReference
    lateinit var uid: String
    lateinit var fragView: View
    lateinit var viewModel: DetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragView = inflater.inflate(R.layout.fragment_study_info, container, false)

        //StudyInfoFrag와 DetailChatFrag는 동일한 DetailActivity에 부착되어있으므로
        //ViewModel Owner가 같으므로 viewModel 공유 가능.
        viewModel = ViewModelProvider(requireActivity()).get(DetailViewModel::class.java)


        dialogPermissionListener =
        DialogOnDeniedPermissionListener.Builder
            .withContext(requireContext())
            .withTitle("저장소 권한 요청")
            .withMessage("프로필 사진 업로드를 위해\n외부 저장소의 권한이 필요합니다.")
            .withButtonText("권한 설정하러 가기") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .build()

        fragView.apply {

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
                requireActivity().finish()
            }
        }
        return fragView
    }


    fun setUi() {
        studyIdRef.get().addOnSuccessListener {

            //해당 스터디의 point객체를 받음
            point = it.toObject(Point::class.java)!!

            point.image?.let {imageUri ->
                Glide.with(requireContext())
                    .load(imageUri)
                    .centerCrop()
                    .into(detail_profile_image)
            }

            //해당 모임에 가입되어있을 경우 가입/탈퇴 버튼 처리
            if (point.member!!.contains(uid)) {

                memberBelongs = true

                //자신이 리더일때 탈퇴버튼은 보이지 않도록, 승인버튼은 보이도록, 이미지 수정 가능하도록
                if (point.leader == uid) {
                    attend_btn.visibility = View.INVISIBLE
                    //가입승인버튼 보이기
                    detail_member_approve.visibility = View.VISIBLE
                    detail_member_approve.setOnClickListener {
                        val intent = Intent(requireActivity(), MemberApproveActivity::class.java)
                        intent.putExtra("studyId", point.studyId)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    detail_profile_image.setOnClickListener {
                        Dexter
                            .withContext(requireContext())
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(dialogPermissionListener)
                            .check()

                        val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        imageIntent.putExtra("number",5)
                        requireActivity().startActivityForResult(imageIntent, REQ_IMAGE_CODE)
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

                    val memberMap = hashMapOf<String,String>()

                    memberList.forEach {
                        memberMap.put(it.uid, it.name!!)
                    }
                    viewModel.memberMap = memberMap

                    detailAdapter = DetailMemberListAdapter(requireActivity())
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

        val alertDialog = AlertDialog.Builder(requireActivity())
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
                            )
                        )

                        batch.update(
                            usersRef.document(uid),
                            hashMapOf<String, Any>(
                                // 가입된 스터디 배열에서 studyId 삭제
                                "studyList" to FieldValue.arrayRemove(studyId)
                            )
                        )
                    }

                        .addOnSuccessListener {
                            requireActivity().finish()
                        }
                }.create().show()
            //가입이 안되있다면
        } else {
            val et: EditText = EditText(requireActivity())
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
                            Toast.makeText(requireActivity(), "신청되었습니다.", Toast.LENGTH_SHORT).show()
                            requireActivity().finish()

                        }
                    //신청 다이얼로그 끝끝
                }.create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Result임둥","$requestCode - 내가 보낸값은 1001인데..")
        if (requestCode == REQ_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val image: Uri = data!!.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image)
                fragView.detail_profile_image.setImageBitmap(bitmap)

            }
        }
    }

    companion object {
        const val REQ_IMAGE_CODE = 1001
    }

    lateinit var dialogPermissionListener: PermissionListener

}