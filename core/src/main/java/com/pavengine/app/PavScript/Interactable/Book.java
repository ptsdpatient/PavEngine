package com.pavengine.app.PavScript.Interactable;

public class Book extends Interactable {

    public String word;

    public Book(String word) {
        this.word = word;
    }

    @Override
    public String text() {
        return word;
    }
}
