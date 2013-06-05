package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.Word;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingCallTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;

/**
 * Totalized automaton of input. Expects that input is deterministic.
 * Throws Illegal ArgumentException as soon as nondeterminism in input is detected.
 * @author heizmann@informatik.uni-freiburg.de
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class TotalizeNwa<LETTER, STATE> implements INestedWordAutomatonSimple<LETTER, STATE> {
	
	private final INestedWordAutomatonSimple<LETTER, STATE> m_Operand;
	private final StateFactory<STATE> m_StateFactory;
	private final STATE m_SinkState;
	public final static String OPERAND_NOT_DETERMINISTIC = "OperandIsNotDeterministic";
	
	
	public TotalizeNwa(INestedWordAutomatonSimple<LETTER, STATE> operand, 
			StateFactory<STATE> sf) {
		m_Operand = operand;
		m_StateFactory = sf;
		m_SinkState = sf.createSinkStateContent();
	}
	
	
	private void throwOperandNotDeterministicException() {
		throw new IllegalArgumentException(OPERAND_NOT_DETERMINISTIC);
	}
	
	
	@Override
	public Iterable<STATE> getInitialStates() {
		Iterator<STATE> it = m_Operand.getInitialStates().iterator();
		STATE initial;
		if (it.hasNext()) {
			initial = it.next();
		} else {
			initial = m_SinkState;
		}
		if (it.hasNext()) {
			throwOperandNotDeterministicException();
		}
		HashSet<STATE> result = new HashSet<STATE>(1);
		result.add(initial);
		return result;
		
	}


	@Override
	public Set<LETTER> getInternalAlphabet() {
		return m_Operand.getInternalAlphabet();
	}

	@Override
	public Set<LETTER> getCallAlphabet() {
		return m_Operand.getCallAlphabet();
	}

	@Override
	public Set<LETTER> getReturnAlphabet() {
		return m_Operand.getReturnAlphabet();
	}

	@Override
	public StateFactory<STATE> getStateFactory() {
		return m_StateFactory;
	}
	
	@Override
	public boolean isInitial(STATE state) {
		return m_Operand.isInitial(state);
	}

	@Override
	public boolean isFinal(STATE state) {
		if (state == m_SinkState) {
			return false;
		} else {
			return m_Operand.isFinal(state);
		}
	}



	@Override
	public STATE getEmptyStackState() {
		return m_Operand.getEmptyStackState();
	}

	@Override
	public Collection<LETTER> lettersInternal(STATE state) {
		return m_Operand.getInternalAlphabet();
	}

	@Override
	public Collection<LETTER> lettersCall(STATE state) {
		return m_Operand.getCallAlphabet();
	}

	@Override
	public Collection<LETTER> lettersReturn(STATE state) {
		return m_Operand.getReturnAlphabet();
	}


	@Override
	public Iterable<OutgoingInternalTransition<LETTER, STATE>> internalSuccessors(
			STATE state, LETTER letter) {
		if (state != m_SinkState) {
			Iterator<OutgoingInternalTransition<LETTER, STATE>> it = 
					m_Operand.internalSuccessors(state).iterator();
			if (it.hasNext()) {
				it.next();
				if (it.hasNext()) {
					throwOperandNotDeterministicException();
				} else {
					return m_Operand.internalSuccessors(state);
				}
			}
		}
		OutgoingInternalTransition<LETTER, STATE> trans = 
				new OutgoingInternalTransition<LETTER, STATE>(letter, m_SinkState);
		ArrayList<OutgoingInternalTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingInternalTransition<LETTER, STATE>>(1);
		result.add(trans);
		return result;
	}

	@Override
	public Iterable<OutgoingInternalTransition<LETTER, STATE>> internalSuccessors(
			STATE state) {
		ArrayList<OutgoingInternalTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingInternalTransition<LETTER, STATE>>();
		for (LETTER letter : getInternalAlphabet()) {
			Iterator<OutgoingInternalTransition<LETTER, STATE>> it = 
					internalSuccessors(state, letter).iterator();
			result.add(it.next());
			assert !it.hasNext();
		}
		return result;
	}

	@Override
	public Iterable<OutgoingCallTransition<LETTER, STATE>> callSuccessors(
			STATE state, LETTER letter) {
		if (state != m_SinkState) {
			Iterator<OutgoingCallTransition<LETTER, STATE>> it = 
					m_Operand.callSuccessors(state).iterator();
			if (it.hasNext()) {
				it.next();
				if (it.hasNext()) {
					throwOperandNotDeterministicException();
				} else {
					return m_Operand.callSuccessors(state);
				}
			}
		}
		OutgoingCallTransition<LETTER, STATE> trans = 
				new OutgoingCallTransition<LETTER, STATE>(letter, m_SinkState);
		ArrayList<OutgoingCallTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingCallTransition<LETTER, STATE>>(1);
		result.add(trans);
		return result;
	}

	@Override
	public Iterable<OutgoingCallTransition<LETTER, STATE>> callSuccessors(
			STATE state) {
		ArrayList<OutgoingCallTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingCallTransition<LETTER, STATE>>();
		for (LETTER letter : getCallAlphabet()) {
			Iterator<OutgoingCallTransition<LETTER, STATE>> it = 
					callSuccessors(state, letter).iterator();
			result.add(it.next());
			assert !it.hasNext();
		}
		return result;
	}



	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSucccessors(
			STATE state, STATE hier, LETTER letter) {
		if (state != m_SinkState) {
			Iterator<OutgoingReturnTransition<LETTER, STATE>> it = 
					m_Operand.returnSucccessors(state, hier, letter).iterator();
			if (it.hasNext()) {
				it.next();
				if (it.hasNext()) {
					throwOperandNotDeterministicException();
				} else {
					return m_Operand.returnSucccessors(state, hier, letter);
				}
			}
		}
		OutgoingReturnTransition<LETTER, STATE> trans = 
				new OutgoingReturnTransition<LETTER, STATE>(hier, letter, m_SinkState);
		ArrayList<OutgoingReturnTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingReturnTransition<LETTER, STATE>>(1);
		result.add(trans);
		return result;
	}

	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSuccessorsGivenHier(
			STATE state, STATE hier) {
		ArrayList<OutgoingReturnTransition<LETTER, STATE>> result = 
				new ArrayList<OutgoingReturnTransition<LETTER, STATE>>();
		for (LETTER letter : getReturnAlphabet()) {
			Iterator<OutgoingReturnTransition<LETTER, STATE>> it = 
					returnSucccessors(state, hier, letter).iterator();
			result.add(it.next());
			assert !it.hasNext();
		}
		return result;
	}

	@Override
	public boolean accepts(Word<LETTER> word) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<LETTER> getAlphabet() {
		throw new UnsupportedOperationException();	}

	@Override
	public String sizeInformation() {
		return "size Information not available";
	}


}
