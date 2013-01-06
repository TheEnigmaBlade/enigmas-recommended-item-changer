package net.enigmablade.lol.lolitem.ui.models;

import java.util.*;
import javax.swing.*;

public class MovableListModel<E> extends DefaultListModel<E>
{
	//Physical up, indices decrease
	
	public int[] moveUp(int[] indecies)
	{
		int[] newIndecies = new int[indecies.length];
		
		Arrays.sort(indecies);
		for(int n = 0; n < indecies.length; n++)
			newIndecies[n] = moveUp(indecies[n]);
		
		return newIndecies;
	}
	
	public int moveUp(int index)
	{
		if(index < 0 || index >= getSize())
			throw new IllegalArgumentException("Bad index");
		
		int index2 = index-1;
		if(index2 >= 0)
			swap(index, index2);
		
		return index2;
	}
	
	//Physical down, indices increase
	public int[] moveDown(int[] indecies)
	{
		int[] newIndecies = new int[indecies.length];
		
		Arrays.sort(indecies);
		for(int n = indecies.length-1; n >= 0 ; n--)
			newIndecies[n] = moveDown(indecies[n]);
		
		return newIndecies;
	}
	
	public int moveDown(int index)
	{
		if(index < 0 || index >= getSize())
			throw new IllegalArgumentException("Bad index");
		
		int index2 = index+1;
		if(index2 < getSize())
			swap(index, index2);
		
		return index2;
	}
	
	//Helpers
	
	private void swap(int i1, int i2)
	{
		if(i1 < 0 || i1 >= getSize() || i2 < 0 || i2 >= getSize() || i1 == i2)
			throw new IllegalArgumentException("Bad indecies");
		
		E e1 = getElementAt(i1);
		setElementAt(getElementAt(i2), i1);
		setElementAt(e1, i2);
	}
}
