package com.eyeclick.sergb.consumer

import org.junit.Test

class BulkProcessTest {
    val bp = BulkProcess()

    @Test
    fun processBulkTest() {
        var tempHistogram: MutableMap<Char, Int> = HashMap()
        val pb = bp.processBulk("I ate an apple today", tempHistogram)
//        System.out.println("TEST: " + pb.toString())

        assert(pb[' '] == 4)
        assert(pb['p'] == 2)
        assert(pb['a'] == 4)
        assert(pb['t'] == 2)
        assert(pb['d'] == 1)
        assert(pb['e'] == 2)
        assert(pb['I'] == 1)
        assert(pb['y'] == 1)
        assert(pb['l'] == 1) // lowercase L, weird l(L) looks like 1(one)
        assert(pb['n'] == 1)
        assert(pb['o'] == 1)
    }

    @Test
    fun stripWhitespaceTest() {
        var sw = bp.stripWhitespace("I ate an apple today")
        assert(sw == "I ate an apple today")

        sw = bp.stripWhitespace("     I      ate an apple today")
        System.out.println(sw)
        assert(sw == "I ate an apple today")
    }

    //TODO more tests!
}