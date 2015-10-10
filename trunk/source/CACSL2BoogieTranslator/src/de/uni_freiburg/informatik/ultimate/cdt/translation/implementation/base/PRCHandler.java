/*
 * Copyright (C) 2014-2015 Alexander Nutz (nutz@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE CACSL2BoogieTranslator plug-in.
 * 
 * The ULTIMATE CACSL2BoogieTranslator plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE CACSL2BoogieTranslator plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE CACSL2BoogieTranslator plug-in. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE CACSL2BoogieTranslator plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE CACSL2BoogieTranslator plug-in grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;

import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.LocationFactory;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.SymbolTable;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.ArrayHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.FunctionHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.InitializationHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.MemoryHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.PRFunctionHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.StructHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.PRSymbolTableValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.SymbolTableValue.StorageClass;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CArray;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CFunction;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPointer;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPrimitive;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPrimitive.PRIMITIVE;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CType;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.exception.UnsupportedSyntaxException;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.CDeclaration;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.DeclarationResult;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.ExpressionListRecResult;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.ExpressionResult;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.HeapLValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.LRValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.LocalLValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.RValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.Result;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.SkipResult;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.TypesResult;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.util.SFO;
import de.uni_freiburg.informatik.ultimate.cdt.translation.interfaces.Dispatcher;
import de.uni_freiburg.informatik.ultimate.cdt.translation.interfaces.handler.ITypeHandler;
import de.uni_freiburg.informatik.ultimate.model.acsl.ACSLNode;
import de.uni_freiburg.informatik.ultimate.model.annotation.Overapprox;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ASTType;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Attribute;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Body;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Declaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.NamedType;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.TypeDeclaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VarList;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableDeclaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.model.location.ILocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.cacsl2boogietranslator.CACSL2BoogieBacktranslator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.cacsl2boogietranslator.preferences.CACSLPreferenceInitializer.UNSIGNED_TREATMENT;

public class PRCHandler extends CHandler {
	
    private LinkedHashSet<IASTNode> variablesOnHeap;

    public HashSet<IASTNode> getVarsForHeap() {
    	return variablesOnHeap;
    }	

    ////////////
	
	
	public PRCHandler(Dispatcher main, CACSL2BoogieBacktranslator backtranslator, boolean errorLabelWarning,
			Logger logger, ITypeHandler typeHandler, boolean bitvectorTranslation) {
		super(main, backtranslator, errorLabelWarning, logger, typeHandler, bitvectorTranslation);
		
		variablesOnHeap = new LinkedHashSet<>();

		this.mTypeHandler = typeHandler;

		this.mArrayHandler = new ArrayHandler();
		this.mFunctionHandler = new PRFunctionHandler(m_ExpressionTranslation);
		this.mMemoryHandler = new MemoryHandler(mFunctionHandler, false, mTypeSizeComputer, m_ExpressionTranslation);
		this.mStructHandler = new StructHandler(mMemoryHandler, mTypeSizeComputer, m_ExpressionTranslation);
		this.mSymbolTable = new SymbolTable(main);
		this.mContract = new ArrayList<ACSLNode>();
		this.mCurrentDeclaredTypes = new ArrayDeque<TypesResult>();
	}
	
	@Override
	public Result visit(Dispatcher main, IASTTranslationUnit node) {

		ILocation loc = LocationFactory.createCLocation(node);

		for (IASTNode child : node.getChildren()) {
			main.dispatch(child);
		}
		ArrayList<Declaration> decl = new ArrayList<>();

		return new Result(new Unit(loc, decl.toArray(new Declaration[0])));
	}

	@Override
	public Result visit(Dispatcher main, IASTFunctionDefinition node) {
		LinkedHashSet<IASTDeclaration> reachableDecs = ((PRDispatcher) main).getReachableDeclarationsOrDeclarators();
		if (reachableDecs != null) {
			if (!reachableDecs.contains(node))
				return new SkipResult();
		}

		TypesResult resType = (TypesResult) main.dispatch(node.getDeclSpecifier());

		mCurrentDeclaredTypes.push(resType);
		DeclarationResult declResult = (DeclarationResult) main.dispatch(node.getDeclarator());
		mCurrentDeclaredTypes.pop();
		return mFunctionHandler.handleFunctionDefinition(main, mMemoryHandler, node, declResult.getDeclarations().get(0),
				mContract);
	}

	@Override
	public Result visit(Dispatcher main, IASTCompoundStatement node) {
		ILocation loc = LocationFactory.createCLocation(node);
		ArrayList<Declaration> decl = new ArrayList<Declaration>();
		ArrayList<Statement> stmt = new ArrayList<Statement>();
		IASTNode parent = node.getParent();

		if (isNewScopeRequired(parent)) {
			this.beginScope();
		}

		for (IASTNode child : node.getChildren()) {
			main.dispatch(child);
		}
		if (isNewScopeRequired(parent)) {
			this.endScope();
		}
		return new Result(new Body(loc, decl.toArray(new VariableDeclaration[0]), stmt.toArray(new Statement[0])));
	}

	private static boolean isNewScopeRequired(final IASTNode env) {
		return !(env instanceof IASTForStatement) && !(env instanceof IASTFunctionDefinition);
	}

	@Override
	public Result visit(Dispatcher main, IASTSimpleDeclaration node) {
		LinkedHashSet<IASTDeclaration> reachableDecs = ((PRDispatcher) main).getReachableDeclarationsOrDeclarators();
		if (reachableDecs != null) {
			if (node.getParent() instanceof IASTTranslationUnit) {
				if (!reachableDecs.contains(node)) {
					boolean skip = true;
					for (IASTDeclarator d : node.getDeclarators())
						if (reachableDecs.contains(d))
							skip = false;
					if (reachableDecs.contains(node.getDeclSpecifier()))
						skip = false;
					if (skip)
						return new SkipResult();
				}
			}
		}

		ILocation loc = LocationFactory.createCLocation(node);

		if (node.getDeclSpecifier() == null) {
			String msg = "This statement can be removed!";
			main.warn(loc, msg);
			return new SkipResult();
		}

		// enum case
		if (node.getDeclSpecifier() instanceof IASTEnumerationSpecifier) {
			handleEnumDeclaration(main, node);
		}

		Result r = main.dispatch(node.getDeclSpecifier());
		assert r instanceof SkipResult || r instanceof TypesResult;
		if (r instanceof SkipResult)
			return r;
		if (r instanceof TypesResult) {
			TypesResult resType = (TypesResult) r;
			Result result = new SkipResult(); // Skip will be overwritten in
												// case of a global or a local
												// initialized variable

			StorageClass storageClass = scConstant2StorageClass(node.getDeclSpecifier().getStorageClass());

			mCurrentDeclaredTypes.push(resType);
			/**
			 * Christian: C allows several declarations of "similar" types in
			 * one go. For instance: <code>int a, b[2];</code> Here
			 * <code>a</code> has type <code>int</code> and <code>b</code> has
			 * type <code>int[]</code>. To solve this, the declaration items are
			 * visited one after another.
			 */
			for (IASTDeclarator d : node.getDeclarators()) {
//				if (d instanceof IASTFieldDeclarator)
//					throw new UnsupportedSyntaxException(loc, "bitfields are not supported at the moment");
				
				
				DeclarationResult declResult = (DeclarationResult) main.dispatch(d);

				// the ResultDeclaration from one Declarator always only
				// contains one CDeclaration, right?
				// or at most one??
				assert declResult.getDeclarations().size() == 1;
				CDeclaration cDec = declResult.getDeclarations().get(0);

				// update symbol table
			
				// functions keep their cId, and their declaration is not stored
				// in the symbolTable but in
				// FunctionHandler.procedures.
				if (cDec.getType() instanceof CFunction && storageClass != StorageClass.TYPEDEF) {
					// update functionHandler.procedures instead of symbol table
					mFunctionHandler.handleFunctionDeclarator(main, LocationFactory.createCLocation(d), null, cDec);
					continue;
				}

				String bId = main.nameHandler.getUniqueIdentifier(node, cDec.getName(),
						mSymbolTable.getCompoundCounter(), false);

				Declaration boogieDec = null;
				boolean globalInBoogie = false;

				// this .put() is only to have a minimal symbolTableEntry
				// (containing boogieID) for
				// translation of the initializer
				mSymbolTable.put(cDec.getName(),
						new PRSymbolTableValue(bId, boogieDec, cDec, globalInBoogie, storageClass, d));
				cDec.translateInitializer(main);
				
				
				//difference from CHandler begin (not the only one)
				if (cDec.getType() instanceof CPointer
						&& cDec.hasInitializer()
						&& cDec.getInitializer().lrVal.getCType() instanceof CArray) {
					String id = ((IdentifierExpression) cDec.getInitializer().lrVal.getValue()).getIdentifier();
					variablesOnHeap.add(((PRSymbolTableValue) mSymbolTable.get(mSymbolTable.getCID4BoogieID(id, loc), loc)).decl);
				}
				//difference from CHandler end
				

				ASTType translatedType = null;

				translatedType = main.typeHandler.ctype2asttype(loc, cDec.getType());

				if (storageClass == StorageClass.TYPEDEF) {
					boogieDec = new TypeDeclaration(loc, new Attribute[0], false, bId, new String[0], translatedType);
					main.typeHandler.addDefinedType(bId, new TypesResult(new NamedType(loc, cDec.getName(), null),
							false, false, cDec.getType()));
					// TODO: add a sizeof-constant for the type??
					globalInBoogie = true;
				} else if (storageClass == StorageClass.STATIC && !mFunctionHandler.noCurrentProcedure()) {
					// we have a local static variable -> special treatment
					// global static variables are treated like normal global variables..
					boogieDec = new VariableDeclaration(loc, new Attribute[0],
							new VarList[] { new VarList(loc, new String[] {bId}, 
									translatedType) });
					globalInBoogie = true;
				} else {
					/**
					 * For Variable length arrays we have a "non-real" initializer which just initializes the aux var for the array's
					 * size. We do not want to treat this like other initializers (call initVar and so).
					 */
					boolean hasRealInitializer = cDec.hasInitializer() && 
							!(cDec.getType() instanceof CArray && !(cDec.getInitializer() instanceof ExpressionListRecResult));

					if (!hasRealInitializer && !mFunctionHandler.noCurrentProcedure()
							&& !mTypeHandler.isStructDeclaration()) {


						VariableLHS lhs = new VariableLHS(loc, bId);

					} else if (hasRealInitializer && !mFunctionHandler.noCurrentProcedure() && !mTypeHandler.isStructDeclaration()) { 
						//in case of a local variable declaration with an initializer, the statements and delcs
						// necessary for the initialization are the result
					

					} else {
						assert result instanceof SkipResult || result instanceof DeclarationResult;
						if (result instanceof SkipResult)
							result = new DeclarationResult();
						((DeclarationResult) result).addDeclaration(cDec);	
					
					}
					boogieDec = new VariableDeclaration(loc, new Attribute[0], new VarList[] { new VarList(loc,
							new String[] { bId }, translatedType) });
					globalInBoogie |= mFunctionHandler.noCurrentProcedure();
				}

				mSymbolTable.put(cDec.getName(), new PRSymbolTableValue(bId,
						boogieDec, cDec, globalInBoogie,
						storageClass, d)); 
			}
			mCurrentDeclaredTypes.pop();
			
			if (result instanceof ExpressionResult)
				((ExpressionResult) result).stmt.addAll(
						createHavocsForAuxVars(((ExpressionResult) result).auxVars));
			return result;
		}
		String msg = "Unknown result type: " + r.getClass();
		throw new UnsupportedSyntaxException(loc, msg);
	}
	
	
//	private Result handleEnumDeclaration(Dispatcher main, IASTSimpleDeclaration node) {
//		Result r = main.dispatch(node.getDeclSpecifier());
//		assert r instanceof ResultTypes;
//		ResultTypes rt = (ResultTypes) r;
//		assert rt.cType instanceof CEnum;
//		
//		ResultDeclaration result = new ResultDeclaration();
//
//		return result;
//	}
	
	@Override
	public Result visit(Dispatcher main, IASTBinaryExpression node) {
		ILocation loc = LocationFactory.createCLocation(node);

		switch (node.getOperator()) {
		case IASTBinaryExpression.op_assign: 
			ExpressionResult l = (ExpressionResult) main.dispatch(node.getOperand1());
			ExpressionResult r = (ExpressionResult) main.dispatch(node.getOperand2());
			return makeAssignment(main, loc, l.lrVal, r.lrVal);
			default:
				return super.visit(main, node);
		}
	}

	
	@Override
	public Result visit(Dispatcher main, IASTUnaryExpression node) {
		switch (node.getOperator()) {
		case IASTUnaryExpression.op_amper:
			ExpressionResult o = (ExpressionResult) main.dispatch(node.getOperand());
			//can't really addressof at this point, returning the value instead but wiht pointer type
//			ResultExpression rop = o.switchToRValueIfNecessary(main, mMemoryHandler, mStructHandler, loc);
			RValue ad = null;
			if (o.lrVal instanceof HeapLValue)
				ad = new RValue(((HeapLValue) o.lrVal).getAddress(), new CPointer(o.lrVal.getCType()));
			else 
				ad = new RValue(o.lrVal.getValue(), new CPointer(o.lrVal.getCType()));

			return new ExpressionResult(ad);
			default:
				return super.visit(main, node);
		}
	}

	private Result handleLoops(Dispatcher main, IASTStatement node, Result bodyResult, ExpressionResult condResult,
			String loopLabel) {
		int scopeDepth = mSymbolTable.getActiveScopeNum();
		assert node instanceof IASTWhileStatement || node instanceof IASTDoStatement
				|| node instanceof IASTForStatement;

		ILocation loc = LocationFactory.createCLocation(node);

		ArrayList<Statement> stmt = new ArrayList<Statement>();
		ArrayList<Declaration> decl = new ArrayList<Declaration>();
		List<Overapprox> overappr = new ArrayList<Overapprox>();
		Map<VariableDeclaration, ILocation> emptyAuxVars = new LinkedHashMap<VariableDeclaration, ILocation>(0);

		if (node instanceof IASTForStatement) {
			IASTForStatement forStmt = (IASTForStatement) node;
			IASTStatement cInitStmt = forStmt.getInitializerStatement();
			if (cInitStmt != null) {
				this.beginScope();
				main.dispatch(cInitStmt);
			}
			IASTExpression cItExpr = forStmt.getIterationExpression();
			if (cItExpr != null)
				main.dispatch(cItExpr);
			IASTExpression cCondExpr = forStmt.getConditionExpression();
			if (cCondExpr != null)
				condResult = (ExpressionResult) main.dispatch(cCondExpr);

			bodyResult = main.dispatch(forStmt.getBody());
		}


		if (node instanceof IASTForStatement) {
			if (((IASTForStatement) node).getInitializerStatement() != null) {
				this.endScope();
			}
		}

		assert (mSymbolTable.getActiveScopeNum() == scopeDepth);
		return new ExpressionResult(stmt, null, decl, emptyAuxVars, overappr);
	}

	@Override
	public Result visit(Dispatcher main, IASTEqualsInitializer node) {
		return main.dispatch(node.getInitializerClause());
	}

	@Override
	public Result visit(Dispatcher main, IASTDeclarationStatement node) {
		return main.dispatch(node.getDeclaration());
	}

	@Override
	public Result visit(Dispatcher main, IASTReturnStatement node) {
		return mFunctionHandler.handleReturnStatement(main, mMemoryHandler, mStructHandler, node);
	}

	@Override
	public Result visit(Dispatcher main, IASTExpressionStatement node) {
		Result r = main.dispatch(node.getExpression());
		return null;
	}

	@Override
	public Result visit(Dispatcher main, IASTIfStatement node) {
		main.dispatch(node.getConditionExpression());
		main.dispatch(node.getThenClause());
		if (node.getElseClause() != null)
			main.dispatch(node.getElseClause());
		return new ExpressionResult(new RValue(new IdentifierExpression(LocationFactory.createIgnoreCLocation(), SFO.NULL), 
				new CPointer(new CPrimitive(PRIMITIVE.VOID))));
	}

	@Override
	public Result visit(Dispatcher main, IASTWhileStatement node) {
		ExpressionResult condResult = (ExpressionResult) main.dispatch(node.getCondition());
		String loopLabel = main.nameHandler.getGloballyUniqueIdentifier(SFO.LOOPLABEL);
//		mInnerMostLoopLabel.push(loopLabel);
		Result bodyResult = main.dispatch(node.getBody());
//		mInnerMostLoopLabel.pop();
		return handleLoops(main, node, bodyResult, condResult, loopLabel);
	}
	@Override
	public Result visit(Dispatcher main, IASTForStatement node) {
		String loopLabel = main.nameHandler.getGloballyUniqueIdentifier(SFO.LOOPLABEL);
		return handleLoops(main, node, null, null, loopLabel);
	}

	@Override
	public Result visit(Dispatcher main, IASTDoStatement node) {
		ExpressionResult condResult = (ExpressionResult) main.dispatch(node.getCondition());
		String loopLabel = main.nameHandler.getGloballyUniqueIdentifier(SFO.LOOPLABEL);
//		mInnerMostLoopLabel.push(loopLabel);
		Result bodyResult = main.dispatch(node.getBody());
//		mInnerMostLoopLabel.pop();
		return handleLoops(main, node, bodyResult, condResult, loopLabel);
	}

	@Override
	public Result visit(Dispatcher main, IASTContinueStatement cs) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Result visit(Dispatcher main, IASTSwitchStatement node) {
		main.dispatch(node.getControllerExpression());
		this.beginScope();
		for (IASTNode child : node.getBody().getChildren()) {
			Result r = main.dispatch(child);
		}
		this.endScope();
		return null;
	}

	@Override
	public Result visit(Dispatcher main, IASTCaseStatement node) {
		ExpressionResult c = (ExpressionResult) main.dispatch(node.getExpression());
		return c.switchToRValueIfNecessary(main, mMemoryHandler, mStructHandler, LocationFactory.createCLocation(node));
	}

	@Override
	public Result visit(Dispatcher main, IASTDefaultStatement node) {
		ArrayList<Statement> stmt = new ArrayList<Statement>();
		ArrayList<Declaration> decl = new ArrayList<Declaration>();
		Map<VariableDeclaration, ILocation> emptyAuxVars = new LinkedHashMap<VariableDeclaration, ILocation>(0);
		List<Overapprox> overappr = new ArrayList<Overapprox>();
		return new ExpressionResult(stmt, new RValue(new BooleanLiteral(LocationFactory.createCLocation(node), true), new CPrimitive(
				PRIMITIVE.INT)), decl, emptyAuxVars, overappr);
	}

	@Override
	public Result visit(Dispatcher main, IASTLabelStatement node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result visit(Dispatcher main, IASTGotoStatement node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result visit(Dispatcher main, IASTCastExpression node) {
		ExpressionResult expr = (ExpressionResult) main.dispatch(node.getOperand()); 
		ILocation loc = LocationFactory.createCLocation(node); 
		ExpressionResult switchedRexpr = expr.switchToRValueIfNecessary(main, mMemoryHandler, mStructHandler, loc);

		// TODO: check validity of cast?

		TypesResult resTypes = (TypesResult) main.dispatch(node.getTypeId().getDeclSpecifier());

		mCurrentDeclaredTypes.push(resTypes);
		DeclarationResult declResult = (DeclarationResult) main.dispatch(node.getTypeId().getAbstractDeclarator());
		assert declResult.getDeclarations().size() == 1;
		CType newCType = declResult.getDeclarations().get(0).getType();
		mCurrentDeclaredTypes.pop();
		
		if (newCType instanceof CPointer && expr.lrVal.getCType() instanceof CArray) {
			if (node.getOperand() instanceof IASTIdExpression){
				IASTNode d = ((PRSymbolTableValue) mSymbolTable.get(((IASTIdExpression) node.getOperand()).getName().toString(), loc)).decl;
				variablesOnHeap.add(d);
			} else {
				//TODO: handle f.i. something like "(int *) a[2]" where a is two-dimensional (thus a[2] is an array)
				throw new UnsupportedOperationException("determine on-heap/off-heap for nested arrays");
			}
			return switchedRexpr;
		}

//		if (newCType instanceof CPointer && expr.lrVal.getCType() instanceof CArray) {
//			HeapLValue hlv = (HeapLValue) expr.lrVal;
//			
//			if (hlv.getAddress() instanceof IdentifierExpression) {
//					String id = ((IdentifierExpression) rexp.lrVal.getValue()).getIdentifier();
//				    IASTNode decl = ((PRSymbolTableValue) this.getSymbolTable().get(this.getSymbolTable().getCID4BoogieID(id, loc), loc)).decl;
//				    ((PRCHandler) this).getVarsForHeap().add(decl);
//			} else {
//				throw new UnsupportedOperationException("yet unsupported cast from " + oldType + " to " + newType);
//			}
//		}
		
		castToType(main, loc, switchedRexpr, newCType);

		// String msg = "Ignored cast! At line: "
		// + node.getFileLocation().getStartingLineNumber();
		// Dispatcher.unsoundnessWarning(loc, msg,
		// "Ignored cast!");
		return switchedRexpr;
	}

	@Override
	public Result visit(Dispatcher main, IASTInitializerList node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result visit(Dispatcher main, IASTArraySubscriptExpression node) {
		return mArrayHandler.handleArraySubscriptExpression(main, mMemoryHandler, mStructHandler, node);
	}

	@Override
	public Result visit(Dispatcher main, IASTTypeIdExpression node) {
		ILocation loc = LocationFactory.createCLocation(node);
		switch (node.getOperator()) {
		case IASTTypeIdExpression.op_sizeof:
			TypesResult rt = (TypesResult) main.dispatch(node.getTypeId().getDeclSpecifier());
			TypesResult checked = checkForPointer(main, node.getTypeId().getAbstractDeclarator().getPointerOperators(),
					rt, false);

			return new ExpressionResult(new RValue(mMemoryHandler.calculateSizeOf(loc, checked.cType), new CPrimitive(
					PRIMITIVE.INT)));
			// }
		default:
			break;
		}
		String msg = "Unsupported boogie AST node type: " + node.getClass();
		throw new UnsupportedSyntaxException(loc, msg);
	}

	@Override
	public Result visit(Dispatcher main, IASTExpression node) {
		// TODO Auto-generated method stub
		return null;
	}
//	public ResultExpression makeAssignment(Dispatcher main, ILocation loc, ArrayList<Statement> stmt, LRValue lrVal,
//			RValue rVal, ArrayList<Declaration> decl, Map<VariableDeclaration, ILocation> auxVars,
//			List<Overapprox> overappr) {
//		return makeAssignment(main, loc, stmt, lrVal, rVal, decl, auxVars, overappr, null);
//	}

	public ExpressionResult makeAssignment(Dispatcher main, ILocation loc,  LRValue lrVal,
			LRValue rVal) {
		LRValue rightHandSide = rVal; //we may change the content of the right hand side later

		//do implicit cast -- assume the types are compatible
//		rightHandSide = castToType(loc, rightHandSide, lrVal.cType);
		
		if (lrVal.getCType().getUnderlyingType() instanceof CPointer
				&& rightHandSide.getCType().getUnderlyingType() instanceof CArray) {
//			variablesOnHeap.add(node);
			Expression valOrAddr = rightHandSide instanceof HeapLValue 
					? ((HeapLValue) rightHandSide).getAddress() 
							: rightHandSide.getValue();
			if (valOrAddr instanceof IdentifierExpression) {
				String id = ((IdentifierExpression) rVal.getValue()).getIdentifier();
				variablesOnHeap.add(((PRSymbolTableValue) mSymbolTable.get(mSymbolTable.getCID4BoogieID(id, loc), loc)).decl);
			}
		}

		if (lrVal instanceof HeapLValue) {
			HeapLValue hlv = (HeapLValue) lrVal;
//			stmt.addAll(mMemoryHandler.getWriteCall(loc, hlv, rightHandSide));
//			return new ResultExpression(rightHandSide);
			return new ExpressionResult(hlv);
		} else if (lrVal instanceof LocalLValue) {
			LocalLValue lValue = (LocalLValue) lrVal;
//			AssignmentStatement assignStmt = new AssignmentStatement(loc, new LeftHandSide[] { lValue.getLHS() },
//					new Expression[] { rightHandSide.getValue() });
//			Map<String, IAnnotations> annots = assignStmt.getPayload().getAnnotations();
//			for (Overapprox overapprItem : overappr) {
//				annots.put(Overapprox.getIdentifier(), overapprItem);
//			}
//			stmt.add(assignStmt);
//
//			// add havocs if we have a write to a union (which is not on heap,
//			// otherwise the heap model should deal with everything)
//			if (unionFieldsToCType != null) {
//				for (Entry<StructLHS, CType> en : unionFieldsToCType.entrySet()) {
//					//do not havoc when the type of the field is "compatible"
//					if (rightHandSide.cType.equals(en.getValue())
//							|| (rightHandSide.cType instanceof CPrimitive && en.getValue() instanceof CPrimitive
//							 && ((CPrimitive) rightHandSide.cType.getUnderlyingType()).getGeneralType().equals(((CPrimitive) en.getValue()).getGeneralType())
//							 && (mMemoryHandler.calculateSizeOfWithGivenTypeSizes(loc, rightHandSide.cType) 
//									 == mMemoryHandler.calculateSizeOfWithGivenTypeSizes(loc, en.getValue())))) {
//						stmt.add(new AssignmentStatement(loc, new LeftHandSide[] { en.getKey() },
//								new Expression[] { rightHandSide.getValue() }));
//					} else { //otherwise we consider the value undefined, thus havoc it
//						// TODO: maybe not use auxiliary variables so lavishly
//						String tmpId = main.nameHandler.getTempVarUID(SFO.AUXVAR.UNION);
//						VariableDeclaration tVarDec = new VariableDeclaration(loc, new Attribute[0], new VarList[] { new VarList(loc,
//								new String[] { tmpId }, main.typeHandler.ctype2asttype(loc, en.getValue())) });
//						decl.add(tVarDec);
//						auxVars.put(tVarDec, loc); //ensures that the variable will be havoced (necessary only when we are inside a loop)
//
//						stmt.add(new AssignmentStatement(loc, new LeftHandSide[] { en.getKey() },
//								new Expression[] { new IdentifierExpression(loc, tmpId) }));
//					}
//				}
//			}
//
//			if (!mFunctionHandler.noCurrentProcedure())
//				mFunctionHandler.checkIfModifiedGlobal(main, BoogieASTUtil.getLHSId(lValue.getLHS()), loc);
			return new ExpressionResult(lValue);
		} else
			throw new AssertionError("Type error: trying to assign to an RValue in Statement" + loc.toString());
	}

	public void beginScope() {
//		this.sT.beginScope();
		this.mTypeHandler.beginScope();
		this.mSymbolTable.beginScope();
	}

	public void endScope() {
//		this.sT.endScope();
		this.mTypeHandler.endScope();
		this.mSymbolTable.endScope();
	}

	@Override
	public boolean isHeapVar(String boogieId) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public InitializationHandler getInitHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UNSIGNED_TREATMENT getUnsignedTreatment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionHandler getFunctionHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
