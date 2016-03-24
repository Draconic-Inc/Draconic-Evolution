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

public class HandsClimbing
{
	public static final int MiddleGrab = 2;
	public static final int UpGrab = 1;
	public static final int NoGrab = 0;

	public static HandsClimbing None = new HandsClimbing(-3);
	public static HandsClimbing Sink = new HandsClimbing(-2);
	public static HandsClimbing TopHold = new HandsClimbing(-1);
	public static HandsClimbing BottomHold = new HandsClimbing(0);
	public static HandsClimbing Up = new HandsClimbing(1);
	public static HandsClimbing FastUp = new HandsClimbing(2);

	private int _value;

	private HandsClimbing(int value)
	{
		_value = value;
	}

	public boolean IsRelevant()
	{
		return _value > None._value;
	}

	public boolean IsUp()
	{
		return this == Up || this == FastUp;
	}

	public HandsClimbing ToUp()
	{
		if(this == BottomHold)
			return Up;
		return this;
	}

	public HandsClimbing ToDown()
	{
		if(this == TopHold)
			return Sink;
		return this;
	}

	public HandsClimbing max(HandsClimbing other, ClimbGap inout_thisClimbGap, ClimbGap otherClimbGap)
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
		if(_value == Sink._value)
			return "Sink";
		if(_value == BottomHold._value)
			return "BottomHold";
		if(_value == TopHold._value)
			return "TopHold";
		if(_value == Up._value)
			return "Up";
		return "FastUp";
	}

	public void print(String name)
	{
		PrintStream stream = System.err;
		if(name != null)
			stream.print(name + " = ");
		stream.println(this);
	}

	private static HandsClimbing get(int value)
	{
		if(value <= None._value)
			return None;
		if(value == Sink._value)
			return Sink;
		if(value == BottomHold._value)
			return BottomHold;
		if(value == TopHold._value)
			return TopHold;
		if(value == Up._value)
			return Up;
		return FastUp;
	}
}