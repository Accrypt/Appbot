package io.mzb.Appbot.log;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppbotLogger extends PrintStream {

    private PrintStream console;

    public AppbotLogger(PrintStream printStream, PrintStream console) throws FileNotFoundException {
        super(printStream);
        this.console = console;
    }

    @Override
    public void print(boolean b) {
        handleOut(b);
    }

    @Override
    public void print(char c) {
        handleOut(c);
    }

    @Override
    public void print(double d) {
        handleOut(d);
    }

    @Override
    public void print(float f) {
        handleOut(f);
    }

    @Override
    public void print(int i) {
        handleOut(i);
    }

    @Override
    public void print(long l) {
        handleOut(l);
    }

    @Override
    public void print(Object obj) {
        handleOut(obj);
    }

    @Override
    public void print(char[] s) {
        handleOut(s);
    }

    @Override
    public void println() {
        handleOut("");
    }

    @Override
    public void println(boolean x) {
        handleOut(x);
    }

    @Override
    public void println(char x) {
        handleOut(x);
    }

    @Override
    public void println(int x) {
        handleOut(x);
    }

    @Override
    public void println(long x) {
        handleOut(x);
    }

    @Override
    public void println(float x) {
        handleOut(x);
    }

    @Override
    public void println(double x) {
        handleOut(x);
    }

    @Override
    public void println(char[] x) {
        handleOut(x);
    }

    @Override
    public void println(String x) {
        handleOut(x);
    }

    @Override
    public void println(Object x) {
        handleOut(x);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        handleOut(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        handleOut(String.format(format, args));
        return this;
    }

    private void handleOut(Object print) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = String.format("[%s] [%s] %s", sdf.format(new Date()), Thread.currentThread().getName(), print.toString());
        super.println(current);
        console.println(current);
    }

}
