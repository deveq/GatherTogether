package com.soldemom.navermapactivity.testFrag

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.navermapactivity.DetailActivity
import com.soldemom.navermapactivity.Point
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.User
import kotlinx.android.synthetic.main.fragment_test_attend.view.*

class TestAttendFrag() : Fragment() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var user: User
    lateinit var studyList: List<Point>
    lateinit var adapter: TestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test_attend, container, false)

        adapter = TestAdapter(::itemLambda)

        view.test_recycler_view.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }




        getStudyListFromDB()

        return view
    }

    override fun onResume() {
        super.onResume()
        getStudyListFromDB()

    }
    
    fun log(str: String) {
        Log.d("TestAttend",str)
    }

    fun getStudyListFromDB() {
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)!!
                db.collection("markers").whereIn("studyId", user.studyList!!)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        studyList = querySnapshot.toObjects(Point::class.java)

                        adapter.studyList = studyList
                        adapter.notifyDataSetChanged()
                    }
            }
    }

    fun itemLambda(point: Point) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("studyId",point.studyId)
        startActivity(intent)
    }
}