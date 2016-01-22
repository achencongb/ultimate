/*
 * Copyright (C) 2014-2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE Core.
 * 
 * The ULTIMATE Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Core. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Core, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE Core grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.result;

import de.uni_freiburg.informatik.ultimate.result.model.IResult;

/**
 * 
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 *
 */
public class WitnessResult implements IResult {

	public static enum WitnessVerificationStatus {
		VERIFIED, UNVERIFIED, VERIFICATION_FAILED, INTERNAL_ERROR
	}

	private final IResult mResult;
	private final String mWitness;
	private final WitnessVerificationStatus mVerificationStatus;
	private final String mPluginId;

	public WitnessResult(String pluginId, IResult result, String witness,
			WitnessVerificationStatus verificationStatus) {
		//TODO: Witness string may be useless and its potentially large, so... maybe remove it? 
		mPluginId = pluginId;
		mResult = result;
		mWitness = witness;
		mVerificationStatus = verificationStatus;
	}

	@Override
	public String getShortDescription() {
		if (isEmpty()) {
			return "No witness for: " + mResult.getShortDescription();
		}

		switch (getVerificationStatus()) {
		case INTERNAL_ERROR:
			return "An error occured during witness verification for: " + mResult.getShortDescription();
		case UNVERIFIED:
			return "Unverified witness for: " + mResult.getShortDescription();
		case VERIFICATION_FAILED:
			return "Witness verification failed for: " + mResult.getShortDescription();
		case VERIFIED:
			return "Verified witness for: " + mResult.getShortDescription();
		default:
			throw new UnsupportedOperationException("Enum value " + getVerificationStatus() + " is unhandled");
		}
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	public WitnessVerificationStatus getVerificationStatus() {
		return mVerificationStatus;
	}

	public boolean isEmpty() {
		return mWitness == null;
	}

	@Override
	public String getPlugin() {
		return mPluginId;
	};

}
