package cell.example.math;

import cell.CellIntf;

public interface IPriceCalculator extends CellIntf {

    default long calcDiscountPrice(long originalPrice, int discountRate) {
        return originalPrice * discountRate / 100;
    }
}

package cell.example.math.impl;

import bap.cells.BasicCell;
import cell.example.math.IPriceCalculator;

public class CPriceCalculator extends BasicCell implements IPriceCalculator {

    public long calcFinalPrice(long originalPrice, int discountRate, long coupon) {
        long discounted = calcDiscountPrice(originalPrice, discountRate);
        return Math.max(discounted - coupon, 0L);
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.example.math.IPriceCalculator;
import org.junit.Test;

public class PriceCalculatorTest extends BapTester {

    @Test
    public void testCalculator() {
        IPriceCalculator calculator = Cells.get(IPriceCalculator.class);
        assert calculator.calcDiscountPrice(1000L, 80) == 800L;
    }
}
