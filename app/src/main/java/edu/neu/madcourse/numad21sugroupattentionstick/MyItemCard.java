package edu.neu.madcourse.numad21sugroupattentionstick;

public class MyItemCard implements ItemClickListener{

    private final String itemName;
    private final String itemDesc;
    private int imageSource;


    //Constructor
    public MyItemCard(int imageSource, String itemName, String itemDesc, boolean isChecked) {

        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.imageSource = imageSource;
    }

    //Getters for the imageSource, itemName and itemDesc


    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemName() {
        return itemName;
    }

    public int getImageSource() {
        return imageSource;
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onCheckBoxClick(int position) {

    }
}
