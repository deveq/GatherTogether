package com.soldemom.navermapactivity.testFrag

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.soldemom.navermapactivity.LoginActivity
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.TestActivity3
import com.soldemom.navermapactivity.User
import kotlinx.android.synthetic.main.fragment_test_info.view.*
import java.security.Permission

class TestInfoFrag : Fragment() {
    
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var user: User
    lateinit var uid: String
    lateinit var dialogPermissionListener: DialogOnDeniedPermissionListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_info, container, false)

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

        uid = auth.currentUser!!.uid

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)!!
                view.apply {
                    info_email.text = auth!!.currentUser!!.email
                    info_name.text = user.name

                    info_introduce.text =
                        if (user.introduce != "") user.introduce
                        else "소개를 입력해주세요"

                    user.image?.let { imageUri ->
                        Glide.with(requireActivity())
                            .load(imageUri)
                            .into(info_profile_image)
                    }

                }
            }

        view.info_my_account.setOnClickListener {

            val editName = EditText(requireContext())

            if (editName.parent != null) {
                (editName.parent as ViewGroup).removeView(editName)
            }

            editName.setText(user.name)
            editName.hint = "이름을 입력해주세요."
            Log.d("정보","정보수정")
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("이름 변경")
                .setView(editName)
                .setNegativeButton("취소",null)
                .setPositiveButton("확인") { _,_ ->
                    user.name = editName.text.toString()
                    view.info_name.text = user.name
                    db.collection("users")
                        .document(uid)
                        .update("name",user.name)
                }.create()
            dialog.show()

        }

        view.info_introduce.setOnClickListener {

            val editIntro = EditText(requireContext())

            if (editIntro.parent != null) {
                (editIntro.parent as ViewGroup).removeView(editIntro)
            }

            editIntro.setText(user.introduce)
            editIntro.hint = "소개를 입력해주세요."
            Log.d("정보","정보수정")
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("소개 변경")
                .setView(editIntro)
                .setNegativeButton("취소",null)
                .setPositiveButton("확인") { _,_ ->
                    user.introduce = editIntro.text.toString()
                    view.info_introduce.text = user.introduce
                    db.collection("users")
                        .document(uid)
                        .update("introduce",user.introduce)
                }.create()
            dialog.show()
        }

        view.info_logout.setOnClickListener {
            Log.d("정보","로그아웃")
            AlertDialog.Builder(requireContext())
                .setMessage("로그아웃 하시겠습니까?")
                .setNegativeButton("취소",null)
                .setPositiveButton("로그아웃") { _,_ ->
                    auth.signOut()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }.create().show()
        }

        view.info_delete_account.setOnClickListener {
            Log.d("정보","탈퇴")
            AlertDialog.Builder(requireContext())
                .setTitle("탈퇴하시겠습니까?")
                .setMessage("불편하시겠지만 로그아웃하고 다시 로그인후에 탈퇴해주세요...\n굽신굽신")
                .setNegativeButton("취소",null)
                .setPositiveButton("탈퇴") { _, _ ->
                    db.collection("users").document(uid)
                        .delete()
                        .addOnSuccessListener {
                            auth.currentUser!!
                                .delete()
                                .addOnSuccessListener {
                                Log.d("정보","탈퇴됨.")
                                Toast.makeText(requireContext(),"탈퇴되었습니다.",Toast.LENGTH_LONG).show()
                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                startActivity(intent)
                            }
                                .addOnFailureListener {
                                    Log.d("정보","${it.message}")
                                    it.printStackTrace()
                                }
                        }
                        .addOnFailureListener {
                            Log.d("정보","완전실패 ${it.message}")
                            it.printStackTrace()
                        }

                }.create().show()
        }

        view.info_profile_image.setOnClickListener {
            Dexter
                .withContext(requireContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(dialogPermissionListener)
                .check()

            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            requireActivity().startActivityForResult(imageIntent, REQ_IMAGE_CODE)
        }

        return view
    }

    companion object {
        const val REQ_IMAGE_CODE = 1002
    }
}