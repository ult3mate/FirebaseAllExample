package com.beer.android.firebaseallfeatureproject

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.beer.android.firebaseallfeatureproject.adapter.MessageAdapter
import com.beer.android.firebaseallfeatureproject.databinding.ActivityChatBinding
import com.beer.android.firebaseallfeatureproject.model.Message
import com.beer.android.firebaseallfeatureproject.model.MessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    val CHAT_DATABASE_REFERENCE_KEY = "global_room"
    private var messageDataList: MutableList<MessageData>? = ArrayList()
    private var messageDatabaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var messageAdapter : MessageAdapter? = null
    private lateinit var binding : ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        setContentView(R.layout.activity_chat)

        setUpFirebase()
        setupRealtimeDatabase()

        initLayoutManager()
        initMessageAdapter()

        binding.etMessageSendBtn.setOnClickListener {
            addNewMessage(binding.etMessage.text.toString())
        }
    }

    private fun setUpFirebase(){
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
    }

    private fun setupRealtimeDatabase() {
        messageDatabaseReference = firebaseDatabase?.reference?.child(CHAT_DATABASE_REFERENCE_KEY)
        messageDatabaseReference?.addValueEventListener(messageValueEventListener)
        messageDatabaseReference?.addChildEventListener(messageChildEventListener)
    }

    private fun initLayoutManager(){
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvMessage.layoutManager = linearLayoutManager
    }

    private fun initMessageAdapter(){
        messageAdapter = MessageAdapter(messageDataList!!, getCurrentUserEmail()!!)
        binding.rvMessage.adapter = messageAdapter
    }

    private fun addNewMessage(message: String) {
        binding.etMessage.setText("")
        val messageData = Message(message, getCurrentUserEmail()!!)
        messageDatabaseReference?.push()?.setValue(messageData)
    }

    private val messageValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            messageDatabaseReference?.removeEventListener(this)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(this@ChatActivity , databaseError.message , Toast.LENGTH_LONG).show()
        }
    }

    private val messageChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val key = dataSnapshot.key
            val message = dataSnapshot.getValue<Message>(Message::class.java)
            val messageData = MessageData(key, message!!)
            messageDataList?.add(messageData)
            updateMessageView()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val key = dataSnapshot.key
            for (messageData in messageDataList!!) {
                if (key == messageData.key) {
                    messageDataList?.remove(messageData)
                    updateMessageView()
                    return
                }
            }
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(this@ChatActivity , databaseError.message , Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMessageView() {
        messageAdapter?.notifyDataSetChanged()
        binding.rvMessage.smoothScrollToPosition(binding.rvMessage.adapter.itemCount)
    }

    private fun getCurrentUserEmail(): String? {
        val firebaseUser = firebaseAuth?.getCurrentUser()
        return if (firebaseUser != null) {
            firebaseUser.getEmail()
        } else ""
    }
}
