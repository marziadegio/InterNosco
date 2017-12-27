package com.polimi.dilapp.levels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.polimi.dilapp.R;
import com.polimi.dilapp.database.AppDatabase;
import com.polimi.dilapp.database.AppDatabase_Impl;
import com.polimi.dilapp.database.ChildDao;
import com.polimi.dilapp.database.DatabaseInitializer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, AppDatabase.class, SystemClock.class, Toast.class, NfcAdapter.class, Intent.class, AsyncTask.class, DatabaseInitializer.class})
public class GamePresenterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IGame.View iGame;

    @Mock
    private AppDatabase_Impl appDatabaseImpl;

    @Mock
    private AppDatabase appDatabase;

    private GamePresenter gamePresenter;

    @Mock
    private ChildDao childDao;

    @Mock
    private ArrayList<String> mockedArray;

    @Mock
    private Toast toast;

    @Mock
    private Tag tag;

    @Mock
    private NfcAdapter nfcAdapter;

    @Mock
    private Intent intent;

    @Mock
    private GamePresenter.NdefReaderTask ndefReaderTask;

    @Mock
    private Activity activity;

    @Mock
    DatabaseInitializer databaseInitializer;


    @Before
    public void test() {

        //startGameTest initialization
        long longNumber = 1;
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(AppDatabase.class);
        PowerMockito.mockStatic(DatabaseInitializer.class);
        PowerMockito.mockStatic(NfcAdapter.class);
        PowerMockito.mockStatic(SystemClock.class);
        PowerMockito.mockStatic(Intent.class);
        PowerMockito.mockStatic(Toast.class);
        PowerMockito.mockStatic(AsyncTask.class);
        when(Log.i(any(String.class), any(String.class))).thenReturn(1);
        when(Log.e(any(String.class), any(String.class), any(Throwable.class))).thenReturn(1);
        when(AppDatabase.getAppDatabase(any(Context.class))).thenReturn(appDatabaseImpl);
        when(appDatabaseImpl.childDao()).thenReturn(childDao);
        when(iGame.getString()).thenReturn("ActivityOneOne");
        when(SystemClock.elapsedRealtime()).thenReturn(longNumber);
        when(Toast.makeText(any(Context.class), any(String.class), any(int.class))).thenReturn(toast);
        //isTheCurrentSessionArrayEmpty
        ArrayList<String> currentSessionArray = new ArrayList<>();
        when(iGame.getSessionArray(any(int.class))).thenReturn(currentSessionArray);

        gamePresenter = new GamePresenter(iGame);

    }

    //Here we test startGame(...);
    @Test
    public void gameIsStarted() {
        ArrayList<String> currentSequence = new ArrayList<>();
        currentSequence.add("a");
        gamePresenter.startGame(currentSequence);
        assertTrue(gamePresenter.isStarted());
    }


    @Test
    public void emptyCurrentSequenceInStartGame() {
        ArrayList<String> currentSequence = new ArrayList<>();
        gamePresenter.startGame(currentSequence);
        assertFalse(gamePresenter.isStarted());
    }

    //Here we test getResourceId(... , ...);
    @Test
    public void getResourceIdCorrectFunctioningTest() {
        String name = "lemon";
        int resourceId = gamePresenter.getResourceId(name, R.drawable.class);

        Assert.assertEquals(resourceId, R.drawable.lemon);
    }

    @Test(expected = Exception.class)
    public void getResourceIdThrowingExceptionTest() {
        gamePresenter.getResourceId(any(String.class), R.drawable.class);
    }


    //Here we test startNewSession(...);
    @Test
    public void startNewSessionTest() {
        String currentSequenceElement = "subset_names_one";
        when(iGame.getSessionArray(any(int.class))).thenReturn(mockedArray);
        try {
            Whitebox.invokeMethod(gamePresenter, "startNewSession", currentSequenceElement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(gamePresenter.getNewSessionStarted());
    }

    //Here we test startNewTurn with empty sequence
    @Test
    public void isTheGameEnded() {
        ArrayList<String> emptySequence = new ArrayList<String>();
        emptySequence.add("test");
        gamePresenter.startGame(emptySequence);
        try {
            Whitebox.invokeMethod(gamePresenter, "startNewTurn");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int diff = gamePresenter.getTotalAttempts() - gamePresenter.getCorrectAnswers();
        int percentage = (20 * gamePresenter.getTotalAttempts()) / 100;
        if (diff > percentage) {
            verify(iGame, Mockito.times(

                    1)).setRepeatOrExitScreen();
        } else {
            verify(iGame, Mockito.times(1)).setGoOnOrExitScreen();
        }
        assertTrue(gamePresenter.isEnded());

    }

    @Test
    public void isTheNewTurnStarted() {
        ArrayList<String> notEmptySequence = new ArrayList<String>();
        notEmptySequence.add("lemon");
        notEmptySequence.add("carrot");
        gamePresenter.startGame(notEmptySequence);

        try {
            Whitebox.invokeMethod(gamePresenter, "startNewTurn");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(gamePresenter.getNewTurnStarted());
        try {
            PowerMockito.verifyPrivate(gamePresenter, times(1)).invoke("startNewSession", "carrot");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Here we test chooseElement();
    @Test
    public void isTheNewElementChosen() {
        ArrayList<String> notEmptySequence = new ArrayList<String>();
        notEmptySequence.add("lemon");
        notEmptySequence.add("carrot");
        gamePresenter.startGame(notEmptySequence);

        gamePresenter.chooseElement();

        assertEquals("carrot", gamePresenter.getCurrentElement());
    }

    @Test
    public void noMoreElementsInTheTempArray() {
        ArrayList<String> notEmptySequence = new ArrayList<String>();
        notEmptySequence.add("lemon");
        notEmptySequence.add("carrot");
        gamePresenter.startGame(notEmptySequence);

        gamePresenter.chooseElement();

        try {
            PowerMockito.verifyPrivate(gamePresenter, times(1)).invoke("startNewTurn");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Here we test askCurrentElement();
    @Test
    public void isTheCurrentElementAsked() {
        gamePresenter.askCurrentElement();
        verify(iGame, Mockito.times(1)).setPresentationAnimation(gamePresenter.getCurrentElement());
    }

    @Test
    public void correctAnswerTest() {
        int total = gamePresenter.getTotalAttempts();
        int correct = gamePresenter.getCorrectAnswers();
        total++;
        correct++;

        try {
            Whitebox.invokeMethod(gamePresenter, "correctAnswer");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(0, gamePresenter.getCounter());
        Assert.assertEquals(total, gamePresenter.getTotalAttempts());
        Assert.assertEquals(correct, gamePresenter.getCorrectAnswers());

        verify(iGame, Mockito.times(1)).setVideoCorrectAnswer();

    }

    @Test
    public void wrongAnswerElseTest() {
        int total = gamePresenter.getTotalAttempts();
        total++;

        gamePresenter.setCounter(2);
        try {
            Whitebox.invokeMethod(gamePresenter, "wrongAnswer");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(0, gamePresenter.getCounter());
        Assert.assertEquals(total, gamePresenter.getTotalAttempts());
        verify(iGame, Mockito.times(1)).setVideoWrongAnswerAndGoOn();
    }

    @Test
    public void wrongAnswerIfTest() {
        int total = gamePresenter.getTotalAttempts();
        int counter = gamePresenter.getCounter();
        counter++;
        total++;

        gamePresenter.setCounter(0);
        try {
            Whitebox.invokeMethod(gamePresenter, "wrongAnswer");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(counter, gamePresenter.getCounter());
        Assert.assertEquals(total, gamePresenter.getTotalAttempts());
        verify(iGame, Mockito.times(1)).setVideoWrongAnswerToRepeat();
    }


    @Test
    public void notifyFirstSubElementTest() {
        gamePresenter.notifyFirstSubElement();
        verify(iGame, Mockito.times(1)).initGridView(gamePresenter.getCurrentSubElement());
    }

    @Test
    public void nfcNotAvailableOrEnabledTest() {
        when(NfcAdapter.getDefaultAdapter(any(Context.class))).thenReturn(null);
        boolean check = gamePresenter.checkNfcAvailability();
        Assert.assertEquals(false, check);
    }

    @Test
    public void nfcAvailableTest() {
        when(NfcAdapter.getDefaultAdapter(any(Context.class))).thenReturn(nfcAdapter);
        boolean check = gamePresenter.checkNfcAvailability();
        Assert.assertEquals(true, check);
    }

    //TODO: cover checkAnswer() [Marzia]
    @Test
    @Ignore
    public void checkAnswerTest() {

    }

    //TODO: cover checkMultipleItems() [Marzia]
    @Test
    @Ignore
    public void checkMultipleItemsTest() {

    }

    @Test
    public void onDestroyTest() {
        gamePresenter.onDestroy();
        Assert.assertEquals(null, gamePresenter.getActivityInterface());
    }

    //TODO: check setLevelCurrentPlayer(); [Giuli]
    @Test
    public void setLevelCurrentPlayerTest() {

        when(gamePresenter.getActivityInterface().getString()).thenReturn("ActivityOneOne");
        gamePresenter.setLevelCurrentPlayer();


    }


    //TODO: [ROBY]
    @Test
    @Ignore
    public void handleIntentTest() {
        when(intent.getAction()).thenReturn("android.nfc.action.NDEF_DISCOVERED");
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getParcelableExtra("android.nfc.extra.TAG")).thenReturn(null);
        PowerMockito.mock(GamePresenter.NdefReaderTask.class);

        gamePresenter.handleIntent(intent);

        verify(ndefReaderTask, Mockito.times(1)).execute(tag);
    }


    //TODO: roby
    @Test
    @Ignore
    public void stopForegroungDispacherTest() {

        when(NfcAdapter.getDefaultAdapter(any(Context.class))).thenReturn(nfcAdapter);
        gamePresenter.checkNfcAvailability();

        gamePresenter.setupForegroundDispatch();

        gamePresenter.stopForegroundDispatch();
        verify(nfcAdapter, Mockito.times(1)).disableForegroundDispatch(activity);
    }

    //TODO: ROBY
    @Test
    @Ignore
    public void setupForegrounfDispacherTest(){

    }

}