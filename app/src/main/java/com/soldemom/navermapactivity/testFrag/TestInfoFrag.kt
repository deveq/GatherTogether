package com.soldemom.navermapactivity.testFrag

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.navermapactivity.LoginActivity
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.TestActivity3
import com.soldemom.navermapactivity.User
import kotlinx.android.synthetic.main.fragment_test_info.view.*

class TestInfoFrag : Fragment() {
    
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test_info, container, false)

        val uid = auth.currentUser!!.uid




        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)!!
                view.apply {
                    info_email.text = auth!!.currentUser!!.email
                    info_name.setText(user.name)


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
                .setMessage("탈퇴하더라도 스터디의 대화내용은 남습니다.")
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
                }.create().show()
        }

        return view
    }

}