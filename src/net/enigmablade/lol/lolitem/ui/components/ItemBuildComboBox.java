package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.enigmablade.lol.lollib.ui.pretty.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class ItemBuildComboBox extends PrettyComboBox<String>
{
	private DefaultComboBoxModel<String> model;
	
	private List<ItemBuildTextListener> textListeners;
	private boolean textUpdateLock = false;
	
	public ItemBuildComboBox()
	{
		super(null, NORTH);
		
		setEditable(true);
		
		model = new DefaultComboBoxModel<String>();
		setModel(model);
		
		//Components
		JPanel widthComponent = new JPanel();
		widthComponent.setPreferredSize(new Dimension(230, 0));
		setComponent(widthComponent);
		
		//Text editing
		JTextComponent field = (JTextComponent)getEditor().getEditorComponent();
		field.getDocument().addDocumentListener(new ItemBuildDocumentListener());
		
		textListeners = new LinkedList<ItemBuildTextListener>();
	}
	
	public void setBuilds(List<String> buildNames)
	{
		textUpdateLock = true;
		
		model.removeAllElements();
		//If no builds exist, set a message and disable
		if(buildNames == null)
		{
			model.addElement("No builds exist");
			setEnabled(false);
		}
		//Otherwise all the item sets to the model
		else
		{
			for(String name : buildNames)
				model.addElement(name);
			setEnabled(true);
		}
		
		textUpdateLock = false;
	}
	
	public void addBuild(String name)
	{
		textUpdateLock = true;
		addItem(name);
		textUpdateLock = false;
		//model.addElement(name);
	}
	
	public void removeBuild(int index)
	{
		textUpdateLock = true;
		removeItemAt(index);
		textUpdateLock = false;
		//model.removeElementAt(index);
	}
	
	private class ItemBuildDocumentListener implements DocumentListener
	{
		@Override
		public void changedUpdate(DocumentEvent evt)
		{
			//Not used
		}

		@Override
		public void insertUpdate(DocumentEvent evt)
		{
			textChanged(evt.getDocument(), evt.getOffset()+evt.getLength());
		}

		@Override
		public void removeUpdate(DocumentEvent evt)
		{
			textChanged(evt.getDocument(), evt.getOffset());
		}
		
		public void textChanged(Document doc, final int caretPos)
		{
			//System.out.println("TEXT CHANGED EVENT");
			final int index = getSelectedIndex();
			//System.out.println("\tIndex: "+index);
			if(index != -1 && !textUpdateLock)
			{
				try
				{
					final String text = doc.getText(0, doc.getLength());
					//final int caretPos = Math.max(((JTextComponent)getEditor().getEditorComponent()).getCaretPosition()+caretChange, 0);
					//writeToLog("UI # Item build text at index "+index+" changed: \""+text+"\"");
					//System.out.println("\tText: \""+text+"\"");
					
					SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run()
						{
							disableItemListeners();
							textUpdateLock = true;
							
							//System.out.println("Before removal:");
							//for(int n = 0; n < model.getSize(); n++)
							//	System.out.println("\t"+model.getElementAt(n));
							model.removeElementAt(index);
							//System.out.println("After removal, before insertion:");
							//for(int n = 0; n < model.getSize(); n++)
							//	System.out.println("\t"+model.getElementAt(n));
							model.insertElementAt(text, index);
							//System.out.println("After insertion:");
							//for(int n = 0; n < model.getSize(); n++)
							//	System.out.println("\t"+model.getElementAt(n));
							
							//getEditor().setItem(text);
							
							setSelectedIndex(index);
							((JTextComponent)getEditor().getEditorComponent()).setCaretPosition(caretPos);
							
							enableItemListeners();
							textUpdateLock = false;
							
							ItemBuildTextEvent evt = new ItemBuildTextEvent(ItemBuildComboBox.this, index, text);
							for(ItemBuildTextListener list : textListeners)
								list.textChanged(evt);
						}
					});
				}
				catch(BadLocationException e)
				{
					writeStackTrace(e);
				}
			}
		}
	}
	
	public interface ItemBuildTextListener extends EventListener
	{
		public void textChanged(ItemBuildTextEvent evt);
	}
	
	public class ItemBuildTextEvent extends AWTEvent
	{
		private int index;
		private String text;
		
		public ItemBuildTextEvent(Object source, int index, String text)
		{
			super(source, 1001);
			this.index = index;
			this.text = text;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public String getText()
		{
			return text;
		}
	}
	
	public void addBuildTextListener(ItemBuildTextListener list)
	{
		textListeners.add(list);
	}
	
	//Locking
	
	private ItemListener[] savedItemListeners;
	
	public void disableItemListeners()
	{
		//System.out.println("Disabling item listeners");
		savedItemListeners = getItemListeners();
		for(ItemListener list : savedItemListeners)
			removeItemListener(list);
	}
	
	public void enableItemListeners()
	{
		//System.out.println("Enabling item listeners");
		if(savedItemListeners != null)
			for(ItemListener list : savedItemListeners)
				addItemListener(list);
		savedItemListeners = null;
	}
}
