package com.brandon3055.draconicevolution.lib;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.lib.OPStorageOP;
import com.google.common.math.BigIntegerMath;
import net.minecraft.client.resources.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by brandon3055 on 21/09/2022
 */
public class OPStorageOPTest {
    public static final Logger LOGGER = LogManager.getLogger("OPStorageOPTest");

    @Test
    public void testToString() {
        OPStorageOP storageOP = new OPStorageOP(() -> -1L);

        //9,999,999,999,999                                         Tera            numprefix.draconicevolution.10-12
        storageOP.receiveOP(9876999999999L, false);
        assertEquals("9.876 numprefix.draconicevolution.10-12", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999                                     Peta            numprefix.draconicevolution.10-15
        storageOP.receiveOP(9876999999999999L - 9999999999999L, false);
        assertEquals("9.876 numprefix.draconicevolution.10-15", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999                                 Exa             numprefix.draconicevolution.10-18
        storageOP.overflowCount = BigInteger.valueOf(1);
        storageOP.valueStorage = 776627963145224191L;
        assertEquals("9.999 numprefix.draconicevolution.10-18", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999                             Zetta           numprefix.draconicevolution.10-21
        storageOP.overflowCount = BigInteger.valueOf(1084);
        storageOP.valueStorage = 1864712049423024127L;
        assertEquals("9.999 numprefix.draconicevolution.10-21", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999                         Yotta           numprefix.draconicevolution.10-24
        storageOP.overflowCount = BigInteger.valueOf(1084202);
        storageOP.valueStorage = 1590897978359414783L;
        assertEquals("9.999 numprefix.draconicevolution.10-24", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999,999                     Octillion       numprefix.draconicevolution.10-27
        storageOP.overflowCount = BigInteger.valueOf(1084202172);
        storageOP.valueStorage = 4477988020393345023L;
        assertEquals("9.999 numprefix.draconicevolution.10-27", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999,999,999                 Nonillion       numprefix.draconicevolution.10-30
        storageOP.overflowCount = BigInteger.valueOf(1084202172485L);
        storageOP.valueStorage = 4652582518778757119L;
        assertEquals("9.999 numprefix.draconicevolution.10-30", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999,999,999,999             Decillion       numprefix.draconicevolution.10-33
        storageOP.overflowCount = BigInteger.valueOf(1084202172485504L);
        storageOP.valueStorage = 4003012203950112767L;
        assertEquals("9.999 numprefix.draconicevolution.10-33", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999,999,999,999,999         Undecillion     numprefix.draconicevolution.10-36
        storageOP.overflowCount = BigInteger.valueOf(1084202172485504434L);
        storageOP.valueStorage = 68739955140067327L;
        assertEquals("9.999 numprefix.draconicevolution.10-36", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        //9,999,999,999,999,999,999,999,999,999,999,999,999,999     Duodecillion    numprefix.draconicevolution.10-39
        storageOP.overflowCount = new BigInteger("1084202172485504434007");
        storageOP.valueStorage = 4176350882083897343L;
        assertEquals("9.999 numprefix.draconicevolution.10-39", storageOP.getReadable());
//        LOGGER.info(storageOP.getScientific());

        storageOP.valueStorage = 0;
        storageOP.overflowCount = BigInteger.ZERO;

        BigInteger total = BigInteger.ZERO;
        Random random = new Random(0);
        for (int i = 0; i < 1000000; i++) {
            long toAdd = Math.abs(random.nextLong());
            total = total.add(BigInteger.valueOf(toAdd));
            storageOP.receiveOP(toAdd, false);

            int digits = BigIntegerMath.log10(total, RoundingMode.DOWN);
            int prefixStep = (digits / 3) * 3;
            BigDecimal decimal = new BigDecimal(total).divide(BigDecimal.valueOf(10).pow(prefixStep), 3, RoundingMode.DOWN);
            assertEquals(decimal + " " + "numprefix.draconicevolution.10-" + prefixStep, storageOP.getReadable());
        }

    }
}
