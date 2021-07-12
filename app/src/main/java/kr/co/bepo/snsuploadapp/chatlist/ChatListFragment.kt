package kr.co.bepo.snsuploadapp.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.bepo.snsuploadapp.DBKey.Companion.CHILD_CHAT
import kr.co.bepo.snsuploadapp.DBKey.Companion.DB_USERS
import kr.co.bepo.snsuploadapp.R
import kr.co.bepo.snsuploadapp.chatdetail.ChatRoomActivity
import kr.co.bepo.snsuploadapp.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {
    private lateinit var chatDB: DatabaseReference

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()
    private var binding: FragmentChatlistBinding? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val userId: String by lazy {
        auth.currentUser?.uid.orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatListBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatListBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatListItem ->
            context?.let { context ->
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatListItem.key)
                startActivity(intent)
            }
        })

        chatRoomList.clear()

        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentChatListBinding.chatListRecyclerView.adapter = chatListAdapter

        if (auth.currentUser == null) {
            return
        }

        chatDB = Firebase.database.reference.child(DB_USERS).child(userId).child(CHILD_CHAT)
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    val model = dataSnapshot.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}