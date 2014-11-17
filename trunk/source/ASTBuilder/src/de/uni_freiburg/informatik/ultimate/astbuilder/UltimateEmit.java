package de.uni_freiburg.informatik.ultimate.astbuilder;

import java.io.IOException;

public class UltimateEmit extends Emit {
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.uni_freiburg.informatik.ultimate.astbuilder.Emit#emitClassDeclaration
     * (de.uni_freiburg.informatik.ultimate.astbuilder.Node)
     */
    // @Override
    public void emitClassDeclaration(Node node) throws IOException {
        mWriter.println("public "
                + (node.isAbstract() ? "abstract " : "")
                + "class "
                + node.getName()
                + (node.getParent() != null ? " extends " + node.getParent().getName()
                        : " extends BoogieASTNode")
                + (node.getInterfaces() != null ? " implements "+node.getInterfaces() : "")
                        + " {");
        formatComment(mWriter, "    ", "The serial version UID.");
        mWriter.println("    private static final long serialVersionUID = 1L;");
    }

    public String getConstructorParam(Node node, boolean optional) {
        if (node == null)
            return "loc";
        return super.getConstructorParam(node, optional);
    }

    protected void fillConstructorParamComment(Node node, StringBuffer param,
            StringBuffer comment, boolean optional) {
        Node parent = node.getParent();
        if (parent == null) {
            param.append("ILocation loc");
            comment.append("\n@param loc the node's location");
        }
        super.fillConstructorParamComment(node, param, comment, optional);
    }

    public void emitConstructors(Node node) throws IOException {
        int numNotWriteableParams = 1;
        int numNotOptionalParams = 1;
        int numTotalParams = 1;

        /* Default constructor is only emitted if all fields are writeable */
        /* Optional constructor is only emitted if there are optional fields */
        Node ancestor = node;
        while (ancestor != null) {
            for (Parameter p : ancestor.parameters) {
                numTotalParams++;
                if (!p.isWriteable())
                    numNotWriteableParams++;
                if (!p.isOptional())
                    numNotOptionalParams++;
            }
            ancestor = ancestor.getParent();
        }
        if (numNotOptionalParams == 0 || numNotWriteableParams == 0) {
            formatComment(mWriter, "    ", "The default constructor.");
            mWriter.println("    public " + node.getName() + "() {");
            mWriter.println("    }");
            mWriter.println();
        }

        if (numNotOptionalParams > 0 && numNotOptionalParams < numTotalParams)
            emitConstructor(node, false);
        if (numTotalParams > 0)
            emitConstructor(node, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_freiburg.informatik.ultimate.astbuilder.Emit#emitPreamble(de.
     * uni_freiburg.informatik.ultimate.astbuilder.Node)
     */
    // @Override
    public void emitPreamble(Node node) throws IOException {
        super.emitPreamble(node);
        mWriter.println("import java.util.List;");
        mWriter.println("import de.uni_freiburg.informatik.ultimate.model.location.ILocation;");
        if (node.getParent() == null)
            mWriter.println("import de.uni_freiburg.informatik.ultimate.model.boogie.ast.wrapper.BoogieASTNode;");

    }

    public void emitNodeHook(Node node) throws IOException {
        mWriter.println();
        mWriter.println("    public List<Object> getChildren() {");
        mWriter.println("        List<Object> children = super.getChildren();");
        Parameter[] parameters = node.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            mWriter.println("        children.add(" + parameters[i].getName()
                    + ");");
        }
        mWriter.println("        return children;");
        mWriter.println("    }");
    }

}
