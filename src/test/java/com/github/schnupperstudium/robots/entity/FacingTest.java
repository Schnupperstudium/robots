package com.github.schnupperstudium.robots.entity;

import com.github.schnupperstudium.robots.entity.Facing;
import org.junit.Assert;
import org.junit.Test;

public class FacingTest {

    @Test
    public void facingOfTest() {
        final Facing west = Facing.of(-1, 0);
        Assert.assertSame(Facing.WEST, west);

        final Facing east = Facing.of(1, 0);
        Assert.assertSame(Facing.EAST, east);

        final Facing north = Facing.of(0, -1);
        Assert.assertSame(Facing.NORTH, north);

        final Facing south = Facing.of(0, 1);
        Assert.assertSame(Facing.SOUTH, south);
    }

    @Test(expected = IllegalArgumentException.class)
    public void facingOfExceptionTest() {
        Facing.of(0, 0);
    }

    @Test
    public void facingLeftTest() {
        Assert.assertSame(Facing.WEST, Facing.NORTH.left());
        Assert.assertSame(Facing.SOUTH, Facing.WEST.left());
        Assert.assertSame(Facing.EAST, Facing.SOUTH.left());
        Assert.assertSame(Facing.NORTH, Facing.EAST.left());
    }

    @Test
    public void facingRightTest() {
        Assert.assertSame(Facing.WEST, Facing.SOUTH.right());
        Assert.assertSame(Facing.SOUTH, Facing.EAST.right());
        Assert.assertSame(Facing.EAST, Facing.NORTH.right());
        Assert.assertSame(Facing.NORTH, Facing.WEST.right());
    }

    @Test
    public void facingOppositeTest() {
        Assert.assertSame(Facing.WEST, Facing.EAST.opposite());
        Assert.assertSame(Facing.SOUTH, Facing.NORTH.opposite());
        Assert.assertSame(Facing.EAST, Facing.WEST.opposite());
        Assert.assertSame(Facing.NORTH, Facing.SOUTH.opposite());
    }
}
