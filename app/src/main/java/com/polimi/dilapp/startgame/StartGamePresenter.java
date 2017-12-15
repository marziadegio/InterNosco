package com.polimi.dilapp.startgame;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;

import com.polimi.dilapp.R;
import com.polimi.dilapp.database.AppDatabase;
import com.polimi.dilapp.database.DatabaseInitializer;
import com.polimi.dilapp.levelmap.LevelMapActivity;


public class StartGamePresenter implements  IStartGame.Presenter {

    private IStartGame.View startGameView;
    private AppDatabase db;

    StartGamePresenter(IStartGame.View view){
        this.startGameView = view;
        db = AppDatabase.getAppDatabase(startGameView.getScreenContext());
        //TODO: get the current child from the model

    }

    @Override
    public void onInit(Animation animation) {
        BounceInterpolator interpolator = new BounceInterpolator(0.7, 10);
        animation.setInterpolator(interpolator);
    }

    @Override
    public void onDestroy() {
        startGameView = null;
    }

    @Override
    public void onPlayButtonPressed() {
        //TODO: Redirect to the last activity not completed, checking the level associated to the current child
    }

    @Override
    public void onItemMenuSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_level:
                Intent intent = new Intent(startGameView.getScreenContext(), LevelMapActivity.class);
                startGameView.getScreenContext().startActivity(intent);
                break;
            case R.id.report:
                //TODO: Start Report Activity
                break;
        }
    }
    @Override
    public void resumeCurrentPlayer(Bundle savedInstanceState) {
        DatabaseInitializer.setCurrentPlayer(db, savedInstanceState.getInt("current_player"));
        DatabaseInitializer.setLevelCurrentPlayer(db, savedInstanceState.getInt("level"));
        Log.i("Current player: ", String.valueOf(DatabaseInitializer.getCurrentPlayer(db)));

    }

    @Override
    public void storeCurrentPlayer(Bundle savedInstanceState) {
        savedInstanceState.putInt("current_player", DatabaseInitializer.getCurrentPlayer(db));
        savedInstanceState.putInt("level", DatabaseInitializer.getLevelCurrentPlayer(db));
    }
}
