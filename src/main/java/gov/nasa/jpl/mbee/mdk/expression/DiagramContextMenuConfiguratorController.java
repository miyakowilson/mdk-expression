package gov.nasa.jpl.mbee.mdk.expression;

import java.awt.event.ActionEvent;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.StructuredClassifier;


public class DiagramContextMenuConfiguratorController {
	
	private final String NEW = "New...";
	
	private SelectedConstraintBlock selectedConstraintBlock; //constraint selected by a user.
	private Constraint selectedConstraint; //constraint selected from the selected constraint block.
	
	public DiagramContextMenuConfiguratorController(Element _selectedContraintBlock)
	{
		this.selectedConstraintBlock = new SelectedConstraintBlock(_selectedContraintBlock);
	}
	//creating submenus
	public void setActions(ActionsCategory _category){
		//adding New
		SimpleContextAction action = new SimpleContextAction(this, null, NEW);
		_category.addAction(action);
		//adding constraints owned by the constraint block to the submenus.
		selectedConstraintBlock.getConstraints().forEach(c -> { 	
			_category.addAction(new SimpleContextAction(this, null, c.getName()));
		});
	}
	public void setAction(ActionsCategory _category, Constraint _selectedConstraint){
		_category.addAction(new SimpleContextAction(this, null, _selectedConstraint.getName()));
		selectedConstraint = _selectedConstraint;
	}
	
	public void handleAction(ActionEvent e) {
	
		if ( selectedConstraint == null ){
			String ACTION_COMMAND  = e.getActionCommand();
			
			if(ACTION_COMMAND.equals(NEW))
			{
				selectedConstraint = (Constraint)Application.getInstance().getProject().getElementsFactory().createConstraintInstance();
				selectedConstraint.setOwner(this.selectedConstraintBlock.getConstraintBlock());	//under which block it lives
				((StructuredClassifier)this.selectedConstraintBlock.getConstraintBlock()).get_constraintOfConstrainedElement().add(selectedConstraint);	//to which block it's referred
			}
			else
			{
				this.selectedConstraintBlock.getConstraints().forEach(c -> { 	
					if (c.getName().equals(ACTION_COMMAND))
						selectedConstraint = (Constraint) c;
				});
				
			} //end of else
		}
		MathEditorMain1Controller mathEditorController = new MathEditorMain1Controller(this.selectedConstraintBlock, selectedConstraint);
		mathEditorController.showView();

	}//end of handleAction
}