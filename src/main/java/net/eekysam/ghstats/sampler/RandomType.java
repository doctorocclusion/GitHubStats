package net.eekysam.ghstats.sampler;

import java.security.GeneralSecurityException;
import java.util.Random;

import org.uncommons.maths.random.AESCounterRNG;
import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.JavaRNG;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;
import org.uncommons.maths.random.XORShiftRNG;

public enum RandomType
{
	JAVA
	{
		@Override
		public Random getRandom(SeedGenerator seed) throws SeedException
		{
			return new JavaRNG(seed);
		}
	},
	MT
	{
		@Override
		public Random getRandom(SeedGenerator seed) throws SeedException
		{
			return new MersenneTwisterRNG(seed);
		}
	},
	CA
	{
		@Override
		public Random getRandom(SeedGenerator seed) throws SeedException
		{
			return new CellularAutomatonRNG(seed);
		}
	},
	AESC
	{
		@Override
		public Random getRandom(SeedGenerator seed) throws SeedException
		{
			try
			{
				return new AESCounterRNG(seed);
			}
			catch (GeneralSecurityException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	},
	XORS
	{
		@Override
		public Random getRandom(SeedGenerator seed) throws SeedException
		{
			return new XORShiftRNG(seed);
		}
	};
	
	public abstract Random getRandom(SeedGenerator seed) throws SeedException;
}
