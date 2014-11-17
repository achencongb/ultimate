package de.uni_freiburg.informatik.ultimate.model.acsl.ast;

import java.util.List;
/**
 * represents the result value, which is used in contracts.
 */
public class ACSLResultExpression extends Expression {
    /**
     * The default constructor.
     */
    public ACSLResultExpression() {
    }

    /**
     * The constructor taking initial values.
     * @param type the type of this expression.
     */
    public ACSLResultExpression(ACSLType type) {
        super(type);
    }

    /**
     * Returns a textual description of this object.
     */
    public String toString() {
        return "ResultExpression";
    }

    public List<Object> getChildren() {
        List<Object> children = super.getChildren();
        return children;
    }

	@Override
	public void accept(ACSLVisitor visitor) {
		
	}

}