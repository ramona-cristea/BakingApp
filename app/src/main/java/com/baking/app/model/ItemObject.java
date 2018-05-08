package com.baking.app.model;

public class ItemObject<Data> {

    private Data contents;
    public ItemObject(Data contents) {
        this.contents = contents;
    }
    public Data getContents() {
        return contents;
    }
}
