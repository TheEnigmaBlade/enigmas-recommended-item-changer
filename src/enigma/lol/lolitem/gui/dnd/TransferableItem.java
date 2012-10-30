package enigma.lol.lolitem.gui.dnd;

import java.awt.datatransfer.*;

import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.gui.components.*;

public class TransferableItem implements Transferable
{
	public static DataFlavor itemFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class="+Item.class.getName(), "Item");
	public static DataFlavor panelFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class="+ItemSetPanel.class.getName(), "Item panel");
	
	private static DataFlavor[] supportedFlavors = {itemFlavor, panelFlavor};
	
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
