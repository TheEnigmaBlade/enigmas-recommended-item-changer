package net.enigmablade.lol.lolitem.ui.dnd;

import java.awt.datatransfer.*;

import net.enigmablade.lol.lolitem.ui.components.*;

public class TransferableGroup implements Transferable
{
	public static DataFlavor itemGroupFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class="+ItemGroupPanel.class.getName(), "Item Group Panel");
	
	private static DataFlavor[] supportedFlavors = {itemGroupFlavor};
	
	private Object data;
	
	public TransferableGroup(Object d)
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
