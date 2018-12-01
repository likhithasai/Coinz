package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.android.gms.R.id.toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_leaderboard.*
import android.R.attr.category
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener




class LeaderboardActivity : AppCompatActivity() {

    private val tag = "LeaderboardActivity"
    var dbRef = FirebaseDatabase.getInstance().reference

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

        dbRef.child("goldcoins").orderByChild("score")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val usersList = ArrayList<User>()
                        for (adSnapshot in dataSnapshot.children) {
                            usersList.add(adSnapshot.getValue(User::class.java)!!)
                        }
                        showPlayersPosition(usersList)
                        Log.d(tag, "no of records of the search is " + usersList.size)

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d(tag, "Error trying to get classified ads for " + category +
                                " " + databaseError)
                    }
                })


        /*val db = FirebaseFirestore.getInstance()

        db.collection("players")
                .orderBy("score", Query.Direction.DESCENDING)
                .addSnapshotListener({ snapshots, error ->
                    if (error != null) {
                        Log.d("TAG", error.message)
                        return@addSnapshotListener
                    }

                    val players = snapshots.map{
                        it.toObject(User::class.java)
                    }
                    val champions = players.take(3)
                    showPlayersPosition(players)
                    //showChampions(champions)
                })*/
    }

    private fun showPlayersPosition(players: List<User>) {
        val adapter = recycler_view.adapter as PlayerAdapter
        adapter.addPlayers(players)
    }

//    private fun showChampions(championPlayers: List<Player>) {
//        iv_champion1.loadImg(championPlayers[0].photo)
//        iv_champion2.loadImg(championPlayers[1].photo)
//        iv_champion3.loadImg(championPlayers[2].photo)
//    }


}
