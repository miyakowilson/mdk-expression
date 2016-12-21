package gov.nasa.jpl.mbee.mdk.expression;

import gov.nasa.jpl.mbee.mdk.expression.antlr.generated.ArithmeticBinaryParser;

import org.antlr.v4.runtime.tree.ParseTree;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ElementValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Expression;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralReal;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;

/// originally Tree2UMLExpressionInfix
public class Tree2UMLExpression_Prefix extends Tree2UMLExpression {
	
	public Tree2UMLExpression_Prefix(MathEditorMain1Controller _controller, ParseTree root) {
		super(_controller, root);
	}
	
	protected ValueSpecification traverse0(ParseTree n){
		
		//distinguish between the different cases defined in the grammar and set stop criteria
		if(n instanceof ArithmeticBinaryParser.BinaryExp1Context || n instanceof ArithmeticBinaryParser.BinaryExp2Context
				|| n instanceof ArithmeticBinaryParser.BinaryExp3Context || n instanceof ArithmeticBinaryParser.EqExpContext){	//=> BINARY EXPRESSION
			
			
			Expression exp = createExpression(n.getChild(1).getText(), AddContextMenuButton.asciiMathLibraryBlock);
			if (exp != null){
				//*********************************TRAVERSE*************************************
				exp.getOperand().add(traverse0(n.getChild(0)));	//left child
				exp.getOperand().add(traverse0(n.getChild(2)));	//right child
			}
			return exp;
			
		}else if(n instanceof ArithmeticBinaryParser.UnaryExpContext){	//=> UNARY EXPRESSION
			
			Expression exp = createExpression(n.getChild(0).getChild(0).getText(), AddContextMenuButton.asciiMathLibraryBlock);
			if (exp != null){
				//*********************************TRAVERSE*************************************
				exp.getOperand().add(traverse0(n.getChild(2)));
			}
			return exp;
		
		}else if(n instanceof ArithmeticBinaryParser.FunExpContext){	//=> CUSTOMIZED FUNCTION EXPRESSION
			
			Expression exp = createExpression(n.getChild(0).getText(), AddContextMenuButton.customFuncBlock);
			if (exp != null){
				//*********************************TRAVERSE*************************************
				int max = n.getChildCount()-2;
				for(int i=2; i<=max; i=i+2){
					exp.getOperand().add(traverse0(n.getChild(i)));
				}
			}
			return exp;
			
		}else if(n instanceof ArithmeticBinaryParser.ParExpContext){	//=> PARENTHESES EXPRESSION
			
			//**************************TRAVERSE AND RETURN*********************************
			return traverse0(n.getChild(1));
			
		}else if(n instanceof ArithmeticBinaryParser.NegExpContext){	//=> NEGATIVE EXPRESSION
			
			Expression exp = createExpression(n.getChild(0).getText(), AddContextMenuButton.asciiMathLibraryBlock);
			if (exp != null){
				//*********************************TRAVERSE*************************************
				exp.getOperand().add(traverse0(n.getChild(1)));
			}
			return exp;
			
		}
		else if(n instanceof ArithmeticBinaryParser.NegLitExpContext){	//=> NEGATIVE LITERAL EXPRESSION
			
			Expression exp = createExpression(n.getChild(0).getText(), AddContextMenuButton.asciiMathLibraryBlock);
			
			if ( exp != null ){
				//LITERAL REAL
				try {
					double lRealDouble = Double.parseDouble(n.getChild(1).getChild(0).getText());
					LiteralReal lReal = Application.getInstance().getProject().getElementsFactory().createLiteralRealInstance();
					lReal.setValue(lRealDouble);
					//add LiteralReal to expression
					exp.getOperand().add(lReal);
				}
				catch(NumberFormatException e){	//ELEMENT VALUE
					//************************************DO****************************************
					//find the correct operand
					ElementValue elemVal1 = createElementValueFromOperation(n.getChild(1).getChild(0).getText(), AddContextMenuButton.asciiMathLibraryBlock);
					if ( elemVal1 != null)
					{
						//add operand to expression
						exp.getOperand().add(elemVal1);
					}
				}
			}
			return exp;
		}
		else if(n instanceof ArithmeticBinaryParser.LitExpContext){	//=> LITERAL EXPRESSION
			
			try{	//LITERAL REAL
				
				//************************************DO****************************************
				double lRealDouble = Double.parseDouble(n.getChild(0).getText());
				LiteralReal lReal = Application.getInstance().getProject().getElementsFactory().createLiteralRealInstance();
				lReal.setValue(lRealDouble);
				
				//**********************************RETURN**************************************
				return lReal;
				
			}
			catch(NumberFormatException e){	//ELEMENT VALUE
				
				System.out.println("Prefix");
				//find the correct operands -- error message show up if the constraint parameter not found in the current constraint block.
				ElementValue elemVal = createElementValueFromOperands(n.getChild(0).getText(), controller.getConstraintBlock());
				if (elemVal == null){ //not able to find a constraint parameter from the constaint block
					if (askToCreateAConstraintParameter(n.getChild(0).getText())){
						
						//!!!!!!!!!!!!!!! even created not show in the editor????
						try {
							System.out.println("Warn: Couldn't find " + n.getChild(0).getText() + " so try to create the constraint parameter...");
							elemVal = createConstaintParameter(n.getChild(0).getText());
							if ( elemVal != null) {//reset error
								error = false;
								this.controller.addOperand((Property)elemVal.getElement());//add newly created constraintParameter(Property) to the view's listoperandsmodel
								return elemVal;
							}
							else {
								showNotAbleToCreateError( n.getChild(0).getText() ,  controller.getConstraintBlock());
								//error = true; --- it is already set as true when not able to find the constraint parameter.
								//this is how implemented in the original version if the constraint parameter is not found
								return Application.getInstance().getProject().getElementsFactory().createExpressionInstance();
							}
							
						}
						catch(Exception e1){
							e1.printStackTrace();
							System.out.println("Not able to create a constraint parameter - " + e1.getMessage());
							showNotAbleToCreateError( n.getChild(0).getText() ,  controller.getConstraintBlock());
							//this is how implemented in the original version if the constraint parameter is not found
							return Application.getInstance().getProject().getElementsFactory().createExpressionInstance();
						}
					}
					else {
						error = true;
						//this is how implemented in the original version.
						return Application.getInstance().getProject().getElementsFactory().createExpressionInstance();
					}
				}
				else { //found
					return elemVal;
				}
			}
			
		}
		else{ //=> couldn't match any Expression => error
			
			javax.swing.JOptionPane.showMessageDialog(null, "Error: Couldn't match any Expression Context!");
			error = true;
			
			//javax.swing.JOptionPane.showMessageDialog(null, "class: " + n.getClass().toString());
		
		}
		
		
		return Application.getInstance().getProject().getElementsFactory().createExpressionInstance();
	}
	
}