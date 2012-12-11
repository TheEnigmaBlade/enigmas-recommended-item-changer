package net.enigmablade.lol.lolitem.ui.dnd;

import java.awt.datatransfer.*;
import net.enigmablade.lol.lolitem.ui.components.items.*;
import net.enigmablade.lol.lollib.data.*;


public class TransferableItem implements Transferable
{
	public static DataFlavor itemFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class="+Item.class.getName(), "Item");
	public static DataFlavor removableItemFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class="+DraggableItem.class.getName(), "Removable Item");
	
	private static DataFlavor[] supportedFlavors = {itemFlavor, removableItemFlavor};
	
	private Object data;
	
	public TransferableItem(Object d)
	{
		data = d;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return supportedFlavors;
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		for(DataFlavor f : supportedFlavors)
			if(flavor.equals(f))
				return true;
		return false;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if(isDataFlavorSupported(flavor)) 
			return data;
		else 
			throw new UnsupportedFlavorException(flavor);
	}
	
}
