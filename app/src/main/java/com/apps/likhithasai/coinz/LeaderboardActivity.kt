package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_leaderboard.*
import android.R.attr.category
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class LeaderboardActivity : AppCompatActivity() {

    private val tag = "LeaderboardActivity"
    private var dbRef = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        //setSupportActionBar(toolbar)
        setupRecyclerView()
        loadPlayers()
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlayerAdapter()
            setHasFixedSize(true)
        }
    }

    private fun loadPlayers() {

        val db = FirebaseFirestore.getInstance()

        db.collection("Leaderboards")
                .orderBy("score", Query.Direction.DESCENDING).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                    if (e != null) {
                        Log.w(tag, "Listen failed.", e)
                        return@EventListener
                    }

                    val players = ArrayList<User>()
                    for (doc in value!!) {
                        if (doc.get("name") != null && doc.get("score") != null) {
                            players.add(User(doc.get("name").toString(),doc.get("score").toString()))
                        }
                    }

                    showPlayersPosition(players)
                })

    }

    private fun showPlayersPosition(players: ArrayList<User>) {
        val adapter = recycler_view.adapter as PlayerAdapter
        adapter.addPlayers(players)
    }

//    private fun showChampions(championPlayers: List<Player>) {
//        iv_champion1.loadImg(championPlayers[0].photo)
//        iv_champion2.loadImg(championPlayers[1].photo)
//        iv_champion3.loadImg(championPlayers[2].photo)
//    }


}
