package com.delarosa.notimedia.model.Entitys;

import com.delarosa.notimedia.R;
import com.nightonke.boommenu.BoomButtons.HamButton;


public class BuilderManagerButton {

    //images to the button
    private static int[] imageResources = new int[]{
            R.drawable.nonoti,
            R.drawable.novideo,
            R.drawable.home,
    };

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    //builder for the button
    public static HamButton.Builder getHamButtonBuilder(String text, String subText) {
        return new HamButton.Builder()
                .normalImageRes(getImageResource())
                .normalText(text)
                .subNormalText(subText);
    }

    private BuilderManagerButton() {}
}
