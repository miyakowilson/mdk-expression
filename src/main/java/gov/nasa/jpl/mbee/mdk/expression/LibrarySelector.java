package gov.nasa.jpl.mbee.mdk.expression;

import java.awt.Frame;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

public class LibrarySelector {
	
	static final String ASCII_LIBRARY = "Block asciiMathOperatorLibrary";
	static final String CUSTOM_FUNC = "Block customFunctions";
	
	Element a;
	Element c;
	
	public LibrarySelector(){
	}
	public Element getAsciiLibrary() { return a;}
	public Element getCustomFunction() { return c;}
	public boolean openDialog()
	{
		//Use ElementSelectionDlgFactory.create(...) methods to create element selection dialog
		Frame dialogParent = MDDialogParentProvider.getProvider().getDialogParent();
		ElementSelectionDlg dlgAM = ElementSelectionDlgFactory.create(dialogParent);
		ElementSelectionDlg dlgCF = ElementSelectionDlgFactory.create(dialogParent);

		//Use ElementSelectionDlgFactory.initMultiple(...) methods to initialize the dialog with multiple element selection mode
		ElementSelectionDlgFactory.initSingle(dlgAM, new SelectElementTypes(null, null), new SelectElementInfo(true, false, null, true), new Object[0]);
		ElementSelectionDlgFactory.initSingle(dlgCF, new SelectElementTypes(null, null), new SelectElementInfo(true, false, null, true), new Object[0]);
		//dlgAM.setName("Select AsciiMath Library");
		dlgAM.setTitle("Select AsciiMath Library");
		//dlgCF.setName("Select Customized Functions");
		dlgCF.setTitle("Select Customized Functions");
		
		//Display dialog for the user to select elements
		
		dlgAM.show();	
		if (dlgAM.isOkClicked()){
			// Get selected elements in multiple selection mode
			java.util.List<BaseElement> selected1 = dlgAM.getSelectedElements();
			if(selected1.size() != 1){
				//javax.swing.JOptionPane.showMessageDialog(null, "Wrong Selection!\nSelect AsciiMath Block!");
				return false; //dlgAM.show();
				}
			java.util.Iterator<BaseElement> selIt = selected1.iterator();
			while(selIt.hasNext()){
				a = (Element) selIt.next();
				//Element el = (Element) selIt.next();
				//MDKExpressionPlugin.asciiMathLibraryBlock = el;
			}
			
		}else {
			return false;
		}
		
		dlgCF.show();
		if (dlgCF.isOkClicked()){
			// Get selected elements in multiple selection mode
			java.util.List<BaseElement> selected1 = dlgCF.getSelectedElements();
			if(selected1.size() != 1){
				//javax.swing.JOptionPane.showMessageDialog(null, "Wrong Selection!\nSelect Customized Function Block!");
				return false; //dlgCF.show();
				}
			java.util.Iterator<BaseElement> selIt = selected1.iterator();
			while(selIt.hasNext()){
				//Element el = (Element) selIt.next();
				//MDKExpressionPlugin.customFuncBlock = el;
				c = (Element) selIt.next();
			}
			return true;
		}else {
			return false;
		}
	}
	
}
