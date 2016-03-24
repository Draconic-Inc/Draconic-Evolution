// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import java.io.*;

public class FeetClimbing
{
	public static final int DownStep = 1;
	public static final int NoStep = 0;

	public static FeetClimbing None = new FeetClimbing(-3);
	public static FeetClimbing BaseHold = new FeetClimbing(-2);
	public static FeetClimbing BaseWithHands = new FeetClimbing(-1);
	public static FeetClimbing TopWithHands = new FeetClimbing(0);
	public static FeetClimbing SlowUpWithHoldWithoutHands = new FeetClimbing(1);
	public static FeetClimbing SlowUpWithSinkWithoutHands = new FeetClimbing(2);
	public static FeetClimbing FastUp = new FeetClimbing(3);

	private int _value;

	private FeetClimbing(int value)
	{
		_value = value;
	}

	public boolean IsRelevant()
	{
		return _value > None._value;
	}

	public boolean IsIndependentlyRelevant()
	{
		return _value > BaseWithHands._value;
	}

	public boolean IsUp()
	{
		return this == SlowUpWithHoldWithoutHands || this == SlowUpWithSinkWithoutHands || this == FastUp;
	}

	public FeetClimbing max(FeetClimbing other, ClimbGap inout_thisClimbGap, ClimbGap otherClimbGap)
	{
		if(!otherClimbGap.SkipGaps)
		{
			inout_thisClimbGap.CanStand |= otherClimbGap.CanStand;
			inout_thisClimbGap.MustCrawl |= otherClimbGap.MustCrawl;
		}
		if(_value < other._value)
		{
			inout_thisClimbGap.Block = otherClimbGap.Block;
			inout_thisClimbGap.Meta = otherClimbGap.Meta;
			inout_thisClimbGap.Direction = otherClimbGap.Direction;
		}
		return get(Math.max(_value, other._value));
	}

	@Override
	public String toString()
	{
		if(_value <= None._value)
			return "None";
		if(_value == BaseHold._value)
			return "BaseHold";
		if(_value == BaseWithHands._value)
			return "BaseWithHands";
		if(_value == TopWithHands._value)
			return "TopWithHands";
		if(_value == SlowUpWithHoldWithoutHands._value)
			return "SlowUpWithHoldWithoutHands";
		if(_value == SlowUpWithSinkWithoutHands._value)
			return "SlowUpWithSinkWithoutHands";
		return "FastUp";
	}

	public void print(String name)
	{
		PrintStream stream = System.err;
		if(name != null)
			stream.print(name + " = ");
		stream.println(this);
	}

	private static FeetClimbing get(int value)
	{
		if(value <= None._value)
			return None;
		if(value == BaseHold._value)
			return BaseHold;
		if(value == BaseWithHands._value)
			return BaseWithHands;
		if(value == TopWithHands._value)
			return TopWithHands;
		if(value == SlowUpWithHoldWithoutHands._value)
			return SlowUpWithHoldWithoutHands;
		if(value == SlowUpWithSinkWithoutHands._value)
			return SlowUpWithSinkWithoutHands;
		return FastUp;
	}
}