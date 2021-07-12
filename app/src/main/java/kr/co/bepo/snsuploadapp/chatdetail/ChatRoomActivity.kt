package kr.co.bepo.snsuploadapp.chatdetail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bepo.snsuploadapp.DBKey.Companion.DB_CHATS
import kr.co.bepo.snsuploadapp.R

class ChatRoomActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val userId: String by lazy {
        auth.currentUser?.uid.orEmpty()
    }

    private val chatRecyclerView: RecyclerView by lazy {
        findViewById(R.id.chatRecyclerView)
    }

    private val messageEditText: EditText by lazy {
        findViewById(R.id.messageEditText)
    }

    private val sendButton: Button by lazy {
        findViewById(R.id.sendButton)
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val chatKey = intent.getLongExtra("chatKey", -1)
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java) ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            val chatItem = ChatItem(
                senderId = userId,
                message = messageEditText.text.toString()
            )

            chatDB?.push()?.setValue(chatItem)
        }
    }
}