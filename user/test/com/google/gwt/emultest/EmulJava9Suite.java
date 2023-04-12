package com.google.gwt.emultest;

import com.google.gwt.emultest.java9.util.ListTest;
import com.google.gwt.emultest.java9.util.MapTest;
import com.google.gwt.emultest.java9.util.SetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Test JRE emulations. */
@RunWith(Suite.class)
@SuiteClasses({
        ListTest.class,
        SetTest.class,
        MapTest.class
})
public class EmulJava9Suite {
}
