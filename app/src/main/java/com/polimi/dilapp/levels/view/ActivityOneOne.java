package com.polimi.dilapp.levels.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.polimi.dilapp.R;
import com.polimi.dilapp.levels.GamePresenter;
import com.polimi.dilapp.levels.IGame;
import com.polimi.dilapp.startgame.StartGameActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
*
* In this Activity the child learns to associate a name to an object, following the order of colours.
*
* */

public class ActivityOneOne extends AppCompatActivity implements IGame.View {

    ArrayList<String> nameSequence;
    IGame.Presenter presenter;
    MediaPlayer request;
    String element;
    CommonActivity common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);

        //set up the presenter and pass it to the common activity view
        try {
            presenter = new GamePresenter(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        common = new CommonActivity(presenter);

        setupSequence();

       boolean availability = presenter.checkNfcAvailability();
        if (availability) {
            setupVideoIntro();
        }else{
            finish();
        }
    }

    private void setupSequence(){
        String[] names = getResources().getStringArray(R.array.names);
        nameSequence = common.getList(names);
    }

    private void setupVideoIntro(){
        //Introduction to the whole activity game
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
        common.startIntro(uri, nameSequence,this);
    }

    @Override
    public void disableViews(){
        ImageView imageToHide = findViewById(R.id.animation_box);
        ImageView animationViewExtra = findViewById(R.id.animation_box_two);
        ImageView animationViewExtraTwo = findViewById(R.id.animation_box_three);

        common.disableView(imageToHide);
        common.disableView(animationViewExtra);
        common.disableView(animationViewExtraTwo);
    }

    @Override
    public void sendEmail(Intent i) {

    }

    @Override
    public void setVideoView(int videoID){
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoID);
        common.startMainVideo(uri, this);
    }

    @Override
    public void setPresentationAnimation(String currentElement){
        element = currentElement;
        int resourceID = presenter.getResourceId(element, R.drawable.class);
        Animation animationBegin = AnimationUtils.loadAnimation(ActivityOneOne.this, R.anim.rotation);
        common.enableLionHeadAnimation(ActivityOneOne.this, this);
        common.startMainAnimation(this,animationBegin,resourceID,this);

        setAudioRequest();
    }

    private void setAudioRequest(){
        int objectClaimedID = presenter.getResourceId("request_" + element, R.raw.class);
        final AppCompatActivity activity = this;
        final Context context = ActivityOneOne.this;

        request = MediaPlayer.create(ActivityOneOne.this, objectClaimedID);
        request.start();
        request.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                common.enableKiteAnimationBackground(activity, context);
                mp.release();
                setWaitingAnimation();
                presenter.setEnableNFC();
                presenter.handleIntent(getIntent());

            }
        });
    }

    public void setWaitingAnimation(){
        int resourceID = presenter.getResourceId(element, R.drawable.class);
        Animation animationWait = AnimationUtils.loadAnimation(ActivityOneOne.this, R.anim.waiting_rotation);

        common.enableLionBackground(this);
        common.enableLionTailAnimation(this, ActivityOneOne.this);
        common.startMainAnimation(this,animationWait,resourceID,this);

    }

    @Override
    public void setVideoCorrectAnswer(){
        disableViews();
        common.enableLionHeadAnimation(ActivityOneOne.this, this);
        ImageView image = findViewById(R.id.animation_box_answer);
        image.setVisibility(View.VISIBLE);
        common.setCorrectAnswer(image, this);
    }

    @Override
    public void setVideoWrongAnswerToRepeat() {
        disableViews();
        common.enableLionHeadAnimation(ActivityOneOne.this, this);
        common.setWrongAnswerToRepeat(this);
    }

    @Override
    public void setVideoWrongAnswerAndGoOn() {
        disableViews();
        common.enableLionHeadAnimation(ActivityOneOne.this, this);
        common.setWrongAnswerAndGoOn( this);
    }

    @Override
    public void setRepeatOrExitScreen() {
        Intent intent = new Intent(getApplicationContext(), EndLevelScreen.class);
        intent.putExtra("Activity","com.polimi.dilapp.levels.view.ActivityOneOne");
        intent.putExtra("ButtonName", "Ripeti");

        startActivity(intent);
    }

    @Override
    public void setGoOnOrExitScreen() {
        Intent intent = new Intent(getApplicationContext(), EndLevelScreen.class);
        intent.putExtra("Activity","com.polimi.dilapp.levels.view.ActivityOneTwo");
        intent.putExtra("ButtonName", "Avanti");
        startActivity(intent);
    }

    @Override
    public String getString() {
        return "ActivityOneOne";
    }

    @Override
    public List<String> getSessionArray(int vectorID) {
        String[] sessionFruitVector = getResources().getStringArray(vectorID);
        if(vectorID == R.array.all_fruits_items){
            return common.getPartialArray(sessionFruitVector);
        }else {
            List<String> array = new ArrayList<>(Arrays.asList(sessionFruitVector));
            Collections.sort(array);
            return array;
        }
    }

    @Override
    public Class getApplicationClass(){

        return this.getClass();
    }

    @Override
    public Context getScreenContext() {
        return this;
    }

    //We want to handle NFC only when the Activity is in the foreground
    @Override
    protected void onResume() {
        super.onResume();
       presenter.setupForegroundDispatch();
    }

    @Override
    protected void onPause() {
      presenter.stopForegroundDispatch();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        common.onDestroy();
        presenter = null;
        common = null;
    }

    //onNewIntent let us stay in the same activity after reading a TAG
    @Override
    protected void onNewIntent(Intent intent) {
        presenter.handleIntent(intent);
    }

    @Override
    public void setSubItemAnimation(String currentSubElement) {
        //NOT USED
    }

    @Override
    public void initTableView(String currentSubItem) {
        //NOT USED
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.disableViews();
        common.disableKiteExtraView(this);
        common.disableLionExtraView(this);
        presenter.getEndTime();
        presenter.setObjectCurrentPlayer();
        presenter.setSubStringCurrentPlayer();
        if(request != null ){
            request.release();
            request=null;
        }
        startActivity(new Intent(ActivityOneOne.this, StartGameActivity.class));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        presenter.storeCurrentPlayer(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
        Log.i("[ACTIVITY 11]", "I'm calling storeCurrentPlayer");

    }

}
