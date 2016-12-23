package gov.nasa.jpl.mbee.mdk.expression;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.annotation.CheckForNull;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.StructuredClassifier;



public class AddContextMenuButton extends Plugin {

	public static Element asciiMathLibraryBlock = null, customFuncBlock = null;
	public static final String pluginName = "Constraint Editor";
	
	private final String NEW = "New...";
	
	public void init(){	
		
		// add action into containment tree context
		ActionsConfiguratorsManager.getInstance()
			.addDiagramContextConfigurator(DiagramType.UML_ANY_DIAGRAM, new DiagramContextAMConfigurator()
				{
					@Override
					public void configure(ActionsManager manager, DiagramPresentationElement myDiagramType, PresentationElement[] selected,
							PresentationElement requestor) { 
						
						//if constraint block is selected show a menu ("Constraint Editor")
						if(requestor != null && selected.length == 1 ) {
							if ( StereotypesHelper.hasStereotype(selected[0].getElement(), MDSysMLConstants.CONSTRAINTBLOCK)){	
								setupMenuForConstraintBlock(manager,(Element)selected[0].getElement());
							}
							else if (selected[0].getElement() instanceof Constraint) {
								setupMenuForConstraint(manager,(Constraint)selected[0].getElement());
							}
						}
					}	
					@Override
					public int getPriority()
					{
						return DiagramContextAMConfigurator.MEDIUM_PRIORITY;
					}
				});
		
		// add action into containment tree context
		ActionsConfiguratorsManager.getInstance()
				.addContainmentBrowserContextConfigurator(new BrowserContextAMConfigurator()
				{
					@Override
					public void configure(ActionsManager manager, Tree browser)
					{
						com.nomagic.magicdraw.ui.browser.Node selectednode = browser.getSelectedNode();
						 if (selectednode != null ){
							 Object o =  selectednode.getUserObject();
							 if ( StereotypesHelper.hasStereotype((Element)o, MDSysMLConstants.CONSTRAINTBLOCK)){	//from tree knows constraint block
									setupMenuForConstraintBlock(manager, (Element)o);
							 }
							 else if (o instanceof Constraint){
								 setupMenuForConstraint(manager, (Constraint)o);
							}
						 }
					}

					@Override
					public int getPriority()
					{
						return AMConfigurator.MEDIUM_PRIORITY;
					}
				});
	}
	public boolean close(){return true;}	
	public boolean isSupported(){return true;}
	
	//setting up menu and submenu when a constraint block is selected
	public void setupMenuForConstraintBlock(ActionsManager _manager, Element _constraintBlock)
	{
		ActionsCategory category = new ActionsCategory(null, pluginName);
		_manager.addCategory(category);									
		category.setNested(true);
		category.addAction(new ConstraintEditorDiagramContextMenuAction(null, NEW,_constraintBlock));
		SelectedConstraintBlock selectedConstraintBlock  = new SelectedConstraintBlock(_constraintBlock);
		//adding constraints owned by the constraint block to the submenus.
		selectedConstraintBlock.getConstraints().forEach(c -> { 	
			category.addAction(new ConstraintEditorDiagramContextMenuAction(null, c.getName(), _constraintBlock));
		});
		
	}
	//setting up menu when a constraint is selected
	public void setupMenuForConstraint(ActionsManager _manager, Constraint _c){
		
		ActionsCategory category = new ActionsCategory(null, "");
		category.addAction(new ConstraintEditorBrowserAction(_c));
		_manager.addCategory(category);
	}
	
	class ConstraintEditorBrowserAction extends DefaultBrowserAction
	{
		private Constraint selectedConstraint; //constraint selected from the selected constraint block.

		public ConstraintEditorBrowserAction(Constraint _c)
		{
			super("", pluginName, null, null);
			this.selectedConstraint = _c;
		}
		public void updateState()
	    {
			 setEnabled(true);
	    }
	    public void actionPerformed(ActionEvent e)
	    {
	    	//*********************************SET LIBRARY IF NECESSARY********************************
	    	if(AddContextMenuButton.asciiMathLibraryBlock == null || AddContextMenuButton.customFuncBlock == null){
				LibrarySelector ls = new LibrarySelector();
				if(!ls.openDialog()){return; }
			}
	    	MathEditorMain1Controller mathEditorController = new MathEditorMain1Controller(new SelectedConstraintBlock(this.selectedConstraint.getOwner()), this.selectedConstraint);
			mathEditorController.showView();
	    }
	    
	}
	class ConstraintEditorDiagramContextMenuAction extends DefaultDiagramAction { //MDAction
		
		private SelectedConstraintBlock selectedConstraintBlock; 
		
		public ConstraintEditorDiagramContextMenuAction( @CheckForNull String id, String name, Element _constraintBlock )
	    {
	        super(id, name, null, null);
	        selectedConstraintBlock = new SelectedConstraintBlock(_constraintBlock);
	    }
		public void updateState()
	    {
			 setEnabled(true);
	    }
	    @Override
		public void actionPerformed(ActionEvent e){
	    	
	    	//*********************************SET LIBRARY IF NECESSARY********************************
	    	if(AddContextMenuButton.asciiMathLibraryBlock == null || AddContextMenuButton.customFuncBlock == null){
				LibrarySelector ls = new LibrarySelector();
				if(!ls.openDialog()){return; }
			}

	    	//finding which sub-menu(Constraint) is selected
	    	String ACTION_COMMAND  = e.getActionCommand();
			Constraint selectedConstraint = null;
			if(ACTION_COMMAND.equals(NEW))
			{
				selectedConstraint = (Constraint)Application.getInstance().getProject().getElementsFactory().createConstraintInstance();
				selectedConstraint.setOwner(this.selectedConstraintBlock.getConstraintBlock());	//under which block it lives
				((StructuredClassifier)this.selectedConstraintBlock.getConstraintBlock()).get_constraintOfConstrainedElement().add(selectedConstraint);	//to which block it's referred
			}
			else
			{
				Constraint c;
				Iterator<Constraint> iter = this.selectedConstraintBlock.getConstraints().iterator();
				while( iter.hasNext()){
					c = iter.next();
					if ( c.getName().equals(ACTION_COMMAND)){
						selectedConstraint = (Constraint) c;
						break;
					}
				}
			} //end of else
			
			MathEditorMain1Controller mathEditorController = new MathEditorMain1Controller(this.selectedConstraintBlock, selectedConstraint);
			mathEditorController.showView();
	    }
	}
}