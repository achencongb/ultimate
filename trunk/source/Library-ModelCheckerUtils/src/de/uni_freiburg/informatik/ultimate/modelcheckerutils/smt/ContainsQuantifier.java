/*
 * Copyright (C) 2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE ModelCheckerUtils Library.
 * 
 * The ULTIMATE ModelCheckerUtils Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE ModelCheckerUtils Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE ModelCheckerUtils Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE ModelCheckerUtils Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE ModelCheckerUtils Library grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt;

import de.uni_freiburg.informatik.ultimate.logic.AnnotatedTerm;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.ConstantTerm;
import de.uni_freiburg.informatik.ultimate.logic.LetTerm;
import de.uni_freiburg.informatik.ultimate.logic.NonRecursive;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;

/**
 * Check if term contains some quantified subformula.
 * @author Matthias Heizmann
 *
 */
public class ContainsQuantifier extends NonRecursive {
	
	private boolean m_QuantifierFound;
	private int m_FirstQuantifierFound = -1;
	
	class QuantifierFinder extends TermWalker {
		QuantifierFinder(Term term) { super(term); }
		
		@Override
		public void walk(NonRecursive walker, ConstantTerm term) {
			// cannot descend
		}
		@Override
		public void walk(NonRecursive walker, AnnotatedTerm term) {
			walker.enqueueWalker(new QuantifierFinder(term.getSubterm()));
		}
		@Override
		public void walk(NonRecursive walker, ApplicationTerm term) {
			for (Term t : term.getParameters()) {
				walker.enqueueWalker(new QuantifierFinder(t));
			}
		}
		@Override
		public void walk(NonRecursive walker, LetTerm term) {
			walker.enqueueWalker(new QuantifierFinder(term.getSubTerm()));
		}
		@Override
		public void walk(NonRecursive walker, QuantifiedFormula term) {
			m_QuantifierFound = true;
			m_FirstQuantifierFound = term.getQuantifier();
			reset();
		}
		@Override
		public void walk(NonRecursive walker, TermVariable term) {
			// cannot descend
		}
	}
	
	public ContainsQuantifier() {
		super();
	}

	/**
	 * Returns true iff this term contains the subterm of this ContainsSubterm 
	 * object.
	 */
	public boolean containsQuantifier(Term term) {
		m_FirstQuantifierFound = -1;
		m_QuantifierFound = false;
		run(new QuantifierFinder(term));
		return m_QuantifierFound;
	}
	
	public int getFirstQuantifierFound() {
		if (m_FirstQuantifierFound == -1) {
			throw new IllegalStateException("no quantifier found");
		} else {
			return m_FirstQuantifierFound;
		}
	}
}
