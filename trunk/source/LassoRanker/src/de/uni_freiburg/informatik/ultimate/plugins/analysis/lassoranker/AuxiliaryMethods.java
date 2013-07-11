package de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.ConstantTerm;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.SMTLIBException;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.exceptions.TermException;


/**
 * Random collection of various methods that don't fit in anywhere else
 * 
 * @author Jan Leike
 */
public class AuxiliaryMethods {
	/**
	 * Define a new constant of sort "Real".
	 * @param script SMT Solver
	 * @param name name of the new constant
	 * @return the new variable as a term
	 * @throws SMTLIBException if something goes wrong, e.g. the name is
	 *          already defined
	 */
	public static Term newRealConstant(Script script, String name)
			throws SMTLIBException {
		try {
			script.declareFun(name, new Sort[0], script.sort("Real"));
		} catch(SMTLIBException iae) {
			if (!iae.getMessage().endsWith("already defined.")) {
				throw iae;
			} else {
				// The function is already defined
				// --> Silence the exception
			}
		}
		return script.term(name);
	}
	
	/**
	 * Convert a Rational into a decimal instance.
	 */
	public static Term rationalToDecimal(Script script, Rational a) {
		Term num = script.decimal(a.numerator().abs().toString());
		Term denom = script.decimal(a.denominator().abs().toString());
		boolean negative = a.numerator().signum() * a.denominator().signum()
				== -1;
		Term t = script.term("/", num, denom);
		if (negative) {
			t = script.term("-", t);
		}
		return t;
	}
	
	/**
	 * Convert a BigDecimal into a Rational.
	 * Stolen from Jochen's code
	 * de.uni_freiburg.informatik.ultimate.smtinterpol.convert.ConvertFormula.
	 */
	public static Rational decimalToRational(BigDecimal d) {
		Rational rat;
		if (d.scale() <= 0) {
			BigInteger num = d.toBigInteger();
			rat = Rational.valueOf(num, BigInteger.ONE);
		} else {
			BigInteger num = d.unscaledValue();
			BigInteger denom = BigInteger.TEN.pow(d.scale());
			rat = Rational.valueOf(num, denom);
		}
		return rat;
	}
	
	/**
	 * Convert a constant term to Rational
	 * Extracts the value of the number from the term
	 * @param ct constant term
	 * @return rational from the value of ct
	 */
	public static Rational convertCT(ConstantTerm ct)
			throws TermException {
		if (ct.getSort().getName().equals("Rational")) {
			return (Rational) ct.getValue();
		} else if (ct.getSort().getName().equals("Real")) {
			BigDecimal d = (BigDecimal) ct.getValue();
			return (Rational) AuxiliaryMethods.decimalToRational(d);
		} else if (ct.getSort().getName().equals("Int")) {
			Rational r = Rational.valueOf((BigInteger) ct.getValue(),
					BigInteger.ONE);
			return r;
		} else
			throw new TermException(
					"Trying to convert a ConstantTerm of unknown sort.", ct);
	}
	
	/**
	 * Convert a constant term retrieved from a model valuation to a Rational
	 * @param t a term containing only +, -, *, / and numerals
	 * @return the rational represented by the term
	 * @throws TermException if an error occurred while parsing the term
	 */
	public static Rational const2Rational(Term t) throws TermException {
		if (t instanceof ApplicationTerm) {
			ApplicationTerm appt = (ApplicationTerm) t;
			if (appt.getFunction().getName() == "+") {
				return const2Rational(appt.getParameters()[0]).add(
						const2Rational(appt.getParameters()[1]));
			}
			if (appt.getFunction().getName() == "-") {
				if (appt.getParameters().length == 1) {
					return const2Rational(appt.getParameters()[0]).mul(
							Rational.MONE);
				} else {
					return const2Rational(appt.getParameters()[0]).sub(
							const2Rational(appt.getParameters()[1]));
				}
			}
			if (appt.getFunction().getName() == "*") {
				return const2Rational(appt.getParameters()[0]).mul(
						const2Rational(appt.getParameters()[1]));
			}
			if (appt.getFunction().getName() == "/") {
				return const2Rational(appt.getParameters()[0]).div(
						const2Rational(appt.getParameters()[1]));
			}
		}
		if (t instanceof ConstantTerm) {
			Object o = ((ConstantTerm) t).getValue();
			if (o instanceof BigInteger) {
				return Rational.valueOf((BigInteger) o, BigInteger.ONE);
			} else if (o instanceof BigDecimal) {
				BigDecimal decimal = (BigDecimal) o;
				Rational rat;
				if (decimal.scale() <= 0) {
					BigInteger num = decimal.toBigInteger();
					rat = Rational.valueOf(num, BigInteger.ONE);
				} else {
					BigInteger num = decimal.unscaledValue();
					BigInteger denom = BigInteger.TEN.pow(decimal.scale());
					rat = Rational.valueOf(num, denom);
				}
				return rat;
			} else if (o instanceof Rational) {
				return (Rational) o;
			} else {
				throw new TermException("Unknown value class", t);
			}
		}
		throw new TermException("Unkown term structure", t);
	}
}
