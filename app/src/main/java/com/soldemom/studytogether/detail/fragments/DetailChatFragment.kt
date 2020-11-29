package com.soldemom.studytogether.detail.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.studytogether.R
import com.soldemom.studytogether.detail.DetailViewModel
import com.soldemom.studytogether.detail.dto.Chat
import com.soldemom.studytogether.detail.recycler.DetailChatAdapter
import com.soldemom.studytogether.main.dto.User
import kotlinx.android.synthetic.main.fragment_detail_chat.view.*
import java.text.SimpleDateFormat


class DetailChatFragment(val studyId: String) : Fragment() {

    lateinit var fragView: View
    val auth = Firebase.auth
    val realtimeDB = Firebase.database
    val db = FirebaseFirestore.getInstance()
    lateinit var user: User
    lateinit var adapter: DetailChatAdapter
    var dateTemp: String? = null
    lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_detail_chat, container, false)


        viewModel = ViewModelProvider(requireActivity()).get(DetailViewModel::class.java)

        adapter = DetailChatAdapter(viewModel.memberMap)

        val uid = auth.currentUser?.uid ?: ""


        val userRef = db.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener {
                Log.d("Chat","성공")
                user = it.toObject(User::class.java) ?: User(uid)
                if (user.studyList.contains(studyId)) {
                    Log.d("Chat","가입되어있음")
                    fragView.detail_chat_recycler_view.visibility = View.VISIBLE
                    fragView.detail_chat_input.visibility = View.VISIBLE
                    fragView.detail_chat_send_btn.visibility = View.VISIBLE

                    setSendBtn()


                    adapter.user = user

                    fragView.detail_chat_recycler_view.adapter = adapter


                } else {
                    Log.d("Chat","가입안됨")
                    fragView.detail_no_chat_text.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { 
                Log.d("Chat","실패")
            }



        return fragView
    }

    override fun onResume() {
        super.onResume()

        val myRef = realtimeDB.getReference("chat").child(studyId)
        val chatList = mutableListOf<Chat>()

        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(Chat::class.java)
                chat?.apply {
                    val sdf = SimpleDateFormat("yyyy. MM. dd.")
                    val date = sdf.format(chat.time)
                    Log.d("Chat","$date 입니당")

                    if (dateTemp == null || dateTemp != date) {
                        Log.d("Chat","$date 입니당")
                        val dateChat = Chat(0,date,"")
                        dateChat.type = 1
                        chatList.add(dateChat)
                        dateTemp = date
                    }
                    chatList.add(this)
                }
                adapter.chatList = chatList
                adapter.notifyDataSetChanged()
                fragView.detail_chat_recycler_view.scrollToPosition(chatList.size-1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })




    }

    fun setSendBtn() {
        fragView.detail_chat_send_btn.setOnClickListener {

            val text = fragView.detail_chat_input.text.toString()
            val chat = Chat(System.currentTimeMillis(), text, user.uid)
//            realtimeDB.getReference("chat").push().setValue(chat)
            realtimeDB.getReference("chat").child(studyId).push().setValue(chat)
                .addOnSuccessListener {
                    fragView.detail_chat_input.setText("")
                }
        }
    }



}