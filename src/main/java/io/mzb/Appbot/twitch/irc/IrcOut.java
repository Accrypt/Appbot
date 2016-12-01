package io.mzb.Appbot.twitch.irc;

import java.io.OutputStream;
import java.io.PrintStream;

public class IrcOut extends PrintStream {

    public IrcOut(OutputStream out) {
        super(out);
    }

    @Override
    public void println() {
        super.println();
        printOut("");
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(char x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(int x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(long x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(float x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(double x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(char[] x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(String x) {
        super.println(x);
        printOut(x);
    }

    @Override
    public void println(Object x) {
        super.println(x);
        printOut(x);
    }

    private void printOut(Object x) {
        Thread.currentThread().setName("IRC-O");
        System.out.println("[IRC] -> " + x.toString());
    }

}
