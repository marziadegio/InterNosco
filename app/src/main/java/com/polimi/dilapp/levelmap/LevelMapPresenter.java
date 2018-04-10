package com.polimi.dilapp.levelmap;


import android.content.Intent;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.polimi.dilapp.levels.view.ActivityOneOne;
import com.polimi.dilapp.levels.view.ActivityOneThree;
import com.polimi.dilapp.levels.view.ActivityOneTwo;
import com.polimi.dilapp.levels.view.ActivityThreeOne;
import com.polimi.dilapp.levels.view.ActivityThreeTwo;
import com.polimi.dilapp.levels.view.ActivityTwoOne;
import com.polimi.dilapp.levels.view.ActivityTwoThree;
import com.polimi.dilapp.levels.view.ActivityTwoTwo;
import com.polimi.dilapp.report.ReportMainActivity;
import com.polimi.dilapp.report.ReportSpecActivity;
import com.polimi.dilapp.startgame.StartGameActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelMapPresenter implements  ILevelMap.Presenter{
    private ILevelMap.View levelMapView;
    private List<String> listTitles;
    private HashMap<String, List<String>> listItems;

    LevelMapPresenter(ILevelMap.View view){

        this.levelMapView= view;
    }

    @Override
    public void initData() {
        listItems = (HashMap<String, List<String>>) ExpandableListData.getData();
        listTitles = new ArrayList<>();
        listTitles.add("OGGETTI E COLORI");
        listTitles.add("LETTERE E NUMERI");
        listTitles.add("LOGICA");

        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(levelMapView.getContext(), listTitles, listItems);
        levelMapView.showAdapter(adapter);
    }

    @Override
    public void onItemSelected(ExpandableListView parent, int mainCategoryPosition, int subcategoryPosition) {
        String itemSelected = listItems.get(listTitles.get(mainCategoryPosition)).get(subcategoryPosition);
        Intent intent = new Intent(levelMapView.getContext(), ReportSpecActivity.class);
        if(levelMapView.getString().equals("ReportLevelMapActivity")){
            switch (itemSelected) {
                case "NOMI":
                    intent.putExtra("level", 11);
                    break;
                case "COLORI":
                    intent.putExtra("level", 12);
                    break;
                case "FORME":
                    intent.putExtra("level", 13);
                    break;
                case "NUMERI":
                    intent.putExtra("level", 21);
                    break;
                case "ALFABETO":
                    intent.putExtra("level", 22);
                    break;
                case "PAROLE":
                    intent.putExtra("level", 23);
                    break;
                case "CONTIAMO INSIEME":
                    intent.putExtra("level", 31);
                    break;
                case "CUCINA CON NOSCO":
                    intent.putExtra("level", 32);
                    break;
                default:
                    break;
            }
            levelMapView.getContext().startActivity(intent);
        }else {
            switch (itemSelected) {
                case "NOMI":
                    intent = new Intent(levelMapView.getContext(), ActivityOneOne.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "COLORI":
                    intent = new Intent(levelMapView.getContext(), ActivityOneTwo.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "FORME":
                    intent = new Intent(levelMapView.getContext(), ActivityOneThree.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "NUMERI":
                    intent = new Intent(levelMapView.getContext(), ActivityTwoOne.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "ALFABETO":
                    intent = new Intent(levelMapView.getContext(), ActivityTwoTwo.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "PAROLE":
                    intent = new Intent(levelMapView.getContext(), ActivityTwoThree.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "CONTIAMO INSIEME":
                    intent = new Intent(levelMapView.getContext(), ActivityThreeOne.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                case "CUCINA CON NOSCO":
                    intent = new Intent(levelMapView.getContext(), ActivityThreeTwo.class);
                    levelMapView.getContext().startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClickBack() {
        Intent intent;
        if(levelMapView.getString().equals("ReportLevelMapActivity")){
            intent = new Intent(levelMapView.getContext(), ReportMainActivity.class);
        }
        else {
            intent = new Intent(levelMapView.getContext(), StartGameActivity.class);
        }
        levelMapView.getContext().startActivity(intent);
        levelMapView = null;
    }
}
