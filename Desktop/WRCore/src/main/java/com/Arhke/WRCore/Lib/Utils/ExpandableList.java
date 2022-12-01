package com.Arhke.WRCore.Lib.Utils;

import java.util.ArrayList;


public class ExpandableList<E> extends ArrayList<E>{
    @Override
    public E set(int index, E element){
        if(index >= 0 && index <  size()){
            return super.set(index, element);
        }
        int insertNulls = index - size();
        for(int i = 0; i < insertNulls; i++){
            super.add(null);
        }
        super.add(element);
        return element;
    }
}